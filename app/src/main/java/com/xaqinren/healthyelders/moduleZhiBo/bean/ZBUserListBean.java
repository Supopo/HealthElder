package com.xaqinren.healthyelders.moduleZhiBo.bean;

/**
 * 主播用户列表
 */
public class ZBUserListBean {
    public String id;
    public Object createdAt;
    public String merchantId;
    public String liveRoomId;
    public String liveRecordId;
    public String userId;
    public String nickname;
    public String avatarUrl;
    public int sortOrder;
    public boolean hasSpeech;//是否被禁言
    public boolean hasAway;
    public String identity;
    public boolean hasMic;
    public boolean hasProsody;
    public String sex;//"MALE",
    public String leaveTime;//"MALE",
    public String identityName;//"陌生人"
    public boolean hasFansTeam;//是否加入粉丝团
    public String levelCode;//
    public String levelName;//
    public String fansTeamName;//粉丝团名字
    public int active;//活跃度

    public String getLevelCode() {
        if (levelCode == null) {
            return "1";
        }
        return levelCode;
    }
    public String getLevelName() {
        if (levelName == null) {
            return "1";
        }
        return levelName;
    }

    public int linkPosition;
    public boolean isSelect;
    public int showTime;


}
