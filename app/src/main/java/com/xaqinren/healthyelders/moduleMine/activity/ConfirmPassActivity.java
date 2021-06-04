package com.xaqinren.healthyelders.moduleMine.activity;

import android.os.Bundle;

import com.tencent.qcloud.tim.uikit.utils.ToastUtil;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivityConfirmPassBinding;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleMine.viewModel.PassWorkViewModel;
import com.xaqinren.healthyelders.utils.LogUtils;
import com.xaqinren.healthyelders.widget.pay.PayPassKeyBoard;
import com.xaqinren.healthyelders.widget.pay.PayPassView;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.base.BaseViewModel;

public class ConfirmPassActivity extends BaseActivity<ActivityConfirmPassBinding, PassWorkViewModel>implements PayPassKeyBoard.OnKeyChange, PayPassView.OnKeyChange{

    private String passWord;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_confirm_pass;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        passWord = getIntent().getStringExtra(Constant.PASSWORD);
        binding.keyBoard.attachPassView(binding.passView);
        binding.keyBoard.setOnKeyChange(this);
        binding.passView.setOnKeyChange(this);
        binding.confirm.setOnClickListener(v -> {
            if (!binding.passView.getValue().equals(passWord)) {
                ToastUtil.toastShortMessage("密码不一致");
                return;
            }
            ToastUtil.toastShortMessage("密码设置成功");
            //设置成功
            startActivity(WalletActivity.class);
        });
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
        binding.confirm.setEnabled(true);
    }

    @Override
    public void onInputVacancy() {
        binding.confirm.setEnabled(false);
    }
}
