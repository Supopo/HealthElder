package com.xaqinren.healthyelders.moduleMine.bean;

public class EditMenuBean {
    private String title;
    private String value;
    private boolean hasMore;
    private String tag;

    public EditMenuBean() {
    }

    public EditMenuBean(String title, String value, boolean hasMore, String tag) {
        this.title = title;
        this.value = value;
        this.hasMore = hasMore;
        this.tag = tag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
