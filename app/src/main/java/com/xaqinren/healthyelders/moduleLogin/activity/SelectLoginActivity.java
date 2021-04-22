package com.xaqinren.healthyelders.moduleLogin.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivitySelectLoginBinding;
import com.xaqinren.healthyelders.moduleLogin.viewModel.SelectLoginViewModel;

import io.dcloud.common.DHInterface.ICallBack;
import io.dcloud.feature.sdk.DCUniMPSDK;
import me.goldze.mvvmhabit.base.BaseActivity;

/**
 * Created by Lee. on 2021/4/22.
 * 选择登录页面
 */
public class SelectLoginActivity extends BaseActivity<ActivitySelectLoginBinding, SelectLoginViewModel> {
    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_select_login;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        setStatusBarTransparent();
        rlTitle.setVisibility(View.GONE);
    }
}
