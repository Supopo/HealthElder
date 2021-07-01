package com.xaqinren.healthyelders.moduleHome.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.chad.library.adapter.base.module.BaseLoadMoreModule;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.databinding.FragmentAllSearchBinding;
import com.xaqinren.healthyelders.databinding.HeaderAllSearchBinding;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.moduleHome.activity.VideoListActivity;
import com.xaqinren.healthyelders.moduleHome.adapter.AllSearchAdapter;
import com.xaqinren.healthyelders.moduleHome.adapter.SearchUserAdapter;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;
import com.xaqinren.healthyelders.moduleHome.bean.VideoListBean;
import com.xaqinren.healthyelders.moduleHome.viewModel.SearchAllViewModel;
import com.xaqinren.healthyelders.modulePicture.activity.TextPhotoDetailActivity;
import com.xaqinren.healthyelders.uniApp.UniService;
import com.xaqinren.healthyelders.uniApp.UniUtil;
import com.xaqinren.healthyelders.uniApp.bean.UniEventBean;
import com.xaqinren.healthyelders.widget.SpeacesItemDecoration;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.utils.ToastUtils;

/**
 * Created by Lee. on 2021/5/27.
 */
public class SearchAllFragment extends BaseFragment<FragmentAllSearchBinding, BaseViewModel> {

    private AllSearchAdapter mAdapter;
    private BaseLoadMoreModule mLoadMore;
    public int page = 1;
    private SearchAllViewModel searchAllViewModel;
    private Disposable subscribe;
    private SearchUserAdapter userAdapter;
    private HeaderAllSearchBinding headBinding;
    private Disposable uniSubscribe;

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_all_search;
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
        mAdapter = new AllSearchAdapter();

