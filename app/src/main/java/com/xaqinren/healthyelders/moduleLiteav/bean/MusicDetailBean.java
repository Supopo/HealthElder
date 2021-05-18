package com.xaqinren.healthyelders.moduleLiteav.bean;

import java.util.List;

public class MusicDetailBean {
    private long musicId;
    private String coverPath;
    private String nickName;
    private long userId;
    private String useCount;//使用人数
    private boolean isOriginal;//原声
    private boolean isCreate;//创作
    private boolean isColl;//收藏
    private List<MusicBean> musicBeans;

    public long getMusicId() {
        return musicId;
    }

    public void setMusicId(long musicId) {
        this.musicId = musicId;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUseCount() {
        return useCount;
    }

    public void setUseCount(String useCount) {
        this.useCount = useCount;
    }

    public boolean isOriginal() {
        return isOriginal;
    }

    public void setOriginal(boolean original) {
        isOriginal = original;
    }

    public boolean isCreate() {
        return isCreate;
    }

    public void setCreate(boolean create) {
        isCreate = create;
    }

    public boolean isColl() {
        return isColl;
    }

    public void setColl(boolean coll) {
        isColl = coll;
    }

    public List<MusicBean> getMusicBeans() {
        return musicBeans;
    }

    public void setMusicBeans(List<MusicBean> musicBeans) {
        this.musicBeans = musicBeans;
    }
}
