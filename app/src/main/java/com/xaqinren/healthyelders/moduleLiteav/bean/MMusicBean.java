package com.xaqinren.healthyelders.moduleLiteav.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MMusicBean {

    @SerializedName("id")
    public String id;
    @SerializedName("createdAt")
    public Object createdAt;
    @SerializedName("merchantId")
    public Object merchantId;
    @SerializedName("name")
    public String name;
    @SerializedName("iconUrl")
    public String iconUrl;
    @SerializedName("hasRecommendHome")
    public Boolean hasRecommendHome;
    @SerializedName("sortOrder")
    public Integer sortOrder;
    @SerializedName("musicList")
    public List<MMusicItemBean> musicList;
}
