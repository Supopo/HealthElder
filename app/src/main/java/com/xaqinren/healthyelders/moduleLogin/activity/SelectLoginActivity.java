package com.xaqinren.healthyelders.moduleLogin.activity;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.qcloud.tim.uikit.utils.ScreenUtil;
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
import com.xaqinren.healthyelders.widget.BottomDialog;

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
        boolean showWebPop = SPUtils.getInstance().getBoolean(Constant.SHOW_POP_WEB, true);
        if (showWebPop) {
            showWebPop();
        }
    }

    private void initEvent() {
        binding.ivBack.setOnClickListener(lis ->{
            finish();
        });
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
        binding.userAgree.setOnClickListener(lis -> {
            UniUtil.openUniApp(getContext(), Constant.JKZL_MINI_APP_ID, Constant.MINI_AGREEMENT, null, true);
        });
        binding.userAgree2.setOnClickListener(lis -> {
            UniUtil.openUniApp(getContext(), Constant.JKZL_MINI_APP_ID, Constant.MINI_PRIVACY, null, true);
        });
        binding.ivPhone.setOnClickListener(lis -> {
            //手机号登录
            if (checkAgree()) {
                Bundle bundle = new Bundle();
                bundle.putInt("type", 1);
                startActivity(PhoneLoginActivity.class, bundle);
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
                    } else if (eventBean.msgId == CodeTable.FINISH_ACT && eventBean.content.equals("login-success")) {
                        finish();
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
        if (disposable != null) {
            disposable.dispose();
        }
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
                    SPUtils.getInstance().put(Constant.SHOW_POP_WEB, false);
                } else if (id == R.id.tvDisagree) {
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

}
