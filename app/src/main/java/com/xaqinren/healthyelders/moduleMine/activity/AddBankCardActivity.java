package com.xaqinren.healthyelders.moduleMine.activity;

import android.os.Bundle;
import android.view.View;

import com.chad.library.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivityAddBankCardBindingImpl;
import com.xaqinren.healthyelders.databinding.ActivityBankCardBinding;
import com.xaqinren.healthyelders.moduleMine.viewModel.BankCardViewModel;

import me.goldze.mvvmhabit.base.BaseActivity;

/**
 * @apiNote 添加银康卡
 */
public class AddBankCardActivity extends BaseActivity <ActivityAddBankCardBindingImpl, BankCardViewModel> {
    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_add_bank_card;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        binding.confirm.setOnClickListener(v -> {

        });
        //TODO 实时查询银行卡信息,若查询到显示布局
        binding.infoLayout.setVisibility(View.VISIBLE);
        binding.agreeLayout.setVisibility(View.VISIBLE);
        binding.payAgree.setOnClickListener(v -> {

        });
        binding.scanLayout.setOnClickListener(v -> {

        });
    }
}
