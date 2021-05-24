package com.xaqinren.healthyelders.moduleLogin.bean;

public class UserInfoBean {

    private String avatarUrl;
    private String mobileNumber;
    private String sex;
    private int levelDiscount;
    private int consumptionAmount;
    private Boolean defaultLevel;
    private String levelImage;
    private String levelName;
    private Boolean hasRealName;
    private String recommendedCode;
    private Boolean enable;
    private WalletAccountBean walletAccount;
    private String nickname;
    private int cumulativeAmount;
    private String id;

    public boolean isMan(){
        if (sex.equals("MALE")) {
            return false;
        }
        return true;
    }

    public String getJKCode() {
        return "健康号:" + recommendedCode;
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

    public Boolean getDefaultLevel() {
        return defaultLevel;
    }

    public void setDefaultLevel(Boolean defaultLevel) {
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

    public Boolean getHasRealName() {
        return hasRealName;
    }

    public void setHasRealName(Boolean hasRealName) {
        this.hasRealName = hasRealName;
    }

    public String getRecommendedCode() {
        return recommendedCode;
    }

    public void setRecommendedCode(String recommendedCode) {
        this.recommendedCode = recommendedCode;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public WalletAccountBean getWalletAccount() {
        return walletAccount;
    }

    public void setWalletAccount(WalletAccountBean walletAccount) {
        this.walletAccount = walletAccount;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getCumulativeAmount() {
        return cumulativeAmount;
    }

    public void setCumulativeAmount(int cumulativeAmount) {
        this.cumulativeAmount = cumulativeAmount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
