package com.xaqinren.healthyelders.moduleMine.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.L;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.databinding.FragmentOrderListBinding;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleLogin.activity.SelectLoginActivity;
import com.xaqinren.healthyelders.moduleMine.adapter.OrderListAdapter;
import com.xaqinren.healthyelders.moduleMine.bean.OrderListBean;
import com.xaqinren.healthyelders.moduleMine.viewModel.OrderListViewModel;
import com.xaqinren.healthyelders.moduleZhiBo.activity.PayActivity;
import com.xaqinren.healthyelders.uniApp.UniService;
import com.xaqinren.healthyelders.uniApp.UniUtil;
import com.xaqinren.healthyelders.uniApp.bean.UniEventBean;
import com.xaqinren.healthyelders.utils.LogUtils;
import com.xaqinren.healthyelders.widget.YesOrNoDialog;

import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;
import me.goldze.mvvmhabit.utils.ToastUtils;
import retrofit2.http.POST;

/**
 * 订单列表
 */
public class OrderListFragment extends BaseFragment <FragmentOrderListBinding, OrderListViewModel> {

    //0 全部 ，1 待支付 2，代发货 ，3待收货 4，待评价
    private int type;
    private int page = 1;
    private int pageSize = 10;
    private OrderListAdapter adapter;
    private String TAG = "OrderListFragment";
    private boolean isInitData;
    private Disposable uniSubscribe;
    private int uniSubKey;
    private boolean visibleToUser;
    private boolean isCanInitData;
    private double balance;

