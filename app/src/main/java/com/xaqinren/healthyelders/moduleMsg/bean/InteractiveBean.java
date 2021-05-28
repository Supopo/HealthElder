package com.xaqinren.healthyelders.moduleMsg.bean;

import com.xaqinren.healthyelders.push.PayLoadBean;

import java.util.List;

public class InteractiveBean extends MessageDetailBean{

    private String id;
    private String createdAt;
    private String merchantId;
    private SendUserBean sendUser;
    private List<String> receiveUserIds;
    private MsgContentBean content;
    private ExtraDTO extra;
    private String messageType;
    private String messageGroup;




    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public SendUserBean getSendUser() {
        return sendUser;
    }

    public void setSendUser(SendUserBean sendUser) {
        this.sendUser = sendUser;
    }

    public List<String> getReceiveUserIds() {
        return receiveUserIds;
    }

    public void setReceiveUserIds(List<String> receiveUserIds) {
        this.receiveUserIds = receiveUserIds;
    }

    public MsgContentBean getContent() {
        return content;
    }

    public void setContent(MsgContentBean content) {
        this.content = content;
    }

    public ExtraDTO getExtra() {
        return extra;
    }

    public void setExtra(ExtraDTO extra) {
        this.extra = extra;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getMessageGroup() {
        return messageGroup;
    }

    public void setMessageGroup(String messageGroup) {
        this.messageGroup = messageGroup;
    }

    @Override
    public int getItemType() {
        return MessageDetailBean.TYPE_TOP;
    }

    public static class ExtraDTO {
        private String objectId;
        private String id;
        private String creationType;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCreationType() {
            return creationType;
        }

        public void setCreationType(String creationType) {
            this.creationType = creationType;
        }

        public String getObjectId() {
            return objectId;
        }

        public void setObjectId(String objectId) {
            this.objectId = objectId;
        }
    }
}
