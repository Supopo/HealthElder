package com.xaqinren.healthyelders.moduleMine.bean;

import android.graphics.Color;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public class BillBean implements MultiItemEntity {
    //USER_WALLET_RECHARGE(AccountRecordTypeEnum.ADD, "余额充值"),
    //USER_SECURITY_DEPOSIT(AccountRecordTypeEnum.ADD, "缴纳保证金"),
    //
    //COMMODITY_CONSUMPTION(AccountRecordTypeEnum.SUB,"购物消费"),
    //COMMODITY_CONSUMPTION_REFUND(AccountRecordTypeEnum.ADD,"消费退款"),
    //
    //TRANSPORT_ORDER_PAY( AccountRecordTypeEnum.SUB,"运输订单支付"),
    //TRANSPORT_ORDER_RETURN( AccountRecordTypeEnum.ADD,"运输订单退款"),
    //
    //RECHARGE_ORDER(AccountRecordTypeEnum.ADD,"微信充值"),
    //RECHARGE_DEPOSIT_ORDER(AccountRecordTypeEnum.ADD,"缴纳保证金"),
    //UNFROZEN_FUNDS_ADD(AccountRecordTypeEnum.ADD,"资金解冻增加"),
    //UNFROZEN_FUNDS_SUB(AccountRecordTypeEnum.SUB,"资金解冻扣减"),
    //
    //WITHDRAWAL_APPEAL(AccountRecordTypeEnum.SUB,"提现申请"),
    //WITHDRAWAL_APPEAL_BACK(AccountRecordTypeEnum.ADD,"提现退还"),
    //
    //CONSUMPTION_REBATE_ADD(AccountRecordTypeEnum.ADD,"消费佣金"),
    //CONSUMPTION_REBATE_SUB(AccountRecordTypeEnum.SUB,"消费佣金"),
    //
    //STORE_COMMISSION_ADD(AccountRecordTypeEnum.ADD,"店铺佣金"),
    //STORE_COMMISSION_SUB(AccountRecordTypeEnum.SUB,"店铺佣金"),
    //
    //MANAGE_ADD_BALANCE(AccountRecordTypeEnum.ADD,"人工增加余额"),
    //MANAGE_SUB_BALANCE(AccountRecordTypeEnum.SUB,"人工扣减余额"),
    //MANAGE_ADD_OUTSTANDING_BALANCE(AccountRecordTypeEnum.ADD,"人工增加未结算金额"),
    //MANAGE_SUB_OUTSTANDING_BALANCE(AccountRecordTypeEnum.SUB,"人工扣减未结算金额"),
    //
    //EXCHANGE_POINTS_SUB(AccountRecordTypeEnum.SUB,"余额兑换积分扣减"),
    //EXCHANGE_POINTS_ADD(AccountRecordTypeEnum.ADD,"余额兑换积分增加"),
    //POINT_RECHARGE(AccountRecordTypeEnum.ADD, "积分充值"),
    //SIGN_IN_SEND_POINT(AccountRecordTypeEnum.ADD,"签到送积分"),
    //PAY_ORDER_SEND_POINT(AccountRecordTypeEnum.ADD,"消费送积分"),
    //ORDER_BACK_SUB_POINT(AccountRecordTypeEnum.SUB,"退款扣积分"),
    //GIVE_GIFT_SUB_POINT(AccountRecordTypeEnum.SUB,"送礼物扣积分")

    private String header_group_name;
    private String header_group_income;

    private String id;
    private String createdAt;
    private String orderType;
    private String orderNo;
    private Float balanceBegin;
    private Float balanceChange;
    private Float balanceEnd;
    private String body;
    private String recordTypeName;
    private String orderTypeName;
    private String orderTypeIcon;

    public String getOrderTypeIcon() {
        return orderTypeIcon;
    }

    public void setOrderTypeIcon(String orderTypeIcon) {
        this.orderTypeIcon = orderTypeIcon;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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
