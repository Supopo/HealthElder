package com.xaqinren.healthyelders.moduleLiteav.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.google.gson.annotations.SerializedName;
import com.xaqinren.healthyelders.widget.share.IShareUser;

import java.io.Serializable;

public class LiteAvUserBean implements MultiItemEntity , Serializable , IShareUser {
    public String nickname;
    public String avatarUrl;
    public long id;
    public long userId;
    public int viewType;
    public boolean isSel;
    public boolean readOnly;
    public String identity;

    public LiteAvUserBean() {

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
