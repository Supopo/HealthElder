package com.xaqinren.healthyelders.moduleMine.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.Observer;

import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.databinding.ActivityWalletBinding;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleLogin.bean.UserInfoBean;
import com.xaqinren.healthyelders.moduleMine.bean.WalletBean;
import com.xaqinren.healthyelders.moduleMine.viewModel.WalletViewModel;
import com.xaqinren.healthyelders.moduleZhiBo.activity.CZSelectPopupActivity;
import com.xaqinren.healthyelders.moduleZhiBo.activity.PayActivity;
import com.xaqinren.healthyelders.moduleZhiBo.activity.StartRenZhengActivity;

import me.goldze.mvvmhabit.base.BaseActivity;

public class WalletActivity extends BaseActivity<ActivityWalletBinding, WalletViewModel> {
    private UserInfoBean userInfoBean;
    private WalletBean walletBean;

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
        userInfoBean = UserInfoMgr.getInstance().getUserInfo();
        binding.jkbLayoaut.setOnClickListener(v -> {
            startActivity(CZSelectPopupActivity.class);
        });
        binding.lqKaihu.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt(Constant.REN_ZHENG_TYPE, 1);
            startActivity(StartRenZhengActivity.class, bundle);
        });
        binding.lqLayout.setOnClickListener(v -> {
            //设置完成,跳转
            /* //TODO有设置过支付密码*/
            if (!walletBean.isHasOpenAccount()) {
                if (userInfoBean.getHasRealName()) {
                    //设置密码
                    startActivity(SettingPayPassActivity.class);
                }else{
                    //认证
                    startActivity(StartRenZhengActivity.class);
                }
                return;
            }
            startActivity(CoinActivity.class);
        });
        binding.cardLayout.setOnClickListener(v -> {
            startActivity(BandCardActivity.class);
        });
        binding.cardLayout.setOnClickListener(v -> {
            startActivity(BandCardActivity.class);
        });

        binding.jkbCountTv.setText(userInfoBean.getPointAccountBalance()+"");
        showDialog();
        viewModel.getWalletInfo();
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.request.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                dismissDialog();
            }
        });
        viewModel.wallet.observe(this, walletBean -> {
            this.walletBean = walletBean;
            if (walletBean.isHasOpenAccount()) {
                //已实名认证
                binding.lqKaihu.setVisibility(View.GONE);
                binding.lqCountTv.setVisibility(View.VISIBLE);
            }else{
                binding.lqKaihu.setVisibility(View.VISIBLE);
                binding.lqCountTv.setVisibility(View.GONE);
            }
            binding.setData(this.walletBean);
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        viewModel.getWalletInfo();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            boolean status = intent.getBooleanExtra("status", false);
            boolean pay_status = intent.getBooleanExtra("pay_status", false);
            if (status) {
                //认证成功
                startActivity(SettingPayPassActivity.class);
            }
            if (pay_status) {
                //密码设置成功
                viewModel.getWalletInfo();
            }
        }
    }

}
