package com.xaqinren.healthyelders.moduleMine.activity;

import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.airbnb.lottie.L;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivityBankCardBinding;
import com.xaqinren.healthyelders.moduleMine.adapter.BankCardAdapter;
import com.xaqinren.healthyelders.moduleMine.viewModel.BankCardViewModel;

import java.util.List;

import me.goldze.mvvmhabit.base.BaseActivity;

public class BandCardActivity extends BaseActivity<ActivityBankCardBinding, BankCardViewModel> {
    private BankCardAdapter adapter;
    private View emptyView;
    private View FooterView;
    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_bank_card;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        setTitle("我的银行卡");
        adapter = new BankCardAdapter();
        binding.content.setLayoutManager(new LinearLayoutManager(this));
        binding.content.setAdapter(adapter);
        emptyView = View.inflate(this, R.layout.empty_bank_card, null);
        adapter.setEmptyView(emptyView);
        View viewById = emptyView.findViewById(R.id.add_card_layout);
        viewById.setOnClickListener(v -> {
            //添加银行卡
            startActivity(AddBankCardActivity.class);
        });
        binding.swipeContent.setOnRefreshListener(()->{
            viewModel.getBackCard();
        });
        viewModel.getBackCard();
        FooterView = View.inflate(this, R.layout.footer_bank_card, null);
        FooterView.setOnClickListener(v -> {
            startActivity(AddBankCardActivity.class);
        });
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.bankCardList.observe(this, bankCardBeans -> {
            binding.swipeContent.setRefreshing(false);
            adapter.setList(bankCardBeans);
            if (!bankCardBeans.isEmpty()) {
                //增加脚布局
                if (!FooterView.isAttachedToWindow()) {
                    adapter.addFooterView(FooterView);
                }
            }
        });
    }
}
