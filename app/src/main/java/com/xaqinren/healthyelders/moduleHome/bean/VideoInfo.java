package com.xaqinren.healthyelders.moduleHome.bean;

import android.text.TextUtils;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.xaqinren.healthyelders.global.AppApplication;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleLiteav.bean.PublishAtBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.PublishFocusItemBean;
import com.xaqinren.healthyelders.utils.ColorsUtils;
import com.xaqinren.healthyelders.utils.LogUtils;
import com.xaqinren.healthyelders.utils.Num2TextUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lee. on 2021/5/12.
 * 首页小视频
 */
public class VideoInfo implements Serializable {
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
        if (TextUtils.isEmpty(favoriteCount)) {
            return 0;
        }
        return Integer.parseInt(favoriteCount);
    }

    public String favoriteCount;//": "0",
    public String commentCount;//": "0",
    public String shareCount;//": "0",
    public String resourceType;//": "VIDEO",
    public String location;//": "34.233237,108.946006",
    public String address;//": "西安北郊大学城",
    public String externalId;//": "5285890818169432613"
    public String liveRoomId;//":
    public String liveRoomCode;//":
    public String hasIntroduce;//":
    public String musicId;//":
    public String musicIcon;//":
    public String musicName;//":

    public boolean hasFavorite;
    public boolean hasAttention;
    public boolean hasLive;

    //"resourceType": "VIDEO",需要
    public String content;
    public List<String> topicList;
    public List<PublishAtBean> atList;
    public List<PublishFocusItemBean> publishFocusItemBeans;


    public String getLiveRoomName() {
        if (TextUtils.isEmpty(title)) {
            return nickname + "的直播间";
        }
        return title;
    }

    public int getVideoType() {
        if (resourceType.equals("LIVE")) {
            return 2;
        } else if (resourceType.equals("VIDEO")) {
            return 1;
        } else if (resourceType.equals("ARTICLE")) {
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
        if (resourceType.equals("ARTICLE")) {
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
}
