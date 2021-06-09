package com.xaqinren.healthyelders.moduleMine.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.lifecycle.Observer;

import com.tencent.qcloud.tim.uikit.utils.ScreenUtil;
import com.tencent.qcloud.ugckit.utils.ScreenUtils;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.databinding.ActivityWalletBinding;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleLogin.activity.SelectLoginActivity;
import com.xaqinren.healthyelders.moduleLogin.bean.UserInfoBean;
import com.xaqinren.healthyelders.moduleMine.bean.WalletBean;
import com.xaqinren.healthyelders.moduleMine.viewModel.WalletViewModel;
import com.xaqinren.healthyelders.moduleZhiBo.activity.CZSelectPopupActivity;
import com.xaqinren.healthyelders.moduleZhiBo.activity.StartRenZhengActivity;
import com.xaqinren.healthyelders.uniApp.UniService;
import com.xaqinren.healthyelders.uniApp.UniUtil;
import com.xaqinren.healthyelders.uniApp.bean.UniEventBean;

import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;
import me.goldze.mvvmhabit.utils.ToastUtils;

public class WalletActivity extends BaseActivity<ActivityWalletBinding, WalletViewModel> {
    private UserInfoBean userInfoBean;
    private WalletBean walletBean;
    private Disposable uniSubscribe;

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
            ScreenUtils.setWindowAlpha(this, 1.0f, 0.6f, 400);
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
                    Bundle bundle = new Bundle();
                    bundle.putInt(Constant.REN_ZHENG_TYPE, 1);
                    startActivity(StartRenZhengActivity.class,bundle);
                }
                return;
            }
            startActivity(CoinActivity.class);
        });
        binding.cardLayout.setOnClickListener(v -> {
//            startActivity(BandCardActivity.class);
            if (UserInfoMgr.getInstance().getAccessToken() == null) {
                startActivity(SelectLoginActivity.class);
                return;
            }
            UniService.startService(this, Constant.JKZL_MINI_APP_ID, 0x10009, Constant.MINI_BANK_CARD_LIST);
        });

        binding.createLive.setOnClickListener(v -> {
            startActivity(WithdrawActivity.class);
        });
        binding.txLive.setOnClickListener(v -> {
            startActivity(WithdrawActivity.class);
        });
        binding.redLive.setOnClickListener(v -> {
            startActivity(WithdrawActivity.class);
        });
        binding.jkbCountTv.setText("0");
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
            initKaiHu();
            binding.setData(this.walletBean);
        });
        uniSubscribe = RxBus.getDefault().toObservable(UniEventBean.class).subscribe(event -> {
            if (event != null) {
                if (event.msgId == CodeTable.UNI_RELEASE) {
                    if (event.taskId == 0x10009) {
                        UniUtil.openUniApp(this, event.appId, event.jumpUrl, null, event.isSelfUni);
                    }
                } else if (event.msgId == CodeTable.UNI_RELEASE_FAIL) {
                    ToastUtils.showShort("打开小程序失败");
                }
            }
        });
        RxSubscriptions.add(uniSubscribe);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        viewModel.getWalletInfo();
        userInfoBean = UserInfoMgr.getInstance().getUserInfo();
    }

    private void initKaiHu() {
        if (walletBean.isHasOpenAccount()) {
            //已实名认证
            binding.lqKaihu.setVisibility(View.GONE);
            binding.lqCountTv.setVisibility(View.VISIBLE);
        }else{
            binding.lqKaihu.setVisibility(View.VISIBLE);
            binding.lqCountTv.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxSubscriptions.remove(uniSubscribe);
    }

    @Override
    protected void onResume() {
        super.onResume();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        float alpha = lp.alpha;
        if (alpha < 1) {
            ScreenUtils.setWindowAlpha(this, 0.6f, 1f, 400);
        }
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
