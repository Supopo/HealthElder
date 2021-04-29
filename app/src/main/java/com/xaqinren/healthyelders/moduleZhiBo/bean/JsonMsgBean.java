package com.xaqinren.healthyelders.moduleZhiBo.bean;

import com.alibaba.fastjson.JSON;

/**
 * Created by Lee. on 2021/4/29.
 * 消息Json类
 */
public class JsonMsgBean {
    public JsonMsgBean(){} //如果定义了带参构造函数那么就要写一个无参构造函数,否则JSON反解析会报错
    public JsonMsgBean(String msgType, String nickname, String content) {
        this.msgType = msgType;
        this.nickname = nickname;
        this.content = content;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String msgType; //0拉黑 1踢出
    public String nickname;
    public String content;



    public static String json(String msgType, String nickname, String content) {
        return JSON.toJSONString(new JsonMsgBean(msgType, nickname, content));
    }
}