        binding.rvContent.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvContent.setAdapter(mAdapter);
        binding.rvContent.addItemDecoration(new SpeacesItemDecoration(getActivity(), 1, 3, 0));
        showDialog();
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
                searchAllViewModel.searchDatas(page, 0);
            }
        });

        binding.srlContent.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                userRes = false;
                contentRes = false;
                mLoadMore.setEnableLoadMore(false);
                page = 1;
                searchAllViewModel.searchUsers(page, 3);
                searchAllViewModel.searchDatas(page, 0);
            }
        });

        mAdapter.setOnItemClickListener(((adapter, view, position) -> {
            //视频
            if (mAdapter.getData().get(position).getItemType() == 0) {
                List<VideoInfo> tempList = new ArrayList<>();
                VideoInfo videoInfo = mAdapter.getData().get(position);
                tempList.add(videoInfo);
                toVideoList(tempList);
            } else if (mAdapter.getData().get(position).getItemType() == 4) {
                //跳转图文详情
                Intent intent = new Intent(getContext(), TextPhotoDetailActivity.class);
                intent.putExtra(com.xaqinren.healthyelders.moduleLiteav.Constant.VIDEO_ID, mAdapter.getData().get(position).resourceId);
                startActivity(intent);
            } else if (mAdapter.getData().get(position).getItemType() == 3) {
                //进入直播
                searchAllViewModel.joinLive(mAdapter.getData().get(position).liveRoomId);
            } else if (mAdapter.getData().get(position).getItemType() == 2) {
                //进入商品
                VideoInfo info = mAdapter.getData().get(position);
                UniService.startService(getContext(), info.appId, 0x20056, info.jumpUrl);
            }
        }));

        mAdapter.setOnItemChildClickListener(((adapter, view, position) -> {
            if (view.getId() == R.id.iv_zan) {
                //视频点赞
                searchAllViewModel.toLike(0, mAdapter.getData().get(position).resourceId, !mAdapter.getData().get(position).hasFavorite, position);
            }
        }));

        initHead();
        ((BaseActivity) getActivity()).showDialog();
    }

    private boolean hasHead;

    private void initHead() {
        if (hasHead) {
            return;
        }

        View headView = LinearLayout.inflate(getActivity(), R.layout.header_all_search, null);
        headBinding = DataBindingUtil.bind(headView);
        userAdapter = new SearchUserAdapter(R.layout.item_search_user);
        headBinding.rvUser.setLayoutManager(new LinearLayoutManager(getActivity()));
        headBinding.rvUser.setAdapter(userAdapter);

        headBinding.tvMore.setOnClickListener(lis -> {
            //通知页面切换到
            searchAllViewModel.toUsers.postValue(true);
        });

        mAdapter.addHeaderView(headView);
        hasHead = true;

        userAdapter.setOnItemChildClickListener(((adapter, view, position) -> {
            followPosition = position;
            searchAllViewModel.toFollow(userAdapter.getData().get(position).id);
        }));
    }

    private int followPosition;

    private void toVideoList(List<VideoInfo> tempList) {
        //跳页 传入数据 pos page list
        VideoListBean listBean = new VideoListBean();

        listBean.page = 0;
        listBean.position = 0;
        listBean.videoInfos = tempList;
        listBean.openType = 2;

        Bundle bundle = new Bundle();
        bundle.putSerializable("key", listBean);
        bundle.putBoolean("key1", true);
        startActivity(VideoListActivity.class, bundle);
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        searchAllViewModel.dzSuccess.observe(this, dzSuccess -> {
            if (dzSuccess != null && dzSuccess.type == 0 && dzSuccess.isSuccess) {
                mAdapter.getData().get(dzSuccess.position).hasFavorite = !mAdapter.getData().get(dzSuccess.position).hasFavorite;
                if (mAdapter.getData().get(dzSuccess.position).hasFavorite) {
                    mAdapter.getData().get(dzSuccess.position).favoriteCount = String.valueOf(mAdapter.getData().get(dzSuccess.position).getFavoriteCount() + 1);
                } else {
                    mAdapter.getData().get(dzSuccess.position).favoriteCount = String.valueOf(mAdapter.getData().get(dzSuccess.position).getFavoriteCount() - 1);
                }
                //加1是因为设置了头布局
                mAdapter.notifyItemChanged(dzSuccess.position + 1, 99);
            }

        });
        uniSubscribe = RxBus.getDefault().toObservable(UniEventBean.class).subscribe(event -> {
            if (event != null) {
                if (event.msgId == CodeTable.UNI_RELEASE) {
                    if (event.taskId == 0x20056) {
                        UniUtil.openUniApp(getContext(), event.appId, event.jumpUrl, null, event.isSelfUni);
                    }
                } else if (event.msgId == CodeTable.UNI_RELEASE_FAIL) {
                    ToastUtils.showShort("打开小程序失败");
                }
            }
        });
        searchAllViewModel.dismissDialog.observe(this, disDialog -> {
            dismissDialog();
        });

        searchAllViewModel.followSuccess.observe(this, dismissDialog -> {
            userAdapter.notifyItemChanged(followPosition, 99);
        });


        searchAllViewModel.userDatas.observe(this, dataList -> {
            if (dataList != null) {
                userAdapter.setNewInstance(dataList);
                userRes = true;
                if (dataList.size() > 0) {
                    headBinding.llHead.setVisibility(View.VISIBLE);
                } else {
                    headBinding.llHead.setVisibility(View.GONE);
                    showNodata();
                }

            }
        });

        searchAllViewModel.allDatas.observe(this, dataList -> {
            binding.srlContent.setRefreshing(false);
            if (dataList != null) {
                contentRes = true;
                dismissDialog();
                if (dataList.size() > 0) {
                    //加载更多加载完成
                    mLoadMore.loadMoreComplete();
                    if (page == 1) {
                        mAdapter.setNewInstance(dataList);
                    } else {
                        mAdapter.addData(dataList);
                    }
                } else {

                    if (page == 1) {
                        showNodata();
                    }

                    mLoadMore.loadMoreEnd(true);
                    page--;
                }

            }
        });

        subscribe = RxBus.getDefault().toObservable(EventBean.class).subscribe(event -> {
            if (event != null && event.msgType == CodeTable.SEARCH_TAG) {
                page = 1;
            }
        });
    }

    public boolean userRes;
    public boolean contentRes;

    private void showNodata() {
        if (userRes && contentRes) {
            if (headBinding.llHead.getVisibility() == View.GONE) {
                mAdapter.setEmptyView(R.layout.item_empty);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (subscribe != null) {
            subscribe.dispose();
        }
        if (uniSubscribe != null) {
            uniSubscribe.dispose();
        }
    }
}
