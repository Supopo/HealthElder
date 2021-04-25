package com.xaqinren.healthyelders.bean;

import android.annotation.SuppressLint;
import android.content.Context;

/**
 * Created by Lee. on 2021/4/25.
 * 全局用户信息管理类
 */
public class UserInfoMgr {

    private static class UserInfoMgrIns {
        @SuppressLint("StaticFieldLeak")
        private static final UserInfoMgr instance = new UserInfoMgr();
    }

    public static UserInfoMgr getInstance() {
        return UserInfoMgrIns.instance;
    }

    private UserInfoMgr() {
    }

    public void initContext(Context context) {
        this.context = context.getApplicationContext();
    }

    private Context context;
    private UserInfo userInfo;
    private String avatarUrl;//
    private String mobileNumber;//"15667073887",
    private String sex;//"MALE"
    private int levelDiscount;//10
    private int consumptionAmount;//0
    private boolean defaultLevel;//true
    private String levelImage;//"https:\/\/img.qianniux.com\/commodity-default.jpg"
    private String levelName;//"VIP_1"
    private boolean hasRealName;//false
    private String recommendedCode;//"X0UDW25J",
    private boolean enable;//true,
    private UserInfo.WlletAccount walletAccount;//
    private String nickname;//"苏坡坡要吃婆婆酥。",
    private String cumulativeAmount;//0,
    private String id;//"1386137830790533120"

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getLevelDiscount() {
        return levelDiscount;
    }

    public void setLevelDiscount(int levelDiscount) {
        this.levelDiscount = levelDiscount;
    }

    public int getConsumptionAmount() {
        return consumptionAmount;
    }

    public void setConsumptionAmount(int consumptionAmount) {
        this.consumptionAmount = consumptionAmount;
    }

    public boolean isDefaultLevel() {
        return defaultLevel;
    }

    public void setDefaultLevel(boolean defaultLevel) {
        this.defaultLevel = defaultLevel;
    }

    public String getLevelImage() {
        return levelImage;
    }

    public void setLevelImage(String levelImage) {
        this.levelImage = levelImage;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public boolean isHasRealName() {
        return hasRealName;
    }

    public void setHasRealName(boolean hasRealName) {
        this.hasRealName = hasRealName;
    }

    public String getRecommendedCode() {
        return recommendedCode;
    }

    public void setRecommendedCode(String recommendedCode) {
        this.recommendedCode = recommendedCode;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public UserInfo.WlletAccount getWalletAccount() {
        return walletAccount;
    }

    public void setWalletAccount(UserInfo.WlletAccount walletAccount) {
        this.walletAccount = walletAccount;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getCumulativeAmount() {
        return cumulativeAmount;
    }

    public void setCumulativeAmount(String cumulativeAmount) {
        this.cumulativeAmount = cumulativeAmount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


}
