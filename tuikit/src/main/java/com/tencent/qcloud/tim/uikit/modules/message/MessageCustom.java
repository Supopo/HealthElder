package com.tencent.qcloud.tim.uikit.modules.message;

public class MessageCustom {
    public static final String BUSINESS_ID_GROUP_CREATE = "group_create";

    public int version = 0;
    public String businessID;
    public String opUser;
    public String content;
    public Object data;
    public String cmd;//用来判断过滤直播间的消息
    public int msgType;//用来判断自定义消息
}
