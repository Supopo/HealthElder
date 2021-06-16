package com.xaqinren.healthyelders.moduleMsg.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public abstract class MessageDetailBean implements MultiItemEntity {
    public static final int TYPE_TOP = 0;
    public static final int TYPE_LOAD_MORE = 1;
    public static final int TYPE_TEXT = 2;
    public static final int TYPE_FRIEND = 3;
    public static final int TYPE_EMPTY = 4;
}
