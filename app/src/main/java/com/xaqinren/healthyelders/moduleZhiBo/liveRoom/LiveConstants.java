package com.xaqinren.healthyelders.moduleZhiBo.liveRoom;


/**
 * Module:   LiveConstants
 * Function: 直播间定义常量的类
 */
public class LiveConstants {
    public static final String NIKENAME = "用户";
    public static final String SHOW_ENTER_LIVE = "进入了直播间";
    public static final String SHOW_EXIT_LIVE = "退出了直播间";
    public static final String SHOW_FOLLOW = "关注了主播";
    public static final String SHOW_LAHEI = "您已被主播拉黑";
    public static final String SHOW_JINYAN = "您已被主播禁言";
    public static final String SHOW_QXJINYAN = "您已被主播解除禁言";
    public static final String SHOW_TICHU = "您已被主播踢出房间";
    public static final String GONGGAO_TICHU = "被主播踢出房间";
    public static final String LINK_ONLY_FANS = "当前直播间只允许粉丝连线";
    public static final String LINK_ONLY_INVITE = "当前直播间只允许主播邀请连线";
    public static final String SHOW_KQLM = "当前直播间已允许连麦";
    public static final String SHOW_JZLM = "当前直播间已禁止连麦";
    public static final String SHOW_KQPL = "当前直播间已允许发言";
    public static final String SHOW_JZPL = "当前直播间已禁止发言";
    public static final String SHOW_KQLW = "当前直播间已允许发礼物";
    public static final String SHOW_JZLW = "当前直播间已禁止发礼物";
    public static int WAIT_MORE_LINK = 15;//等待多人连麦占座的时间
    public static int TO_LINK_TIME = 30;//申请连麦的时间

    public static final String LIVE_STATUS_FREE = "FREE";
    public static final String LIVE_STATUS_PK = "PK";
    public static final String LIVE_STATUS_DOUBLE_TALK = "DOUBLE_TALK";
    public static final String LIVE_STATUS_CHAT_ROOM = "CHAT_ROOM";
    public static final String LIVE_STATUS_CONNECTION = "CONNECTION";


    public static final int ZBJ_SET_KQLTS = 101;                  // 直播间设置-开启聊天室
    public static final int ZBJ_SET_GBLTS = 102;                  // 直播间设置-关闭聊天室
    public static final int ZBJ_SET_SUCCESS = 103;                  // 直播间设置-成功
    public static final int ZBJ_MORE_SETTING = 104;                  // 直播间设置-更多 1-开启连麦 2关闭连麦 3开启评论 4禁止评论 5开启礼物 6禁止礼物

    public static final int SETTING_DES = 1003;                  // 直播间介绍设置开关
    public static final int DISMISS_ET = 1002;                  // 直播间发送弹窗关闭
    public static final int SHOW_ET = 1001;                     // 直播间发送弹窗展示
    public static final int SEND_MSG = 10011;                   // 直播间发送消息
    public static final int SEND_WORD = 10012;                   // 直播间添加屏蔽词
    public static final int ZB_LINK_YQ = 2001;                  // 邀请连麦
    public static final int ZB_LINK_GB = 200101;                 // 关闭全部连麦按钮
    public static final int ZB_USER_SET = 2002;                 // 用禁言 拉黑设置
    public static final int ZB_SEND_GIFT = 2003;                // 发送礼物通知
    public static final int SETTING_JINYAN = 200201;                 // 禁言
    public static final int SETTING_LAHEI = 200202;                  // 拉黑
    public static final int SETTING_TICHU = 200203;                  // 踢出
    public static final int TYPE_SHOW = 0;                      // 直播间消息列表消息类型 - 展示消息
    public static final int TYPE_DES = -1;                      // 直播间消息列表消息类型 - 介绍消息
    public static final String TYPE_SHOW_TEXT = "本平台提倡绿色健康直播，严禁在平台内外出现诱导未成年人送礼打赏、诈骗、赌博、非法转移财产、低俗色情、吸烟酗酒等不当行为， 若有违反，平台有权对您采取包括暂停支付收益、冻结或封禁帐号等措施，同时向相关部门依法追究您的法律责任。如因此给平台造成损失，有权向您全额追偿。";

