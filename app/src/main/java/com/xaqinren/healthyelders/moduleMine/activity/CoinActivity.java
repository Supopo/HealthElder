package com.xaqinren.healthyelders.moduleMine.activity;

import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.Observer;

import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivityCoinBinding;
import com.xaqinren.healthyelders.moduleMine.bean.WalletBean;
import com.xaqinren.healthyelders.moduleMine.viewModel.WalletViewModel;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.base.BaseViewModel;

public class CoinActivity extends BaseActivity<ActivityCoinBinding, WalletViewModel> {
    private WalletBean walletBean;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_coin;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        setTvRight("零钱明细");
        tvRight.setOnClickListener(v -> startActivity(CoinDetailActivity.class));

        viewModel.getWalletInfo();

        binding.layoutRecharge.setOnClickListener(v -> {
            //充值

        });
        binding.layoutWithdraw.setOnClickListener(v -> {
            //提现
            startActivity(WithdrawActivity.class);
        });
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
            binding.setData(this.walletBean);
            binding.balance.setText(walletBean.getWallAccountBalance());
            if (walletBean.isHasOpenAccount()) {
                //已实名认证
            }else{

            }
        });
    }

}
