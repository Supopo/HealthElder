package com.xaqinren.healthyelders.moduleHome.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


import com.xaqinren.healthyelders.global.Constant;

import com.xaqinren.healthyelders.databinding.ActivityWebBinding;
import com.xaqinren.healthyelders.moduleHome.viewModel.WebViewModel;
import com.xaqinren.healthyelders.R;


import me.goldze.mvvmhabit.base.BaseActivity;

import static android.view.KeyEvent.KEYCODE_BACK;


//注意ActivityBaseBinding换成自己activity_layout对应的名字 ActivityXxxBinding
public class WebActivity extends BaseActivity<ActivityWebBinding, WebViewModel> {
    private String mUrl;
    private String mTitle;
    private boolean isRedirect; //是否是重定向
    private boolean isPageOk; //目的地地址加载完成

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_web;

    }

    @Override
    public int initVariableId() {
        return com.xaqinren.healthyelders.BR.viewModel;
    }

    @Override
    public void initParam() {
        super.initParam();
        mUrl = getIntent().getStringExtra(Constant.PageUrl);
        mTitle = getIntent().getStringExtra(Constant.PageTitle);
    }


    //页面数据初始化方法
    @Override
    public void initData() {
        //设置标题栏
        setTitle(mTitle, "关闭");

        WebSettings seting = binding.webView.getSettings();

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
        binding.webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (binding.bar != null) {
                    if (newProgress == 100) {
                        binding.bar.setVisibility(View.GONE);
                    } else {
                        if (binding.bar.getVisibility() == View.GONE) {
                            binding.bar.setVisibility(View.VISIBLE);
                        }
                        binding.bar.setProgress(newProgress);
                    }
                }

            }
        });


        binding.webView.setWebViewClient(webViewClient);
        binding.webView.loadUrl(mUrl);

    }


    //页面事件监听的方法，一般用于ViewModel层转到View层的事件注册
    @Override
    public void initViewObservable() {
        ivLeft.setOnClickListener(lis -> {
            if (binding.webView.canGoBack()) {
                binding.webView.goBack();
            }
        });
        tvRight.setOnClickListener(lis -> {
            finish();
        });

    }


    protected WebViewClient webViewClient = new WebViewClient() {
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();  // 接受所有网站的证书
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            isRedirect = true;
            isPageOk = false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            isPageOk = isRedirect;
        }

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            isRedirect = false;
            if (isPageOk) {
                //这段是防止网页面内部视频等打不开
                try {
                    if (!url.startsWith("http://") && !url.startsWith("https://")) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                        return true;
                    }
                } catch (Exception e) {
                    return true;
                }
                //这段是为了打开重定向网页
                view.loadUrl(url);
                return true;
            }
            return false;//如果这里返回true，B站打不开
        }
    };

    //重写手机返回键
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KEYCODE_BACK) && binding.webView.canGoBack()) {
            binding.webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
