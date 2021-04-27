package com.xaqinren.healthyelders.moduleZhiBo.bean;

/**
 * Module:   TCUserInfo
 * <p>
 * Function: 用户基本信息封装
 */
public class TCUserInfo {

    public String userid;       // userId
    public String nickname;     // 昵称
    public String avatar;       // 头像链接
    public Boolean isForbidden = false;
    public String giftIcon;//

    public String getGiftName() {
        return giftName;
    }

    public void setGiftName(String giftName) {
        this.giftName = giftName;
    }

    public String giftName;

    public String getGiftIcon() {
        return giftIcon;
    }

    public void setGiftIcon(String giftIcon) {
        this.giftIcon = giftIcon;
    }


    public TCUserInfo(String userId, String nickname, String avatar) {
        this.userid = userId;
        this.nickname = nickname;
        this.avatar = avatar;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Boolean getForbidden() {
        return isForbidden;
    }

    public void setForbidden(Boolean forbidden) {
        isForbidden = forbidden;
    }
}
