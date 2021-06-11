package com.xaqinren.healthyelders.moduleMsg.bean;

public class FriendBean extends MessageDetailBean{

    private String name;
    private String identity;
    private String nickname;
    private String avatarUrl;
    private String userId;
    private String mobileNumber;
    private ShareDTO share;

    public ShareDTO getShare() {
        return share;
    }

    public void setShare(ShareDTO share) {
        this.share = share;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getId() {
        return userId;
    }

    public void setId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getNickname() {
        return nickname;
    }

    public String getShowName() {
        return nickname == null ? name : nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    @Override
    public int getItemType() {
        return TYPE_FRIEND;
    }

    public static class ShareDTO {
        private String recommendedCode;
        private String smsTemplate;
        private String title;
        private String url;
        private String introduce;

        public String getRecommendedCode() {
            return recommendedCode;
        }

        public void setRecommendedCode(String recommendedCode) {
            this.recommendedCode = recommendedCode;
        }

        public String getSmsTemplate() {
            return smsTemplate;
        }

        public void setSmsTemplate(String smsTemplate) {
            this.smsTemplate = smsTemplate;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getIntroduce() {
            return introduce;
        }

        public void setIntroduce(String introduce) {
            this.introduce = introduce;
        }
    }
}
