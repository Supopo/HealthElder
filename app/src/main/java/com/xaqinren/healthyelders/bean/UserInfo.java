package com.xaqinren.healthyelders.bean;

/**
 * Created by Lee. on 2021/4/25.
 * 用户信息类
 */
public class UserInfo {
    public String avatarUrl;//
    public String mobileNumber;//"15667073887",
    public String sex;//"MALE"
    public int levelDiscount;//10
    public int consumptionAmount;//0
    public boolean defaultLevel;//true
    public String levelImage;//"https:\/\/img.qianniux.com\/commodity-default.jpg"
    public String levelName;//"VIP_1"
    public boolean hasRealName;//false
    public String recommendedCode;//"X0UDW25J",
    public boolean enable;//true,
    public WlletAccount walletAccount;//
    public String nickname;//"苏坡坡要吃婆婆酥。",
    public String cumulativeAmount;//0,
    public String id;//"1386137830790533120"

    public class WlletAccount {
        public int depositAmount;
        public int outstandingAmount;
        public int withdrawalAmount;
        public int accountBalance;
    }
}
