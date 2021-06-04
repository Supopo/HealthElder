package com.xaqinren.healthyelders.moduleMine.activity;

import android.content.Intent;
import android.os.Bundle;

import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivityWalletBinding;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleMine.viewModel.WalletViewModel;
import com.xaqinren.healthyelders.moduleZhiBo.activity.CZSelectPopupActivity;
import com.xaqinren.healthyelders.moduleZhiBo.activity.PayActivity;
import com.xaqinren.healthyelders.moduleZhiBo.activity.StartRenZhengActivity;

import me.goldze.mvvmhabit.base.BaseActivity;

public class WalletActivity extends BaseActivity<ActivityWalletBinding, WalletViewModel> {
    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_wallet;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        setTitle("钱包");
        binding.jkbLayoaut.setOnClickListener(v -> {
            startActivity(CZSelectPopupActivity.class);
        });
        binding.lqKaihu.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt(Constant.REN_ZHENG_TYPE, 1);
            startActivity(StartRenZhengActivity.class, bundle);
        });
        binding.lqLayout.setOnClickListener(v -> {
            startActivity(SettingPayPassActivity.class);
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            boolean status = intent.getBooleanExtra("status", false);
            boolean pay_status = intent.getBooleanExtra("pay_status", false);
            if (status) {
                //认证成功
            }
            if (pay_status) {
                //密码设置成功
            }
        }
    }

}
