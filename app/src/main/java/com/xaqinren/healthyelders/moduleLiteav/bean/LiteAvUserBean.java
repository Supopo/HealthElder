package com.xaqinren.healthyelders.moduleLiteav.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.xaqinren.healthyelders.widget.share.IShareUser;

import java.io.Serializable;

public class LiteAvUserBean implements MultiItemEntity , Serializable , IShareUser {
    public String nickname;
    public String avatarUrl;
    public double id;
    public long userId;
    public int viewType;
    public boolean isSel;
    public boolean readOnly;

    public LiteAvUserBean() {
    }

    public LiteAvUserBean(String name, String avatar, double id) {
        this.nickname = name;
        this.avatarUrl = avatar;
        this.id = id;
    }

    @Override
    public int getItemType() {
        return viewType;
    }

    @Override
    public String getAvatar() {
        return avatarUrl;
    }

    @Override
    public String getName() {
        return nickname;
    }

    @Override
    public Object getKey() {
        return userId;
    }
}
