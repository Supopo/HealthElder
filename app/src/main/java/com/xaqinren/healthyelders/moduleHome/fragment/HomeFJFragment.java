package com.xaqinren.healthyelders.moduleHome.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.chad.library.adapter.base.module.BaseLoadMoreModule;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.databinding.FragmentHomeFjBinding;
import com.xaqinren.healthyelders.global.AppApplication;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleHome.adapter.FJVideoAdapter;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;
import com.xaqinren.healthyelders.moduleHome.viewModel.HomeFJViewModel;
import com.xaqinren.healthyelders.moduleLiteav.bean.LocationBean;
import com.xaqinren.healthyelders.utils.LogUtils;
import com.xaqinren.healthyelders.widget.SpeacesItemDecoration;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.bus.RxBus;

/**
 * Created by Lee. on 2021/5/13.
 * 首页-附近
 */
public class HomeFJFragment extends BaseFragment<FragmentHomeFjBinding, HomeFJViewModel> {

    private FJVideoAdapter mAdapter;
    private int page = 1;
    private BaseLoadMoreModule mLoadMore;
    private Disposable subscribe;
    private double lat;
    private double lon;

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_home_fj;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();

        mAdapter = new FJVideoAdapter(R.layout.item_fj_video);
        mLoadMore = mAdapter.getLoadMoreModule();//创建适配器.上拉加载
        mLoadMore.setEnableLoadMore(true);//打开上拉加载
        mLoadMore.setAutoLoadMore(true);//自动加载
        mLoadMore.setPreLoadNumber(1);//设置滑动到倒数第几个条目时自动加载，默认为1
        mLoadMore.setEnableLoadMoreIfNotFullPage(true);//当数据不满一页时继续自动加载
        mLoadMore.setOnLoadMoreListener(new OnLoadMoreListener() {//设置加载更多监听事件
            @Override
            public void onLoadMore() {
                page++;
                viewModel.getVideoData(page);
            }
        });

        //瀑布流
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        //防止Item切换
        manager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        binding.rvVideo.setLayoutManager(manager);
        binding.rvVideo.setAdapter(mAdapter);
        //防止刷新跳动
        binding.rvVideo.setItemAnimator(null);
        binding.rvVideo.addItemDecoration(new SpeacesItemDecoration(getActivity(), 3, true));

//        List<VideoInfo> list = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            VideoInfo videoInfo = new VideoInfo();
//            list.add(videoInfo);
//        }
//
//        mAdapter.setNewInstance(list);
    }

    private boolean isInit;//设置懒加载，点到关注才开始加载

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        subscribe = RxBus.getDefault().toObservable(EventBean.class).subscribe(event -> {
            if (event != null) {
                if (event.msgId == 101 && event.msgType == 2) {
                    //判断是不是第一次切换到关注列表
                    if (!isInit) {
                        viewModel.getVideoData(page);
                    }
                }
                else  if (event.msgId == CodeTable.LOCATION_SUCCESS) {
                    //定位成功
                    LocationBean locationBean = (LocationBean) event.data;
                    lat = locationBean.lat;
                    lon = locationBean.lon;
                    AppApplication.get().setmLat(lat);
                    AppApplication.get().setmLon(lon);
                    LogUtils.v(Constant.TAG_LIVE,"我的坐标"+lat);
                    LogUtils.v(Constant.TAG_LIVE,"我的坐标"+lon);
                }
            }
        });
        viewModel.datas.observe(this, dataList -> {
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
                    }
                    mAdapter.addData(dataList);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        subscribe.dispose();
    }
}
