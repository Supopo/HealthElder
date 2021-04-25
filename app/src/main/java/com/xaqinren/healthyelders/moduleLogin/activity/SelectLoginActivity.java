package com.xaqinren.healthyelders.moduleLogin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.MainActivity;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.apiserver.ApiServer;
import com.xaqinren.healthyelders.apiserver.CustomObserver;
import com.xaqinren.healthyelders.apiserver.MBaseResponse;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.databinding.ActivitySelectLoginBinding;
import com.xaqinren.healthyelders.global.AppApplication;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.global.InfoCache;
import com.xaqinren.healthyelders.http.RetrofitClient;
import com.xaqinren.healthyelders.moduleLogin.bean.LoginTokenBean;
import com.xaqinren.healthyelders.moduleLogin.bean.LoginUserBean;
import com.xaqinren.healthyelders.moduleLogin.bean.WeChatUserInfoBean;
import com.xaqinren.healthyelders.moduleLogin.viewModel.SelectLoginViewModel;
import com.xaqinren.healthyelders.wxapi.WXEntryActivity;

import java.util.HashMap;

import io.dcloud.common.DHInterface.ICallBack;
import io.dcloud.feature.sdk.DCUniMPSDK;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;
import me.goldze.mvvmhabit.utils.SPUtils;
import okhttp3.MediaType;
import okhttp3.RequestBody;

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
        binding.rlSelect.setOnClickListener(lis ->{
            isAgree = !isAgree;
            if (isAgree) {
                binding.ivSelect.setBackgroundResource(R.mipmap.login_rad_sel);
            }else {
                binding.ivSelect.setBackgroundResource(R.mipmap.login_rad_nor);
            }
        });
        binding.ivPhone.setOnClickListener(lis -> {
            //手机号登录
            if (checkAgree()) {
                startActivity(PhoneLoginActivity.class);
            }
        });

    }
    private boolean checkAgree() {
        if (!isAgree) {
            Toast.makeText(this, "请先同意用户协议", Toast.LENGTH_LONG).show();
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
                        toWxChatRealLogin(JSON.parseObject(wxInfo,WeChatUserInfoBean.class));
                    }
                });
        RxSubscriptions.add(disposable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }

    /**
     * 登录
     * @param infoBean
     */
    private void toWxChatRealLogin(WeChatUserInfoBean infoBean) {
        HashMap<String, String> map = new HashMap<>();
        map.put("unionId", infoBean.unionId);
        map.put("openId", infoBean.openId);
        map.put("nickName", infoBean.nickName);
        map.put("sex", infoBean.sex);
        map.put("city", infoBean.city);
        map.put("province", infoBean.province);
        map.put("country", infoBean.country);
        map.put("avatarUrl", infoBean.avatarUrl);
        map.put("sessionKey", infoBean.sessionKey);
        map.put("rCode", infoBean.rcode);

        String json = JSON.toJSONString(map);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"),json);
        //去通知服务器
        showDialog();
        RetrofitClient.getInstance().create(ApiServer.class)
                .toWxChatRealLogin(Constant.auth,Constant.mid,body)
                .compose(this.bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CustomObserver<MBaseResponse<LoginTokenBean>>(){

                    @Override
                    protected void dismissDialog() {

                    }

                    @Override
                    public void onSuccess(MBaseResponse<LoginTokenBean> baseResponse) {
                        SPUtils.getInstance().put(Constant.SP_KEY_WX_INFO, "");
                        if (baseResponse.isOk()) {

                            //SPUtils.getInstance().put(Constant.SP_KEY_TOKEN_INFO,JSON.toJSONString(baseResponse.getData()));
                            InfoCache.getInstance().setTokenInfo(baseResponse.getData());
                            getUserInfo(baseResponse.getData().access_token);
                        }else{
                            SelectLoginActivity.this.dismissDialog();
                        }
                    }

                    @Override
                    public void onFail(String code, MBaseResponse data) {
                        super.onFail(code, data);
                        SelectLoginActivity.this.dismissDialog();
                        SPUtils.getInstance().put(Constant.SP_KEY_WX_INFO, "");
                        if (code.equals(CodeTable.NO_PHONE_CODE)) {
                            //需要绑定手机号,跳转登录页
                            Intent intent = new Intent(SelectLoginActivity.this, PhoneLoginActivity.class);
                            intent.putExtra("openId", infoBean.openId);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }

    private void getUserInfo(String token) {
        RetrofitClient.getInstance().create(ApiServer.class)
                .userInfo("Bearer " + token)
                .compose(this.bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CustomObserver<MBaseResponse<LoginUserBean>>() {
                    @Override
                    protected void dismissDialog() {
                        SelectLoginActivity.this.dismissDialog();
                    }

                    @Override
                    public void onSuccess(MBaseResponse<LoginUserBean> data) {
                        InfoCache.getInstance().setLoginUser(data.getData());
                        //跳转首页
                        Toast.makeText(SelectLoginActivity.this,"登录成功",Toast.LENGTH_LONG).show();
                        startActivity(new Intent(SelectLoginActivity.this, MainActivity.class));
                        finish();
                    }
                });
    }
}
