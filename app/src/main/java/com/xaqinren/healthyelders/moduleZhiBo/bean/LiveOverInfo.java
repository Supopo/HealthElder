package com.xaqinren.healthyelders.moduleZhiBo.bean;

import com.xaqinren.healthyelders.utils.Num2TextUtil;

import java.util.List;

/**
 * Created by Lee. on 2021/4/29.
 * 直播间结束统计信息
 */
public class LiveOverInfo {
    public String liveTimeStr;//"null-19:32",
    public String beyondPercentage;// "共0分钟",
    public String liveDurationStr;// "34.15301099637668%",
    public String giftCount;//
    public String userCount;//
    public String newFansCount;//
    public String payUserCount;//
    public String commentUserCount;//
    public String favoriteCount;//
    public List<ZBUserListBean> liveRoomUsers;//

    public String nickname;//
    public String avatarUrl;//

    public int getLiveRoomUsersSize() {
        if (liveRoomUsers == null) {
            return 0;
        }
        return liveRoomUsers.size();
    }

    public ZBUserListBean getTop1User() {
        if (liveRoomUsers != null && liveRoomUsers.size() > 0) {
            return liveRoomUsers.get(0);
        }
        ZBUserListBean bean = new ZBUserListBean();
        bean.nickname = "";
        bean.contribution = "0";
        bean.avatarUrl = "";
        return bean;
    }

    public ZBUserListBean getTop2User() {
        if (liveRoomUsers != null && liveRoomUsers.size() > 1) {
            return liveRoomUsers.get(1);
        }
        ZBUserListBean bean = new ZBUserListBean();
        bean.nickname = "";
        bean.contribution = "0";
        bean.avatarUrl = "";
        return bean;
    }

    public ZBUserListBean getTop3User() {
        if (liveRoomUsers != null && liveRoomUsers.size() > 2) {
            return liveRoomUsers.get(2);
        }
        ZBUserListBean bean = new ZBUserListBean();
        bean.nickname = "";
        bean.contribution = "0";
        bean.avatarUrl = "";
        return bean;
    }


    public String getUserCount() {
        return "本次直播观看人数" + Num2TextUtil.sNum2Text2(userCount);
    }
}
