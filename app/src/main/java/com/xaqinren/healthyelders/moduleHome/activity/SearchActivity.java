package com.xaqinren.healthyelders.moduleHome.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;

import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.bean.SlideBarBean;
import com.xaqinren.healthyelders.databinding.ActivitySearchBinding;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleHome.adapter.HistoryTagAdapter;
import com.xaqinren.healthyelders.moduleHome.adapter.HotTagAdapter;
import com.xaqinren.healthyelders.moduleHome.bean.SearchBean;
import com.xaqinren.healthyelders.moduleHome.viewModel.SearchViewModel;
import com.xaqinren.healthyelders.moduleMall.activity.GoodsSearchActivity;
import com.xaqinren.healthyelders.utils.ACache;
import com.xaqinren.healthyelders.widget.AutoLineLayoutManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.bus.RxBus;

/**
 * Created by Lee. on 2021/5/27.
 * 首页-搜索页面
 */
public class SearchActivity extends BaseActivity<ActivitySearchBinding, SearchViewModel> {

    private HotTagAdapter hotTagAdapter;
    private HistoryTagAdapter historyTagAdapter;
    private SlideBarBean searchListCache;
    private String tags;
    private int searchType;
    private final int TYPE_HOME = 0, TYPE_GOODS = 1;
    private Disposable subscribe;

    public static void startActivity(Context context, int searchType /*0首页搜索，1商品搜索 */) {
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra("type", searchType);
        context.startActivity(intent);
    }

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_search;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        searchType = getIntent().getIntExtra("type", 0);
        super.initData();
        setStatusBarTransparentBlack();

        historyTagAdapter = new HistoryTagAdapter(R.layout.item_search_history);
        binding.rvHistory.setLayoutManager(new AutoLineLayoutManager());
        binding.rvHistory.setAdapter(historyTagAdapter);

        hotTagAdapter = new HotTagAdapter(R.layout.item_search_hot);
        binding.rvHot.setLayoutManager(new GridLayoutManager(this, 2));
        binding.rvHot.setAdapter(hotTagAdapter);

        //获取缓存数据
        Object asObject = null;
        if (searchType == TYPE_HOME) {
            asObject = ACache.get(this).getAsObject(Constant.SearchId);
        } else if (searchType == TYPE_GOODS) {
            asObject = ACache.get(this).getAsObject(Constant.SearchGId);
        }

        if (asObject instanceof SearchBean) {
            //以前搜索过的，现在需要转换
            SearchBean searchBean = (SearchBean) asObject;
            SlideBarBean slideBarBean = new SlideBarBean();
            slideBarBean.setMenuInfoList(new ArrayList<>());
            for (SearchBean bean : searchBean.searchBeans) {
                SlideBarBean.MenuInfoListDTO dto = new SlideBarBean.MenuInfoListDTO();
                dto.setMenuName(bean.hotWord);
                slideBarBean.getMenuInfoList().add(dto);
            }
            if (searchType == TYPE_HOME) {
                ACache.get(this).put(Constant.SearchId, slideBarBean);
            } else if (searchType == TYPE_GOODS) {
                ACache.get(this).put(Constant.SearchGId, slideBarBean);
            }
        }
        if (searchType == TYPE_HOME) {
            searchListCache = (SlideBarBean) ACache.get(this).getAsObject(Constant.SearchId);
        } else if (searchType == TYPE_GOODS) {
            searchListCache = (SlideBarBean) ACache.get(this).getAsObject(Constant.SearchGId);
        }
        if (searchListCache == null) {
            searchListCache = new SlideBarBean();
        }
        if (searchListCache.getMenuInfoList() != null && searchListCache.getMenuInfoList().size() > 0) {
            binding.rlSearchHistory.setVisibility(View.VISIBLE);
            historyTagAdapter.setNewInstance(searchListCache.getMenuInfoList());
        }
        if (searchType == TYPE_HOME)
            viewModel.getHotList();
        else if (searchType == TYPE_GOODS)
            viewModel.getGoodsHotList();

