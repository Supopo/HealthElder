package com.xaqinren.healthyelders.moduleHome.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.chad.library.adapter.base.module.BaseLoadMoreModule;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivityVideoGridBinding;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleHome.adapter.FJVideoAdapter;
import com.xaqinren.healthyelders.moduleHome.adapter.GridVideoAdapter;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;
import com.xaqinren.healthyelders.moduleHome.bean.VideoListBean;
import com.xaqinren.healthyelders.moduleHome.viewModel.VideoGridViewModel;
import com.xaqinren.healthyelders.modulePicture.activity.TextPhotoDetailActivity;
import com.xaqinren.healthyelders.widget.SpeacesItemDecoration;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.base.BaseActivity;

/**
 * Created by Lee. on 2021/5/24.
 * 首页菜单-视频页面
 */
public class VideoGridActivity extends BaseActivity<ActivityVideoGridBinding, VideoGridViewModel> {

    private GridVideoAdapter mAdapter;
    private int page = 1;
    private BaseLoadMoreModule mLoadMore;
    public RecyclerView recyclerView;
    private String title;
    private String tags;
    private long firstLikeTime;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_video_grid;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initParam() {
        super.initParam();
        Bundle extras = getIntent().getExtras();
        title = extras.getString("title");
        tags = extras.getString("tags");
    }

    @Override
    public void initData() {
        super.initData();
        setTitle(title);

        recyclerView = binding.rvVideo;

        mAdapter = new GridVideoAdapter(R.layout.item_grid_video);
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
                viewModel.getVideoData(page, tags);
            }
        });

        binding.srlContent.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mLoadMore.setEnableLoadMore(false);
                page = 1;
                showLoadView();
                viewModel.getVideoData(page, tags);
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
        binding.rvVideo.addItemDecoration(new SpeacesItemDecoration(this, 3, true));

        viewModel.getVideoData(page, tags);

        mAdapter.setOnItemChildClickListener(((adapter, view, position) -> {
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstLikeTime < 500) {
                return;
            }
            //点赞请求
            viewModel.toLikeVideo(mAdapter.getData().get(position).resourceId, !mAdapter.getData().get(position).hasFavorite,position);
            firstLikeTime = secondTime;
        }));


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
                listBean.type = 2;
                listBean.tags = tags;

                Bundle bundle = new Bundle();
                bundle.putSerializable("key", listBean);
                startActivity(VideoListActivity.class, bundle);

            } else {
                Intent intent = new Intent(VideoGridActivity.this, TextPhotoDetailActivity.class);
                intent.putExtra(com.xaqinren.healthyelders.moduleLiteav.Constant.VIDEO_ID, adapterList.get(position).resourceId);
                startActivity(intent);
            }
        }));
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.dismissDialog.observe(this, dis -> {
            dismissDialog();
        });

        viewModel.dzSuccess.observe(this, dzRes -> {
            if (dzRes != null && dzRes.isSuccess) {
                //点赞
                mAdapter.getData().get(dzRes.position).hasFavorite = !mAdapter.getData().get(dzRes.position).hasFavorite;
                int favoriteCount = mAdapter.getData().get(dzRes.position).getFavoriteCount();

                if (mAdapter.getData().get(dzRes.position).hasFavorite) {
                    favoriteCount++;
                } else {
                    favoriteCount--;
                }
                mAdapter.getData().get(dzRes.position).favoriteCount = String.valueOf(favoriteCount);
                //刷新
                mAdapter.notifyItemChanged(dzRes.position, 99);
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
        binding.loadView.playAnimation();
        binding.loadView.setVisibility(View.VISIBLE);
    }

    private void closeLoadView() {
        binding.loadView.cancelAnimation();
        binding.loadView.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
