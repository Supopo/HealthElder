package com.xaqinren.healthyelders.moduleZhiBo.bean;

public class SendGiftBean {
    public String userId;
    public String giftsIcon;
    public String giftsName;
    public String svgaUrl;
    public String id;
    public String hasAnimation;
    public int num = 1;


    public String sendUserName;
    public String sendUserPhoto;

    public String getSvgaUrl() {
        return svgaUrl;
    }

    public void setSvgaUrl(String svgaUrl) {
        this.svgaUrl = svgaUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHasAnimation() {
        return hasAnimation;
    }

    public void setHasAnimation(String hasAnimation) {
        this.hasAnimation = hasAnimation;
    }

    public String getGiftsIcon() {
        return giftsIcon;
    }

    public void setGiftsIcon(String giftsIcon) {
        this.giftsIcon = giftsIcon;
    }

    public String getGiftsName() {
        return giftsName;
    }

    public void setGiftsName(String giftsName) {
        this.giftsName = giftsName;
    }

    public SendGiftBean() {

    }
}