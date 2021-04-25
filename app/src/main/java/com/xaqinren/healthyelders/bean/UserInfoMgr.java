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


    public UserInfoBean getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfoBean userInfo) {
        this.userInfo = userInfo;
    }

}
