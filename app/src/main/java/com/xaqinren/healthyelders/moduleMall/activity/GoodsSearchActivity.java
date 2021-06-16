package com.xaqinren.healthyelders.moduleMall.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.databinding.ActivityGoodsSearchBinding;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.impl.TextWatcherImpl;
import com.xaqinren.healthyelders.moduleLogin.activity.SelectLoginActivity;
import com.xaqinren.healthyelders.moduleMall.adapter.GoodsLinearAdapter;
import com.xaqinren.healthyelders.moduleMall.adapter.GoodsStaggeredAdapter;
import com.xaqinren.healthyelders.moduleMall.bean.GoodsItemBean;
import com.xaqinren.healthyelders.moduleMall.viewModel.GoodsSearchViewModel;
import com.xaqinren.healthyelders.moduleZhiBo.bean.GoodsBean;
import com.xaqinren.healthyelders.uniApp.UniService;
import com.xaqinren.healthyelders.uniApp.UniUtil;
import com.xaqinren.healthyelders.uniApp.bean.UniEventBean;
import com.xaqinren.healthyelders.widget.SpeacesItemDecoration;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;

public class GoodsSearchActivity extends BaseActivity<ActivityGoodsSearchBinding, GoodsSearchViewModel> {
    private String tags;
    private GoodsStaggeredAdapter goodsStaggeredAdapter;
    private GoodsLinearAdapter goodsLinearAdapter;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private LinearLayoutManager linearLayout;
    private int layoutType = 0;
    private List<GoodsBean> itemBeans;
    private int page = 1;
    private int pageSize = 10;
    private String sortBy = "";
    private String orderBy = "DESC";//DESC ASC
    private Disposable subscribe;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_goods_search;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initParam() {
        super.initParam();
        tags = (String) getIntent().getExtras().get("tags");
    }

