package com.xaqinren.healthyelders.moduleZhiBo.bean;

/**
 * Created by Lee. on 2021/5/7.
 * 双人聊设置
 */
public class DoubleTalkStatusManageDto {

    public boolean onlyFansMic;
    public boolean onlyInviteMic;
    public boolean userApplyMic;
    public boolean adminMicAuth;
    public boolean canGiftAudienceToGuest;

    public void setOnlyFansMic(boolean onlyFansMic) {
        this.onlyFansMic = onlyFansMic;
    }

    public boolean getOnlyFansMic() {
        return onlyFansMic;
    }

    public void setOnlyInviteMic(boolean onlyInviteMic) {
        this.onlyInviteMic = onlyInviteMic;
    }

    public boolean getOnlyInviteMic() {
        return onlyInviteMic;
    }

    public void setUserApplyMic(boolean userApplyMic) {
        this.userApplyMic = userApplyMic;
    }

    public boolean getUserApplyMic() {
        return userApplyMic;
    }

    public void setAdminMicAuth(boolean adminMicAuth) {
        this.adminMicAuth = adminMicAuth;
    }

    public boolean getAdminMicAuth() {
        return adminMicAuth;
    }

    public void setCanGiftAudienceToGuest(boolean canGiftAudienceToGuest) {
        this.canGiftAudienceToGuest = canGiftAudienceToGuest;
    }

    public boolean getCanGiftAudienceToGuest() {
        return canGiftAudienceToGuest;
    }

}

