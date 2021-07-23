package com.xaqinren.healthyelders.bean;

/**
 * =====================================================
 * 描    述: RxBus通讯专业
 * =====================================================
 */
public class EventBean<T> {
    public int msgId;
    public int msgType;
    public int status;
    public int pos;
    public long time;
    public boolean doIt;
    public String type;
    public String content;
    public String name;
    public Object object;
    public String nickname;
    public T data;

    public EventBean(int msgId, T data) {
        this.msgId = msgId;
        this.data = data;
    }

    public EventBean(int msgId, boolean doIt) {
        this.msgId = msgId;
        this.doIt = doIt;
    }

    public EventBean(int msgId, int msgType) {
        this.msgId = msgId;
        this.msgType = msgType;
    }


    public EventBean(int msgId, String content) {
        this.msgId = msgId;
        this.content = content;
    }

    public EventBean(int msgId, String content,long time) {
        this.msgId = msgId;
        this.content = content;
        this.time = time;
    }

    public EventBean(int msgId, String content, int msgType) {
        this.msgId = msgId;
        this.content = content;
        this.msgType = msgType;
    }

    public EventBean(int msgId, String content, String type, int pos) {
        this.msgId = msgId;
        this.content = content;
        this.pos = pos;
        this.type = type;
    }


    public EventBean(int msgId, Object object, int status) {
        this.msgId = msgId;
        this.object = object;
        this.status = status;
    }

    public EventBean(int msgId, int msgType, String content, int status) {
        this.msgId = msgId;
        this.msgType = msgType;
        this.content = content;
        this.status = status;
    }

    public EventBean(int msgId, int msgType, String content, String nickname) {
        this.msgId = msgId;
        this.msgType = msgType;
        this.content = content;
        this.nickname = nickname;
    }
}
