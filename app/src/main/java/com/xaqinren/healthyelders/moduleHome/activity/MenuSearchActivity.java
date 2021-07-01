package com.xaqinren.healthyelders.moduleHome.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.chad.library.adapter.base.module.BaseLoadMoreModule;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.bean.SlideBarBean;
import com.xaqinren.healthyelders.databinding.ActivityMenuSearchBinding;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleHome.adapter.GridVideoAdapter;
import com.xaqinren.healthyelders.moduleHome.adapter.HistoryTagAdapter;
import com.xaqinren.healthyelders.moduleHome.adapter.MenuTagAdapter;
import com.xaqinren.healthyelders.moduleHome.adapter.MenuTextTagAdapter;
import com.xaqinren.healthyelders.moduleHome.bean.SearchBean;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;
import com.xaqinren.healthyelders.moduleHome.bean.VideoListBean;
import com.xaqinren.healthyelders.moduleHome.viewModel.MenuSearchViewModel;
import com.xaqinren.healthyelders.modulePicture.activity.TextPhotoDetailActivity;
import com.xaqinren.healthyelders.utils.ACache;
import com.xaqinren.healthyelders.utils.Cn2Spell;
import com.xaqinren.healthyelders.widget.AutoLineLayoutManager;
import com.xaqinren.healthyelders.widget.SpeacesItemDecorationEx;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.bus.RxBus;

/**
 * Created by Lee. on 2021/5/27.
 * 首页菜单-搜索页面
 */
public class MenuSearchActivity extends BaseActivity<ActivityMenuSearchBinding, MenuSearchViewModel> {

