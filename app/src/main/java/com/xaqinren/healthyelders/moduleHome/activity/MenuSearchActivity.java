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
 * ????????????-????????????
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
    private boolean hasSearch;


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


        //??????????????????
        Object asObject = ACache.get(this).getAsObject(pinYin);


        if (asObject instanceof SearchBean) {
            //???????????????????????????????????????
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
        mLoadMore = mAdapter.getLoadMoreModule();//???????????????.????????????
        mLoadMore.setEnableLoadMore(true);//??????????????????
        mLoadMore.setAutoLoadMore(true);//????????????
        mLoadMore.setPreLoadNumber(1);//???????????????????????????????????????????????????????????????1
        mLoadMore.setEnableLoadMoreIfNotFullPage(true);//??????????????????????????????????????????
        mLoadMore.setOnLoadMoreListener(new OnLoadMoreListener() {//??????????????????????????????
            @Override
            public void onLoadMore() {
                page++;
                viewModel.getVideoData(page, menuType, key);
            }
        });

        //?????????
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        //??????Item??????
        manager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        binding.rvContent.setLayoutManager(manager);
        binding.rvContent.setAdapter(mAdapter);
        //??????????????????
        binding.rvContent.setItemAnimator(null);
        binding.rvContent.addItemDecoration(new SpeacesItemDecorationEx(this, 0, getResources().getDimension(R.dimen.dp_7), 0, getResources().getDimension(R.dimen.dp_7), true));

        mAdapter.setOnItemClickListener(((adapter, view, position) -> {
            //???????????????????????????
            List<VideoInfo> adapterList = mAdapter.getData();

            //????????????
            if (!adapterList.get(position).isArticle()) {

                //??????????????????????????????
                VideoInfo nowInfo = adapterList.get(position);
                List<VideoInfo> tempList = new ArrayList<>();
                //???????????? ??????position
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

                //?????? ???????????? pos page list
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
            if (hasSearch) {
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
            //??????????????????
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
                    binding.tvCancel.setText("??????");
                    isSearch = true;
                } else {
                    binding.tvCancel.setText("??????");
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

        //?????????????????????????????????
        if (!TextUtils.isEmpty(binding.etSearch.getText().toString().trim())) {
            key = binding.etSearch.getText().toString().trim();
        }
        binding.rlSearch.setVisibility(View.GONE);
        binding.rvContent.setVisibility(View.VISIBLE);

        if (binding.rlSearchHistory.getVisibility() == View.GONE) {
            binding.rlSearchHistory.setVisibility(View.VISIBLE);
        }
        addCache(key);

        //??????
        toJump();
    }

    private void addCache(String content) {
        if (searchListCache.getMenuInfoList() != null) {

            //???????????????
            int temp = -1;
            for (int i = 0; i < searchListCache.getMenuInfoList().size(); i++) {
                if (searchListCache.getMenuInfoList().get(i).getMenuName().equals(content)) {
                    temp = i;
                }
            }

            if (temp > -1) {
                searchListCache.getMenuInfoList().remove(temp);
            }

            //????????????????????????
            if (searchListCache.getMenuInfoList().size() > 9) {
                searchListCache.getMenuInfoList().remove(searchListCache.getMenuInfoList().size() - 1);
            }
        }

        //????????????????????????
        SlideBarBean.MenuInfoListDTO searchBean = new SlideBarBean.MenuInfoListDTO();
        searchBean.setMenuName(content);

        //?????????????????????
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
        closeInputMethod();//????????????
        //????????????
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

    //?????????????????? ???????????????new Adapter
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
                hasSearch = true;
                binding.rvContent.setVisibility(View.VISIBLE);
                if (dataList.size() > 0) {
                    //????????????????????????
                    mLoadMore.loadMoreComplete();
                }
                if (page == 1) {
                    if (dataList.size() == 0) {
                        //???????????????.?????????????????????????????????????????????
                        mAdapter.setEmptyView(R.layout.list_empty);
                    } else {
                        mAdapter.setNewInstance(dataList);
                    }
                } else {
                    if (dataList.size() == 0) {
                        //????????????????????????
                        mLoadMore.loadMoreEnd();
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
