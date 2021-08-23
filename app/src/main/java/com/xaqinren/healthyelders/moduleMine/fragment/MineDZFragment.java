package com.xaqinren.healthyelders.moduleMine.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.chad.library.adapter.base.module.BaseLoadMoreModule;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.databinding.FragmentMineDzBinding;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleHome.activity.VideoListActivity;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;
import com.xaqinren.healthyelders.moduleHome.bean.VideoListBean;
import com.xaqinren.healthyelders.moduleMine.adapter.DZVideoAdapter;
import com.xaqinren.healthyelders.moduleMine.bean.DZVideoInfo;
import com.xaqinren.healthyelders.moduleMine.viewModel.MineDZViewModel;
import com.xaqinren.healthyelders.modulePicture.activity.TextPhotoDetailActivity;
import com.xaqinren.healthyelders.widget.SpeacesItemDecoration;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.bus.RxBus;

/**
 * Created by Lee. on 2021/5/24.
 * 我的-点赞作品
 */
public class MineDZFragment extends BaseFragment<FragmentMineDzBinding, MineDZViewModel> {
    private int page = 1;
    private int pageSize = 10;
    private DZVideoAdapter videoAdapter;
    private BaseLoadMoreModule mLoadMore;
    private Disposable subscribe;


    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_mine_dz;
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
        mLoadMore.setPreLoadNumber(1);//设置滑动到倒数第几个条目时自动加载，默认为1
        mLoadMore.setEnableLoadMoreIfNotFullPage(true);//当数据不满一页时继续自动加载
        mLoadMore.setOnLoadMoreListener(new OnLoadMoreListener() {//设置加载更多监听事件
            @Override
            public void onLoadMore() {
                page++;
                viewModel.getMyLikeVideoList(page, pageSize);
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
            if (videoAdapter.getData().get(position).homeComprehensiveHall == null) {
                return;
            }

            if (videoAdapter.getData().get(position).homeComprehensiveHall.isArticle()) {
                Intent intent = new Intent(getContext(), TextPhotoDetailActivity.class);
                intent.putExtra(com.xaqinren.healthyelders.moduleLiteav.Constant.VIDEO_ID, videoAdapter.getData().get(position).homeComprehensiveHall.resourceId);
                startActivity(intent);
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

            //重新计算点击的位置
            int newPos = 0;
            for (int i = 0; i < listBean.videoInfos.size(); i++) {
                if (listBean.videoInfos.get(i).resourceId.equals(videoAdapter.getData().get(position).homeComprehensiveHall.resourceId)) {
                    newPos = i;
                    break;
                }
            }

            listBean.position = newPos;


            //里面每页10条数据 重新计算
            if (videoAdapter.getData().size() % 10 == 0) {
                listBean.page = (videoAdapter.getData().size() / 10);
            } else {
                listBean.page = (videoAdapter.getData().size() / 10) + 1;
            }
            listBean.openType = 5;

            bundle.putSerializable("key", listBean);
            bundle.putBoolean(Constant.MINE_OPEN, true);
            bundle.putInt("openType", 3);
            bundle.putBoolean("isSingle", true);
            Intent intent = new Intent();
            intent.putExtras(bundle);
            intent.setClass(getActivity(), VideoListActivity.class);
            startActivity(intent);

        }));
    }


    public void toRefresh() {
        if (mLoadMore != null) {
            mLoadMore.loadMoreComplete();
        }
        page = 1;
        viewModel.getMyLikeVideoList(page, pageSize);
    }

    public void getVideoList() {
        toRefresh();
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.mVideoList.observe(this, dataList -> {
            if (dataList != null) {
                if (dataList.size() > 0) {
                    //加载更多加载完成
                    mLoadMore.loadMoreComplete();
                    mLoadMore.setAutoLoadMore(true);//自动加载
                }
                if (page == 1) {
                    //为了防止刷新时候图片闪烁统一用notifyItemRangeInserted刷新
                    videoAdapter.setList(dataList);
                    if (dataList.size() == 0) {
                        //创建适配器.空布局，没有数据时候默认展示的
                        videoAdapter.setEmptyView(R.layout.item_empty);
                    }
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


        subscribe = RxBus.getDefault().toObservable(EventBean.class).subscribe(event -> {
            if (event.msgId == CodeTable.VIDEO_PL) {
                //找出对应视频，评论数加1
                for (DZVideoInfo datum : videoAdapter.getData()) {
                    if (datum.homeComprehensiveHall.resourceId.equals(event.content)) {
                        try {
                            if (TextUtils.isEmpty(datum.homeComprehensiveHall.commentCount)) {
                                datum.homeComprehensiveHall.commentCount = "1";
                            } else {
                                datum.homeComprehensiveHall.commentCount = String.valueOf(Integer.parseInt(datum.homeComprehensiveHall.commentCount) + 1);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else if (event.msgId == CodeTable.VIDEO_DZ) {
                //找出对应视频，点赞数加1
                int temp = -1;
                for (int i = 0; i < videoAdapter.getData().size(); i++) {
                    VideoInfo datum = videoAdapter.getData().get(i).homeComprehensiveHall;
                    if (datum.resourceId.equals(event.content)) {
                        temp = i;
                        try {
                            if (event.msgType == 1) {
                                datum.hasFavorite = true;
                                datum.favoriteCount = String.valueOf(datum.getFavoriteCount() + 1);
                            } else {
                                datum.hasFavorite = false;
                                datum.favoriteCount = String.valueOf(datum.getFavoriteCount() - 1);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (temp != -1) {
                    videoAdapter.notifyItemChanged(temp, 99);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (subscribe != null) {
            subscribe.dispose();
        }
    }
}
