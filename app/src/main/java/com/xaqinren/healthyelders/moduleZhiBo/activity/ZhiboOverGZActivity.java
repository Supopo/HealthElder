package com.xaqinren.healthyelders.moduleZhiBo.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.chad.library.adapter.base.module.BaseLoadMoreModule;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivityZhiboOverGzBinding;
import com.xaqinren.healthyelders.databinding.HeaderZhiboOverGzBinding;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleHome.adapter.GridVideoAdapter;
import com.xaqinren.healthyelders.moduleZhiBo.adapter.SomeVideoListAdapter;
import com.xaqinren.healthyelders.moduleZhiBo.viewModel.ZhiboOverGZViewModel;
import com.xaqinren.healthyelders.utils.MScreenUtil;
import com.xaqinren.healthyelders.widget.SpeacesItemDecoration;

import me.goldze.mvvmhabit.base.BaseActivity;

/**
 * Created by Lee. on 2021/4/30.
 * 主结束直播用户展示页面
 */
public class ZhiboOverGZActivity extends BaseActivity<ActivityZhiboOverGzBinding, ZhiboOverGZViewModel> {
    private String liveRoomRecordId;
    private SomeVideoListAdapter mAdapter;
    private BaseLoadMoreModule mLoadMore;
    private HeaderZhiboOverGzBinding headBinding;


    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_zhibo_over_gz;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initParam() {
        super.initParam();
        Bundle extras = getIntent().getExtras();
        liveRoomRecordId = (String) extras.get("liveRoomRecordId");
    }

    private int page = 1;

    @Override
    public void initData() {
        super.initData();
        setStatusBarTransparent();
        viewModel.getLiveOverInfo(liveRoomRecordId);
        viewModel.getMoreLives(page);

        mAdapter = new SomeVideoListAdapter(R.layout.item_some_video);
        binding.rvContent.setLayoutManager(new GridLayoutManager(this, 2));
        binding.rvContent.setAdapter(mAdapter);
        binding.rvContent.addItemDecoration(new SpeacesItemDecoration(this, 5, false, true));

        mLoadMore = mAdapter.getLoadMoreModule();//创建适配器.上拉加载
        mLoadMore.setEnableLoadMore(true);//打开上拉加载
        mLoadMore.setAutoLoadMore(true);//自动加载
        mLoadMore.setPreLoadNumber(1);//设置滑动到倒数第几个条目时自动加载，默认为1
        mLoadMore.setEnableLoadMoreIfNotFullPage(true);//当数据不满一页时继续自动加载
        mLoadMore.setOnLoadMoreListener(new OnLoadMoreListener() {//设置加载更多监听事件
            @Override
            public void onLoadMore() {
                page++;
                viewModel.getMoreLives(page);
            }
        });
        mAdapter.setOnItemClickListener(((adapter, view, position) -> {
            //判断是直播间
            if (mAdapter.getData().get(position).getVideoType() == 2) {
                //进入直播间
                viewModel.joinLive(mAdapter.getData().get(position).liveRoomId);
            }
        }));


        View headView = getLayoutInflater().inflate(R.layout.header_zhibo_over_gz, null);
        headBinding = DataBindingUtil.bind(headView);

        mAdapter.addHeaderView(headView);

        headBinding.ivClose.setOnClickListener(lis -> {
            finish();
        });

        binding.srlContent.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                binding.srlContent.setRefreshing(false);
                page = 1;
                viewModel.getMoreLives(page);
            }
        });
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();

        viewModel.liveOverInfo.observe(this, liveOverInfo -> {
            if (liveOverInfo != null) {
                headBinding.setViewModel(liveOverInfo);
            }
        });

        viewModel.dismissDialog.observe(this, dis -> {
            dismissDialog();
        });

        viewModel.videoList.observe(this, dataList -> {

            if (dataList != null) {
                if (dataList.size() > 0) {
                    //加载更多加载完成
                    mLoadMore.loadMoreComplete();
                }
                if (page == 1) {
                    //为了防止刷新时候图片闪烁统一用notifyItemRangeInserted刷新
                    mAdapter.setList(dataList);
                } else {
                    if (dataList.size() == 0) {
                        //加载更多加载结束
                        mLoadMore.loadMoreEnd(true);
                    }
                    mAdapter.addData(dataList);
                }
            }
        });
        viewModel.liveInfo.observe(this, liveInfo -> {
            if (liveInfo != null) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constant.LiveInitInfo, liveInfo);
                startActivity(LiveGuanzhongActivity.class, bundle);
            }
        });
    }
}
