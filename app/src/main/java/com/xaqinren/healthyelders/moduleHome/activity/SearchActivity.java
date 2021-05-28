package com.xaqinren.healthyelders.moduleHome.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;

import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivitySearchBinding;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleHome.adapter.HistoryTagAdapter;
import com.xaqinren.healthyelders.moduleHome.adapter.HotTagAdapter;
import com.xaqinren.healthyelders.moduleHome.bean.SearchBean;
import com.xaqinren.healthyelders.moduleHome.viewModel.SearchViewModel;
import com.xaqinren.healthyelders.utils.ACache;
import com.xaqinren.healthyelders.widget.AutoLineLayoutManager;

import java.util.List;

import me.goldze.mvvmhabit.base.BaseActivity;

/**
 * Created by Lee. on 2021/5/27.
 * 首页-搜索页面
 */
public class SearchActivity extends BaseActivity<ActivitySearchBinding, SearchViewModel> {

    private HotTagAdapter hotTagAdapter;
    private HistoryTagAdapter historyTagAdapter;
    private SearchBean searchListCache;
    private String tags;

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
        super.initData();
        setStatusBarTransparentBlack();

        historyTagAdapter = new HistoryTagAdapter(R.layout.item_search_history);
        binding.rvHistory.setLayoutManager(new AutoLineLayoutManager());
        binding.rvHistory.setAdapter(historyTagAdapter);

        hotTagAdapter = new HotTagAdapter(R.layout.item_search_hot);
        binding.rvHot.setLayoutManager(new GridLayoutManager(this, 2));
        binding.rvHot.setAdapter(hotTagAdapter);

        //获取缓存数据
        searchListCache = (SearchBean) ACache.get(this).getAsObject(Constant.SearchId);
        if (searchListCache == null) {
            searchListCache = new SearchBean();
        } else if (searchListCache.searchBeans != null && searchListCache.searchBeans.size() > 0) {
            binding.rlSearchHistory.setVisibility(View.VISIBLE);
            historyTagAdapter.setNewInstance(searchListCache.searchBeans);
        }

        viewModel.getHotList();

        binding.tvCancel.setOnClickListener(lis -> {
            if (isSearch) {
                toSearch();
            } else {
                finish();
            }
        });
        binding.tvClean.setOnClickListener(lis -> {
            //清除搜索历史
            SearchBean searchBean = new SearchBean();
            ACache.get(this).put(Constant.SearchId, searchBean);
            resetHistoryTagAdapter(searchBean.searchBeans);
        });

        binding.etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
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

        historyTagAdapter.setOnItemClickListener(((adapter, view, position) -> {
            tags = historyTagAdapter.getData().get(position).hotWord;
            toJump();
        }));

        hotTagAdapter.setOnItemClickListener(((adapter, view, position) -> {
            tags = hotTagAdapter.getData().get(position).hotWord;
            toJump();
        }));
    }

    private void toSearch() {
        if (binding.rlSearchHistory.getVisibility() == View.GONE) {
            binding.rlSearchHistory.setVisibility(View.VISIBLE);
        }

        //往下面的搜索插入
        SearchBean searchBean = new SearchBean();
        searchBean.hotWord = binding.etSearch.getText().toString().trim();
        tags = binding.etSearch.getText().toString().trim();
        //判断超过十条的话移除一条

        historyTagAdapter.addData(searchBean);
        List<SearchBean> searchBeans = historyTagAdapter.getData();
        if (historyTagAdapter.getData().size() > 10) {
            searchBeans.remove(0);
            //重新加载绘制 目前只能重new Adapter
            resetHistoryTagAdapter(searchBeans);
        }
        //更新本地缓存
        searchListCache.searchBeans = searchBeans;
        ACache.get(SearchActivity.this).put(Constant.SearchId, searchListCache);
        //跳页
        toJump();
    }

    private void toJump() {
        Bundle bundle = new Bundle();
        bundle.putString("tags", tags);
        startActivity(SearchAllActivity.class,bundle);
    }

    private boolean isSearch;

    //重新加载绘制 目前只能重new Adapter
    private void resetHistoryTagAdapter(List<SearchBean> searchBeans) {
        historyTagAdapter = new HistoryTagAdapter(R.layout.item_search_history);
        binding.rvHistory.setAdapter(historyTagAdapter);
        historyTagAdapter.setNewInstance(searchBeans);
    }


    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.searchHistoryList.observe(this, datas -> {
            if (datas != null) {
                historyTagAdapter.setNewInstance(datas);
            }
        });
        viewModel.searchHotList.observe(this, datas -> {
            if (datas != null) {
                hotTagAdapter.setNewInstance(datas);
            }
        });
    }
}