    public OrderListFragment(int type) {
        this.type = type ;
        uniSubKey = 0x10020 + type;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }


    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_order_list;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        LogUtils.e(TAG, "setUserVisibleHint -> " + isVisibleToUser);
        visibleToUser = isVisibleToUser;
        if (visibleToUser && !isInitData && isCanInitData) {
            initData();
        }
    }

    @Override
    public void initData() {
        super.initData();
        isCanInitData = true;
        if (!visibleToUser) {
            return;
        }
        isInitData = true;
        adapter = new OrderListAdapter();
        adapter.setUniSubKey(uniSubKey);
        binding.content.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.content.setAdapter(adapter);
        binding.content.setNestedScrollingEnabled(false);
        adapter.getLoadMoreModule().loadMoreEnd(true);
        adapter.getLoadMoreModule().setAutoLoadMore(true);
        adapter.getLoadMoreModule().setOnLoadMoreListener(() -> {
            showDialog();
            viewModel.getOrderList(type, page, pageSize);
        });
         binding.swipeContent.setOnRefreshListener(() -> {
            page = 1;
            viewModel.getOrderList(type, page, pageSize);
        });
        adapter.setEmptyView(R.layout.list_empty);
        showDialog();
        viewModel.getOrderList(type, page, pageSize);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> a, @NonNull View view, int position) {
                //跳转详情
                LogUtils.e(TAG, "onItemClick ->" + position);
                if (UserInfoMgr.getInstance().getAccessToken() == null) {
                    startActivity(SelectLoginActivity.class);
                    return;
                }
                UniService.startService(getContext(), Constant.JKZL_MINI_APP_ID, uniSubKey, adapter.getData().get(position).getJumpUrl());
            }
        });
        adapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter<?, ?> a, @NonNull View view, int position) {
                //跳转详情
                LogUtils.e(TAG, "onItemChildClick ->" + position);
                switch (view.getId()) {
                    case R.id.store_name:
                        //跳转商家
                        break;
                    case R.id.xgdz:
                        //修改地址
                        break;
                    case R.id.qxdd:
                        //取消订单
                        showCancelOrder(adapter.getData().get(position));
                        break;
                    case R.id.ycsh:
                        //延长收货
                        break;
                    case R.id.ckwl:
                        //查看物流
                        break;
                    case R.id.scdd:
                        showDelOrder(adapter.getData().get(position));
                        //删除订单
                        break;
                    case R.id.sqsh:
                        //申请售后
                        break;
                    case R.id.jxfk:
                        //继续付款
                        showPayOrder(adapter.getData().get(position));
                        break;
                    case R.id.qrsh:
                        //确认收货
                        showReceiptOrder(adapter.getData().get(position));
                        break;
                    case R.id.pj:
                        //评价
                        break;
                }
            }
        });

    }

    private void showPayOrder(OrderListBean orderListBean) {
        Bundle bundle = new Bundle();
        bundle.putDouble("czNum", orderListBean.getPayableAmount());
        bundle.putDouble("yeNum", balance);
        bundle.putString("orderNo", orderListBean.getOrderNo());
        bundle.putString("orderType", "COMMODITY_CONSUMPTION");
        bundle.putInt("theme", R.style.EditDialogStyle);
        startActivity(PayActivity.class, bundle);
    }

    private void showCancelOrder(OrderListBean orderListBean) {
        YesOrNoDialog yesOrNoDialog = new YesOrNoDialog(getContext());
        yesOrNoDialog.setMessageText("确认取消该订单?");
        yesOrNoDialog.setRightBtnClickListener(v -> {
            yesOrNoDialog.dismissDialog();
            showDialog();
            viewModel.cancelOrder(orderListBean.getId());
        });
        yesOrNoDialog.showDialog();
    }
    private void showDelOrder(OrderListBean orderListBean) {
        YesOrNoDialog yesOrNoDialog = new YesOrNoDialog(getContext());
        yesOrNoDialog.setMessageText("确认删除该订单?");
        yesOrNoDialog.setRightBtnClickListener(v -> {
            yesOrNoDialog.dismissDialog();
            showDialog();
            viewModel.delOrder(orderListBean.getId());
        });
        yesOrNoDialog.showDialog();
    }
    private void showReceiptOrder(OrderListBean orderListBean) {
        YesOrNoDialog yesOrNoDialog = new YesOrNoDialog(getContext());
        yesOrNoDialog.setMessageText("确认收货?");
        yesOrNoDialog.setRightBtnClickListener(v -> {
            yesOrNoDialog.dismissDialog();
            showDialog();
            viewModel.receiptOrder(orderListBean.getId());
        });
        yesOrNoDialog.showDialog();
    }

    public void onPaySuccess() {
        page = 1;
        viewModel.getOrderList(type, page, pageSize);
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.requestSuccess.observe(this,(Bloon)-> dismissDialog());
        viewModel.orderList.observe(this,(list)->{
            if (page == 1) {
                adapter.setList(null);
            }
            if (list.isEmpty()) {
                adapter.getLoadMoreModule().loadMoreEnd(false);
            }else{
                adapter.getLoadMoreModule().loadMoreComplete();
                page++;
            }
            binding.swipeContent.setRefreshing(false);
            adapter.addData(list);
        });
        viewModel.orderCancelStatus.observe(this,bool ->{
            LogUtils.e(TAG, "orderCancelStatus -> " + bool);
            page = 1;
            viewModel.getOrderList(type, page, pageSize);
        });
        viewModel.orderDelStatus.observe(this,bool ->{
            LogUtils.e(TAG, "orderDelStatus -> " + bool);
            page = 1;
            viewModel.getOrderList(type, page, pageSize);
        });
        viewModel.orderReceiptStatus.observe(this,bool ->{
            LogUtils.e(TAG, "orderReceiptStatus -> " + bool);
            page = 1;
            viewModel.getOrderList(type, page, pageSize);
        });

        uniSubscribe = RxBus.getDefault().toObservable(UniEventBean.class).subscribe(event -> {
            if (event != null) {
                if (event.msgId == CodeTable.UNI_RELEASE) {
                    if (event.taskId == uniSubKey) {
                        UniUtil.openUniApp(getContext(), event.appId, event.jumpUrl, null, event.isSelfUni);
                    }
                } else if (event.msgId == CodeTable.UNI_RELEASE_FAIL) {
                    ToastUtils.showShort("打开小程序失败");
                }
            }
        });
        RxSubscriptions.add(uniSubscribe);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxSubscriptions.remove(uniSubscribe);
    }
}
