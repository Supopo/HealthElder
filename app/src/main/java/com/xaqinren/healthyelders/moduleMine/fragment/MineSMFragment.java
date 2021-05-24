package com.xaqinren.healthyelders.moduleMine.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.chad.library.adapter.base.module.BaseLoadMoreModule;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.FragmentMineSmBinding;
import com.xaqinren.healthyelders.moduleMine.adapter.SMVideoAdapter;
import com.xaqinren.healthyelders.moduleMine.viewModel.MineSMViewModel;
import com.xaqinren.healthyelders.widget.SpeacesItemDecoration;

import me.goldze.mvvmhabit.base.BaseFragment;

/**
 * Created by Lee. on 2021/5/24.
 * 我的-私密作品
 */
public class MineSMFragment extends BaseFragment<FragmentMineSmBinding, MineSMViewModel> {
    private int page = 1;
    private int pageSize = 10;
    private SMVideoAdapter videoAdapter;
    private BaseLoadMoreModule mLoadMore;


    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_mine_sm;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();

        videoAdapter = new SMVideoAdapter(R.layout.item_mine_sm_video);

        mLoadMore = videoAdapter.getLoadMoreModule();//创建适配器.上拉加载
        mLoadMore.setEnableLoadMore(true);//打开上拉加载
        mLoadMore.setAutoLoadMore(true);//自动加载
        mLoadMore.setPreLoadNumber(1);//设置滑动到倒数第几个条目时自动加载，默认为1
        mLoadMore.setEnableLoadMoreIfNotFullPage(true);//当数据不满一页时继续自动加载
        mLoadMore.setOnLoadMoreListener(new OnLoadMoreListener() {//设置加载更多监听事件
            @Override
            public void onLoadMore() {
                page++;
                viewModel.getMyVideoList(page, pageSize);
            }
        });

        //瀑布流
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        //防止Item切换
        manager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        binding.rvContent.setLayoutManager(manager);
        binding.rvContent.setAdapter(videoAdapter);
        //防止刷新跳动
        binding.rvContent.setItemAnimator(null);
        binding.rvContent.addItemDecoration(new SpeacesItemDecoration(getActivity(), 4, 3, true));

        viewModel.getMyVideoList(page, pageSize);
    }

    public void getVideoList() {
        if (videoAdapter.getData().size() == 0) {
            page = 1;
            viewModel.getMyVideoList(page, pageSize);
        }
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.mVideoList.observe(this, dataList -> {
            if (dataList != null) {
                if (dataList.size() > 0) {
                    //加载更多加载完成
                    mLoadMore.loadMoreComplete();
                }
                if (page == 1) {
                    //为了防止刷新时候图片闪烁统一用notifyItemRangeInserted刷新
                    videoAdapter.setList(dataList);
                    if (dataList.size() == 0) {
                        //创建适配器.空布局，没有数据时候默认展示的
                        videoAdapter.setEmptyView(R.layout.list_empty);
                    }
                } else {
                    if (dataList.size() == 0) {
                        //加载更多加载结束
                        mLoadMore.loadMoreEnd(true);
                    }
                    videoAdapter.addData(dataList);
                }
            }
        });
    }
}
