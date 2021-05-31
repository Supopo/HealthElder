package com.xaqinren.healthyelders.uniApp.bean;

public class UniEventBean {
    public int msgId;
    public String appId;
    public int taskId;
    public String data;

    public UniEventBean() {
    }

    public UniEventBean(int msgId, String appId, int taskId) {
        this.msgId = msgId;
        this.appId = appId;
        this.taskId = taskId;
    }

    public UniEventBean(int msgId, String appId, int taskId, String data) {
        this.msgId = msgId;
        this.appId = appId;
        this.taskId = taskId;
        this.data = data;
    }
}
