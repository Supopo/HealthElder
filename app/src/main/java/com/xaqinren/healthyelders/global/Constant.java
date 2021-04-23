package com.xaqinren.healthyelders.global;

/**
 * Description: 常量
 */
public class Constant {
    public static boolean DEBUG = true;
    public static String baseUrl = "http://test.hjyiyuanjiankang.com/";
    public static String PageUrl = "WEB_URL";
    public static String PageTitle = "WEB_TITLE";
    public static final int NET_SPEED = 10086;
    public static String IM_APPID = "IM_APPID_M";
    public static String IM_USERID = "IM_USERID_M";
    public static String IM_SIG = "IM_SIG_M";
    public static String USER_ID = "user_id";

    //生成roomId
    public static String getRoomId(String userId) {
        return "room_" + userId;
    }
}
