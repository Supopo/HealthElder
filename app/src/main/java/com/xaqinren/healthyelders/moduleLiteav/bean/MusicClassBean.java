package com.xaqinren.healthyelders.moduleLiteav.bean;

import com.google.gson.annotations.SerializedName;

public class MusicClassBean {
    @SerializedName("id")
    public String id;
    @SerializedName("createdAt")
    public String createdAt;
    @SerializedName("merchantId")
    public String merchantId;
    @SerializedName("menuName")
    public String menuName;
    @SerializedName("subMenuName")
    public Object subMenuName;
    @SerializedName("backgroundColor")
    public String backgroundColor;
    @SerializedName("fontColor")
    public String fontColor;
    @SerializedName("subFontColor")
    public Object subFontColor;
    @SerializedName("icon")
    public String icon;
    @SerializedName("jumpUrl")
    public String jumpUrl;
    @SerializedName("jumpType")
    public String jumpType;
    @SerializedName("onlyShowImage")
    public Boolean onlyShowImage;
    @SerializedName("imageUrl")
    public Object imageUrl;
    @SerializedName("sortOrder")
    public Integer sortOrder;
    @SerializedName("showChannel")
    public String showChannel;
}
