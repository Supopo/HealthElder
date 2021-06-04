package com.xaqinren.healthyelders.moduleZhiBo.bean;

import java.io.Serializable;

/**
 * Created by Lee. on 2021/5/7.
 * 直播间设置类
 */
public class ZBSettingBean implements Serializable {
    public String liveRoomId;
    private Boolean canComment;
    private Boolean canGift;
    private Boolean canRecordVideo;
    private Boolean canMic;

    public Boolean getCanComment() {
        return canComment;
    }

    public void setCanComment(Boolean canComment) {
        this.canComment = canComment;
    }

    public Boolean getCanGift() {
        return canGift ;
    }

    public void setCanGift(Boolean canGift) {
        this.canGift = canGift;
    }

    public Boolean getCanRecordVideo() {
        return canRecordVideo ;
    }

    public void setCanRecordVideo(Boolean canRecordVideo) {
        this.canRecordVideo = canRecordVideo;
    }

    public Boolean getCanMic() {
        return canMic;
    }

    public void setCanMic(Boolean canMic) {
        this.canMic = canMic;
    }

    public String liveRoomConnection;//
    public ChatStatusManageDto chatStatusManageDto;//连麦
    public AnchorInteractionDto anchorInteractionDto;//PL
    public ConnectionDto connectionDto;//连线
}
