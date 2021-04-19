package com.xaqinren.healthyelders.moduleHome.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.chad.library.adapter.base.module.BaseLoadMoreModule;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.moduleHome.activity.WebActivity;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.databinding.FragmentGirlsBinding;
import com.xaqinren.healthyelders.moduleHome.adapter.GirlsAdapter;
import com.xaqinren.healthyelders.moduleHome.viewModel.GirlsViewModel;
import com.xaqinren.healthyelders.widget.SpeacesItemDecoration;

import me.goldze.mvvmhabit.base.BaseFragment;

//注意ActivityBaseBinding换成自己fragment_layout对应的名字 FragmentXxxBinding
public class GirlsFragment extends BaseFragment<FragmentGirlsBinding, GirlsViewModel> {

    private GirlsAdapter mAdapter;
    private int pageNum = 1;
    private BaseLoadMoreModule mLoadMore;

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_girls;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    //页面数据初始化方法
    @Override
    public void initData() {
        initAdapter();
    }


    //页面事件监听的方法，一般用于ViewModel层转到View层的事件注册
    @Override
    public void initViewObservable() {
        //下拉刷新
        binding.srlContent.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mLoadMore.setEnableLoadMore(false);
                pageNum = 1;
                viewModel.getDataList(pageNum);
                //刷新完立即关闭刷新效果是因为本身请求就带有Dialog
                //如果此处不关闭的话应该在网络请求结束后关闭
                binding.srlContent.setRefreshing(false);
            }
        });

        //往adapter里面加载数据
        viewModel.dataList.observe(this, dataList -> {
            if (dataList != null) {
                if (dataList.size() > 0) {
                    //加载更多加载完成
                    mLoadMore.loadMoreComplete();
                }
                if (pageNum == 1) {
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

    private void initAdapter() {
        mAdapter = new GirlsAdapter(R.layout.item_girls);
        mLoadMore = mAdapter.getLoadMoreModule();//创建适配器.上拉加载
        mLoadMore.setEnableLoadMore(true);//打开上拉加载
        mLoadMore.setAutoLoadMore(true);//自动加载
        mLoadMore.setPreLoadNumber(1);//设置滑动到倒数第几个条目时自动加载，默认为1
        mLoadMore.setEnableLoadMoreIfNotFullPage(true);//当数据不满一页时继续自动加载
        //mLoadMore.setLoadMoreView(new BaseLoadMoreView)//设置自定义加载布局
        mLoadMore.setOnLoadMoreListener(new OnLoadMoreListener() {//设置加载更多监听事件
            @Override
            public void onLoadMore() {
                pageNum++;
                viewModel.getDataList(pageNum);
            }
        });

        //        binding.rvContent.setLayoutManager(new LinearLayoutManager(getActivity()));

        //瀑布流
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        //防止Item切换
        manager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        //设置间距
        binding.rvContent.addItemDecoration(new SpeacesItemDecoration(getActivity(), 10));
        binding.rvContent.setLayoutManager(manager);
        binding.rvContent.setAdapter(mAdapter);
        //防止刷新跳动
        binding.rvContent.setItemAnimator(null);
        viewModel.getDataList(pageNum);

        //Item点击事件
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            Intent intent = new Intent();
            intent.putExtra(Constant.PageTitle, mAdapter.getData().get(position).getTitle());
            intent.putExtra(Constant.PageUrl, mAdapter.getData().get(position).getUrl());
            intent.setClass(getActivity(), WebActivity.class);
            startActivity(intent);
        });


        //	子View点击事件 当前Item点击失效是因为设置 ll_item
        //        mAdapter.addChildClickViewIds(R.id.ll_item);
        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {

        });

        binding.rvContent.setOnScrollListener(new RecyclerView.OnScrollListener() {
            int mRvScrollY = 0; // 列表滑动距离

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mRvScrollY += dy;

                //向上滑动时候
                if (dy < 0) {
                    binding.tvTop.setVisibility(View.VISIBLE);
                } else {
                    binding.tvTop.setVisibility(View.INVISIBLE);
                }
            }
        });

        binding.tvTop.setOnClickListener(lis -> {
            //            binding.rvContent.scrollToPosition(0);
            //带滚动动画
            binding.rvContent.smoothScrollToPosition(0);
        });
    }
}
