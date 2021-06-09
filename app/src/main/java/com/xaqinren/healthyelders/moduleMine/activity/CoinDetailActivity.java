package com.xaqinren.healthyelders.moduleMine.activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.tencent.bugly.proguard.B;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivityCoinDetailBinding;
import com.xaqinren.healthyelders.moduleMine.adapter.CoinDetailAdapter;
import com.xaqinren.healthyelders.moduleMine.bean.BillBean;
import com.xaqinren.healthyelders.moduleMine.bean.BillRecodeBean;
import com.xaqinren.healthyelders.moduleMine.viewModel.WalletViewModel;
import com.xaqinren.healthyelders.widget.StarItemDecrationextends;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseActivity;

public class CoinDetailActivity extends BaseActivity<ActivityCoinDetailBinding, WalletViewModel> {
    private CoinDetailAdapter coinDetailAdapter;
    private int index;
    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_coin_detail;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        setTitle("账单");
        coinDetailAdapter = new CoinDetailAdapter();
        binding.content.setLayoutManager(new LinearLayoutManager(this));
        binding.content.addItemDecoration(new StarItemDecrationextends(this));
        binding.content.setAdapter(coinDetailAdapter);
        showDialog();
        viewModel.getBillInfo(getDate());
        coinDetailAdapter.getLoadMoreModule().setEnableLoadMore(true);
        coinDetailAdapter.getLoadMoreModule().setAutoLoadMore(true);
        coinDetailAdapter.getLoadMoreModule().setOnLoadMoreListener(() -> {
            showDialog();
            viewModel.getBillInfo(getDate());
        });
        binding.swipeContent.setEnabled(false);
        coinDetailAdapter.setOnItemClickListener((adapter, view, position) -> {
            BillBean billBean = coinDetailAdapter.getData().get(position);
            Bundle bundle = new Bundle();
            bundle.putString("id",billBean.getId());
            startActivity(WithdrawWXActivity.class,bundle);
        });
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.request.observe(this,b->{dismissDialog();});
        viewModel.billList.observe(this, billRecodeBean -> {
            if (billRecodeBean == null) {
                coinDetailAdapter.getLoadMoreModule().loadMoreFail();
                return;
            }
            List<BillBean> list = billRecodeBean.getRecordList();
            if (!list.isEmpty()) {
                for (BillBean billBean : list) {
                    billBean.setHeader_group_name(billRecodeBean.getMonth());
                    billBean.setHeader_group_income("收入" + billRecodeBean.getIncome() + "  支出" + billRecodeBean.getExpenditure());
                }
                coinDetailAdapter.getLoadMoreModule().loadMoreComplete();
                coinDetailAdapter.addData(list);
                index--;
            }else{
                coinDetailAdapter.getLoadMoreModule().loadMoreEnd(false);
            }
            if (!coinDetailAdapter.getData().isEmpty()) {
                binding.emptyLayout.setVisibility(View.GONE);
            }else{
                binding.emptyLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private String getDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, index);
        Date time = calendar.getTime();
        return new SimpleDateFormat("yyyy-MM").format(time);
    }
}
