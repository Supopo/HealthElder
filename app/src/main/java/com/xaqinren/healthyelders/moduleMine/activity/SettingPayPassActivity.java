package com.xaqinren.healthyelders.moduleMine.activity;

import android.os.Bundle;

import com.tencent.bugly.proguard.B;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivitySettingPayPassBinding;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleMine.viewModel.PassWorkViewModel;
import com.xaqinren.healthyelders.utils.LogUtils;
import com.xaqinren.healthyelders.widget.pay.PayPassKeyBoard;
import com.xaqinren.healthyelders.widget.pay.PayPassView;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.base.BaseViewModel;

public class SettingPayPassActivity extends BaseActivity<ActivitySettingPayPassBinding, PassWorkViewModel> implements PayPassKeyBoard.OnKeyChange, PayPassView.OnKeyChange {
    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_setting_pay_pass;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        binding.keyBoard.attachPassView(binding.passView);
        binding.keyBoard.setOnKeyChange(this);
        binding.passView.setOnKeyChange(this);
    }

    @Override
    public void onKeyDown(String value) {

    }

    @Override
    public void onKeyBack() {

    }

    @Override
    public void onInputComplete(String value) {
        //输入完成
        LogUtils.e("SettingPayPassActivity", value);
        Bundle bundle = new Bundle();
        bundle.putString(Constant.PASSWORD, value);
        startActivity(ConfirmPassActivity.class, bundle);
    }

    @Override
    public void onInputVacancy() {

    }
}
