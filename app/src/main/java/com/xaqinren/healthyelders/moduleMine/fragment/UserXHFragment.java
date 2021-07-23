package com.xaqinren.healthyelders.moduleMine.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.chad.library.adapter.base.module.BaseLoadMoreModule;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.FragmentUserXhBinding;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleHome.activity.VideoListActivity;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;
import com.xaqinren.healthyelders.moduleHome.bean.VideoListBean;
import com.xaqinren.healthyelders.moduleMine.adapter.DZVideoAdapter;
import com.xaqinren.healthyelders.moduleMine.bean.DZVideoInfo;
import com.xaqinren.healthyelders.moduleMine.viewModel.UserXHViewModel;
import com.xaqinren.healthyelders.modulePicture.activity.TextPhotoDetailActivity;
import com.xaqinren.healthyelders.widget.SpeacesItemDecoration;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseFragment;

/**
 * Created by Lee. on 2021/5/24.
 * 用户-喜欢
 */
public class UserXHFragment extends BaseFragment<FragmentUserXhBinding, UserXHViewModel> {
    private int page = 1;
    private int pageSize = 10;
    private DZVideoAdapter videoAdapter;
    private BaseLoadMoreModule mLoadMore;
    private String userId;

    public UserXHFragment(String userId) {
        this.userId = userId;
    }


    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_user_xh;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();

        videoAdapter = new DZVideoAdapter(R.layout.item_mine_dz_video);

        mLoadMore = videoAdapter.getLoadMoreModule();//创建适配器.上拉加载
        mLoadMore.setEnableLoadMore(true);//打开上拉加载
        mLoadMore.setAutoLoadMore(true);//自动加载
        mLoadMore.setPreLoadNumber(1);//设置滑动到倒数第几个条目时自动加载，默认为1
        mLoadMore.setEnableLoadMoreIfNotFullPage(true);//当数据不满一页时继续自动加载
        mLoadMore.setOnLoadMoreListener(new OnLoadMoreListener() {//设置加载更多监听事件
            @Override
            public void onLoadMore() {
                page++;
                viewModel.getMyLikeVideoList(page, pageSize, userId);
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

        videoAdapter.setOnItemClickListener(((adapter, view, position) -> {
            if (videoAdapter.getData().get(position).homeComprehensiveHall.isArticle()) {
                Intent intent = new Intent(getContext(), TextPhotoDetailActivity.class);
                intent.putExtra(com.xaqinren.healthyelders.moduleLiteav.Constant.VIDEO_ID, videoAdapter.getData().get(position).homeComprehensiveHall.resourceId);
                intent.putExtra("userId", userId);
                startActivity(intent);
                return;
            }
            if (videoAdapter.getData().get(position).homeComprehensiveHall == null) {
                return;
            }

            Bundle bundle = new Bundle();
            VideoListBean listBean = new VideoListBean();

            List<VideoInfo> temp = new ArrayList<>();

            for (DZVideoInfo dzVideoInfo : videoAdapter.getData()) {
                if (dzVideoInfo.homeComprehensiveHall != null && !dzVideoInfo.homeComprehensiveHall.isArticle()) {
                    temp.add(dzVideoInfo.homeComprehensiveHall);
                }
            }

            listBean.videoInfos = temp;
            listBean.position = position;

            //里面每页3条数据 重新计算
            if (videoAdapter.getData().size() % Constant.loadVideoSize == 0) {
                listBean.page = (videoAdapter.getData().size() / Constant.loadVideoSize);
            } else {
                listBean.page = (videoAdapter.getData().size() / Constant.loadVideoSize) + 1;
            }
            listBean.openType = 7;//别人的点赞

            bundle.putSerializable("key", listBean);
            bundle.putString("userId", userId);
            startActivity(VideoListActivity.class, bundle);

        }));
    }

    public void toRefresh() {
        page = 1;
        viewModel.getMyLikeVideoList(page, pageSize, userId);
    }

    public void getVideoList() {
        if (videoAdapter.getData().size() == 0) {
            toRefresh();
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
                    if (dataList.size() == 0) {
                        videoAdapter.setEmptyView(R.layout.item_empty);
                    }
                    //为了防止刷新时候图片闪烁统一用notifyItemRangeInserted刷新
                    videoAdapter.setList(dataList);
                } else {
                    if (dataList.size() == 0) {
                        //加载更多加载结束
                        mLoadMore.loadMoreEnd(true);
                        page--;
                    }
                    videoAdapter.addData(dataList);
                }
            }
        });
    }
}
