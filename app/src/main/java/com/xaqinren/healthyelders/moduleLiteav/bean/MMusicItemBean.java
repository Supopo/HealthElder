package com.xaqinren.healthyelders.moduleLiteav.bean;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.google.gson.annotations.SerializedName;

public class MMusicItemBean implements MultiItemEntity {
    public Object createdAt;
    public Object merchantId;
    public String name;
    public String coverUrl;
    public String musicUrl;
    public String authorName;
    public String sheetId;
    public Integer useCount;
    public Integer sortOrder;
    public Boolean del;
    public Boolean hasFavorite;
    public int duration;
    public long size;
    public Boolean hasRecommend;
    public Object musicStatus;
    private boolean isOperation;

    private String id;
    public int itemType;
    public String localPath;
    public String musicId;


    public int myMusicStatus = 0;//0常态 1加载中 2播放中



    public MMusicItemBean() {
    }

    public MMusicItemBean(String id) {
        this.id = id;
    }

    public MMusicItemBean(int itemType) {
        this.itemType = itemType;
    }

    public boolean isOperation() {
        return isOperation;
    }

    public void setOperation(boolean operation) {
        isOperation = operation;
    }

    public String getId() {
        if (musicId != null) {
            return musicId;
        }
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public MMusicItemBean cloneThis()  {
        MMusicItemBean bean = new MMusicItemBean();
        String json = JSON.toJSONString(this);
        bean = JSON.parseObject(json, MMusicItemBean.class);
        return bean;
    }
}
