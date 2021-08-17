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
import com.xaqinren.healthyelders.databinding.FragmentMineSmBinding;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleHome.activity.VideoListActivity;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;
import com.xaqinren.healthyelders.moduleHome.bean.VideoListBean;
import com.xaqinren.healthyelders.moduleMine.adapter.SMVideoAdapter;
import com.xaqinren.healthyelders.moduleMine.viewModel.MineSMViewModel;
import com.xaqinren.healthyelders.modulePicture.activity.TextPhotoDetailActivity;
import com.xaqinren.healthyelders.widget.SpeacesItemDecoration;

import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.bus.RxBus;

/**
 * Created by Lee. on 2021/5/24.
 * 我的-私密作品
 */
public class MineSMFragment extends BaseFragment<FragmentMineSmBinding, MineSMViewModel> {
    private int page = 1;
    private int pageSize = 10;
    private SMVideoAdapter videoAdapter;
    private BaseLoadMoreModule mLoadMore;
    private Disposable subscribe;


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

        videoAdapter.setOnItemClickListener(((adapter, view, position) -> {

            if (videoAdapter.getData().get(position).isArticle()) {
                Intent intent = new Intent(getContext(), TextPhotoDetailActivity.class);
                intent.putExtra(com.xaqinren.healthyelders.moduleLiteav.Constant.VIDEO_ID, videoAdapter.getData().get(position).resourceId);
                intent.putExtra(Constant.MINE_OPEN, true);
                startActivity(intent);
                return;
            }
            Bundle bundle = new Bundle();
            VideoListBean listBean = new VideoListBean();

            listBean.videoInfos = videoAdapter.getData();
            listBean.position = position;

            //里面每页3条数据 重新计算
            if (videoAdapter.getData().size() % Constant.loadVideoSize == 0) {
                listBean.page = (videoAdapter.getData().size() / Constant.loadVideoSize);
            } else {
                listBean.page = (videoAdapter.getData().size() / Constant.loadVideoSize) + 1;
            }
            listBean.openType = 4;

            bundle.putSerializable("key", listBean);
            bundle.putBoolean(Constant.MINE_OPEN, true);
            bundle.putInt("openType", 2);
            bundle.putBoolean("key1", true);
            Intent intent = new Intent();
            intent.setClass(getActivity(), VideoListActivity.class);
            intent.putExtras(bundle);

            this.startActivityForResult(intent, 10087);

        }));
    }

    public void toRefresh() {
        page = 1;
        viewModel.getMyVideoList(page, pageSize);
    }

    public void getVideoList() {
        toRefresh();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (subscribe != null) {
            subscribe.dispose();
        }
    }

    private int resCode;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10087 && resCode == 99) {
            toRefresh();
            resCode = 0;
        }
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        subscribe = RxBus.getDefault().toObservable(EventBean.class).subscribe(event -> {
            if (event.msgId == CodeTable.CODE_SUCCESS && event.content.equals("mine-sm")) {
                resCode = 99;
            }else if (event.msgId == CodeTable.VIDEO_PL) {
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
    }
}
