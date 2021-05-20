package com.xaqinren.healthyelders.moduleHome.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.chad.library.adapter.base.module.BaseLoadMoreModule;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.databinding.FragmentHomeFjBinding;
import com.xaqinren.healthyelders.global.AppApplication;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleHome.activity.VideoListActivity;
import com.xaqinren.healthyelders.moduleHome.adapter.FJVideoAdapter;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;
import com.xaqinren.healthyelders.moduleHome.bean.VideoListBean;
import com.xaqinren.healthyelders.moduleHome.viewModel.HomeFJViewModel;
import com.xaqinren.healthyelders.moduleLiteav.bean.LocationBean;
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
    public RecyclerView recyclerView;
    public View viewTop;

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
        recyclerView = binding.rvVideo;
        viewTop = binding.viewTop;


        mAdapter = new FJVideoAdapter(R.layout.item_fj_video);
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
                viewModel.getVideoData(page);
            }
        });

        binding.srlContent.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mLoadMore.setEnableLoadMore(false);
                page = 1;
                showLoadView();
                viewModel.getVideoData(page);
                binding.srlContent.setRefreshing(false);
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

        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
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
                    listBean.type = 2;

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("key", listBean);
                    startActivity(VideoListActivity.class, bundle);

                } else {
                }
            }
        });
    }

    private boolean isFirst = true;

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        subscribe = RxBus.getDefault().toObservable(EventBean.class).subscribe(event -> {
            if (event != null) {
                if (event.msgId == 101 && event.msgType == 2) {
                    if (isFirst) {
                        //判断是不是第一次切换到关注列表
                        showLoadView();
                        viewModel.getVideoData(page);
                    }
                    isFirst = false;
                } else if (event.msgId == CodeTable.LOCATION_SUCCESS) {
                    //定位成功
                    LocationBean locationBean = (LocationBean) event.data;
                    lat = locationBean.lat;
                    lon = locationBean.lon;
                    AppApplication.get().setmLat(lat);
                    AppApplication.get().setmLon(lon);
                }
            }
        });
        viewModel.datas.observe(this, dataList -> {
            closeLoadView();
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

    private void showLoadView() {
        binding.loadView.start();
        binding.loadView.setVisibility(View.VISIBLE);
    }

    private void closeLoadView() {
        binding.loadView.stop();
        binding.loadView.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        subscribe.dispose();
    }
}