        initEvent();
    }

    public void initEvent() {
        binding.tvCancel.setOnClickListener(lis -> {
            if (isSearch) {
                toSearch();
            } else {
                finish();
            }
        });
        binding.tvClean.setOnClickListener(lis -> {
            //清除搜索历史
            SlideBarBean searchBean = new SlideBarBean();
            searchBean.setMenuInfoList(new ArrayList<>());
            if (searchType == TYPE_HOME)
                ACache.get(this).put(Constant.SearchId, searchBean);
            else if (searchType == TYPE_GOODS)
                ACache.get(this).put(Constant.SearchGId, searchBean);

            resetHistoryTagAdapter(searchBean.getMenuInfoList());
            searchListCache.getMenuInfoList().clear();
        });

        binding.etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (!TextUtils.isEmpty(binding.etSearch.getText().toString())) {
                        tags = binding.etSearch.getText().toString();
                    }
                    toSearch();
                }
                return false;
            }
        });


        binding.etSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    binding.tvCancel.setText("搜索");
                    isSearch = true;
                } else {
                    binding.tvCancel.setText("取消");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        hotTagAdapter.setOnItemClickListener(((adapter, view, position) -> {
            tags = hotTagAdapter.getData().get(position).getMenuName();
            addCache(tags);
            toJump();
        }));

        historyAdapterEvent();
    }

    public void historyAdapterEvent() {
        historyTagAdapter.setOnItemClickListener(((adapter, view, position) -> {
            tags = historyTagAdapter.getData().get(position).getMenuName();
            addCache(tags);
            toJump();
        }));
    }

    private void toSearch() {
        //如果没有输入不存进历史
        if (TextUtils.isEmpty(binding.etSearch.getText().toString())) {
            return;
        }


        if (binding.rlSearchHistory.getVisibility() == View.GONE) {
            binding.rlSearchHistory.setVisibility(View.VISIBLE);
        }
        addCache(binding.etSearch.getText().toString().trim());

        //跳页
        toJump();
    }

    private void addCache(String content) {
        if (searchListCache.getMenuInfoList() != null) {

            //去重重插入
            int temp = -1;
            for (int i = 0; i < searchListCache.getMenuInfoList().size(); i++) {
                if (searchListCache.getMenuInfoList().get(i).getMenuName().equals(content)) {
                    temp = i;
                }
            }

            if (temp > -1) {
                searchListCache.getMenuInfoList().remove(temp);
            }

            //超过十条去除一条
            if (searchListCache.getMenuInfoList().size() > 9) {
                searchListCache.getMenuInfoList().remove(searchListCache.getMenuInfoList().size() - 1);
            }
        }

        //往下面的搜索插入
        SlideBarBean.MenuInfoListDTO searchBean = new SlideBarBean.MenuInfoListDTO();
        searchBean.setMenuName(content);

        //最新的放在前面
        List<SlideBarBean.MenuInfoListDTO> menuInfoList = searchListCache.getMenuInfoList();
        menuInfoList.add(0, searchBean);
        searchListCache.setMenuInfoList(menuInfoList);

        resetHistoryTagAdapter(menuInfoList);

        if (searchType == TYPE_HOME)
            ACache.get(SearchActivity.this).put(Constant.SearchId, searchListCache);
        else if (searchType == TYPE_GOODS)
            ACache.get(SearchActivity.this).put(Constant.SearchGId, searchListCache);
    }

    private void toJump() {
        binding.etSearch.setHint(tags);

        Bundle bundle = new Bundle();
        bundle.putString("tags", tags);
        if (searchType == TYPE_HOME) {
            startActivity(SearchAllActivity.class, bundle);
        } else if (searchType == TYPE_GOODS) {
            startActivity(GoodsSearchActivity.class, bundle);
        }
    }

    private boolean isSearch;

    //重新加载绘制 目前只能重new Adapter
    private void resetHistoryTagAdapter(List<SlideBarBean.MenuInfoListDTO> searchBeans) {
        historyTagAdapter = new HistoryTagAdapter(R.layout.item_search_history);
        binding.rlSearchHistory.setVisibility(View.VISIBLE);
        binding.rvHistory.setAdapter(historyTagAdapter);
        historyTagAdapter.setNewInstance(searchBeans);
        historyAdapterEvent();

    }

    private Random random = new Random();

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        subscribe = RxBus.getDefault().toObservable(EventBean.class).subscribe(eventBean -> {
            if (eventBean != null && eventBean.msgId == CodeTable.HOME_SEARCHER) {
                addCache(eventBean.content);
            }
        });
        viewModel.searchHistoryList.observe(this, datas -> {
            if (datas != null) {
                historyTagAdapter.setNewInstance(datas.getMenuInfoList());
            }
        });
        viewModel.searchHotList.observe(this, datas -> {
            if (datas != null) {
                if (datas.getMenuInfoList().size() > 0) {
                    int temp = random.nextInt(datas.getMenuInfoList().size());
                    tags = datas.getMenuInfoList().get(temp).getMenuName();
                    binding.etSearch.setHint(tags);
                }
                hotTagAdapter.setNewInstance(datas.getMenuInfoList());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subscribe != null) {
            subscribe.dispose();
        }
    }
}
