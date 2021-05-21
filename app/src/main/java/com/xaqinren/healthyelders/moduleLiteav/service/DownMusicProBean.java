package com.xaqinren.healthyelders.moduleLiteav.service;

//音乐下载进度
public class DownMusicProBean {
    public final static int WAIT = 0;//等待中
    public final static int DOWN = 2;//正在下载
    public final static int ERROR = 3;//下载错误
    public final static int FULL = 4;//下载完成
    public final static int IDLE = 5;//未开始
    public int status = 5;
    public String err;
    public long progress;
    public long total;
    public String id;
    public DownMusicProBean(String id , int status, String err) {
        this.status = status;
        this.err = err;
        this.id = id;
    }

    public DownMusicProBean setProgress(long progress , long total) {
        this.progress = progress;
        this.total = total;
        return this;
    }
}
