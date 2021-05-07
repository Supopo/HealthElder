package com.xaqinren.healthyelders.moduleZhiBo.bean;

/**
 * Created by Lee. on 2021/5/7.
 * 直播间设置类
 */
public class ZBSettingBean {
    public String liveRoomId;
    public boolean canComment;
    public boolean canGift;
    public boolean canRecordVideo;
    public boolean canMicIng;//是否开启多人连麦
    public DoubleTalkStatusManageDto doubleTalkStatusManageDto;
    public ChatRoomStatusManageDto chatRoomStatusManageDto;
    public AnchorInteractionDto anchorInteractionDto;

    public void setLiveRoomId(String liveRoomId) {
        this.liveRoomId = liveRoomId;
    }

    public String getLiveRoomId() {
        return liveRoomId;
    }

    public void setCanComment(boolean canComment) {
        this.canComment = canComment;
    }

    public boolean getCanComment() {
        return canComment;
    }

    public void setCanGift(boolean canGift) {
        this.canGift = canGift;
    }

    public boolean getCanGift() {
        return canGift;
    }

    public void setCanRecordVideo(boolean canRecordVideo) {
        this.canRecordVideo = canRecordVideo;
    }

    public boolean getCanRecordVideo() {
        return canRecordVideo;
    }

    public void setCanMicIng(boolean canMicIng) {
        this.canMicIng = canMicIng;
    }

    public boolean getCanMicIng() {
        return canMicIng;
    }

    public void setDoubleTalkStatusManageDto(DoubleTalkStatusManageDto doubleTalkStatusManageDto) {
        this.doubleTalkStatusManageDto = doubleTalkStatusManageDto;
    }

    public DoubleTalkStatusManageDto getDoubleTalkStatusManageDto() {
        return doubleTalkStatusManageDto;
    }

    public void setChatRoomStatusManageDto(ChatRoomStatusManageDto chatRoomStatusManageDto) {
        this.chatRoomStatusManageDto = chatRoomStatusManageDto;
    }

    public ChatRoomStatusManageDto getChatRoomStatusManageDto() {
        return chatRoomStatusManageDto;
    }

    public void setAnchorInteractionDto(AnchorInteractionDto anchorInteractionDto) {
        this.anchorInteractionDto = anchorInteractionDto;
    }

    public AnchorInteractionDto getAnchorInteractionDto() {
        return anchorInteractionDto;
    }

}
