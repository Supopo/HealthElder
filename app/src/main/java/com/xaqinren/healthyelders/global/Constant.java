package com.xaqinren.healthyelders.global;

import com.tencent.qcloud.ugckit.utils.Signature;

import java.net.URISyntaxException;

import retrofit2.http.Url;

/**
 * Description: 常量
 */
public class Constant {

    public static final String JKZL_MINI_APP_ID = "__UNI__DFE7692";

    public static String SERVICE_PHONE = "";//客服电话
    public static boolean DEBUG = false;
    public static boolean TEST = false;
    public static boolean ENABLE_CHAT = false;//打开单聊功能

    public static String baseH5Url = "http://h5.hjyiyuanjiankang.com/";//分享短视频-文章-直播
    public static String baseUrl = "https://api.hjyiyuanjiankang.com/";
    public static String testUrl = "http://test.hjyiyuanjiankang.com/";
    public static String debugUrl = "http://192.168.1.113:18080/";

    public static String PageUrl = "WEB_URL";
    public static String PageTitle = "WEB_TITLE";
    public static final int NET_SPEED = 10086;
    public static String IM_APPID = "IM_APPID_M";
    public static String IM_USERID = "IM_USERID_M";
    public static String IM_SIG = "IM_SIG_M";
    public static String USER_ID = "user_id";
    public static String API_HEADER = "Bearer ";//用户接口传token时候前面拼接的字符串
    public static String TAG_LIVE = "Live-zb";
    public static int TIME_RUSH_LIVEINFO = 1500;//刷新直播间信息接口保护时间
    public static int TIME_DIAN_ZAN_WAIT = 2000;//点赞请求保护时间
    public static int MAX_DIAN_ZAN = 2;//点赞限制器 1s 2次
    public static String DraftId = "DRAFT_ID";//草稿箱id
    public static String SearchId = "SEARCH_ID";//搜索id
    public static String SearchGId = "SEARCH_GOODS_ID";//商品搜索id
    public static String SearchVId = "SEARCH_VIDEO_ID";//搜索视频id
    public static int loadVideoSize = 3;
    public static int flashDuration = 800;//骨架图动画时间
    public static int flashCloseDuration = 1000;//骨架图动画时间
    public static int flashAngle = 5;//骨架图动画角度

    public static String REQ_TAG_ZB = "LIVE";//直播
    public static String REQ_TAG_SP = "VIDEO";//视频
    public static String REQ_TAG_TW = "USER_DIARY";//日记
    public static String REQ_TAG_YH = "USER";//用户
    public static String REQ_TAG_GOODS = "COMMODITY";//商品
    public static String REQ_TAG_WZ = "ARTICLE";//文章
    public static String REQ_ZB_TYPE_XN = "PSEUDO_LIVE";//虚拟直播
    public static String REQ_ZB_TYPE_SP = "VIDEO_LIVE";//虚拟直播

    public static String ZP_STATUS_GK = "OPEN";//公开
    public static String ZP_STATUS_HY = "FRIENDS";//好友可见
    public static String ZP_STATUS_SM = "PRIVATE";//私密
    //认证类型
    public static String REN_ZHENG_TYPE = "ren_zheng_type";
    //支付密码
    public static String PASSWORD = "pay_password";

    //支付标记
    public static String PAY_WAY = "PAY_WAY";
    //发布成功标记
    public static String PUBLISH_SUCCESS = "publish";
    //是否从个人中心打开视频/日记列表
    public static String MINE_OPEN = "isMine";

    //生成roomId
    public static String getRoomId(String userId) {
        return "room_" + userId;
    }

    //----OPEN接口需要传-----
    public static String HEADER_DEF = "Basic MTM3ODYxODg2MDYxMzE0NDU3NjprMnF3c2sxNTMzZm1jMGhqdHJiYWowcjk=";
    public static String APP_MID = "1378618860613144576";//微信商户号 登陆需要
    //----OPEN接口需要传-----

