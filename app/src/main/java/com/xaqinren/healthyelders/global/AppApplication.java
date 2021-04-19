package com.xaqinren.healthyelders.global;

import android.content.Context;

import com.squareup.leakcanary.LeakCanary;

import com.xaqinren.healthyelders.BuildConfig;
import com.xaqinren.healthyelders.R;

import me.goldze.mvvmhabit.base.BaseApplication;
import me.goldze.mvvmhabit.crash.CaocConfig;
import me.goldze.mvvmhabit.utils.KLog;


public class AppApplication extends BaseApplication {
    public static Context mContext;

    public static Context getContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        //是否开启打印日志
        KLog.init(BuildConfig.DEBUG);
        //初始化全局异常崩溃
        initCrash();
        //内存泄漏检测
        if (BuildConfig.DEBUG) {
            if (!LeakCanary.isInAnalyzerProcess(this)) {
                LeakCanary.install(this);
            }
        }
    }

    private void initCrash() {
        CaocConfig.Builder.create()
                .backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT) //背景模式,开启沉浸式
                .enabled(true) //是否启动全局异常捕获
                .showErrorDetails(true) //是否显示错误详细信息
                .showRestartButton(true) //是否显示重启按钮
                .trackActivities(true) //是否跟踪Activity
                .minTimeBetweenCrashesMs(2000) //崩溃的间隔时间(毫秒)
                .errorDrawable(R.mipmap.ic_launcher) //错误图标
                // .restartActivity(LoginActivity.class) //重新启动后的activity
                // .errorActivity(YourCustomErrorActivity.class) //崩溃后的错误activity
                // .eventListener(new YourCustomEventListener()) //崩溃后的错误监听
                .apply();
    }
}
