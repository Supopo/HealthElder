package com.xaqinren.healthyelders.global;

import com.alibaba.fastjson.JSON;
import com.xaqinren.healthyelders.moduleLogin.bean.LoginTokenBean;
import com.xaqinren.healthyelders.moduleLogin.bean.UserInfoBean;

import me.goldze.mvvmhabit.utils.SPUtils;
import me.goldze.mvvmhabit.utils.StringUtils;

/**
 * 信息缓存类
 * token,userInfo
 */
public class InfoCache {
    private static InfoCache cache;
    private InfoCache(){}

    private Object lock = "lock";

    public static InfoCache getInstance(){
        if (cache == null) {
            cache = new InfoCache();
        }
        return cache;
    }
    public boolean checkLogin() {
        synchronized (lock){
            String userInfoStr = SPUtils.getInstance().getString(Constant.SP_KEY_LOGIN_USER);
            if (StringUtils.isEmpty(userInfoStr)) return false;
            UserInfoBean userInfoBean = JSON.parseObject(userInfoStr, UserInfoBean.class);
            if (userInfoBean ==null) return false;
            return true;
        }
    }
    public UserInfoBean getLoginUser() {
        synchronized (lock) {
            String userInfoStr = SPUtils.getInstance().getString(Constant.SP_KEY_LOGIN_USER);
            if (StringUtils.isEmpty(userInfoStr)) return null;
            UserInfoBean userInfoBean = JSON.parseObject(userInfoStr, UserInfoBean.class);
            if (userInfoBean == null) return null;
            return userInfoBean;
        }
    }
    public void setLoginUser(UserInfoBean user) {
        synchronized (lock) {
            SPUtils.getInstance().put(Constant.SP_KEY_LOGIN_USER,JSON.toJSONString(user));
        }
    }
    public void setTokenInfo(LoginTokenBean bean) {
        synchronized (lock) {
            SPUtils.getInstance().put(Constant.SP_KEY_TOKEN_INFO,JSON.toJSONString(bean));
        }
    }
    public String getAccessToken() {
        synchronized (lock) {
            String tokenStr = SPUtils.getInstance().getString(Constant.SP_KEY_TOKEN_INFO);
            LoginTokenBean tokenBean = JSON.parseObject(tokenStr, LoginTokenBean.class);
            return tokenBean.access_token;
        }
    }
    public String getRefreshToken() {
        synchronized (lock) {
            String tokenStr = SPUtils.getInstance().getString(Constant.SP_KEY_TOKEN_INFO);
            LoginTokenBean tokenBean = JSON.parseObject(tokenStr, LoginTokenBean.class);
            return tokenBean.refresh_token;
        }
    }
}