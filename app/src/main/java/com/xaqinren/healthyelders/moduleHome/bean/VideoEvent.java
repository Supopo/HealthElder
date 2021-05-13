package com.xaqinren.healthyelders.moduleHome.bean;

/**
 * Created by Lee. on 2021/5/13.
 * 视频播放专用
 */
public class VideoEvent {
    public int msgId;
    public int intContent;

    public VideoEvent(int msgId) {
        this.msgId = msgId;
    }

    public VideoEvent(int msgId, int intContent) {
        this.msgId = msgId;
        this.intContent = intContent;
    }

}
