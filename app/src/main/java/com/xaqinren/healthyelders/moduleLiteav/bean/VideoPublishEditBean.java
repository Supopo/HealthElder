package com.xaqinren.healthyelders.moduleLiteav.bean;

public class VideoPublishEditBean {
    private int startPoint;
    private int endPoint;
    private int textType;
    private boolean isBlock;
    private String content;

    public static final int TOPIC_TYPE = 1;
    public static final int AT_TYPE = 2;


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(int startPoint) {
        this.startPoint = startPoint;
    }

    public int getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(int endPoint) {
        this.endPoint = endPoint;
    }

    public int getTextType() {
        return textType;
    }

    public void setTextType(int textType) {
        this.textType = textType;
    }

    public boolean getIsBlock() {
        return isBlock;
    }

    public void setIsBlock(boolean isBlock) {
        this.isBlock = isBlock;
    }
}