    public static String PlayUrl = "LIVE_PLAYURL";
    public static String LiveRoomId = "LIVE_ROOMID";
    public static String LiveInitInfo = "LIVE_INIT_INFO";


    public static final int QR_CODE = 0x65895;


    /* SPUtils 存储 KEY */
    //第一次展示隐私政策弹窗
    public static final String SHOW_POP_WEB = "showPopWeb";
    /**
     * 微信登录返回的数据
     */
    public static final String WX_OPEN_ID = "wxOpenId";
    public static final String SP_KEY_WX_INFO = "wxUserInfo";
    /**
     * 登录token
     */
    public static final String SP_KEY_TOKEN_INFO = "tokenInfo";
    //刷新token用的
    public static final String SP_KEY_REFRESH_TOKEN = "refresh_token";
    /**
     * 登录用户
     */
    public static final String SP_KEY_LOGIN_USER = "loginUser";
    public static final String SP_KEY_SIG_USER = "userSig";
    /**
     * 本地会话
     */
    public static final String SP_KEY_CONVERSATION = "conversation";

    public static final String CONVERSATION_SYS_ID = "11111";//系统
    public static final String CONVERSATION_INT_ID = "22222";//互动
    public static final String CONVERSATION_FANS_ID = "33333";//粉丝
    public static final String CONVERSATION_LIVE_ID = "44444";//直播
    public static final String CONVERSATION_SERVICE_ID = "55555";//服务
    public static final String CONVERSATION_WALLET_ID = "66666";//钱包
    public static final String CONVERSATION_CUSTOMER_SERVICE_ID = "77777";//客服

    //用户协议与隐私政策
    public static final String PRIMARY_RULE = "https://jkzl.hjyiyuanjiankang.com/healthelder/h5/content.html?title=yhxy";

    /**
     * UNI小程序 事件
     */
    public static final String UNI_LOGIN = "login";

    //银行卡列表
    public static final String MINI_BANK_CARD_LIST = "pages_user/wallet/bankCardList?action=redirect";
    //用户协议
    public static final String MINI_AGREEMENT = "pages_base/agreement/userService?action=redirect";
    //充值协议
    public static final String CHONGZHI_AGREEMENT = "pages_base/agreement/paymentService";
    //隐私协议
    public static final String MINI_PRIVACY = "pages_base/agreement/userPrivacy?action=redirect";
    //关于我们
    public static final String MINI_ABOUT_US = "pages_base/agreement/aboutUs?action=redirect";
    //提现 服务条款
    public static final String MINI_WITHDRAW_SERVICE = "pages_base/agreement/withdrawService?action=redirect";
    //直播功能使用条款
    public static final String ZB_SYTK = "pages_base/agreement/liveClause?action=redirect";
    //直播功能行为规范
    public static final String ZB_XWGF = "pages_base/agreement/liveStandard?action=redirect";

    //视频举报
    public static final String VIDEO_REPORT = "pages_base/feedback/reportType?action=redirect&title=视频举报&type=USER_CREATION_REPORT&id=";
    //用户举报
    public static final String USER_REPORT = "pages_base/feedback/reportType?action=redirect&title=用户举报&type=USER_REPORT&id=";
    //日记举报
    public static final String USERDIARY_REPORT = "pages_base/feedback/reportType?action=redirect&title=日记举报&type=USER_CREATION_REPORT&id=";
    //直播举报
    public static final String LIVE_REPORT = "pages_base/feedback/reportType?action=redirect&title=直播举报&type=LIVE_REPORT&id=";
    //直播间添加商品小程序
    public static final String LIVE_ADD_GOODS = "pages_live/addLiveCommodity?action=redirect";


    //腾讯云小视频加防盗链
    public static String setVideoSigUrl(String playUrl) {
        int time = (int) (System.currentTimeMillis() / 1000 + (24 * 60 * 60 * 10));
        String t = Signature.getTimeExpire(time);
        String playToken = null;
        try {
            playToken = Signature.singVideo(playUrl, t);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return playUrl + "?t=" + t + "&sign=" + playToken;
    }

}
