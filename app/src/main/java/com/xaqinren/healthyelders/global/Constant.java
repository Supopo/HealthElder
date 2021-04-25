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
    public static String API_HEADER = "Bearer ";//用户接口传token时候前面拼接的字符串

    //生成roomId
    public static String getRoomId(String userId) {
        return "room_" + userId;
    }

    public static String auth = "Basic MTM3ODYxODg2MDYxMzE0NDU3NjprMnF3c2sxNTMzZm1jMGhqdHJiYWowcjk=";
    public static String mid = "1378618860613144576";




    /* SPUtils 存储 KEY */
    /**
     * 微信登录返回的数据
     */
    public static final String SP_KEY_WX_INFO = "wxUserInfo";
    /**
     * 登录token
     */
    public static final String SP_KEY_TOKEN_INFO = "tokenInfo";
    /**
     * 登录用户
     */
    public static final String SP_KEY_LOGIN_USER = "loginUser";


}
