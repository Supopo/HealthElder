package com.xaqinren.healthyelders.moduleZhiBo.bean;

import java.io.Serializable;

/**
 * Created by Lee. on 2021/4/25.
 */
public class LiveInitInfo implements Serializable {
    //------主播创建房间返回字段-----
    public String liveRoomId;
    public String liveRecordId;// 直播记录ID
    public String liveRoomIntroduce;//"此处省略500字",直播间介绍
    public String[] groupIds;//上次加入的群


    //------用户进入直播间返回字段-----
    public Object liveRoomMicUsers;    // 连麦中的用户
    public String liveRoomRecordId;//直播记录ID
    public String liveRoomStatus;// 房间状态 LIVE_ING：直播中 LIVE_OFF：直播断开 NO_LIVE：禁止直播 LIVE_OVER：直播结束 NOT_LIVE： 未直播
    public String liveRoomCode;// 直播间编码  同IM直播间ID
    public String liveRoomType;//":"VIDEO_LIVE",
    public String userId;//":"1386137830790533120",// 主播ID
    public String nickname;//":"苏坡坡要吃婆婆酥。",
    public String avatarUrl;//":;
    public String pullStreamUrl;// 拉流地址
    public double accountBalance;//0, // 账户余额
    public Object commodityInfoDto;//null, // 讲解中商品
    public Boolean hasIntroduce;//":true,
    public Boolean canSale;//false, // 是否允许卖货，0：否；1：是
    public Boolean canMic;//true,//  是否允许连麦  0: 否; 1:是
    public Boolean canComment;//true,// 是否允许评论，0：否；1：是
    public Boolean canGift;//true,// 是否允许送礼物，0：否；1：是
    public Boolean canRecordVideo;//true, // 是否允许观众录制，0：否；1：是
    public Boolean hasVertical;//true,// 是否竖屏，0：否；1：是 , 默认竖屏
    public Boolean onlyFansMic;//true, // 仅接收粉丝的连麦申请，0：否；1：是
    public Boolean onlyInviteMic;//false, // 仅允许通过邀请上麦，0：否；1：是
    public Boolean userApplyMic;//true,// 观众连线需要通过申请，0：否；1：是
    public Boolean adminMicAuth;//true,// 管理员对连线嘉宾有操作权限，0：否；1：是
    public Boolean canMicIng;//false, // 连麦状态 0：未开启 1：连麦中
    public Boolean hasFollow;//false,// 是否已关注
    public Boolean hasSpeech;//false, // 是否静音
    public Boolean hasAdmin;//false, // 是否是管理员
    public Boolean canShield;//null, // 管理员是否可以设置屏蔽词
}