    @Override
    public void initData() {
        super.initData();
        setTitle("搜索");
        itemBeans = new ArrayList<>();
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        staggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        goodsStaggeredAdapter = new GoodsStaggeredAdapter(R.layout.item_goods_staggered);
        goodsStaggeredAdapter.setEmptyView(R.layout.list_empty);
        binding.recyclerView.setItemAnimator(null);

        goodsStaggeredAdapter.getLoadMoreModule().setEnableLoadMore(true);
        goodsStaggeredAdapter.getLoadMoreModule().setAutoLoadMore(true);
        goodsStaggeredAdapter.getLoadMoreModule().setPreLoadNumber(1);
        goodsStaggeredAdapter.getLoadMoreModule().setOnLoadMoreListener(() -> {
            page++;
            viewModel.getGoods(tags, page, pageSize, sortBy, orderBy);
        });

        linearLayout = new LinearLayoutManager(this);
        goodsLinearAdapter = new GoodsLinearAdapter(R.layout.item_goods_linear);
        goodsLinearAdapter.setEmptyView(R.layout.list_empty);
        goodsLinearAdapter.getLoadMoreModule().setEnableLoadMore(true);
        goodsLinearAdapter.getLoadMoreModule().setAutoLoadMore(true);
        goodsLinearAdapter.getLoadMoreModule().setPreLoadNumber(1);
        goodsLinearAdapter.getLoadMoreModule().setOnLoadMoreListener(() -> {
            page++;
            viewModel.getGoods(tags, page, pageSize, sortBy, orderBy);
        });

        binding.swipeContent.setOnRefreshListener(()->{
            page = 1;
            viewModel.getGoods(tags, page, pageSize, sortBy, orderBy);
        });
        changeStaggeredLayout();

        goodsStaggeredAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (UserInfoMgr.getInstance().getAccessToken() == null) {
                startActivity(SelectLoginActivity.class);
                return;
            }
            GoodsBean goodsBean = goodsStaggeredAdapter.getData().get(position);
            UniService.startService(getContext(), goodsBean.appId, 0x10002, goodsBean.jumpUrl);
        });
        goodsLinearAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (UserInfoMgr.getInstance().getAccessToken() == null) {
                startActivity(SelectLoginActivity.class);
                return;
            }
            GoodsBean goodsBean = goodsLinearAdapter.getData().get(position);
            UniService.startService(getContext(), goodsBean.appId, 0x10002, goodsBean.jumpUrl);
        });

        showDialog();
        viewModel.getGoods(tags, page, pageSize, sortBy, orderBy);
        binding.close.setOnClickListener(v -> binding.search.setText(null));
        binding.search.addTextChangedListener(new TextWatcherImpl(){
            @Override
            public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
                binding.close.setVisibility(editable.length() > 0 ? View.VISIBLE : View.GONE);
            }
        });
        binding.search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    tags = binding.search.getText().toString().trim();
                    page = 1;
                    showDialog();
                    viewModel.getGoods(tags, page, pageSize, sortBy, orderBy);
                    return true;
                }
                return false;
            }
        });
        binding.order1.setSelected(true);
        binding.layoutType.setOnClickListener(v -> {
            if (layoutType == 0) {
                changeLinearLayout();
            }else{
                changeStaggeredLayout();
            }
        });
        binding.searchTv.setOnClickListener(v -> {
            tags = binding.search.getText().toString().trim();
            page = 1;
            showDialog();
            viewModel.getGoods(tags, page, pageSize, sortBy, orderBy);
        });
        binding.order1.setOnClickListener(v -> {
            sortBy = "";
            orderBy = "DESC";
            binding.order1.setSelected(true);
            binding.order4.setSelected(false);
            binding.order2.setSelected(false);
            binding.order.setImageResource(R.mipmap.jiag_drop_nor);
            showDialog();
            viewModel.getGoods(tags, page, pageSize, sortBy, orderBy);
        });
        binding.order2.setOnClickListener(v -> {
            orderBy = "DESC";
            sortBy = "totalSoldCount";
            binding.order1.setSelected(false);
            binding.order4.setSelected(false);
            binding.order2.setSelected(true);
            binding.order.setImageResource(R.mipmap.jiag_drop_nor);
            showDialog();
            viewModel.getGoods(tags, page, pageSize, sortBy, orderBy);
        });
        binding.order4.setOnClickListener(v -> {
            if (sortBy.equals("minSalesPrice")) {
                if (orderBy.equals("DESC")) {
                    orderBy = "ASC";
                    binding.order.setImageResource(R.mipmap.jiag_drop_jx);
                } else {
                    orderBy = "DESC";
                    binding.order.setImageResource(R.mipmap.jiag_drop_sx);
                }
            }else{
                binding.order.setImageResource(R.mipmap.jiag_drop_sx);
            }
            sortBy = "minSalesPrice";
            binding.order1.setSelected(false);
            binding.order4.setSelected(true);
            binding.order2.setSelected(false);
            showDialog();
            viewModel.getGoods(tags, page, pageSize, sortBy, orderBy);
        });
    }
    private void changeStaggeredLayout() {
        if ( binding.recyclerView.getItemDecorationCount() == 1)
        binding.recyclerView.removeItemDecorationAt(0);
        binding.swipeContent.setBackgroundColor(getResources().getColor(R.color.color_FFF8F8F8));
        binding.recyclerView.setLayoutManager(staggeredGridLayoutManager);
        binding.recyclerView.setAdapter(goodsStaggeredAdapter);
        layoutType = 0;
        binding.recyclerView.addItemDecoration(new SpeacesItemDecoration(getActivity(), 3, true));
    }
    private void changeLinearLayout() {
        if ( binding.recyclerView.getItemDecorationCount() == 1)
        binding.recyclerView.removeItemDecorationAt(0);
        binding.recyclerView.setLayoutManager(linearLayout);
        binding.recyclerView.setAdapter(goodsLinearAdapter);
        binding.swipeContent.setBackgroundColor(getResources().getColor(R.color.white));
        layoutType = 1;
        binding.recyclerView.addItemDecoration(new SpeacesItemDecoration(getActivity(), 12, false));
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.dismissDialog.observe(this, aBoolean -> dismissDialog());
        viewModel.goodsLiveData.observe(this, goodsItemBeans -> {
            if (page == 1) {
                itemBeans.clear();
                goodsStaggeredAdapter.getData().clear();
                goodsLinearAdapter.getData().clear();
            }
            binding.swipeContent.setRefreshing(false);
            itemBeans.addAll(goodsItemBeans);
            goodsStaggeredAdapter.addData(goodsItemBeans);
            goodsLinearAdapter.addData(goodsItemBeans);
            if (layoutType == 0) {
                if (goodsItemBeans.isEmpty() || goodsItemBeans.size() < pageSize) {
                    goodsStaggeredAdapter.getLoadMoreModule().loadMoreEnd(false);
                }else{
                    goodsStaggeredAdapter.getLoadMoreModule().loadMoreComplete();
                }
            }else{
                if (goodsItemBeans.isEmpty() || goodsItemBeans.size() < pageSize) {
                    goodsLinearAdapter.getLoadMoreModule().loadMoreEnd(false);
                }else{
                    goodsLinearAdapter.getLoadMoreModule().loadMoreComplete();
                }
            }
        });
        subscribe = RxBus.getDefault().toObservable(UniEventBean.class).subscribe(uniEventBean -> {
            if (uniEventBean.msgId == CodeTable.UNI_RELEASE) {
                if (uniEventBean.taskId == 0x10002)
                    UniUtil.openUniApp(getContext(), uniEventBean.appId, uniEventBean.jumpUrl, null, uniEventBean.isSelfUni);
            }
        });
        RxSubscriptions.add(subscribe);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxSubscriptions.remove(subscribe);
    }
}
