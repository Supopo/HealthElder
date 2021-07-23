package com.xaqinren.healthyelders.moduleMine.bean;

public class OrderListItemBean {

    private String commodityId;
    private String itemName;
    private String itemSpec;
    private String itemImage;
    private Integer itemNumber;
    private Double itemPrice;
    private Double itemSalesPrice;
    private String refundStatus;
    private String refundStatusName;

    public String getCommodityId() {
        return commodityId;
    }

    public void setCommodityId(String commodityId) {
        this.commodityId = commodityId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemSpec() {
        return itemSpec;
    }

    public void setItemSpec(String itemSpec) {
        this.itemSpec = itemSpec;
    }

    public String getItemImage() {
        return itemImage;
    }

    public void setItemImage(String itemImage) {
        this.itemImage = itemImage;
    }

    public Integer getItemNumber() {
        return itemNumber;
    }

    public void setItemNumber(Integer itemNumber) {
        this.itemNumber = itemNumber;
    }

    public Double getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(Double itemPrice) {
        this.itemPrice = itemPrice;
    }

    public Double getItemSalesPrice() {
        return itemSalesPrice;
    }

    public void setItemSalesPrice(Double itemSalesPrice) {
        this.itemSalesPrice = itemSalesPrice;
    }
}
