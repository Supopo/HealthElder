package com.xaqinren.healthyelders.moduleLiteav.bean;

public class TopicBean {
    private Object id;
    private Object createdAt;
    private Object merchantId;
    private String name;
    private Object hot;
    private Object useCount;
    private Object playCount;
    private Object icon;
    private Object userId;
    private Object approvalStatus;
    private Object ObjectId;
    private Object homeResourceType;
    private Object objectId;


    public TopicBean(String topicTitle) {
        this.name = topicTitle;
    }

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public Object getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Object createdAt) {
        this.createdAt = createdAt;
    }

    public Object getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Object merchantId) {
        this.merchantId = merchantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getHot() {
        return hot;
    }

    public void setHot(Object hot) {
        this.hot = hot;
    }

    public Object getUseCount() {
        return useCount;
    }

    public void setUseCount(Object useCount) {
        this.useCount = useCount;
    }

    public Object getPlayCount() {
        return playCount;
    }

    public void setPlayCount(Object playCount) {
        this.playCount = playCount;
    }

    public Object getIcon() {
        return icon;
    }

    public void setIcon(Object icon) {
        this.icon = icon;
    }

    public Object getUserId() {
        return userId;
    }

    public void setUserId(Object userId) {
        this.userId = userId;
    }

    public Object getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(Object approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public Object getObjectId() {
        return ObjectId;
    }

    public void setObjectId(Object objectId) {
        ObjectId = objectId;
    }

    public Object getHomeResourceType() {
        return homeResourceType;
    }

    public void setHomeResourceType(Object homeResourceType) {
        this.homeResourceType = homeResourceType;
    }
}
