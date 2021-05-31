package com.xaqinren.healthyelders.moduleHome.bean;

import android.text.TextUtils;
import android.util.Log;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.global.AppApplication;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleLiteav.bean.PublishAtBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.PublishFocusItemBean;
import com.xaqinren.healthyelders.utils.ColorsUtils;
import com.xaqinren.healthyelders.utils.DateUtils;
import com.xaqinren.healthyelders.utils.LogUtils;
import com.xaqinren.healthyelders.utils.Num2TextUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lee. on 2021/5/12.
 * 首页小视频
 */
public class VideoInfo implements Serializable, MultiItemEntity {
    public String id;//": "1392445338140332032",
    public String createdAt;//": "2021-05-12 19:43:33",
    public String merchantId;//": "1378618860613144576",
    public String resourceId;//": "1392445266996514816",
    public String coverUrl;//":
    public String resourceUrl;//":
    public String title;//": "小视频",
    public String introduce;//":
    public String userId;//": "1385879201982189568",
    public String nickname;//": "夕夜空",
    public String avatarUrl;//":

    public int getFavoriteCount() {
        if (TextUtils.isEmpty(favoriteCount) || Integer.parseInt(favoriteCount) < 0) {
            return 0;
        }
        return Integer.parseInt(favoriteCount);
    }

    public int getCommentCount() {
        if (TextUtils.isEmpty(commentCount) || Integer.parseInt(commentCount) < 0) {
            return 0;
        }
        return Integer.parseInt(commentCount);
    }

    public int getShareCount() {
        if (TextUtils.isEmpty(shareCount) || Integer.parseInt(shareCount) < 0) {
            return 0;
        }
        return Integer.parseInt(shareCount);
    }

    public String favoriteCount;//": "0",
    public String commentCount;//": "0",
    public String shareCount;//": "0",
    public String resourceType;//": "VIDEO", LIVE,VIDEO,USER_DIARY
    public String location;//": "34.233237,108.946006",
    public String address;//": "西安北郊大学城",
    public String externalId;//": "5285890818169432613"
    public String liveRoomId;//":
    public String liveRoomCode;//":
    public String hasIntroduce;//":
    public String musicId;//":
    public String musicIcon;//":
    public String musicName;//":

    public VideoInfo share;//":
    public String roomPassword;//":
    public String refuseUserIds;//":
    public String approvalStatus;//":
    public String creationViewAuth;//":
    public String businessHub;//":
    public String district;//":
    public String city;//":
    public String province;//":

    public String recommendedCode;//":
    public int fansCount;

    public String getFansCount() {
        return "粉丝 " + Num2TextUtil.sNum2Text2(String.valueOf(fansCount));
    }

    public String getRecommendedCode() {
        if (!TextUtils.isEmpty(recommendedCode)) {
            return "健康号 " + recommendedCode;
        }
        return "";
    }

    public String storeId;//": "1329045982251323392",
    public String storeLogo;//": null,
    public String storeName;//": null,
    public String totalSoldCount;//": "0",
    public double minSalesPrice;//": 302.4,

    public String getMinSalesPrice() {
        return "¥" + minSalesPrice;
    }

    public String getTotalSoldCount() {
        return "库存" + totalSoldCount;
    }

    public boolean hasFavorite;
    public boolean hasAttention;
    public boolean hasLive;

    public boolean isDraft;//草稿箱
    public int draftCount;//草稿箱数量

    public String getDraftCount() {
        return "草稿箱 +" + draftCount;
    }

    //"resourceType": "VIDEO",需要
    public String content;
    public List<String> topicList;
    public List<PublishAtBean> atList;
    public List<PublishFocusItemBean> publishFocusItemBeans;

    public boolean showFollow() {
        if (UserInfoMgr.getInstance().getUserInfo() == null || TextUtils.isEmpty(userId)) {
            //未登录
            return true;
        }
        if (userId.equals(UserInfoMgr.getInstance().getUserInfo().getId())) {
            return false;
        }
        if (hasAttention) {
            return false;
        }

        return true;
    }

    public boolean showSeachFollow() {
        if (id.equals(UserInfoMgr.getInstance().getUserInfo().getId())) {
            return false;
        }
        return true;
    }


    public String getLiveRoomName() {
        if (TextUtils.isEmpty(title)) {
            return nickname + "的直播间";
        }
        return title;
    }

    public int getVideoType() {
        if (TextUtils.isEmpty(resourceType)) {
            return 0;
        }

        if (resourceType.equals("LIVE")) {
            return 2;
        } else if (resourceType.equals("VIDEO")) {
            return 1;
        } else if (resourceType.equals("USER_DIARY")) {
            return 3;
        }
        return 0;
    }

    public String getMusicName() {
        if (TextUtils.isEmpty(musicName)) {
            return "视频原声 - @" + nickname;
        }
        return musicName;
    }


    public String getFavoriteCountEx() {
        if (favoriteCount == null || favoriteCount.equals("0")) {
            return "点赞";
        }
        return Num2TextUtil.sNum2Text2(favoriteCount);
    }

    public String getCommentCountEx() {
        if (commentCount == null || commentCount.equals("0")) {
            return "写评论";
        }
        return Num2TextUtil.sNum2Text2(commentCount);
    }

    public String getShareCountEx() {
        if (shareCount == null || shareCount.equals("0")) {
            return "分享";
        }
        return Num2TextUtil.sNum2Text2(shareCount);
    }

    public int getPlaceholderRes() {
        return ColorsUtils.randomColor();
    }

    public String getDistance() {
        if (AppApplication.get().getmLat() == 0) {
            return "";
        }
        if (AppApplication.get().getmLon() == 0) {
            return "";
        }
        if (!TextUtils.isEmpty(location)) {
            String[] loc = location.split(",");
            if (loc.length == 2) {
                double lat = Double.parseDouble(loc[0]);
                double lon = Double.parseDouble(loc[1]);
                LatLng latLng1 = new LatLng(AppApplication.get().getmLat(), AppApplication.get().getmLon());//我的坐标
                LatLng latLng2 = new LatLng(lat, lon);
                int mm = (int) AMapUtils.calculateLineDistance(latLng1, latLng2);
                return Num2TextUtil.m2Km(mm);
            }

        }
        return "";
    }

    public boolean isArticle() {
        if (resourceType.equals("USER_DIARY")) {
            return true;
        }
        return false;
    }

    public boolean showLocInfo() {
        if (isArticle()) {
            return false;
        }
        if (!TextUtils.isEmpty(address)) {
            return true;
        }

        return false;
    }

    public String getMonth() {
        return DateUtils.getMonth(createdAt);
    }

    public String getDay() {
        return DateUtils.getDay(createdAt);
    }

    public String getRelativeTime() {
        return DateUtils.getRelativeTime(createdAt);
    }

    @Override
    public int getItemType() {
        if (resourceType.equals(Constant.REQ_TAG_SP)) {
            return 0;
        } else if (resourceType.equals(Constant.REQ_TAG_YH)) {
            return 1;
        } else if (resourceType.equals(Constant.REQ_TAG_GOODS)) {
            return 2;
        } else if (resourceType.equals(Constant.REQ_TAG_ZB)) {
            return 3;
        } else if (resourceType.equals(Constant.REQ_TAG_TW)) {
            return 4;
        }
        return 0;
    }

}
