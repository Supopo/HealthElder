package com.xaqinren.healthyelders.moduleMine.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.chad.library.adapter.base.module.BaseLoadMoreModule;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.apiserver.LiteAvRepository;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.databinding.FragmentMineZpBinding;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleHome.activity.DraftActivity;
import com.xaqinren.healthyelders.moduleHome.activity.VideoListActivity;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;
import com.xaqinren.healthyelders.moduleHome.bean.VideoListBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.SaveDraftBean;
import com.xaqinren.healthyelders.moduleMine.adapter.ZPVideoAdapter;
import com.xaqinren.healthyelders.moduleMine.viewModel.MineZPViewModel;
import com.xaqinren.healthyelders.modulePicture.activity.TextPhotoDetailActivity;
import com.xaqinren.healthyelders.widget.SpeacesItemDecoration;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.bus.RxBus;

/**
 * Created by Lee. on 2021/5/24.
 * 我的-作品
 */
public class MineZPFragment extends BaseFragment<FragmentMineZpBinding, MineZPViewModel> {
    private int page = 1;
    private int pageSize = 10;
    private ZPVideoAdapter videoAdapter;
    private BaseLoadMoreModule mLoadMore;
    private boolean hasDraft;
    private int draftCount;
    private String draftDoverPath;
    private Disposable subscribe;

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_mine_zp;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        getDraft();

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


        getVideoList();


        videoAdapter.setOnItemClickListener(((adapter, view, position) -> {
            if (hasDraft) {
                //判断是否有草稿箱内容
                if (position == 0) {
                    startActivity(DraftActivity.class);
                    return;
                }
            }

            if (videoAdapter.getData().get(position).isArticle()) {
                Intent intent = new Intent(getContext(), TextPhotoDetailActivity.class);
                intent.putExtra(com.xaqinren.healthyelders.moduleLiteav.Constant.VIDEO_ID, videoAdapter.getData().get(position).resourceId);
                intent.putExtra(Constant.MINE_OPEN, true);
                startActivity(intent);
                return;
            }

            Bundle bundle = new Bundle();
            VideoListBean listBean = new VideoListBean();

            listBean.videoInfos = new ArrayList<>();


            //去除文章
            for (VideoInfo videoInfo : videoAdapter.getData()) {
                if (!videoInfo.isArticle()) {
                    listBean.videoInfos.add(videoInfo);
                }
            }

            if (hasDraft) {
                listBean.videoInfos.remove(0);
            }

            //重新计算点击的位置
            int newPos = 0;
            for (int i = 0; i < listBean.videoInfos.size(); i++) {
                if (listBean.videoInfos.get(i).resourceId.equals(videoAdapter.getData().get(position).resourceId)) {
                    newPos = i;
                    break;
                }
            }

            listBean.position = newPos;

            //里面每页3条数据 重新计算
            if (listBean.videoInfos.size() % 10 == 0) {
                listBean.page = (listBean.videoInfos.size() / 10);
            } else {
                listBean.page = (listBean.videoInfos.size() / 10) + 1;
            }
            listBean.openType = 3;

            bundle.putSerializable("key", listBean);
            bundle.putBoolean(Constant.MINE_OPEN, true);
            bundle.putBoolean("isSingle", true);
            bundle.putInt("openType", 1);
            Intent intent = new Intent();
            intent.setClass(getActivity(), VideoListActivity.class);
            intent.putExtras(bundle);

            this.startActivityForResult(intent, 10086);

        }));
    }

    private int resCode;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10086 && resCode == 99) {
            toRefresh();
            resCode = 0;
        }
    }

    public void toRefresh() {
        page = 1;
        getDraft();
        viewModel.getMyVideoList(page, pageSize);
    }

    private void getDraft() {
        if (UserInfoMgr.getInstance().getUserInfo() != null) {
            String fileName = UserInfoMgr.getInstance().getUserInfo().getId();
            List<SaveDraftBean> list = LiteAvRepository.getInstance().getDraftsList(getActivity(), fileName);
            hasDraft = false;
            if (list != null && list.size() > 0) {
                hasDraft = true;
                draftCount = list.size();
                draftDoverPath = list.get(0).getCoverPath();
            }
        }
    }

    public void getVideoList() {
        page = 1;
        viewModel.getMyVideoList(page, pageSize);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (subscribe != null) {
            subscribe.dispose();
        }
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        subscribe = RxBus.getDefault().toObservable(EventBean.class).subscribe(event -> {
            if (event.msgId == CodeTable.CODE_SUCCESS && event.content.equals("mine-zp")) {
                resCode = 99;
            } else if (event.msgId == CodeTable.VIDEO_PL) {
                //找出对应视频，评论数加1
                for (VideoInfo datum : videoAdapter.getData()) {
                    if (datum.resourceId.equals(event.content)) {
                        try {
                            if (TextUtils.isEmpty(datum.commentCount)) {
                                datum.commentCount = "1";
                            } else {
                                datum.commentCount = String.valueOf(Integer.parseInt(datum.commentCount) + 1);
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
                    VideoInfo datum = videoAdapter.getData().get(i);
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

        viewModel.mVideoList.observe(this, dataList -> {
            if (dataList != null) {
                if (dataList.size() > 0) {
                    //加载更多加载完成
                    mLoadMore.loadMoreComplete();
                }
                if (page == 1) {

                    if (hasDraft) {
                        VideoInfo videoInfo = new VideoInfo();
                        videoInfo.isDraft = true;
                        videoInfo.coverUrl = draftDoverPath;
                        dataList.add(0, videoInfo);
                        videoInfo.draftCount = draftCount;
                    }

                    if (dataList.size() == 0) {
                        videoAdapter.setEmptyView(R.layout.item_empty);
                    }
                    //为了防止刷新时候图片闪烁统一用notifyItemRangeInserted刷新
                    videoAdapter.setList(dataList);
                } else {
                    if (dataList.size() == 0) {
                        //加载更多加载结束
                        mLoadMore.loadMoreEnd();
                        page--;
                    }
                    videoAdapter.addData(dataList);
                }
            }
        });
    }
}
