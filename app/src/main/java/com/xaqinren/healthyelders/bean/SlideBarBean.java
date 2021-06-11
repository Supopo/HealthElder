package com.xaqinren.healthyelders.bean;

import java.util.List;

public class SlideBarBean {

    private String groupName;
    private String groupCode;
    private String groupDesc;
    private List<MenuInfoListDTO> menuInfoList;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getGroupDesc() {
        return groupDesc;
    }

    public void setGroupDesc(String groupDesc) {
        this.groupDesc = groupDesc;
    }

    public List<MenuInfoListDTO> getMenuInfoList() {
        return menuInfoList;
    }

    public void setMenuInfoList(List<MenuInfoListDTO> menuInfoList) {
        this.menuInfoList = menuInfoList;
    }

    public static class MenuInfoListDTO {
        private String menuName;
        private String subMenuName;
        private String subFontColor;
        private String backgroundColor;
        private String fontColor;
        private String icon;
        private String jumpUrl;
        private String eventType;
        private String event;
        private Boolean onlyShowImage;
        private String imageUrl;
        private Integer sortOrder;

        public String getMenuName() {
            return menuName;
        }

        public void setMenuName(String menuName) {
            this.menuName = menuName;
        }

        public String getSubMenuName() {
            return subMenuName;
        }

        public void setSubMenuName(String subMenuName) {
            this.subMenuName = subMenuName;
        }

        public String getSubFontColor() {
            return subFontColor;
        }

        public void setSubFontColor(String subFontColor) {
            this.subFontColor = subFontColor;
        }

        public String getBackgroundColor() {
            return backgroundColor;
        }

        public void setBackgroundColor(String backgroundColor) {
            this.backgroundColor = backgroundColor;
        }

        public String getFontColor() {
            return fontColor;
        }

        public void setFontColor(String fontColor) {
            this.fontColor = fontColor;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getJumpUrl() {
            return jumpUrl;
        }

        public void setJumpUrl(String jumpUrl) {
            this.jumpUrl = jumpUrl;
        }

        public String getEventType() {
            return eventType;
        }

        public void setEventType(String eventType) {
            this.eventType = eventType;
        }

        public String getEvent() {
            return event;
        }

        public void setEvent(String event) {
            this.event = event;
        }

        public Boolean getOnlyShowImage() {
            return onlyShowImage;
        }

        public void setOnlyShowImage(Boolean onlyShowImage) {
            this.onlyShowImage = onlyShowImage;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public Integer getSortOrder() {
            return sortOrder;
        }

        public void setSortOrder(Integer sortOrder) {
            this.sortOrder = sortOrder;
        }
    }
}
