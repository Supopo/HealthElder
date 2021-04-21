package com.xaqinren.healthyelders.moduleZhiBo.liveRoom;


/**
 * Module:   TCConstants
 * Function: 直播定义常量的类
 */
public class TCConstants {
    /**
     * 常量字符串
     */
    public static final String USER_ID = "user_id";
    public static final String USER_NICK = "user_nick";
    public static final String USER_HEADPIC = "user_headpic";
    public static final String USER_LOC = "user_location";

    //主播退出广播字段
    public static final String EXIT_APP = "EXIT_APP";

    public static final int USER_INFO_MAXLEN = 20;
    public static final int TV_TITLE_MAX_LEN = 30;
    public static final int NICKNAME_MAX_LEN = 20;

    //直播类型
    public static final int RECORD_TYPE_CAMERA = 991;
    public static final int RECORD_TYPE_SCREEN = 992;

    //直播端右下角listview显示type
    public static final int SHOW_TYPE = 111;//展示类型消息
    public static final int LIWU_TYPE = 4;
    public static final int TEXT_TYPE = 0;
    public static final int MEMBER_ENTER = 1;
    public static final int MEMBER_EXIT = 2;
    public static final int PRAISE = 3;

    public static final int LOCATION_PERMISSION_REQ_CODE = 1;
    public static final int WRITE_PERMISSION_REQ_CODE = 2;
    public static final int CAMERA_PERMISSION_REQ_CODE = 3;

    public static final String LIVESCENE_ID = "liveScene_id";
    public static final String LIVE_ID = "live_id";
    public static final String ROOM_ID = "room_id";
    public static final String SET_FANZHUAN = "is_fanzhuan";
    public static final String ROOM_TITLE = "room_title";
    public static final String COVER_PIC = "cover_pic";
    public static final String GROUP_ID = "group_id";
    public static final String PLAY_URL = "play_url";
    public static final String PLAY_TYPE = "play_type";
    public static final String PUSHER_AVATAR = "pusher_avatar";
    public static final String PUSHER_ID = "pusher_id";
    public static final String PUSHER_NAME = "pusher_name";
    public static final String MEMBER_COUNT = "member_count";
    public static final String HEART_COUNT = "heart_count";
    public static final String FILE_ID = "file_id";
    public static final String TIMESTAMP = "timestamp";
    public static final String ACTIVITY_RESULT = "activity_result";

    public static final String PUSH_URL = "push_url";
    public static final String PULL_URL = "pull_url";

    public static final String IS_MORE_LINK = "is_moreLink";
    public static final String IS_LM = "is_lianmai";
    public static final String IS_JY = "is_jinyan";
    public static final String IS_FOLLOW = "is_follow";
    public static final String IS_DB = "is_dianbo";//是否点播
    public static final String IS_HP = "is_henping";//横竖屏判断
    public static final String IS_TEACHER = "is_teacher";//是否讲师


    /**
     * IM 互动消息类型
     */
    public static final int IMCMD_PAILN_TEXT = 1;   // 文本消息
    public static final int IMCMD_ENTER_LIVE = 2;   // 用户加入直播
    public static final int IMCMD_EXIT_LIVE = 3;   // 用户退出直播
    public static final int IMCMD_SENDPRESENT = 4;   // 礼物消息
    public static final int IMCMD_DANMU = 5;   // 弹幕消息
    public static final int IMCMD_SHOW_GOODS = 6;   // 商品讲解
    public static final int IMCMD_SHOW_GOODS_CANCEL = 7;   // 取消讲解
    public static final int IMCMD_FORBIDDER_TALK = 8;   // 禁言
    public static final int IMCMD_CANCEL_FORBIDDER_TALK = 9;   // 取消禁言
    public static final int IMCMD_PUT_BLACK = 10;   // 拉黑
    public static final int IMCMD_SHOW_MIC = 11;   // 观众可以连麦
    public static final int IMCMD_TO_LINK = 19;   // 用户去申请连麦
    public static final int IMCMD_FORBIDDEN_MIC = 12;   // 观众禁止连麦
    public static final int IMCMD_CANCEL_LINK = 13;   //观众取消连麦
    public static final int IMCMD_REFUSE_LINK = 14;   //观众拒绝连麦
    public static final int IMCMD_INVITE_LINK = 15;   //主播邀请连麦
    public static final int IMCMD_OPEN_MORE_LINK = 16;   //主播开启多人连麦
    public static final int IMCMD_CLOSE_MORE_LINK = 17;   //主播关闭多人连麦

    //每添加一种状态都要在下面这两个方法中添加判断
    //MLVBLiveRoomImpl - onC2CCustomMessage
    //IMMessageMgr - onNewMessages - case Custom


    public static final int IMCMD_LIKE = 18;   //点赞消息
    public static final int IMCMD_CANCEL_PUT_BLACK = 20;   //取消拉黑
    public static final int IMCMD_ZB_LINKING = 21;   //主播进入连麦

    public static final int IMCMD_MORE_LINK_YQ = 22;   //主播邀请并通知上麦者上麦以及座位号
    public static final int IMCMD_MORE_LINK_JS = 24;   //主播接受上麦者上麦以及座位号 -1表示没位置
    public static final int IMCMD_MORE_LINK_NUM = 23;   //上麦者告诉主播自己想上麦的座位号
    public static final int IMCMD_RESH_HOME_INFO = 25;   //通知观众们刷新信息
    public static final int IMCMD_MORE_ANCHOR_JY = 26;   //多人连麦-静音
    public static final int IMCMD_MORE_ANCHOR_QXJY = 27;   //多人连麦-取消静音


    public static final int MALE = 0;
    public static final int FEMALE = 1;


    // ELK统计上报事件
    public static final String ELK_ACTION_INSTALL = "install";
    public static final String ELK_ACTION_START_UP = "startup";
    public static final String ELK_ACTION_STAY_TIME = "stay_time";
    public static final String ELK_ACTION_REGISTER = "register";
    public static final String ELK_ACTION_LOGIN = "login";

    public static final String ELK_ACTION_VOD_PLAY = "vod_play";
    public static final String ELK_ACTION_VOD_PLAY_DURATION = "vod_play_duration";
    public static final String ELK_ACTION_LIVE_PLAY = "live_play";
    public static final String ELK_ACTION_LIVE_PLAY_DURATION = "live_play_duration";

    public static final String ELK_ACTION_CAMERA_PUSH = "camera_push";
    public static final String ELK_ACTION_CAMERA_PUSH_DURATION = "camera_push_duration";
    public static final String ELK_ACTION_SCREEN_PUSH = "screen_push";
    public static final String ELK_ACTION_SCREEN_PUSH_DURATION = "screen_push_duration";
}

