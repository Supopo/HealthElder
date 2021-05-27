package com.xaqinren.healthyelders.push;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PayLoadBean {

//    SYSTEM_NOTIFICATION("NOTIFICATION", "SYSTEM", "系统通知"),
//    ATTENTION("ATTENTION", "FANS", "关注"),
//    CREATION_FAVORITE("FAVORITE", "INTERACTIVE_MESSAGE", "互动消息-作品点赞"),
//    COMMENT_FAVORITE("FAVORITE", "INTERACTIVE_MESSAGE", "互动消息-评论赞"),
//    REPLY_FAVORITE("FAVORITE", "INTERACTIVE_MESSAGE", "互动消息-回复赞"),
//    CREATION_AT("AT", "INTERACTIVE_MESSAGE","互动消息-作品中@"),
//    COMMENT_AT("AT", "INTERACTIVE_MESSAGE","互动消息-评论中@"),
//    REPLY_AT("AT", "INTERACTIVE_MESSAGE","互动消息-点赞中@"),
//    INTERACTIVE_MESSAGE_COMMENT("COMMENT", "INTERACTIVE_MESSAGE", "互动消息-评论"),
//    INTERACTIVE_MESSAGE_REPLY("REPLY", "INTERACTIVE_MESSAGE", "互动消息-回复评论"),
//    CUSTOMER_SERVICE_MESSAGE("MESSAGE", "CUSTOMER_SERVICE", "客服消息"),
//    SERVICE_NOTIFICATION("NOTIFICATION", "SERVICE", "服务通知"),
//    WALLET_NOTIFICATION("NOTIFICATION", "WALLET", "钱包通知"),
//    LIVE_NOTIFICATION("NOTIFICATION", "LIVE", "直播通知"),
//    LIVE_SERVICE_OPEN("OPEN", "LIVE", "直播服务开通")

    public static final String SYSTEM = "SYSTEM";
    public static final String FANS = "FANS";
    public static final String INTERACTIVE_MESSAGE = "INTERACTIVE_MESSAGE";
    public static final String CUSTOMER_SERVICE = "CUSTOMER_SERVICE";
    public static final String SERVICE = "SERVICE";
    public static final String WALLET = "WALLET";
    public static final String LIVE = "LIVE";

    public static String getNameByGroup(String name) {
        switch (name) {
            case SYSTEM:
                return "系统通知";
            case FANS:
                return "关注通知";
            case INTERACTIVE_MESSAGE:
                return "互动消息";
            case LIVE:
                return "直播通知";
            case SERVICE:
                return "服务通知";
            case WALLET:
                return "钱包通知";
            case CUSTOMER_SERVICE:
                return "客服消息";
        }
        return null;
    }

    @SerializedName("content")
    public ContentDTO content;
    @SerializedName("createdAt")
    public Long createdAt;
    @SerializedName("extra")
    public ExtraDTO extra;
    @SerializedName("id")
    public String id;
    @SerializedName("messageGroup")
    public String messageGroup;
    @SerializedName("messageType")
    public String messageType;
    @SerializedName("receiveUserIds")
    public List<String> receiveUserIds;
    @SerializedName("sendUser")
    public SendUserDTO sendUser;

    public static class ContentDTO {
        @SerializedName("body")
        public String body;
        @SerializedName("clickType")
        public String clickType;
        @SerializedName("logoUrl")
        public String logoUrl;
        @SerializedName("payload")
        public String payload;
        @SerializedName("title")
        public String title;
    }

    public static class ExtraDTO {
        @SerializedName("objectId")
        public String objectId;
    }

    public static class SendUserDTO {
        @SerializedName("avatarUrl")
        public String avatarUrl;
        @SerializedName("nickname")
        public String nickname;
        @SerializedName("userId")
        public Long userId;
    }
}
