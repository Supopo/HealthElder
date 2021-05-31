package com.xaqinren.healthyelders.uniApp.bean;

public class SaveBean {
    private String id;
    private String name;
    private boolean autoUpdateApplet;
    private int currentVersion;
    private String downUrl;
    private String savePath;
    private boolean downComplete;
    private boolean needDown;
    private String appId;
    private boolean openActivity;

    public boolean isOpenActivity() {
        return openActivity;
    }

    public void setOpenActivity(boolean openActivity) {
        this.openActivity = openActivity;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public boolean isNeedDown() {
        return needDown;
    }

    public void setNeedDown(boolean needDown) {
        this.needDown = needDown;
    }

    public boolean isDownComplete() {
        return downComplete;
    }

    public void setDownComplete(boolean downComplete) {
        this.downComplete = downComplete;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAutoUpdateApplet() {
        return autoUpdateApplet;
    }

    public void setAutoUpdateApplet(boolean autoUpdateApplet) {
        this.autoUpdateApplet = autoUpdateApplet;
    }

    public int getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(int currentVersion) {
        this.currentVersion = currentVersion;
    }

    public String getDownUrl() {
        return downUrl;
    }

    public void setDownUrl(String downUrl) {
        this.downUrl = downUrl;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }
}
