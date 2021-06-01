package com.xaqinren.healthyelders.modulePicture.bean;

import com.google.gson.annotations.SerializedName;
import com.xaqinren.healthyelders.moduleLiteav.bean.PublishBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.PublishDesBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.PublishFocusItemBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.PublishSummaryBean;

import java.util.List;

public class DiaryInfoBean {

    public String id;
    public String createdAt;
    public String merchantId;
    public String coverUrl;
    public List<BannerImagesDTO> bannerImages;
    public String title;
    public String releaseStatus;
    public PublishSummaryBean summary;
    public Integer favoriteCount;
    public Integer commentCount;
    public Integer shareCount;
    public String userId;
    public String nickname;
    public String avatarUrl;
    public String province;
    public String city;
    public String district;
    public Object businessHub;
    public Double latitude;
    public Double longitude;
    public String address;
    public Boolean canRecommendFriends;
    public Boolean del;
    public Boolean hasFavorite;
    public Object contentViewAuth;
    public Object refuseUserIds;

    public static class SummaryDTO {
        public List<?> topicList;
        public String content;
        public List<?> atList;
        public List<?> publishFocusItemBeans;
    }

    public static class BannerImagesDTO {
        public String url;
    }
}
