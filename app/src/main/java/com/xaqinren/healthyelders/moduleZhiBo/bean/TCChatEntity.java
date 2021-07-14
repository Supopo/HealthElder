package com.xaqinren.healthyelders.moduleZhiBo.bean;


import android.text.TextUtils;

import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.LiveConstants;


/**
 * Module:   TCChatEntity
 * <p>
 * Function: 消息载体类。
 */
public class TCChatEntity {
    private String sendName;    // 发送者的名字
    private String content;        // 消息内容
    private int type;            // 消息类型

    public String getSendName() {
        return sendName;
    }

    public void setSendName(String sendName) {
        this.sendName = sendName;
    }

    public String getLeaveName() {
        if (TextUtils.isEmpty(leaveName)) {
            return "1";
        }
        return leaveName;
    }

    public void setLeaveName(String leaveName) {
        this.leaveName = leaveName;
    }

    private String leaveName;            // 用户等级

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
