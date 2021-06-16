package com.xaqinren.healthyelders.uniApp;

import android.content.Context;

import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.global.AppApplication;
import com.xaqinren.healthyelders.uniApp.widget.LoadingView;
import com.xaqinren.healthyelders.uniApp.widget.SplashView;
import com.xaqinren.healthyelders.utils.LogUtils;


import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import io.dcloud.feature.sdk.DCUniMPSDK;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class UniUtil {

    public static void openUniApp(Context context, String appId, String page , JSONObject data, boolean isSelf) {
        JSONObject jsonObject = data;
        if (jsonObject == null) {
            jsonObject = new JSONObject();
        }
        try {
            if (isSelf) {
                String token = UserInfoMgr.getInstance().getAccessToken();
                jsonObject.put("qnx_user_token", token);
            }
            LogUtils.e("UniUtil", "appid = " + appId + "\tpage = " + page);
            Class viewCls;
            if (page == null) {
                viewCls = SplashView.class;
            }else{
                viewCls = page.contains("pages/index/index") ? SplashView.class : LoadingView.class;
            }
            int isDelay = 0;
            LogUtils.e("UniUtil", "小程序是否初始化  " + DCUniMPSDK.getInstance().isInitialize());
            if (!DCUniMPSDK.getInstance().isInitialize()) {
                //需要重新初始化了
                initUni(() -> {
                    openUniApp(context, appId, page, data, isSelf);
                });
                return;
            }
            if (DCUniMPSDK.getInstance().getRuningAppid() != null) {
                //则有正打开的小程序
                if (appId.equals(DCUniMPSDK.getInstance().getRuningAppid())) {
                    //表面打开的是正在运行的
                    com.alibaba.fastjson.JSONObject object = new com.alibaba.fastjson.JSONObject();
                    object.put("page", page);
                    object.put("data", jsonObject);
                    DCUniMPSDK.getInstance().sendUniMPEvent("navigateTo", object);
                    isDelay = 200;
                } else {
                    //是其他小程序正在运行,需要销毁
                    DCUniMPSDK.getInstance().closeCurrentApp();
                }
            }
            JSONObject finalJsonObject = jsonObject;

            Observable.timer(isDelay, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(aLong -> {
                        LogUtils.e("UniUtil", "" + System.currentTimeMillis());
                        DCUniMPSDK.getInstance().startApp(context, appId, null, page, finalJsonObject);
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void initUni(AppApplication.OnUniIntCallBack callBack) {
        AppApplication.get().initUni(callBack);
    }




}
