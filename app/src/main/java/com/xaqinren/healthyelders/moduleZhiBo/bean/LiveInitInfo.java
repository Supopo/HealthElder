package com.xaqinren.healthyelders.moduleZhiBo.bean;

import java.io.Serializable;

/**
 * Created by Lee. on 2021/4/25.
 */
public class LiveInitInfo implements Serializable {
    //------主播创建房间返回字段-----
    public String liveRoomId;
    public String liveRoomIntroduce;//"此处省略500字",直播间介绍
    public String[] groupIds;//上次加入的群

    //------主播检查权限信息
    public String id;//"1386206506794930176",
    public String liveRoomCover;//直播间封面,
    public String liveRoomName;//直播主题,
    public String liveRoomTag;//直播标签, - 多个逗号隔开
    public String liveRoomRemark;//直播备注
    public String noticeTime;//预告直播时间
    public String roomPassword;//房间密码
    public Boolean hasNotice;//是否开播提醒直播时间
    public String shields;//屏蔽词, - 多个逗号隔开
    public Boolean hasLocation;//是否开启定位


    //------用户进入直播间返回字段-----
    public Object liveRoomMicUsers;    // 连麦中的用户
    public String liveRoomRecordId;//直播记录ID
    public String liveRoomStatus;// 房间状态 LIVE_ING：直播中 LIVE_OFF：直播断开 NO_LIVE：禁止直播 LIVE_OVER：直播结束 NOT_LIVE： 未直播
    public String liveRoomCode;// 直播间编码  同IM直播间ID --创建房间群号用这个
    public String liveRoomType;//":"VIDEO_LIVE",
    public String userId;//":"1386137830790533120",// 主播ID
    public String nickname;//":"苏坡坡要吃婆婆酥。",
    public String avatarUrl;//":主播头像
    public String pullStreamUrl;// 拉流地址
    public double accountBalance;//0, // 账户余额
    public Object commodityInfoDto;//null, // 讲解中商品
    public Boolean hasIntroduce;//":true, 是否开启介绍
    public Boolean canSale;//false, // 是否允许卖货，0：否；1：是
    public Boolean canMic;//true,//  是否允许连麦  0: 否; 1:是
    public Boolean canComment;//true,// 是否允许评论，0：否；1：是                    -房间属性
    public Boolean canGift;//true,// 是否允许送礼物，0：否；1：是
    public Boolean canRecordVideo;//true, // 是否允许观众录制，0：否；1：是
    public Boolean hasVertical;//true,// 是否竖屏，0：否；1：是 , 默认竖屏
    public Boolean onlyFansMic;//true, // 仅接收粉丝的连麦申请，0：否；1：是
    public Boolean onlyInviteMic;//false, // 仅允许通过邀请上麦，0：否；1：是
    public Boolean userApplyMic;//true,// 观众连线需要通过申请，0：否；1：是
    public Boolean adminMicAuth;//true,// 管理员对连线嘉宾有操作权限，0：否；1：是
    public String liveRoomConnection;//直播间状态 FREE("自由")  PK("PK"), DOUBLE_TALK("双人聊"),  CHAT_ROOM("聊天室"), CONNECTION("连线")
    public Boolean hasFollow;//false,// 是否已关注
    public Boolean hasSpeech;//false, // 用户能否发言
    public Boolean hasAdmin;//false, // 是否是管理员
    public Boolean canShield;//null, // 管理员是否可以设置屏蔽词

    public Boolean getAdminCanMuteAuth() {
        return adminCanMuteAuth == null ? false : adminCanMuteAuth;
    }

    public void setAdminCanMuteAuth(Boolean adminCanMuteAuth) {
        this.adminCanMuteAuth = adminCanMuteAuth;
    }

    public Boolean getAdminCanBlackAuth() {
        return adminCanBlackAuth == null ? false : adminCanBlackAuth;
    }

    public void setAdminCanBlackAuth(Boolean adminCanBlackAuth) {
        this.adminCanBlackAuth = adminCanBlackAuth;
    }

    public Boolean getAdminCanKickAuth() {
        return adminCanKickAuth == null ? false : adminCanKickAuth;
    }

    public void setAdminCanKickAuth(Boolean adminCanKickAuth) {
        this.adminCanKickAuth = adminCanKickAuth;
    }

    public Boolean getChatRoomOnlyFansMic() {
        return chatRoomOnlyFansMic == null ? false : chatRoomOnlyFansMic;
    }

    public void setChatRoomOnlyFansMic(Boolean chatRoomOnlyFansMic) {
        this.chatRoomOnlyFansMic = chatRoomOnlyFansMic;
    }

    public Boolean getChatRoomOnlyInviteMic() {
        return chatRoomOnlyInviteMic == null ? false : chatRoomOnlyInviteMic;
    }

    public void setChatRoomOnlyInviteMic(Boolean chatRoomOnlyInviteMic) {
        this.chatRoomOnlyInviteMic = chatRoomOnlyInviteMic;
    }

