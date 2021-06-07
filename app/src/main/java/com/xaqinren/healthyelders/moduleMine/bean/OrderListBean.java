package com.xaqinren.healthyelders.moduleMine.bean;

import java.util.List;
public class OrderListBean {

    private List<OrderListItemBean> orderItemList;
    private String id;
    private String storeId;
    private String storeName;
    private String orderNo;
    private String distributionChannel;
    private Double commodityTotalAmount;
    private Integer couponDiscountAmount;
    private Integer vipDiscountAmount;
    private Double receivableAmount;
    private Double payableAmount;
    private Integer receiptsAmount;
    private String queryStatus;
    private String queryStatusName;
    private String distributionChannelName;
    private String createdAt;
    private String jumpUrl;

    public String getJumpUrl() {
        return jumpUrl;
    }

    public void setJumpUrl(String jumpUrl) {
        this.jumpUrl = jumpUrl;
    }

    public List<OrderListItemBean> getItems() {
        return orderItemList;
    }

    public void setItems(List<OrderListItemBean> items) {
        this.orderItemList = items;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getDistributionChannel() {
        return distributionChannel;
    }

    public void setDistributionChannel(String distributionChannel) {
        this.distributionChannel = distributionChannel;
    }

    public Double getCommodityTotalAmount() {
        return commodityTotalAmount;
    }

    public void setCommodityTotalAmount(Double commodityTotalAmount) {
        this.commodityTotalAmount = commodityTotalAmount;
    }

    public Integer getCouponDiscountAmount() {
        return couponDiscountAmount;
    }

    public void setCouponDiscountAmount(Integer couponDiscountAmount) {
        this.couponDiscountAmount = couponDiscountAmount;
    }

    public Integer getVipDiscountAmount() {
        return vipDiscountAmount;
    }

    public void setVipDiscountAmount(Integer vipDiscountAmount) {
        this.vipDiscountAmount = vipDiscountAmount;
    }

    public Double getReceivableAmount() {
        return receivableAmount;
    }

    public void setReceivableAmount(Double receivableAmount) {
        this.receivableAmount = receivableAmount;
    }

    public Double getPayableAmount() {
        return payableAmount;
    }

    public void setPayableAmount(Double payableAmount) {
        this.payableAmount = payableAmount;
    }

    public Integer getReceiptsAmount() {
        return receiptsAmount;
    }

    public void setReceiptsAmount(Integer receiptsAmount) {
        this.receiptsAmount = receiptsAmount;
    }

    public String getQueryStatus() {
        return queryStatus;
    }

    public void setQueryStatus(String queryStatus) {
        this.queryStatus = queryStatus;
    }

    public String getQueryStatusName() {
        return queryStatusName;
    }

    public void setQueryStatusName(String queryStatusName) {
        this.queryStatusName = queryStatusName;
    }

    public String getDistributionChannelName() {
        return distributionChannelName;
    }

    public void setDistributionChannelName(String distributionChannelName) {
        this.distributionChannelName = distributionChannelName;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCount() {
        int count = 0;
        for (OrderListItemBean bean : orderItemList) {
            count += bean.getItemNumber();
        }
        return "共" + count + "件";
    }
}
