package com.xaqinren.healthyelders.moduleLiteav.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public class LiteAvUserBean implements MultiItemEntity {
    public String name;
    public String avatar;
    public int id;
    public int viewType;
    public boolean isSel;
    public boolean readOnly;
    public LiteAvUserBean(String name, String avatar, int id) {
        this.name = name;
        this.avatar = avatar;
        this.id = id;
    }

    @Override
    public int getItemType() {
        return viewType;
    }
}
