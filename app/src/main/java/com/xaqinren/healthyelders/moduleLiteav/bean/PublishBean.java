package com.xaqinren.healthyelders.moduleLiteav.bean;

import com.xaqinren.healthyelders.widget.LiteAvOpenModePopupWindow;

import java.util.ArrayList;
import java.util.List;

/**
 * 发布bean
 */
public class PublishBean {
    public String shortVideoName;
    public String shortVideoUrl;
    public String shortVideoCover;
    public String shortVideoAuth;
    public String shortVideoId;
    public PublishSummaryBean summary;
    public String latitude;
    public String longitude;
    public String address;
    public String musicId;
    public String musicName;
    public boolean canRecommendFriends;
    public String province;
    public String city;
    public String district;
    public List<String> refuseUserIds = new ArrayList<>();


    public static final String OPEN = "OPEN";
    public static final String FRIENDS = "FRIENDS";
    public static final String PRIVATE  = "PRIVATE";
    public static final String PARTIALLY_OPEN = "PARTIALLY_OPEN";

    public String getMode(int mode) {
        String a = null;
        if (mode == LiteAvOpenModePopupWindow.OPEN_MODE) {
            a = OPEN;
        } else if (mode == LiteAvOpenModePopupWindow.FRIEND_MODE) {
            a = FRIENDS;
        } else if (mode == LiteAvOpenModePopupWindow.PRIVATE_MODE) {
            a = PRIVATE;
        } else if (mode == LiteAvOpenModePopupWindow.HIDE_MODE) {
            a = PARTIALLY_OPEN;
        }
        return a;
    }

}
