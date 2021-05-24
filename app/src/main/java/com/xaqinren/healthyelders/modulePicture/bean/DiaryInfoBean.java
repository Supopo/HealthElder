package com.xaqinren.healthyelders.modulePicture.bean;

import com.google.gson.annotations.SerializedName;
import com.xaqinren.healthyelders.moduleLiteav.bean.PublishBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.PublishDesBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.PublishFocusItemBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.PublishSummaryBean;

import java.util.List;

public class DiaryInfoBean {

    @SerializedName("id")
    public String id;
    @SerializedName("createdAt")
    public String createdAt;
    @SerializedName("merchantId")
    public String merchantId;
    @SerializedName("coverUrl")
    public String coverUrl;
    @SerializedName("bannerImages")
    public List<BannerImagesDTO> bannerImages;
    @SerializedName("title")
    public String title;
    @SerializedName("releaseStatus")
    public String releaseStatus;
    @SerializedName("summary")
    public PublishSummaryBean summary;
    @SerializedName("favoriteCount")
    public Integer favoriteCount;
    @SerializedName("commentCount")
    public Integer commentCount;
    @SerializedName("shareCount")
    public Integer shareCount;
    @SerializedName("userId")
    public String userId;
    @SerializedName("nickname")
    public String nickname;
    @SerializedName("avatarUrl")
    public String avatarUrl;
    @SerializedName("province")
    public String province;
    @SerializedName("city")
    public String city;
    @SerializedName("district")
    public String district;
    @SerializedName("businessHub")
    public Object businessHub;
    @SerializedName("latitude")
    public Double latitude;
    @SerializedName("longitude")
    public Double longitude;
    @SerializedName("address")
    public String address;
    @SerializedName("canRecommendFriends")
    public Boolean canRecommendFriends;
    @SerializedName("del")
    public Boolean del;
    @SerializedName("hasFavorite")
    public Boolean hasFavorite;
    @SerializedName("contentViewAuth")
    public Object contentViewAuth;
    @SerializedName("refuseUserIds")
    public Object refuseUserIds;

    public static class SummaryDTO {
        @SerializedName("topicList")
        public List<?> topicList;
        @SerializedName("content")
        public String content;
        @SerializedName("atList")
        public List<?> atList;
        @SerializedName("publishFocusItemBeans")
        public List<?> publishFocusItemBeans;
    }

    public static class BannerImagesDTO {
        @SerializedName("url")
        public String url;
    }
}
