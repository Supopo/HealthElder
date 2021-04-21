package com.xaqinren.healthyelders.global;

/**
 * Description: 常量
 */
public class Constant {
    public static boolean DEBUG = true;
    public static String baseUrl = "https://gank.io/api/v2/";
    public static String PageUrl = "WEB_URL";
    public static String PageTitle = "WEB_TITLE";
    private static final String ROOM_NAME = "room_";
    public static final int NET_SPEED = 10086;
    public static String IM_APPID = "IM_APPID_M";
    public static String IM_USERID = "IM_USERID_M";
    public static String IM_SIG = "IM_SIG_M";
    public static String USER_ID = "user_id";


    //生成roomId
    public static String getRoomId(String userId) {
        return ROOM_NAME + userId;
    }
}
