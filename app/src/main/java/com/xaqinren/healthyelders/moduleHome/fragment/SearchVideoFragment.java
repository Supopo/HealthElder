package com.xaqinren.healthyelders.moduleHome.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.chad.library.adapter.base.module.BaseLoadMoreModule;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.databinding.FragmentSearchVideoBinding;
import com.xaqinren.healthyelders.databinding.FragmentSearchZbBinding;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.moduleHome.activity.VideoListActivity;
import com.xaqinren.healthyelders.moduleHome.adapter.SearchVideoAdapter;
import com.xaqinren.healthyelders.moduleHome.adapter.SearchZhiboAdapter;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;
import com.xaqinren.healthyelders.moduleHome.bean.VideoListBean;
import com.xaqinren.healthyelders.moduleHome.viewModel.SearchAllViewModel;
import com.xaqinren.healthyelders.widget.SpeacesItemDecoration;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.bus.RxBus;

/**
 * Created by Lee. on 2021/5/28.
 * 搜索 - 视频列表
 */
public class SearchVideoFragment extends BaseFragment<FragmentSearchVideoBinding, SearchAllViewModel> {

    private SearchVideoAdapter mAdapter;
    private int page = 1;
    private BaseLoadMoreModule mLoadMore;
    private SearchAllViewModel searchAllViewModel;
    private Disposable subscribe;


    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_search_video;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        //获取别的ViewModel
        searchAllViewModel = ViewModelProviders.of(getActivity()).get(SearchAllViewModel.class);
        mAdapter = new SearchVideoAdapter(R.layout.item_search_video);
        //瀑布流
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        //防止Item切换
        manager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        binding.rvContent.setLayoutManager(manager);
        binding.rvContent.addItemDecoration(new SpeacesItemDecoration(getActivity(), 3, true));
        //防止刷新跳动
        binding.rvContent.setItemAnimator(null);

        binding.rvContent.setAdapter(mAdapter);

        mLoadMore = mAdapter.getLoadMoreModule();//创建适配器.上拉加载
        mLoadMore.setEnableLoadMore(true);//打开上拉加载
        mLoadMore.setAutoLoadMore(true);//自动加载
        mLoadMore.setPreLoadNumber(1);//设置滑动到倒数第几个条目时自动加载，默认为1
        mLoadMore.setEnableLoadMoreIfNotFullPage(true);//当数据不满一页时继续自动加载
        mLoadMore.setOnLoadMoreListener(new OnLoadMoreListener() {//设置加载更多监听事件
            @Override
            public void onLoadMore() {
                binding.srlContent.setRefreshing(false);
                page++;
                searchAllViewModel.searchDatas(page,1);
            }
        });

        binding.srlContent.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mLoadMore.setEnableLoadMore(false);
                binding.srlContent.setRefreshing(false);
                page = 1;
                searchAllViewModel.searchDatas(page,1);
            }
        });

        mAdapter.setOnItemClickListener(((adapter, view, position) -> {
            List<VideoInfo> tempList = new ArrayList<>();
            VideoInfo videoInfo = mAdapter.getData().get(position);
            tempList.add(videoInfo);
            toVideoList(tempList);
        }));

    }

    private void toVideoList(List<VideoInfo> tempList) {
        //跳页 传入数据 pos page list
        VideoListBean listBean = new VideoListBean();

        listBean.page = 0;
        listBean.position = 0;
        listBean.videoInfos = tempList;
        listBean.type = 2;

        Bundle bundle = new Bundle();
        bundle.putSerializable("key", listBean);
        bundle.putBoolean("key1", true);
        startActivity(VideoListActivity.class, bundle);
    }


    @Override
    public void initViewObservable() {
        super.initViewObservable();

        searchAllViewModel.videoDatas.observe(this, dataList -> {
            if (dataList != null) {
                if (dataList.size() > 0) {
                    //加载更多加载完成
                    mLoadMore.loadMoreComplete();
                }
                if (page == 1) {
                    //为了防止刷新时候图片闪烁统一用notifyItemRangeInserted刷新
                    mAdapter.setList(dataList);
                    if (dataList.size() == 0) {
                        //创建适配器.空布局，没有数据时候默认展示的
                        mAdapter.setEmptyView(R.layout.list_empty);
                    }
                } else {
                    if (dataList.size() == 0) {
                        //加载更多加载结束
                        mLoadMore.loadMoreEnd(true);
                        page--;
                    }
                    mAdapter.addData(dataList);
                }
            }
        });
        subscribe = RxBus.getDefault().toObservable(EventBean.class).subscribe(event -> {
            if (event != null && event.msgType == CodeTable.SEARCH_TAG) {
                page = 1;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (subscribe != null) {
            subscribe.dispose();
        }
    }
}
