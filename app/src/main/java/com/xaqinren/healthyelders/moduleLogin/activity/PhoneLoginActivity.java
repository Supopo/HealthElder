package com.xaqinren.healthyelders.moduleLogin.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.igexin.sdk.PushManager;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.MainActivity;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivityPhoneLoginBinding;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleLogin.bean.WeChatUserInfoBean;
import com.xaqinren.healthyelders.moduleLogin.viewModel.PhoneLoginViewModel;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.utils.RegexUtils;
import me.goldze.mvvmhabit.utils.SPUtils;
import me.goldze.mvvmhabit.utils.StringUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;

public class PhoneLoginActivity extends BaseActivity<ActivityPhoneLoginBinding, PhoneLoginViewModel> {
    private String openId;
    private Disposable observable;
    private int maxTime = 20;
    private int type;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_phone_login;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        tvTitle.setText("");
        initEvent();
    }

    private void initEvent() {
        binding.getVCode.setOnClickListener(view -> {
            //获取验证码
            String trim = binding.etPhone.getText().toString().trim();
            if (StringUtils.isEmpty(trim)) {
                ToastUtils.showShort("请输入手机号");
                return;
            }
            if (!RegexUtils.isMobileSimple(trim)) {
                ToastUtils.showShort("手机号不合法");
                return;
            }
            ToastUtils.showShort("短信发送成功");
            viewModel.sendSms(trim);
            view.setEnabled(false);
            observable = Observable.interval(0, 1, TimeUnit.SECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .take(maxTime)
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(Long o) throws Exception {
                            binding.getVCode.setText((maxTime - o) + "S秒后可重新获取");
                            if (o == maxTime - 1) {
                                view.setEnabled(true);
                                binding.getVCode.setText("获取验证码");
                            }
                        }
                    });
        });
        binding.conformBtn.setOnClickListener(view -> {
            if (checkParam())
                showDialog();
            viewModel.loginByPhone(binding.etPhone.getText().toString().trim(), binding.etVCode.getText().toString().trim(), openId);
        });
        if (type == 1) {
            //登录/注册
            binding.tvBindPhone.setText("手机号快捷登录");
            binding.tvFriend.setText("未注册过的手机将自动创建账号");
        } else {
            //绑定
            binding.tvBindPhone.setText("绑定您的手机号");
            binding.tvFriend.setText("可以使您更快的找到您的好友");
        }
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        //监听登录请求状态
        viewModel.loginSuccess.observe(this, isSuccess -> {
            if (isSuccess != null) {
                dismissDialog();
                if (isSuccess) {
                    startActivity(MainActivity.class);
                    finish();
                }
            }

        });
    }

    @Override
    protected void onDestroy() {
        if (observable != null) {
            observable.dispose();
        }
        super.onDestroy();
    }

    @Override
    public void initParam() {
        super.initParam();
        if (getIntent().getExtras() != null) {
            type = getIntent().getExtras().getInt("type", 0);
        }
        openId = getIntent().getStringExtra("openId");
        if (TextUtils.isEmpty(openId)) {
            String wxInfo = SPUtils.getInstance().getString(Constant.SP_KEY_WX_INFO);
            WeChatUserInfoBean weChatUserInfoBean = JSON.parseObject(wxInfo, WeChatUserInfoBean.class);
            if (weChatUserInfoBean != null) {
                openId = weChatUserInfoBean.openId;
            }
        }
    }

    private boolean checkParam() {
        String phone = binding.etPhone.getText().toString().trim();
        String code = binding.etVCode.getText().toString().trim();
        if (StringUtils.isEmpty(phone)) {
            Toast.makeText(this, "请填写手机号", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (StringUtils.isEmpty(code)) {
            Toast.makeText(this, "请填写验证码", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}
