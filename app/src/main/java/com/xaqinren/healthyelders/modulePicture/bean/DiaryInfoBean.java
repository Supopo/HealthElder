package com.xaqinren.healthyelders.modulePicture.bean;

import com.xaqinren.healthyelders.moduleHome.bean.ShareBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.PublishSummaryBean;

import java.util.List;

public class DiaryInfoBean {

    public String id;
    public long createdAt;
    public String merchantId;
    public String coverUrl;
    public List<BannerImagesDTO> bannerImages;
    public String title;
    public String releaseStatus;
    public PublishSummaryBean summary;
    public int favoriteCount;
    public int commentCount;
    public int shareCount;
    public String userId;
    public String nickname;
    public String avatarUrl;
    public String province;
    public String city;
    public String district;
    public Object businessHub;
    public double latitude;
    public double longitude;
    public String address;
    public boolean canRecommendFriends;
    public boolean del;
    public boolean hasFavorite;
    public boolean hasAttention;
    public String creationViewAuth;
    public String approvalStatus;//审核字段
    public Object refuseUserIds;
    public ShareBean share;
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
