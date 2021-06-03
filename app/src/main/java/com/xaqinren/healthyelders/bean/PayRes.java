package com.xaqinren.healthyelders.bean;

/**
 * Created by Lee. on 2021/6/3.
 */
public class PayRes {
    public String appId;
    public String nonceStr;
    public String packageValue = "Sign=WXPay";
    public String partnerId;
    public String prepayId;
    public String sign;
    public String timeStamp;

    @Override
    public String toString() {
        return "PayRes{" +
                "appId='" + appId + '\'' +
                ", nonceStr='" + nonceStr + '\'' +
                ", packageValue='" + packageValue + '\'' +
                ", partnerId='" + partnerId + '\'' +
                ", prepayId='" + prepayId + '\'' +
                ", sign='" + sign + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                '}';
    }
}
