package com.xaqinren.healthyelders.bean;

import android.annotation.SuppressLint;
import android.content.Context;

import com.xaqinren.healthyelders.moduleLogin.bean.UserInfoBean;

/**
 * Created by Lee. on 2021/4/25.
 * 全局用户信息管理类
 */
public class UserInfoMgr {

    private static class UserInfoMgrIns {
        @SuppressLint("StaticFieldLeak")
        private static final UserInfoMgr instance = new UserInfoMgr();
    }

    public static UserInfoMgr getInstance() {
        return UserInfoMgrIns.instance;
    }

    private UserInfoMgr() {
    }

    public void initContext(Context context) {
        this.context = context.getApplicationContext();
    }

    private Context context;
    private UserInfoBean userInfo;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getHttpToken() {
        return httpToken;
    }

    public void setHttpToken(String httpToken) {
        this.httpToken = httpToken;
    }

    private String httpToken;
    private String accessToken;
    private String userSig;
    private String sdkAppID;


    public String getUserSig() {
        return userSig;
    }

    public void setUserSig(String userSig) {
        this.userSig = userSig;
    }

    public String getSdkAppID() {
        return sdkAppID;
    }

    public void setSdkAppID(String sdkAppID) {
        this.sdkAppID = sdkAppID;
    }

    public UserInfoBean getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfoBean userInfo) {
        this.userInfo = userInfo;
    }

}
