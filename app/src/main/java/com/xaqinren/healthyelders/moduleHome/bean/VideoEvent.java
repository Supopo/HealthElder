package com.xaqinren.healthyelders.moduleHome.bean;

/**
 * Created by Lee. on 2021/5/13.
 * 视频播放专用
 */
public class VideoEvent {
    public int msgId;//1-说明是上下滑了 2-暂停播放 3-继续播放 101-说明是 首页左右滑动了 10010-说明回到首页顶部了 暂停播放 隐藏视图
    public int position;
    public String fragmentId;
    public String tag;

    public VideoEvent(int msgId) {
        this.msgId = msgId;
    }

    public VideoEvent(int msgId, int position) {
        this.msgId = msgId;
        this.position = position;
    }

    public VideoEvent(int msgId, String fragmentId) {
        this.msgId = msgId;
        this.fragmentId = fragmentId;
    }

    public VideoEvent(int msgId, String fragmentId,String tag) {
        this.msgId = msgId;
        this.fragmentId = fragmentId;
        this.tag = tag;
    }
    public VideoEvent(int msgId, int position, String fragmentId) {
        this.msgId = msgId;
        this.position = position;
        this.fragmentId = fragmentId;
    }

    @Override
    public String toString() {
        return "VideoEvent{" +
                "msgId=" + msgId +
                ", position=" + position +
                ", fragmentId='" + fragmentId + '\'' +
                '}';
    }
}
