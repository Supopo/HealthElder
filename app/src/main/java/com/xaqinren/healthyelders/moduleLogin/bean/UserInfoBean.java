package com.xaqinren.healthyelders.moduleLogin.bean;

import android.text.TextUtils;

import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.moduleHome.bean.MenuBean;
import com.xaqinren.healthyelders.moduleMine.bean.WalletBean;
import com.xaqinren.healthyelders.utils.DateUtils;

import java.io.Serializable;
import java.text.ParseException;
import java.util.List;

import me.goldze.mvvmhabit.utils.StringUtils;
import me.goldze.mvvmhabit.utils.Utils;

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
    private String nickname;
    private String realName;
    private String id;
    private int favoriteCount;
    private int fansCount;
    private int attentionCount;
    private MenuBean menu;

    public boolean hasMobileNum() {
        if (TextUtils.isEmpty(mobileNumber)) {
            return false;
        }
        return true;
    }

    public WalletBean getAccountInfo() {
        return accountInfo;
    }

    public void setAccountInfo(WalletBean accountInfo) {
        this.accountInfo = accountInfo;
    }

    public boolean isHasFollow() {
        return hasFollow;
    }

    public void setHasFollow(boolean hasFollow) {
        this.hasFollow = hasFollow;
    }

    private WalletBean accountInfo;
    private List<Object> userBankCardList;
    private List<Object> storeList;


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

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }


    public String getIntroduce() {
        if (TextUtils.isEmpty(introduce)) {
            return "来一句地表最强的自我介绍吧！";
        }
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getCityAddress() {
        return cityAddress;
    }

    public boolean showCity() {
        if (TextUtils.isEmpty(cityAddress)) {
            return false;
        }
        return true;
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

    public String isHasMan() {
        String age = getAge();
        String cityAddress = getCityAddress();
        if (StringUtils.isEmpty(age) && StringUtils.isEmpty(cityAddress)) {
            boolean man = isMan();
            return man ? "男" : "女";
        }
        return null;
    }

    public String getSexRightText() {
        String age = getAge();
        String cityAddress = getCityAddress();
        if (!StringUtils.isEmpty(age)) {
            return age;
        }
        boolean man = isMan();
        return man ? "男" : "女";
    }

    public boolean isMan() {
        if (sex.equals("MALE")) {
            return true;
        }
        return false;
    }

    public String getJKCode() {
        return "健康号：" + recommendedCode;
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
        if (TextUtils.isEmpty(levelName)) {
            return "99";
        }
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public Boolean getHasRealName() {
        return hasRealName == null ? false : hasRealName;
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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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
        if (UserInfoMgr.getInstance().getUserInfo() != null) {
            if (getId().equals(UserInfoMgr.getInstance().getUserInfo().getId())) {
                return true;
            }
        }
        return false;
    }

    public String getAge() {
        if (birthday == null) {
            return null;
        }
        int ageByTime = 0;
        try {
            ageByTime = DateUtils.getAgeByTime(birthday);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (ageByTime == -1) {
            return null;
        }
        return ageByTime + "岁";
    }
}
