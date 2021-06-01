package com.xaqinren.healthyelders.moduleMsg.bean;

import com.google.gson.annotations.SerializedName;

public class ContactsBean {
    private String name;
    private String mobile;
    private String photo;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
