package com.xaqinren.healthyelders.moduleLiteav.bean;

import java.io.Serializable;
import java.util.List;

//生成id，发布内容，视频path，封面path，定位，权限，禁止查看列表，草稿箱时间，是否推荐，音乐，
public class SaveDraftBean implements Serializable {
    //存储ID
    private long id;
    //发布内容
    private String content;
    //内容描述
    private List<PublishFocusItemBean> publishFocusItemBeans;
    //视频路径
    private String videoPath;
    //封面路径
    private String coverPath;
    //经纬度
    private double lat;
    private double lon;
    //定位地址
    private String address;
    //省
    private String province;
    //市
    private String city ;
    //区
    private String district;
    //公开模式
    private int openMode;
    //禁止查阅
    private List<LiteAvUserBean> unLookUser;
    //存储时间
    private long saveTime;
    //视频是否推荐,图文保存相册
    private boolean isComment;
    //音乐ID
    private long musicId;
    //类型
    private int type;//0视频 1图文
    //图片list
    private List<String> filePaths;
    //图片正文
    private String bodyStr;

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getBodyStr() {
        return bodyStr;
    }

    public void setBodyStr(String bodyStr) {
        this.bodyStr = bodyStr;
    }

    public List<String> getFilePaths() {
        return filePaths;
    }

    public void setFilePaths(List<String> filePaths) {
        this.filePaths = filePaths;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<PublishFocusItemBean> getPublishFocusItemBeans() {
        return publishFocusItemBeans;
    }

    public void setPublishFocusItemBeans(List<PublishFocusItemBean> publishFocusItemBeans) {
        this.publishFocusItemBeans = publishFocusItemBeans;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getOpenMode() {
        return openMode;
    }

    public void setOpenMode(int openMode) {
        this.openMode = openMode;
    }

    public List<LiteAvUserBean> getUnLookUser() {
        return unLookUser;
    }

    public void setUnLookUser(List<LiteAvUserBean> unLookUser) {
        this.unLookUser = unLookUser;
    }

    public long getSaveTime() {
        return saveTime;
    }

    public void setSaveTime(long saveTime) {
        this.saveTime = saveTime;
    }

    public boolean isComment() {
        return isComment;
    }

    public void setComment(boolean comment) {
        isComment = comment;
    }

    public long getMusicId() {
        return musicId;
    }

    public void setMusicId(long musicId) {
        this.musicId = musicId;
    }
}