    private MenuTagAdapter hotTagAdapter;
    private HistoryTagAdapter historyTagAdapter;
    private SlideBarBean searchListCache;
    private String key;
    private String menuType;
    private Disposable subscribe;
    private GridVideoAdapter mAdapter;
    private BaseLoadMoreModule mLoadMore;
    private MenuTextTagAdapter hotTextTagAdapter;
    private String pinYin;


    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_menu_search;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }


    @Override
    public void initData() {
        super.initData();
        if (getIntent().getExtras() != null) {
            menuType = getIntent().getExtras().getString("title");
        }
        pinYin = Cn2Spell.getPinYin(menuType);

        setStatusBarTransparentBlack();

        historyTagAdapter = new HistoryTagAdapter(R.layout.item_search_history);
        binding.rvHistory.setLayoutManager(new AutoLineLayoutManager());
        binding.rvHistory.setAdapter(historyTagAdapter);


        //获取缓存数据
        Object asObject = ACache.get(this).getAsObject(pinYin);


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

            ACache.get(this).put(pinYin, slideBarBean);

        }

        searchListCache = (SlideBarBean) ACache.get(this).getAsObject(pinYin);

        if (searchListCache == null) {
            searchListCache = new SlideBarBean();
        }
        if (searchListCache.getMenuInfoList() != null && searchListCache.getMenuInfoList().size() > 0) {
            binding.rlSearchHistory.setVisibility(View.VISIBLE);
            historyTagAdapter.setNewInstance(searchListCache.getMenuInfoList());
        }

        viewModel.getAllWordsList(menuType);
        initEvent();
        initContent();

    }

    public void initContent() {
        mAdapter = new GridVideoAdapter(R.layout.item_grid_video);
        mLoadMore = mAdapter.getLoadMoreModule();//创建适配器.上拉加载
        mLoadMore.setEnableLoadMore(true);//打开上拉加载
        mLoadMore.setAutoLoadMore(true);//自动加载
        mLoadMore.setPreLoadNumber(1);//设置滑动到倒数第几个条目时自动加载，默认为1
        mLoadMore.setEnableLoadMoreIfNotFullPage(true);//当数据不满一页时继续自动加载
        mLoadMore.setOnLoadMoreListener(new OnLoadMoreListener() {//设置加载更多监听事件
            @Override
            public void onLoadMore() {
                page++;
                viewModel.getVideoData(page, menuType, key);
            }
        });

        //瀑布流
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        //防止Item切换
        manager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        binding.rvContent.setLayoutManager(manager);
        binding.rvContent.setAdapter(mAdapter);
        //防止刷新跳动
        binding.rvContent.setItemAnimator(null);
        binding.rvContent.addItemDecoration(new SpeacesItemDecorationEx(this, 0, getResources().getDimension(R.dimen.dp_7), 0, getResources().getDimension(R.dimen.dp_7), true));

        mAdapter.setOnItemClickListener(((adapter, view, position) -> {
            //判断是不是文章类型
            List<VideoInfo> adapterList = mAdapter.getData();

            //不是文章
            if (!adapterList.get(position).isArticle()) {

                //从数据中判断移除文章
                VideoInfo nowInfo = adapterList.get(position);
                List<VideoInfo> tempList = new ArrayList<>();
                //移除文章 计算position
                for (VideoInfo videoInfo : adapterList) {
                    if (!videoInfo.isArticle()) {
                        tempList.add(videoInfo);
                    }
                }

                int tempPos = 0;
                for (int i = 0; i < tempList.size(); i++) {
                    if (nowInfo.resourceId.equals(tempList.get(i).resourceId)) {
                        tempPos = i;
                    }
                }

                //跳页 传入数据 pos page list
                VideoListBean listBean = new VideoListBean();

                if (tempList.size() % Constant.loadVideoSize == 0) {
                    listBean.page = (tempList.size() / 2);
                } else {
                    listBean.page = (tempList.size() / 2) + 1;
                }


                listBean.videoInfos = tempList;
                listBean.position = tempPos;
                listBean.openType = 2;
                listBean.tags = key;

                Bundle bundle = new Bundle();
                bundle.putSerializable("key", listBean);
                startActivity(VideoListActivity.class, bundle);

            } else {
                Intent intent = new Intent(MenuSearchActivity.this, TextPhotoDetailActivity.class);
                intent.putExtra(com.xaqinren.healthyelders.moduleLiteav.Constant.VIDEO_ID, adapterList.get(position).resourceId);
                startActivity(intent);
            }
        }));

        binding.rlAll.setOnClickListener(lis -> {
            if (mAdapter.getData().size() > 0) {
                cancelFocus();
                binding.rlSearch.setVisibility(View.GONE);
                binding.rvContent.setVisibility(View.VISIBLE);
            }
        });
    }

    private int page = 1;


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

            ACache.get(this).put(pinYin, searchBean);

            resetHistoryTagAdapter(searchBean.getMenuInfoList());
            searchListCache.getMenuInfoList().clear();
        });

        binding.etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (!TextUtils.isEmpty(binding.etSearch.getText().toString())) {
                        key = binding.etSearch.getText().toString();
                    }
                    binding.rlSearch.setVisibility(View.GONE);
                    binding.rvContent.setVisibility(View.VISIBLE);
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

        binding.etSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    binding.rlSearch.setVisibility(View.VISIBLE);
                    binding.rvContent.setVisibility(View.GONE);
                }
            }
        });


        historyAdapterEvent();
    }

    public void historyAdapterEvent() {
        historyTagAdapter.setOnItemClickListener(((adapter, view, position) -> {
            key = historyTagAdapter.getData().get(position).getMenuName();
            binding.rlSearch.setVisibility(View.GONE);
            binding.rvContent.setVisibility(View.VISIBLE);
            addCache(key);
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

        ACache.get(MenuSearchActivity.this).put(pinYin, searchListCache);
    }

    private void toJump() {
        mAdapter.getData().clear();

        showDialog();
        binding.etSearch.setHint(key);
        cancelFocus();

        page = 1;
        viewModel.getVideoData(page, menuType, key);
    }

    private void cancelFocus() {
        closeInputMethod();//收起键盘
        //取消焦点
        binding.rlSearch.setFocusable(true);
        binding.rlSearch.setFocusableInTouchMode(true);
        binding.rlSearch.requestFocus();
        binding.rlSearch.requestFocusFromTouch();
    }

    private void closeInputMethod() {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
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
                    key = datas.getMenuInfoList().get(temp).getMenuName();
                    binding.etSearch.setHint(key);

                    if (datas.getMenuInfoList().get(0).getOnlyShowImage()) {
                        initHotImgAdapter(datas);
                    } else {
                        initHotTextAdapter(datas);
                    }
                }
            }
        });
        viewModel.dismissDialog.observe(this, dis -> {
            dismissDialog();
        });
        viewModel.datas.observe(this, dataList -> {
            if (dataList != null) {
                binding.rvContent.setVisibility(View.VISIBLE);
                if (dataList.size() > 0) {
                    //加载更多加载完成
                    mLoadMore.loadMoreComplete();
                }
                if (page == 1) {
                    if (dataList.size() == 0) {
                        //创建适配器.空布局，没有数据时候默认展示的
                        mAdapter.setEmptyView(R.layout.list_empty);
                    } else {
                        mAdapter.setNewInstance(dataList);
                    }
                } else {
                    if (dataList.size() == 0) {
                        //加载更多加载结束
                        mLoadMore.loadMoreEnd(true);
                    }
                    mAdapter.addData(dataList);
                }
            }
        });
    }

    public void initHotTextAdapter(SlideBarBean datas) {
        hotTextTagAdapter = new MenuTextTagAdapter(R.layout.item_search_text_menu);
        binding.rvHot.setLayoutManager(new AutoLineLayoutManager());
        binding.rvHot.setAdapter(hotTextTagAdapter);
        hotTextTagAdapter.setNewInstance(datas.getMenuInfoList());
        hotTextTagAdapter.setOnItemClickListener(((adapter, view, position) -> {
            key = hotTextTagAdapter.getData().get(position).getMenuName();
            binding.rlSearch.setVisibility(View.GONE);
            binding.rvContent.setVisibility(View.VISIBLE);
            addCache(key);
            toJump();
        }));
    }

    public void initHotImgAdapter(SlideBarBean datas) {
        hotTagAdapter = new MenuTagAdapter(R.layout.item_search_menu_tag);
        binding.rvHot.setLayoutManager(new GridLayoutManager(this, 2));
        binding.rvHot.setAdapter(hotTagAdapter);
        hotTagAdapter.setNewInstance(datas.getMenuInfoList());
        hotTagAdapter.setOnItemClickListener(((adapter, view, position) -> {
            key = hotTagAdapter.getData().get(position).getMenuName();
            binding.rlSearch.setVisibility(View.GONE);
            binding.rvContent.setVisibility(View.VISIBLE);
            addCache(key);
            toJump();
        }));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subscribe != null) {
            subscribe.dispose();
        }
    }
}
