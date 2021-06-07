package com.xaqinren.healthyelders.moduleLogin.bean;

import android.text.TextUtils;

import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.moduleHome.bean.MenuBean;

import java.io.Serializable;
import java.util.List;

public class UserInfoBean implements Serializable {

    private String avatarUrl;
    private String mobileNumber;
    private String sex;
    private int levelDiscount;
    private Boolean defaultLevel;
    private String levelImage;
    private String levelName;
    private Boolean hasRealName;
    private String recommendedCode;
    private String birthday;
    private Boolean enable;
    private WalletAccountBean walletAccount;
    private String nickname;
    private String realName;
    private int cumulativeAmount;
    private int consumptionAmount;
    private String id;
    private double pointAccountBalance;//金币余额
    private double wallAccountBalance;//钱包
    private int favoriteCount;
    private int fansCount;
    private int attentionCount;
    private int bankCardCount;
    private int pointAmount;

    private MenuBean menu;
    private Boolean hasOpenAccount;
    private List<Object> userBankCardList;

    private String introduce;
    private String cityAddress;

    private Boolean hasAdmin;//是否管理员
    private Boolean hasSpeech;
    private Boolean hasAttention;

    public boolean hasFollow;

    public MenuBean getMenu() {
        return menu;
    }

    public void setMenu(MenuBean menu) {
        this.menu = menu;
    }

    public Boolean getHasOpenAccount() {
        return hasOpenAccount;
    }

    public void setHasOpenAccount(Boolean hasOpenAccount) {
        this.hasOpenAccount = hasOpenAccount;
    }

    public List<Object> getUserBankCardList() {
        return userBankCardList;
    }

    public void setUserBankCardList(List<Object> userBankCardList) {
        this.userBankCardList = userBankCardList;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    private String identity;//": "STRANGER",

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public int getBankCardCount() {
        return bankCardCount;
    }

    public void setBankCardCount(int bankCardCount) {
        this.bankCardCount = bankCardCount;
    }

    public int getPointAmount() {
        return pointAmount;
    }

    public void setPointAmount(int pointAmount) {
        this.pointAmount = pointAmount;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public Double getPointAccountBalance() {
        return pointAccountBalance;
    }

    public void setPointAccountBalance(Double pointAccountBalance) {
        this.pointAccountBalance = pointAccountBalance;
    }

    public Double getWallAccountBalance() {
        return wallAccountBalance;
    }

    public void setWallAccountBalance(Double wallAccountBalance) {
        this.wallAccountBalance = wallAccountBalance;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getCityAddress() {
        return cityAddress;
    }

    public void setCityAddress(String cityAddress) {
        this.cityAddress = cityAddress;
    }


    public int getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(int favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public int getFansCount() {
        return fansCount;
    }

    public void setFansCount(int fansCount) {
        this.fansCount = fansCount;
    }

    public int getAttentionCount() {
        return attentionCount;
    }

    public void setAttentionCount(int attentionCount) {
        this.attentionCount = attentionCount;
    }

    public Boolean getHasAdmin() {
        return hasAdmin == null ? false : hasAdmin;
    }

    public void setHasAdmin(Boolean hasAdmin) {
        this.hasAdmin = hasAdmin;
    }


    public Boolean getHasSpeech() {
        return hasSpeech;
    }

    public void setHasSpeech(Boolean hasSpeech) {
        this.hasSpeech = hasSpeech;
    }

    public Boolean getHasAttention() {
        return hasAttention;
    }

    public void setHasAttention(Boolean hasAttention) {
        this.hasAttention = hasAttention;
    }


    public boolean isMan() {
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

    public String getAuthStatus() {
        return hasRealName ? "已认证" : "未认证";
    }

    public String getAuthSuccessRealName() {
        return "真实姓名: " + realName;
    }

    public String getAuthSuccessIdCard() {
        return "身份证号: " + realName;
    }


    public boolean getShowFollow() {
        if (!TextUtils.isEmpty(getIdentity()) && (getIdentity().equals("FRIEND") || getIdentity().equals("FOLLOW"))) {
            hasFollow = true;
            return false;
        }
        hasFollow = false;
        return true;
    }

    public boolean isMe() {
        if (UserInfoMgr.getInstance().getUserInfo() != null ) {
            if (getId().equals(UserInfoMgr.getInstance().getUserInfo().getId()) ) {
                return true;
            }
        }
        return false;
    }
}
