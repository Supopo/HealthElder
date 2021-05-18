package com.xaqinren.healthyelders.moduleLiteav.bean;

public class MusicBean {
    private long musicId;
    private String musicName;
    private String musicType;
    private String coverPath;
    private boolean isShoufa;

    public boolean isShoufa() {
        return isShoufa;
    }

    public void setShoufa(boolean shoufa) {
        isShoufa = shoufa;
    }

    public long getMusicId() {
        return musicId;
    }

    public void setMusicId(long musicId) {
        this.musicId = musicId;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public String getMusicType() {
        return musicType;
    }

    public void setMusicType(String musicType) {
        this.musicType = musicType;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }
}
