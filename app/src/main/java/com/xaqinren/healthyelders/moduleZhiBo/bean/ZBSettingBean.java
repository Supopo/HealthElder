package com.xaqinren.healthyelders.moduleZhiBo.bean;

import java.io.Serializable;

/**
 * Created by Lee. on 2021/5/7.
 * 直播间设置类
 */
public class ZBSettingBean implements Serializable {
    public String liveRoomId;
    public boolean canComment;
    public boolean canGift;
    public boolean canRecordVideo;
    public String liveRoomConnection;//
    public ChatStatusManageDto chatStatusManageDto;
    public AnchorInteractionDto anchorInteractionDto;
    public ConnectionDto connectionDto;
}
