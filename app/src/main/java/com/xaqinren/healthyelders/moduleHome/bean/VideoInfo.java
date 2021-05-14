package com.xaqinren.healthyelders.moduleHome.bean;

import com.xaqinren.healthyelders.utils.Num2TextUtil;

/**
 * Created by Lee. on 2021/5/12.
 * 首页小视频
 */
public class VideoInfo {
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
    public String favoriteCount;//": "0",
    public String commentCount;//": "0",
    public String shareCount;//": "0",
    public String resourceType;//": "VIDEO",
    public String location;//": "34.233237,108.946006",
    public String address;//": "西安北郊大学城",
    public String externalId;//": "5285890818169432613"
    public String liveRoomId;//":
    public String liveRoomCode;//":
    public String musicId;//":
    public String musicName;//":

    public String getFavoriteCountEx(){
        return Num2TextUtil.sNum2Text2(favoriteCount);
    }

    public String getCommentCountEx(){
        return Num2TextUtil.sNum2Text2(commentCount);
    }

    public String getShareCountEx(){
        return Num2TextUtil.sNum2Text2(shareCount);
    }

}
