package com.xaqinren.healthyelders.uniApp.bean;

public class UniEventBean {
    public int msgId;
    public String appId;
    public int taskId;
    public String data;
    public boolean isSelfUni;//是否是自身的应用

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

    public UniEventBean(int msgId, String appId, int taskId, String data, boolean isSelfUni) {
        this.msgId = msgId;
        this.appId = appId;
        this.taskId = taskId;
        this.data = data;
        this.isSelfUni = isSelfUni;
    }
}