    public Boolean getChatRoomUserApplyMic() {
        return chatRoomUserApplyMic == null ? false : chatRoomUserApplyMic;
    }

    public void setChatRoomUserApplyMic(Boolean chatRoomUserApplyMic) {
        this.chatRoomUserApplyMic = chatRoomUserApplyMic;
    }

    public Boolean getChatRoomAdminMicAuth() {
        return chatRoomAdminMicAuth == null ? false : chatRoomAdminMicAuth;
    }

    public void setChatRoomAdminMicAuth(Boolean chatRoomAdminMicAuth) {
        this.chatRoomAdminMicAuth = chatRoomAdminMicAuth;
    }

    public Boolean adminCanMuteAuth;//": true,
    public Boolean adminCanBlackAuth;//": true,
    public Boolean adminCanKickAuth;//": true,
    public Boolean chatRoomOnlyFansMic;//": true,
    public Boolean chatRoomOnlyInviteMic;//": true,
    public Boolean chatRoomUserApplyMic;//": true,
    public Boolean chatRoomAdminMicAuth;//": true,

    //开启直播间需要传
    public double longitude;
    public double latitude;
    public String address;
    public String pushUrl;

    //开启直播间外面设置的一些参数
    public boolean isMirror;//是否开启观众端镜像 需要推流成功后设置才有效果
    public int beautyStyle;//美颜类型
    public int beautyLevel;//美颜等级
    public int whitenessLevel;//美白等级
    public int ruddinessLevel;//红润等级

    public int linkType;


    public void setHasNotice(Boolean hasNotice) {
        this.hasNotice = hasNotice;
    }

    public void setHasLocation(Boolean hasLocation) {
        this.hasLocation = hasLocation;
    }

    public void setHasIntroduce(Boolean hasIntroduce) {
        this.hasIntroduce = hasIntroduce;
    }

    public void setCanSale(Boolean canSale) {
        this.canSale = canSale;
    }

    public void setCanMic(Boolean canMic) {
        this.canMic = canMic;
    }

    public void setCanComment(Boolean canComment) {
        this.canComment = canComment;
    }

    public void setCanGift(Boolean canGift) {
        this.canGift = canGift;
    }

    public void setCanRecordVideo(Boolean canRecordVideo) {
        this.canRecordVideo = canRecordVideo;
    }

    public void setHasVertical(Boolean hasVertical) {
        this.hasVertical = hasVertical;
    }

    public void setOnlyFansMic(Boolean onlyFansMic) {
        this.onlyFansMic = onlyFansMic;
    }

    public void setOnlyInviteMic(Boolean onlyInviteMic) {
        this.onlyInviteMic = onlyInviteMic;
    }

    public void setUserApplyMic(Boolean userApplyMic) {
        this.userApplyMic = userApplyMic;
    }

    public void setAdminMicAuth(Boolean adminMicAuth) {
        this.adminMicAuth = adminMicAuth;
    }

    public void setHasFollow(Boolean hasFollow) {
        this.hasFollow = hasFollow;
    }

    public void setHasSpeech(Boolean hasSpeech) {
        this.hasSpeech = hasSpeech;
    }

    public void setHasAdmin(Boolean hasAdmin) {
        this.hasAdmin = hasAdmin;
    }

    public void setCanShield(Boolean canShield) {
        this.canShield = canShield;
    }

    public Boolean getHasNotice() {
        return hasNotice == null ? false : hasNotice;
    }

    public Boolean getHasLocation() {
        return hasLocation == null ? false : hasLocation;
    }

    public Boolean getHasIntroduce() {
        return hasIntroduce == null ? false : hasIntroduce;
    }

    public Boolean getCanSale() {
        return canSale == null ? false : canSale;
    }

    public Boolean getCanMic() {
        return canMic == null ? false : canMic;
    }

    public Boolean getCanComment() {
        return canComment == null ? false : canComment;
    }

    public Boolean getCanGift() {
        return canGift == null ? false : canGift;
    }

    public Boolean getCanRecordVideo() {
        return canRecordVideo == null ? false : canRecordVideo;
    }

    public Boolean getHasVertical() {
        return hasVertical == null ? false : hasVertical;
    }

    public Boolean getOnlyFansMic() {
        return onlyFansMic == null ? false : onlyFansMic;
    }

    public Boolean getOnlyInviteMic() {
        return onlyInviteMic == null ? false : onlyInviteMic;
    }

    public Boolean getUserApplyMic() {
        return userApplyMic == null ? false : userApplyMic;
    }

    public Boolean getAdminMicAuth() {
        return adminMicAuth == null ? false : adminMicAuth;
    }

    public Boolean getHasFollow() {
        return hasFollow == null ? false : hasFollow;
    }

    public Boolean getHasSpeech() {
        return hasSpeech == null ? false : hasSpeech;
    }

    public Boolean getHasAdmin() {
        return hasAdmin == null ? false : hasAdmin;
    }

    public Boolean getCanShield() {
        return canShield == null ? false : canShield;
    }
}
