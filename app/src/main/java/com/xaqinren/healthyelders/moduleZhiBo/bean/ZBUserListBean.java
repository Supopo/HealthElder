package com.xaqinren.healthyelders.moduleZhiBo.bean;

import com.xaqinren.healthyelders.utils.Num2TextUtil;

import java.io.Serializable;

/**
 * 主播用户列表
 */
public class ZBUserListBean implements Serializable {
    public String id;
    public String createdAt;
    public String merchantId;
    public String liveRoomId;
    public String liveRoomRecordId;
    public String userId;
    public String nickname;
    public String avatarUrl;
    public int sortOrder;
    public boolean hasSpeech;//是否被禁言
    public boolean hasAway;
    public String identity;
    public boolean hasMic;
    public String sex;//"MALE",
    public String leaveTime;//"MALE",
    public String identityName;//"陌生人"
    public boolean hasFansTeam;//是否加入粉丝团
    public String levelCode;//
    public String levelName;//
    public String fansTeamName;//粉丝团名字
    public int active;//活跃度
    public String viewerDuration;//用户看播时间

    public boolean showFollow() {
        if (identity != null && (identity.equals("FOLLOW") || identity.equals("FRIEND"))) {
            return false;
        }
        return true;
    }

    public String operatorNickname;//操作

    public String getOperatorNickname() {
        return operatorNickname + " 处理";
    }


    public int position;//麦序
    public boolean hasProsody;//连麦是否静音

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

    public String contribution;//贡献值

    public String getContribution() {
        return "贡献值" + Num2TextUtil.sNum2Text2(contribution);
    }

    public ZBUserListBean attentionUserInfo;//用户信息
    public String attentionUserId;//用户id

    public int showType;
}
