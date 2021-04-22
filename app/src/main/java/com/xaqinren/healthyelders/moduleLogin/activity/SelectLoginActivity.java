package com.xaqinren.healthyelders.moduleLogin.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivitySelectLoginBinding;
import com.xaqinren.healthyelders.global.AppApplication;
import com.xaqinren.healthyelders.moduleLogin.viewModel.SelectLoginViewModel;

import io.dcloud.common.DHInterface.ICallBack;
import io.dcloud.feature.sdk.DCUniMPSDK;
import me.goldze.mvvmhabit.base.BaseActivity;

/**
 * Created by Lee. on 2021/4/22.
 * 选择登录页面
 */
public class SelectLoginActivity extends BaseActivity<ActivitySelectLoginBinding, SelectLoginViewModel> {
    private boolean isAgree;//是否同意协议

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
        initEvent();
    }

    private void initEvent() {
        binding.btnLogin.setOnClickListener(lis -> {
            wxLogin();
        });
        binding.rlSelect.setOnClickListener(lis ->{
            isAgree = !isAgree;
            if (isAgree) {
                binding.ivSelect.setBackgroundResource(R.mipmap.login_rad_sel);
            }else {
                binding.ivSelect.setBackgroundResource(R.mipmap.login_rad_nor);
            }
        });
    }

    //微信登录页
    private void wxLogin() {
        if (!AppApplication.mWXapi.isWXAppInstalled()) {
            Toast.makeText(this, "您还未安装微信客户端", Toast.LENGTH_SHORT).show();
            return;
        }

        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wx_login";//这个字段可以任意更改
        AppApplication.mWXapi.sendReq(req);

    }
}
