package com.xaqinren.healthyelders.push;

import com.google.gson.annotations.SerializedName;
import com.xaqinren.healthyelders.moduleMsg.Constant;

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





    public static String getInteractiveMessageBody(PayLoadBean url) {
        if (url.messageType.equals(Constant.FAVORITE)) {
            return url.sendUser.nickname + url.content.body;
        } else if (url.messageType.equals(Constant.AT)) {
            return url.sendUser.nickname + url.content.title;
        } else if (url.messageType.equals(Constant.COMMENT)) {
            return url.sendUser.nickname + url.content.title;
        } else if (url.messageType.equals(Constant.REPLY)) {
            return url.sendUser.nickname + url.content.title;
        } else {
            return url.sendUser.nickname + url.content.body;
        }
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
