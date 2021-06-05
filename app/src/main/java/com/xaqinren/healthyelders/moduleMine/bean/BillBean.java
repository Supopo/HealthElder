package com.xaqinren.healthyelders.moduleMine.bean;

import android.graphics.Color;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public class BillBean implements MultiItemEntity {

    private String header_group_name;
    private String header_group_income;

    private String createdAt;
    private String orderType;
    private String orderNo;
    private Float balanceBegin;
    private Float balanceChange;
    private Float balanceEnd;
    private String body;
    private String recordTypeName;
    private String orderTypeName;


    public String getHeader_group_name() {
        return header_group_name;
    }

    public void setHeader_group_name(String header_group_name) {
        this.header_group_name = header_group_name;
    }

    public String getHeader_group_income() {
        return header_group_income;
    }

    public void setHeader_group_income(String header_group_income) {
        this.header_group_income = header_group_income;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Float getBalanceBegin() {
        return balanceBegin;
    }

    public void setBalanceBegin(Float balanceBegin) {
        this.balanceBegin = balanceBegin;
    }

    public Float getBalanceChange() {
        return balanceChange;
    }

    public void setBalanceChange(Float balanceChange) {
        this.balanceChange = balanceChange;
    }

    public Float getBalanceEnd() {
        return balanceEnd;
    }

    public void setBalanceEnd(Float balanceEnd) {
        this.balanceEnd = balanceEnd;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getRecordTypeName() {
        return recordTypeName;
    }

    public void setRecordTypeName(String recordTypeName) {
        this.recordTypeName = recordTypeName;
    }

    public String getOrderTypeName() {
        return orderTypeName;
    }

    public void setOrderTypeName(String orderTypeName) {
        this.orderTypeName = orderTypeName;
    }

    public String getAmount() {
        if (recordTypeName.equals("扣除")) {
            return balanceChange + "";
        }else{
            return "+"+balanceChange;
        }
    }

    public String getTypeName() {
        if (recordTypeName.equals("扣除")) {
            return  orderTypeName;
        }else{
            return null;
        }
    }

    public int getColor() {
        if (recordTypeName.equals("扣除")) {
            return Color.parseColor("#252525");
        }else{
            return Color.parseColor("#F81E4D");
        }
    }

    @Override
    public int getItemType() {
        return 0;
    }
}
