package com.xaqinren.healthyelders.moduleLiteav.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.google.gson.annotations.SerializedName;
import com.xaqinren.healthyelders.widget.share.IShareUser;

import java.io.Serializable;

public class LiteAvUserBean implements MultiItemEntity , Serializable , IShareUser {
    private String id;
    private String userId;
    private String attentionUserId;
    public int viewType;
    public boolean isSel;
    public boolean readOnly;
    public String identity;
    public String attentionSource;
    public String hasLive;
    public String merchantId;
    public String createdAt;
    public UserInfoBean userInfo;
    public UserInfoBean attentionUserInfo;
    private String nickname;
    private String avatarUrl;


    public LiteAvUserBean() {

    }

    public String getId() {
        if (attentionUserId != null) {
            return attentionUserId;
        }
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public void setId(String id) {
        this.attentionUserId = id;
    }

    @Override
    public int getItemType() {
        return viewType;
    }

    @Override
    public String getAvatar() {
        if (attentionUserInfo == null) return avatarUrl;
        return attentionUserInfo.avatarUrl;
    }

    public void setAvatar(String avatar) {
        if (attentionUserInfo == null) attentionUserInfo = new UserInfoBean();
        attentionUserInfo.avatarUrl = avatar;
    }

    @Override
    public String getName() {
        if (attentionUserInfo == null) return nickname;
        return attentionUserInfo.nickname;
    }

    public void setName(String name) {
        if (attentionUserInfo == null) attentionUserInfo = new UserInfoBean();
        attentionUserInfo.nickname = name;
    }

    @Override
    public Object getKey() {
        return userId;
    }

}
