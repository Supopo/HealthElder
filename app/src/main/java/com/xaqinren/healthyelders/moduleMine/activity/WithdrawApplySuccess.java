package com.xaqinren.healthyelders.moduleMine.activity;

import android.os.Bundle;

import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivityWithdrawApplySuccessBinding;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.base.BaseViewModel;

public class WithdrawApplySuccess extends BaseActivity <ActivityWithdrawApplySuccessBinding, BaseViewModel>{
    private String amount;
    private String card;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_withdraw_apply_success;
    }

    @Override
    public int initVariableId() {
        return 0;
    }

    @Override
    public void initParam() {
        super.initParam();
        setTitle("提现结果");
        amount = getIntent().getStringExtra("amount");
        card = getIntent().getStringExtra("card");
    }

    @Override
    public void initData() {
        super.initData();
        binding.amount.setText(amount);
        binding.hint.setText("申请提现到¥ " + amount + "至银行卡" + card.substring(card.length() - 4, card.length()) + "成功");
        binding.btnNext.setOnClickListener(v -> {
            startActivity(WalletActivity.class);
        });
    }
}