    /**
     * IM 互动消息类型
     * 每添加一种状态都要在下面这两个方法中添加判断
     * MLVBLiveRoomImpl - onC2CCustomMessage -onGroupCustomMessage
     * IMMessageMgr - onNewMessages - case Custom
     */

    public static final int IMCMD_TEXT_MSG = 1;                  // 文本消息
    public static final int IMCMD_ENTER_LIVE = 2;                // 用户加入直播
    public static final int IMCMD_EXIT_LIVE = 3;                 // 用户退出直播
    public static final int IMCMD_GIFT = 4;                      // 礼物消息
    public static final int IMCMD_FOLLOW = 5;                    // 关注消息
    public static final int IMCMD_SHOW_GOODS = 6;                // 商品讲解
    public static final int IMCMD_SHOW_GOODS_CANCEL = 7;         // 取消讲解
    public static final int IMCMD_FORBIDDER_TALK = 8;            // 禁言
    public static final int IMCMD_CANCEL_FORBIDDER_TALK = 9;     // 取消禁言
    public static final int IMCMD_PUT_BLACK = 10;                // 拉黑
    public static final int IMCMD_SHOW_MIC = 11;                 // 观众可以连麦
    public static final int IMCMD_FORBIDDEN_MIC = 12;            // 观众禁止连麦
    public static final int IMCMD_CANCEL_LINK = 13;              // 观众取消连麦
    public static final int IMCMD_REFUSE_LINK = 14;              // 观众拒绝连麦
    public static final int IMCMD_INVITE_LINK = 15;              // 主播邀请连麦
    public static final int IMCMD_OPEN_MORE_LINK = 16;           // 主播开启多人连麦
    public static final int IMCMD_CLOSE_MORE_LINK = 17;          // 主播关闭多人连麦
    public static final int IMCMD_LIKE = 18;                     // 点赞消息
    public static final int IMCMD_TO_LINK = 19;                  // 用户发送申请连麦消息
    public static final int IMCMD_CANCEL_PUT_BLACK = 20;         // 取消拉黑
    public static final int IMCMD_ZB_LINKING = 21;               // 主播进入连麦
    public static final int IMCMD_MORE_LINK_YQ = 22;             // 主播邀请并通知上麦者上麦以及座位号
    public static final int IMCMD_MORE_LINK_JS = 24;             // 主播接受上麦者上麦以及座位号 -1表示没位置
    public static final int IMCMD_MORE_LINK_NUM = 23;            // 上麦者告诉主播自己想上麦的座位号
    public static final int IMCMD_RESH_MORELINK_INFO = 25;       // 通知观众们刷新座位信息
    public static final int IMCMD_MORE_ANCHOR_JY = 26;           // 多人连麦-静音
    public static final int IMCMD_MORE_ANCHOR_QXJY = 27;         // 多人连麦-取消静音
    public static final int IMCMD_ZB_COMEBACK = 28;              // 主播断开重新回来了 通知大家重新拉一下流
    public static final int IMCMD_GONGGAO_MSG = 29;              // 公告消息 msg为json串 类型-名字-内容  msgType-nickname-content      msgType: 0-拉黑 1-踢出
    public static final int IMCMD_RESH_HOME_INFO = 30;           // 通知观众们刷新房间信息
    public static final int IMCMD_SETTING_PL = 31;               // 设置-直播间评论状态 0 禁止 1允许
    public static final int IMCMD_SETTING_LW = 32;               // 设置-直播间送礼状态 0 禁止 1允许
    public static final int IMCMD_BLOCK_WORD_ADD = 33;           // 设置-直播间屏蔽词添加 消息内容为屏蔽词
    public static final int IMCMD_BLOCK_WORD_DEL = 34;           // 设置-直播间屏蔽词删除


}

