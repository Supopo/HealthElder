package com.xaqinren.healthyelders.moduleZhiBo.bean;


import android.text.TextUtils;

import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.LiveConstants;


/**
 * Module:   TCChatEntity
 * <p>
 * Function: 消息载体类。
 */
public class TCChatEntity {
    private String userId;    // 发送者的Id
    private String sendName;    // 发送者的名字
    private String content;        // 消息内容
    private int type;            // 消息类型
    private String levelName;            // 用户等级
    private String levelIcon;

    public String getLevelIcon() {
        return levelIcon;
    }

    public void setLevelIcon(String levelIcon) {
        this.levelIcon = levelIcon;
    }



    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSendName() {
        return sendName;
    }

    public void setSendName(String sendName) {
        this.sendName = sendName;
    }

    public String getLevelName() {
        if (TextUtils.isEmpty(levelName)) {
            return "0";
        }
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }



    public String getSenderName() {
        if (TextUtils.isEmpty(sendName)) {
            return LiveConstants.NIKENAME;
        }

        return sendName;
    }

    public void setSenderName(String grpSendName) {
        this.sendName = grpSendName;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String context) {
        this.content = context;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof TCChatEntity))
            return false;

        TCChatEntity that = (TCChatEntity) o;

        if (getType() != that.getType())
            return false;
        if (sendName != null ? !sendName.equals(that.sendName) : that.sendName != null)
            return false;
        return getContent() != null ? getContent().equals(that.getContent()) : that.getContent() == null;

    }

    @Override
    public int hashCode() {
        int result = sendName != null ? sendName.hashCode() : 0;
        result = 31 * result + (getContent() != null ? getContent().hashCode() : 0);
        result = 31 * result + getType();
        return result;
    }
}
