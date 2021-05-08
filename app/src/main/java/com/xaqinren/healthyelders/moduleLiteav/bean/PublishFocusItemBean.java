package com.xaqinren.healthyelders.moduleLiteav.bean;

import java.io.Serializable;

public class PublishFocusItemBean implements Serializable {
    public int start;
    public int end;
    public int type;
    public long uId;
    public static final int TOPIC_TYPE = 1;
    public static final int AT_TYPE = 2;
}
