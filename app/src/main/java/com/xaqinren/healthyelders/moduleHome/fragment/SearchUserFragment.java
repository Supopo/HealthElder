package com.xaqinren.healthyelders.moduleHome.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.chad.library.adapter.base.module.BaseLoadMoreModule;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.databinding.FragmentSearchYhBinding;
import com.xaqinren.healthyelders.databinding.FragmentSearchZbBinding;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.global.InfoCache;
import com.xaqinren.healthyelders.moduleHome.adapter.SearchUserAdapter;
import com.xaqinren.healthyelders.moduleHome.adapter.SearchZhiboAdapter;
import com.xaqinren.healthyelders.moduleHome.viewModel.SearchAllViewModel;
import com.xaqinren.healthyelders.moduleHome.viewModel.SearchUserViewModel;
import com.xaqinren.healthyelders.moduleLogin.activity.SelectLoginActivity;

import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.bus.RxBus;

/**
 * Created by Lee. on 2021/5/28.
 * 搜索 - 用户列表
 */
public class SearchUserFragment extends BaseFragment<FragmentSearchYhBinding, SearchUserViewModel> {

    private SearchUserAdapter mAdapter;
    private int page = 1;
    private BaseLoadMoreModule mLoadMore;
    private SearchAllViewModel searchAllViewModel;
    private Disposable subscribe;
    private int mPosition;


    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_search_yh;
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

        mAdapter = new SearchUserAdapter(R.layout.item_search_user);

        binding.rvContent.setLayoutManager(new LinearLayoutManager(getActivity()));
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
                searchAllViewModel.searchDatas(page, 2);
            }
        });

        binding.srlContent.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mLoadMore.setEnableLoadMore(false);
                binding.srlContent.setRefreshing(false);
                page = 1;
                searchAllViewModel.searchDatas(page, 2);
            }
        });

        mAdapter.setOnItemChildClickListener(((adapter, view, position) -> {
            //关注
            if (!InfoCache.getInstance().checkLogin()) {
                startActivity(SelectLoginActivity.class);
                return;
            }
            mPosition = position;
            viewModel.toFollow(mAdapter.getData().get(position).userId);
        }));
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();

        viewModel.followSuccess.observe(this, isSuccess -> {
            if (isSuccess != null && isSuccess) {
                mAdapter.getData().get(mPosition).hasAttention = !mAdapter.getData().get(mPosition).hasAttention;
                mAdapter.notifyItemChanged(mPosition, 99);
            }
        });

        searchAllViewModel.userDatas.observe(this, dataList -> {
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
                        page--;
                    }
                    mAdapter.addData(dataList);
                }
            }
        });
        subscribe = RxBus.getDefault().toObservable(EventBean.class).subscribe(event -> {
            if (event != null && event.msgType == CodeTable.SEARCH_TAG) {
                page = 1;
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
