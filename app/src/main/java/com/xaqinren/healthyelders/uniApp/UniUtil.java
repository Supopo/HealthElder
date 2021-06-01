package com.xaqinren.healthyelders.uniApp;

import android.content.Context;

import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.uniApp.widget.LoadingView;
import com.xaqinren.healthyelders.uniApp.widget.SplashView;
import com.xaqinren.healthyelders.utils.LogUtils;

import org.json.JSONObject;

import io.dcloud.feature.sdk.DCUniMPSDK;

public class UniUtil {

    public static void openUniApp(Context context,String appId,String page , JSONObject data,boolean isSelf) {
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

            if (page != null) {
                DCUniMPSDK.getInstance().startApp(context, appId, viewCls, page, jsonObject);
            }else{
                DCUniMPSDK.getInstance().startApp(context, appId, viewCls,  jsonObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
