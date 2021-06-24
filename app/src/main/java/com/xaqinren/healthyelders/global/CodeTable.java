package com.xaqinren.healthyelders.global;

/**
 * Created by Lee. on 2021/4/19.
 * 编码一览表
 */
public class CodeTable {
    public static int CODE_SUCCESS = 100;
    public static int RESH_VIEW_CODE = 99;
    public static int SET_MENU_WHITE = 97;//设置首页底部菜单栏变白
    public static int SET_MENU_COLOR = 9710;//设置首页底部菜单逐渐变白
    public static int SHOW_HOME1_TOP = 971;//展示推荐列表顶部
    public static int TJ_RESH_OVER = 972;//展示推荐列表刷新完毕
    public static int HOME_SEARCHER = 973;//首页搜索
    public static int SET_MENU_TOUMING = 98;//设置首页底部菜单栏透明
    public static int SET_MENU_SUCCESS = 981;//设置首页底部菜单栏透明-成功
    public static int SHOW_TAB_LAYOUT = 96;//展示HomeFragment-TabLayout
    public static int EVENT_HOME = 10101001;
    public static int EVENT_MUSIC_OP = 10086;
    public static int VIDEO_SEND_COMMENT = 1010001;//发送评论
    public static int VIDEO_SEND_COMMENT_OVER = 1010002;//发送完评论
    public static int RESH_MALL_LIST = 992;//刷新商城
    public static int ADD_MALL_LIST = 982;//添加商城数据
    public static int SEARCH_TAG = 961;//搜索消息
    public static int WX_PAY_CODE = 8888;//微信支付消息
    public static int ZHJ_SEND_GIFT = 77771;//直播间礼物发送事件
    public static int ZHJ_SELECT_GIFT = 77772;//直播间礼物点击事件
    public static int MUSIC_BACK = 90001;//直播间礼物点击事件

    /**
     * 成功
     */
    public static final String SUCCESS_CODE = "0000000";
    /**
     * 手机号未绑定
     */
    public static final String NO_PHONE_CODE = "20018";
    /**
     * 网络不稳定
     */
    public static final String UNKNOWN_CODE = "1000001";
    /**
     * 频繁调用
     */
    public static final String NET_LOOP_CODE = "1000002";
    /**
     * 参数错误
     */
    public static final String PARAMS_ERR_CODE = "1000003";
    /**
     * 服务器忙
     */
    public static final String SERVICE_ERR_CODE = "1000004";
    /**
     * token过期
     */
    public static final String TOKEN_ERR_CODE = "040010";
    /**
     * token不存在
     */
    public static final String TOKEN_NO_CODE = "040005";
    public static final String NO_CARD_ID = "20025";
    public static final String NO_PHONE = "20018";


    /* RxBus Event Code */
    /**
     * 微信登录,未绑定手机号
     */
    public static final int WX_UN_LOGIN_PHONE = 0x100012;
    /**
     * token 过期
     */
    public static final int TOKEN_ERR = 0x100013;

    /**
     * 微信登录成功
     */
    public static final int WX_LOGIN_SUCCESS = 0x100014;
    /**
     * 定位成功
     */
    public static final int LOCATION_SUCCESS = 0x100015;
    public static final int NO_CARD = 0x100016;

    /**
     * IM 登录成功
     */
    public static final int IM_LOGIN_SUCCESS = 0x100017;
    /**
     * 退出登录
     */
    public static final int LOGIN_OUT = 0x100018;
    /**
     * 小程序释放成功
     */
    public static final int UNI_RELEASE = 0x100020;
    public static final int UNI_RELEASE_FAIL = 0x100021;

    public static final int MSG_NO_PHONE = 0x100022;

}
