package com.xaqinren.healthyelders.moduleLogin.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.tencent.qcloud.tim.uikit.utils.ScreenUtil;
import com.xaqinren.healthyelders.MainActivity;
import com.xaqinren.healthyelders.databinding.ActivitySplashBinding;
import com.xaqinren.healthyelders.global.AppApplication;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.global.InfoCache;
import com.xaqinren.healthyelders.moduleHome.LoadGiftService;
import com.xaqinren.healthyelders.moduleLogin.bean.LoginTokenBean;
import com.xaqinren.healthyelders.moduleLogin.viewModel.SplashViewModel;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.moduleZhiBo.activity.StartLiveActivity;
import com.xaqinren.healthyelders.utils.MScreenUtil;
import com.xaqinren.healthyelders.widget.BottomDialog;

import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.utils.SPUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;

public class SplashActivity extends BaseActivity<ActivitySplashBinding, SplashViewModel> {

    private Handler handler;
    private Runnable runnable;
    private Timer timer = new Timer();
    private int recLen = 1;//跳过倒计时提示3秒
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() { // UI thread
                @Override
                public void run() {
                    recLen--;
                    binding.tvJump.setText("跳过 " + recLen);
                    if (recLen < 0) {
                        timer.cancel();
                        binding.tvJump.setVisibility(View.GONE);//倒计时到0隐藏字体
                    }
                }
            });
        }
    };
    private boolean isHasBar;
    private Disposable disposable;

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
            //            if (System.currentTimeMillis() >= ((loginTokenBean.expires_in * 1000) + loginTokenBean.saveTime)) {
            //                //token过期
            //            }
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
        boolean showPop = SPUtils.getInstance().getBoolean(Constant.SHOW_POP_WEB, true);
        if (showPop) {
            showWebPop();
        }else {
            checkPermissions();
        }

        LoadGiftService.startService(this);

        //        binding.tvJump.setText("跳过 " + recLen);
        //        timer.schedule(task, 1000, 1000);//等待时间一秒，停顿时间一秒

        handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(runnable = new Runnable() {
            @Override
            public void run() {
                canNext = true;
                toNext();
            }
        }, recLen * 1000);
    }

    public void checkPermissions() {
        disposable = permissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) {
                        SPUtils.getInstance().put(Constant.SHOW_POP_WEB, false);
                        hasParm = true;
                        if (canNext) {
                            toNext();
                        }
                    } else {
                        ToastUtils.showShort("访问权限已拒绝");
                        //暂时不让退出，直接进入
                        hasParm = true;
                        if (canNext) {
                            toNext();
                        }
                    }

                });
    }

    private void toNext() {
        if (hasParm) {
            startActivity(MainActivity.class);
            finish();
        }

    }

    private boolean hasParm;
    private boolean canNext;

    //页面事件监听的方法，一般用于ViewModel层转到View层的事件注册
    @Override
    public void initViewObservable() {
        binding.tvJump.setOnClickListener(listener -> {
            toNext();
            if (runnable != null) {
                handler.removeCallbacks(runnable);
            }
        });
    }

    private void showWebPop() {
        View popView = View.inflate(this, R.layout.pop_web, null);
        BottomDialog dialog = new BottomDialog(this, popView,
                new int[]{R.id.tvAgree, R.id.tvDisagree}, (int) (ScreenUtil.getScreenWidth(this) * 0.8), (int) (ScreenUtil.getScreenHeight(this) * 0.85), Gravity.CENTER);
        ProgressBar bar = popView.findViewById(R.id.bar);
        bar.setProgressDrawable(getResources().getDrawable(R.drawable.web_progress_bar));
        WebView webView = popView.findViewById(R.id.webView);
        WebSettings seting = webView.getSettings();
        seting.setJavaScriptCanOpenWindowsAutomatically(true);//设置js可以直接打开窗口，如window.open()，默认为false
        seting.setJavaScriptEnabled(true);//是否允许执行js，默认为false。设置true时，会提醒可能造成XSS漏洞
        seting.setSupportZoom(true);//是否可以缩放，默认true
        seting.setBuiltInZoomControls(true);//是否显示缩放按钮，默认false
        seting.setUseWideViewPort(true);//设置此属性，可任意比例缩放。大视图模式
        seting.setLoadWithOverviewMode(true);//和setUseWideViewPort(true)一起解决网页自适应问题
        seting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        seting.setAppCacheEnabled(true);//是否使用缓存
        seting.setDomStorageEnabled(true);//DOM Storage
        seting.setUseWideViewPort(true); // 关键点
        seting.setSupportMultipleWindows(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            seting.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        WebViewClient webViewClient = new WebViewClient() {
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();  // 接受所有网站的证书
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                webView.loadUrl(url);
                return true;//如果这里返回true，B站打不开
            }
        };

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (bar != null) {
                    if (newProgress == 100) {
                        bar.setVisibility(View.GONE);
                    } else {
                        if (bar.getVisibility() == View.GONE) {
                            bar.setVisibility(View.VISIBLE);
                        }
                        bar.setProgress(newProgress);
                    }
                }
            }
        });
        webView.setWebViewClient(webViewClient);
        webView.loadUrl(Constant.PRIMARY_RULE);
        dialog.setOnBottomItemClickListener(new BottomDialog.OnBottomItemClickListener() {
            @Override
            public void onBottomItemClick(BottomDialog dialog, View view) {
                int id = view.getId();
                if (id == R.id.tvAgree) {
                    dialog.dismiss();
                    checkPermissions();
                } else if (id == R.id.tvDisagree) {
                    dialog.dismiss();
                    finish();
                }
            }
        });
        dialog.show();
    }


}
