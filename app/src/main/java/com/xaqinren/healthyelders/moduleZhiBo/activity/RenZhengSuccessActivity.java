package com.xaqinren.healthyelders.moduleZhiBo.activity;

import android.os.Bundle;

import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.databinding.ActivityRenzhengSuccessBinding;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.global.InfoCache;
import com.xaqinren.healthyelders.moduleLogin.bean.UserInfoBean;
import com.xaqinren.healthyelders.moduleMine.activity.SettingActivity;
import com.xaqinren.healthyelders.moduleMine.activity.WalletActivity;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.base.BaseViewModel;

/**
 * Created by Lee. on 2021/4/24.
 * 身份证认证成功
 */
public class RenZhengSuccessActivity extends BaseActivity<ActivityRenzhengSuccessBinding, BaseViewModel> {

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_renzheng_success;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        tvTitle.setText("实名认证");
        int key = getIntent().getIntExtra(Constant.REN_ZHENG_TYPE, 0);
        binding.btnToLive.setOnClickListener(lis -> {
            if (key == 0) {
                startActivity(StartLiveActivity.class);
            } else if (key == 1) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("status", true);
                startActivity(WalletActivity.class, bundle);
            } else if (key == 2) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("status", true);
                startActivity(SettingActivity.class, bundle);
            }
            finish();
        });
        String name = getIntent().getExtras().getString("name");
        binding.tvName.setText(name);
        String idNumber = getIntent().getExtras().getString("idNumber");
        binding.tvNum.setText(idNumber);
        UserInfoBean userInfo = UserInfoMgr.getInstance().getUserInfo();
        userInfo.setRealName(name);
        userInfo.setHasRealName(true);
        InfoCache.getInstance().setLoginUser(userInfo);
        if (key != 0) {
            binding.btnToLive.setText("完成");
        }
    }

}
