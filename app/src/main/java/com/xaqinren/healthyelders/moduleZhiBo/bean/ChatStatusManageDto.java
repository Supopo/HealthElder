package com.xaqinren.healthyelders.moduleZhiBo.bean;

/**
 * Created by Lee. on 2021/5/7.
 * 连麦设置
 */
public class ChatStatusManageDto {
    public boolean onlyFansMic;     //仅接收粉丝的连麦申请，0：否；1：是
    public boolean onlyInviteMic;   //仅允许通过邀请上麦，0：否；1：是
    public boolean userApplyMic;    //观众连线需要通过申请，0：否；1：是
    public boolean adminMicAuth;    //管理员对连线嘉宾有操作权限，0：否；1：是
    public boolean canGiftAudienceToGuest;  //允许观众给嘉宾送礼 -- 双人聊，0：否；1：是

}

