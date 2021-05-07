package com.xaqinren.healthyelders.moduleZhiBo.bean;

/**
 * Created by Lee. on 2021/5/7.
 * 多人聊设置
 */
public class ChatRoomStatusManageDto {

    public boolean chatRoomOnlyFansMic;
    public boolean chatRoomOnlyInviteMic;
    public boolean chatRoomUserApplyMic;
    public boolean chatRoomAdminMicAuth;

    public void setChatRoomOnlyFansMic(boolean chatRoomOnlyFansMic) {
        this.chatRoomOnlyFansMic = chatRoomOnlyFansMic;
    }

    public boolean getChatRoomOnlyFansMic() {
        return chatRoomOnlyFansMic;
    }

    public void setChatRoomOnlyInviteMic(boolean chatRoomOnlyInviteMic) {
        this.chatRoomOnlyInviteMic = chatRoomOnlyInviteMic;
    }

    public boolean getChatRoomOnlyInviteMic() {
        return chatRoomOnlyInviteMic;
    }

    public void setChatRoomUserApplyMic(boolean chatRoomUserApplyMic) {
        this.chatRoomUserApplyMic = chatRoomUserApplyMic;
    }

    public boolean getChatRoomUserApplyMic() {
        return chatRoomUserApplyMic;
    }

    public void setChatRoomAdminMicAuth(boolean chatRoomAdminMicAuth) {
        this.chatRoomAdminMicAuth = chatRoomAdminMicAuth;
    }

    public boolean getChatRoomAdminMicAuth() {
        return chatRoomAdminMicAuth;
    }

}

