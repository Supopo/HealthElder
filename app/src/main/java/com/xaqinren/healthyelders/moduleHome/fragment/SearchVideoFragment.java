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
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.moduleHome.activity.VideoListActivity;
import com.xaqinren.healthyelders.moduleHome.adapter.GridVideoAdapter;
import com.xaqinren.healthyelders.moduleHome.adapter.SearchVideoAdapter;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;
import com.xaqinren.healthyelders.moduleHome.bean.VideoListBean;
import com.xaqinren.healthyelders.moduleHome.viewModel.SearchAllViewModel;
import com.xaqinren.healthyelders.widget.SpeacesItemDecoration;
import com.xaqinren.healthyelders.widget.SpeacesItemDecorationEx;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.bus.RxBus;

/**
 * Created by Lee. on 2021/5/28.
 * 搜索 - 视频列表
 */
public class SearchVideoFragment extends BaseFragment<FragmentSearchVideoBinding, SearchAllViewModel> {

    private GridVideoAdapter mAdapter;
    public int page = 1;
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
        ((BaseActivity) getActivity()).showDialog();
        initAdapter();

        //瀑布流
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        //防止Item切换
        manager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        binding.rvContent.setLayoutManager(manager);
        binding.rvContent.addItemDecoration(new SpeacesItemDecorationEx(getActivity(), getResources().getDimension(R.dimen.dp_7), 0, 0, getResources().getDimension(R.dimen.dp_7), true));

        //防止刷新跳动
        binding.rvContent.setItemAnimator(null);

        binding.srlContent.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mLoadMore.setEnableLoadMore(false);
                page = 1;
                searchAllViewModel.searchDatas(page, 1);
            }
        });


    }

    private void initAdapter() {
        mAdapter = new GridVideoAdapter(R.layout.item_grid_video);
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
                searchAllViewModel.searchDatas(page, 1);
            }
        });

        mAdapter.setOnItemClickListener(((adapter, view, position) -> {
            List<VideoInfo> tempList = new ArrayList<>();
            VideoInfo videoInfo = mAdapter.getData().get(position);
            tempList.add(videoInfo);
            toVideoList(tempList);
        }));

        mAdapter.setOnItemChildClickListener(((adapter, view, position) -> {
            if (view.getId() == R.id.ll_like) {
                //点赞
                searchAllViewModel.toLike(1, mAdapter.getData().get(position).resourceId, !mAdapter.getData().get(position).hasFavorite, position);
            }
        }));

    }

    private void toVideoList(List<VideoInfo> tempList) {
        //跳页 传入数据 pos page list
        VideoListBean listBean = new VideoListBean();

        listBean.page = 0;
        listBean.position = 0;
        listBean.videoInfos = tempList;
        listBean.openType = 2;

        Bundle bundle = new Bundle();
        bundle.putSerializable("key", listBean);
        bundle.putBoolean("isSingle", true);
        startActivity(VideoListActivity.class, bundle);
    }


    @Override
    public void initViewObservable() {
        super.initViewObservable();

        searchAllViewModel.dismissDialog.observe(this, dismissDialog -> {
            if (dismissDialog != null) {
                dismissDialog();
            }
        });
        searchAllViewModel.dzSuccess.observe(this, dzSuccess -> {
            if (dzSuccess != null && dzSuccess.type == 1 && dzSuccess.isSuccess) {
                mAdapter.getData().get(dzSuccess.position).hasFavorite = !mAdapter.getData().get(dzSuccess.position).hasFavorite;
                if (mAdapter.getData().get(dzSuccess.position).hasFavorite) {
                    mAdapter.getData().get(dzSuccess.position).favoriteCount = String.valueOf(mAdapter.getData().get(dzSuccess.position).getFavoriteCount() + 1);
                } else {
                    mAdapter.getData().get(dzSuccess.position).favoriteCount = String.valueOf(mAdapter.getData().get(dzSuccess.position).getFavoriteCount() - 1);
                }
                mAdapter.notifyItemChanged(dzSuccess.position, 99);
            }
        });

        searchAllViewModel.videoDatas.observe(this, dataList -> {
            binding.srlContent.setRefreshing(false);
            if (dataList != null) {
                if (dataList.size() > 0) {
                    //加载更多加载完成
                    mLoadMore.loadMoreComplete();
                }
                if (page == 1) {
                    initAdapter();
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
        subscribe = RxBus.getDefault().toObservable(EventBean.class).subscribe(eventBean -> {
            if (eventBean != null) {
                if (eventBean.msgType == CodeTable.SEARCH_TAG) {
                    page = 1;
                } else if (eventBean.msgId == CodeTable.VIDEO_DZ) {
                    //找出adapter中对应pos
                    int temp = -1;
                    for (int i = 0; i < mAdapter.getData().size(); i++) {
                        if (mAdapter.getData().get(i).resourceId.equals(eventBean.content)) {
                            temp = i;
                        }
                    }
                    if (temp != -1) {
                        //局部刷新
                        int favoriteCount = mAdapter.getData().get(temp).getFavoriteCount();

                        if (eventBean.msgType == 1) {
                            favoriteCount++;
                        } else {
                            favoriteCount--;
                        }
                        mAdapter.getData().get(temp).favoriteCount = String.valueOf(favoriteCount);

                        mAdapter.getData().get(temp).hasFavorite = eventBean.msgType == 1 ? true : false;
                        mAdapter.notifyItemChanged(temp, 99);
                    }
                }
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
