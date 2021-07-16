package com.xaqinren.healthyelders.moduleLogin.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;

import com.xaqinren.healthyelders.MainActivity;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivitySplashBinding;
import com.xaqinren.healthyelders.global.AppApplication;
import com.xaqinren.healthyelders.global.InfoCache;
import com.xaqinren.healthyelders.moduleLogin.bean.LoginTokenBean;
import com.xaqinren.healthyelders.moduleLogin.viewModel.SplashViewModel;
import com.xaqinren.healthyelders.utils.MScreenUtil;

import me.goldze.mvvmhabit.base.BaseActivity;

public class Splash2Activity extends BaseActivity<ActivitySplashBinding, SplashViewModel> {

    private Handler handler;
    private Runnable runnable;
    private boolean isHasBar;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_splash;
    }

    @Override
    public int initVariableId() {
        return com.xaqinren.healthyelders.BR.viewModel;
    }

    @Override
    public void initParam() {
        super.initParam();

        //这段代码很重要，如果安装之后直接打开应用而非桌面打开，任务栈里面会重复存在A-B-C-A-C，在C页面关闭时候会重新弹出A
        if (!isTaskRoot()
                && getIntent().hasCategory(Intent.CATEGORY_LAUNCHER)
                && getIntent().getAction() != null
                && getIntent().getAction().equals(Intent.ACTION_MAIN)) {

            finish();
            return;
        }


    }

    //页面数据初始化方法
    @Override
    public void initData() {

        //设置全屏，隐藏状态栏
        //如果有刘海先要在手机的应用设置显示刘海区域内容
        //如果有的手机开启全屏之后顶部有彩色条，那是因为手机的全屏设置没有设置该app
        //        setFullScreen();
        //状态栏透明的全屏效果
        setStatusBarTransparent();

        if (Build.VERSION.SDK_INT >= 28) {
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(params);
        }
        rlTitle.setVisibility(View.GONE);


        //刷新token
        LoginTokenBean loginTokenBean = InfoCache.getInstance().getLoginTokenBean();
        if (loginTokenBean != null) {
            String refreshToken = loginTokenBean.refresh_token;
            if (!TextUtils.isEmpty(refreshToken)) {
                viewModel.refreshToken(refreshToken);
            }

        }


        initView();
    }

    public void initView() {
        isHasBar = false;
        int screenHeight = MScreenUtil.getScreenHeight(this);
        binding.container.post(new Runnable() {
            @Override
            public void run() {
                int height = binding.container.getMeasuredHeight();
                if (screenHeight == height) {
                    isHasBar = false;
                } else {
                    isHasBar = true;
                }
                AppApplication.get().setHasNavBar(isHasBar);
            }
        });

        handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(runnable = new Runnable() {
            @Override
            public void run() {
                startActivity(MainActivity.class);
                finish();
            }
        }, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}
