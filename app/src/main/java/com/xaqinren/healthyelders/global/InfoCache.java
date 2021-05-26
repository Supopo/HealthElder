package com.xaqinren.healthyelders.global;

import com.alibaba.fastjson.JSON;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.moduleLogin.bean.LoginTokenBean;
import com.xaqinren.healthyelders.moduleLogin.bean.UserInfoBean;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.MLVBLiveRoom;

import me.goldze.mvvmhabit.utils.SPUtils;
import me.goldze.mvvmhabit.utils.StringUtils;

/**
 * 信息缓存类
 * token,userInfo
 */
public class InfoCache {
    private static InfoCache cache;

    private InfoCache() {
    }

    private Object lock = "lock";

    public static InfoCache getInstance() {
        if (cache == null) {
            cache = new InfoCache();
        }
        return cache;
    }


    public boolean checkLogin() {
        synchronized (lock) {
            String userInfoStr = SPUtils.getInstance().getString(Constant.SP_KEY_LOGIN_USER);
            if (StringUtils.isEmpty(userInfoStr))
                return false;
            UserInfoBean userInfoBean = JSON.parseObject(userInfoStr, UserInfoBean.class);
            if (userInfoBean == null)
                return false;
            return true;
        }
    }



    public UserInfoBean getLoginUser() {
        synchronized (lock) {
            String userInfoStr = SPUtils.getInstance().getString(Constant.SP_KEY_LOGIN_USER);
            if (StringUtils.isEmpty(userInfoStr))
                return null;
            UserInfoBean userInfoBean = JSON.parseObject(userInfoStr, UserInfoBean.class);
            if (userInfoBean == null)
                return null;
            return userInfoBean;
        }
    }

    public void setLoginUser(UserInfoBean user) {
        synchronized (lock) {
            SPUtils.getInstance().put(Constant.SP_KEY_LOGIN_USER, JSON.toJSONString(user));
        }
    }

    public void setTokenInfo(LoginTokenBean bean) {
        synchronized (lock) {
            SPUtils.getInstance().put(Constant.SP_KEY_TOKEN_INFO, JSON.toJSONString(bean));
        }
    }

    public void setUserSig(String userSig) {
        synchronized (lock) {
            SPUtils.getInstance().put(Constant.SP_KEY_SIG_USER, userSig);
        }
    }

    public String getUserSig() {
        synchronized (lock) {
            String userSig = SPUtils.getInstance().getString(Constant.SP_KEY_SIG_USER);
            if (StringUtils.isEmpty(userSig))
                return null;
            return userSig;
        }
    }

    public String getAccessToken() {
        synchronized (lock) {
            String tokenStr = SPUtils.getInstance().getString(Constant.SP_KEY_TOKEN_INFO);
            LoginTokenBean tokenBean = JSON.parseObject(tokenStr, LoginTokenBean.class);
            if (tokenBean == null)
                return null;
            return tokenBean.access_token;
        }
    }

    public String getRefreshToken() {
        synchronized (lock) {
            String tokenStr = SPUtils.getInstance().getString(Constant.SP_KEY_TOKEN_INFO);
            LoginTokenBean tokenBean = JSON.parseObject(tokenStr, LoginTokenBean.class);
            if (tokenBean == null)
                return null;
            return tokenBean.refresh_token;
        }
    }

    public void clearLogin() {
        SPUtils.getInstance().put(Constant.SP_KEY_LOGIN_USER, "");
        SPUtils.getInstance().put(Constant.SP_KEY_TOKEN_INFO, "");
        SPUtils.getInstance().put(Constant.SP_KEY_SIG_USER, "");
        SPUtils.getInstance().put(Constant.SP_KEY_WX_INFO, "");

        UserInfoMgr.getInstance().clearLogin();

        //退出IM
        MLVBLiveRoom.sharedInstance(AppApplication.getContext()).logout();
    }

}
