package com.xaqinren.healthyelders.moduleLogin.activity;

import android.os.Bundle;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.MainActivity;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.databinding.ActivitySelectLoginBinding;
import com.xaqinren.healthyelders.global.AppApplication;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleLogin.bean.WeChatUserInfoBean;
import com.xaqinren.healthyelders.moduleLogin.viewModel.SelectLoginViewModel;
import com.xaqinren.healthyelders.uniApp.UniUtil;

import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;
import me.goldze.mvvmhabit.utils.SPUtils;

/**
 * Created by Lee. on 2021/4/22.
 * 选择登录页面
 */
public class SelectLoginActivity extends BaseActivity<ActivitySelectLoginBinding, SelectLoginViewModel> {
    private boolean isAgree;//是否同意协议
    private Disposable disposable;

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
            if (checkAgree())
                wxLogin();
        });
        binding.rlSelect.setOnClickListener(lis -> {
            isAgree = !isAgree;
            if (isAgree) {
                binding.ivSelect.setBackgroundResource(R.mipmap.login_rad_sel);
            } else {
                binding.ivSelect.setBackgroundResource(R.mipmap.login_rad_nor);
            }
        });
        binding.userAgree.setOnClickListener(lis->{
            UniUtil.openUniApp(getContext(),Constant.JKZL_MINI_APP_ID,Constant.MINI_AGREEMENT,null,true);
        });
        binding.ivPhone.setOnClickListener(lis -> {
            //手机号登录
            if (checkAgree()) {
                Bundle bundle = new Bundle();
                bundle.putInt("type", 1);
                startActivity(PhoneLoginActivity.class,bundle);
            }
        });

    }

    private boolean checkAgree() {
        if (!isAgree) {
            Toast.makeText(this, "请先同意用户协议", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
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

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        disposable = RxBus.getDefault().toObservable(EventBean.class)
                .subscribe(eventBean -> {
                    if (eventBean.msgId == CodeTable.WX_LOGIN_SUCCESS) {
                        String wxInfo = SPUtils.getInstance().getString(Constant.SP_KEY_WX_INFO);
                        viewModel.toWxChatRealLogin(JSON.parseObject(wxInfo, WeChatUserInfoBean.class));
                    }
                });
        RxSubscriptions.add(disposable);

        viewModel.loginStatus.observe(this, status -> {
            if (status != null) {
                dismissDialog();
                if (status == 1) {
                    startActivity(MainActivity.class);
                } else if (status == 2) {
                    startActivity(PhoneLoginActivity.class);
                }
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }

}
