package com.xaqinren.healthyelders.modulePicture.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.tencent.bugly.proguard.M;

public class LocalPhotoBean implements MultiItemEntity {
    private String path;
    public int type;
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public int getItemType() {
        return type;
    }
}
