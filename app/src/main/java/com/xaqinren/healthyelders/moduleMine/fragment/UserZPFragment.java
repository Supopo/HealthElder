package com.xaqinren.healthyelders.moduleMine.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.chad.library.adapter.base.module.BaseLoadMoreModule;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.apiserver.LiteAvRepository;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.databinding.FragmentMineZpBinding;
import com.xaqinren.healthyelders.databinding.FragmentUserZpBinding;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleHome.activity.DraftActivity;
import com.xaqinren.healthyelders.moduleHome.activity.VideoListActivity;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;
import com.xaqinren.healthyelders.moduleHome.bean.VideoListBean;
import com.xaqinren.healthyelders.moduleHome.viewModel.SearchAllViewModel;
import com.xaqinren.healthyelders.moduleLiteav.bean.SaveDraftBean;
import com.xaqinren.healthyelders.moduleMine.adapter.ZPVideoAdapter;
import com.xaqinren.healthyelders.moduleMine.viewModel.MineZPViewModel;
import com.xaqinren.healthyelders.moduleMine.viewModel.UserInfoViewModel;
import com.xaqinren.healthyelders.moduleMine.viewModel.UserZPViewModel;
import com.xaqinren.healthyelders.widget.SpeacesItemDecoration;

import java.util.List;

import me.goldze.mvvmhabit.base.BaseFragment;

/**
 * Created by Lee. on 2021/5/24.
 * 用户-作品
 */
public class UserZPFragment extends BaseFragment<FragmentUserZpBinding, UserZPViewModel> {
    private int page = 1;
    private int pageSize = 10;
    private ZPVideoAdapter videoAdapter;
    private BaseLoadMoreModule mLoadMore;
    private String userId;

    public UserZPFragment(String userId) {
        this.userId = userId;
    }

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_user_zp;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();

        videoAdapter = new ZPVideoAdapter(R.layout.item_mine_zp_video);

        mLoadMore = videoAdapter.getLoadMoreModule();//创建适配器.上拉加载
        mLoadMore.setEnableLoadMore(true);//打开上拉加载
        mLoadMore.setAutoLoadMore(true);//自动加载
        mLoadMore.setPreLoadNumber(1);//设置滑动到倒数第几个条目时自动加载，默认为1
        mLoadMore.setEnableLoadMoreIfNotFullPage(true);//当数据不满一页时继续自动加载
        mLoadMore.setOnLoadMoreListener(new OnLoadMoreListener() {//设置加载更多监听事件
            @Override
            public void onLoadMore() {
                page++;
                viewModel.getMyVideoList(page, pageSize, userId);
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

        getVideoList();

        videoAdapter.setOnItemClickListener(((adapter, view, position) -> {

            Bundle bundle = new Bundle();
            VideoListBean listBean = new VideoListBean();

            listBean.videoInfos = videoAdapter.getData();

            listBean.position = position;

            //里面每页3条数据 重新计算
            if (listBean.videoInfos.size() % Constant.loadVideoSize == 0) {
                listBean.page = (listBean.videoInfos.size() / 2);
            } else {
                listBean.page = (listBean.videoInfos.size() / 2) + 1;
            }
            listBean.type = 3;

            bundle.putSerializable("key", listBean);
            startActivity(VideoListActivity.class, bundle);

        }));
    }

    public void toRefresh() {
        page = 1;
        viewModel.getMyVideoList(page, pageSize, userId);
    }

    public void getVideoList() {
        if (videoAdapter.getData().size() == 0) {
            page = 1;
            viewModel.getMyVideoList(page, pageSize, userId);
        }
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.dismissDialog.observe(this, dis -> {
            if (dis != null) {
                UserInfoViewModel userInfoViewModel = ViewModelProviders.of(getActivity()).get(UserInfoViewModel.class);
                userInfoViewModel.dismissDialog.postValue(true);
            }
        });

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
                    }
                    videoAdapter.addData(dataList);
                }
            }
        });
    }
}
