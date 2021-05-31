package com.xaqinren.healthyelders.uniApp.bean;

public class UniBean {

    private String id;
    private String createdAt;
    private String merchantId;
    private String appId;
    private String appName;
    private String appLogo;
    private Boolean putApplet;
    private Boolean autoUpdateApplet;
    private NewAppVersionDTO newAppVersion;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppLogo() {
        return appLogo;
    }

    public void setAppLogo(String appLogo) {
        this.appLogo = appLogo;
    }

    public Boolean getPutApplet() {
        return putApplet;
    }

    public void setPutApplet(Boolean putApplet) {
        this.putApplet = putApplet;
    }

    public Boolean getAutoUpdateApplet() {
        return autoUpdateApplet;
    }

    public void setAutoUpdateApplet(Boolean autoUpdateApplet) {
        this.autoUpdateApplet = autoUpdateApplet;
    }

    public NewAppVersionDTO getNewAppVersion() {
        return newAppVersion;
    }

    public void setNewAppVersion(NewAppVersionDTO newAppVersion) {
        this.newAppVersion = newAppVersion;
    }

    public static class NewAppVersionDTO {
        private String id;
        private String createdAt;
        private String merchantId;
        private Long appletInfoId;
        private Integer versionNumber;
        private String resVersionNumber;
        private String upgradeUrl;
        private String upgradeContent;
        private String upgradeType;
        private String upgradeSystem;
        private String upgradeTypeName;
        private String upgradeSystemName;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getMerchantId() {
            return merchantId;
        }

        public void setMerchantId(String merchantId) {
            this.merchantId = merchantId;
        }

        public Long getAppletInfoId() {
            return appletInfoId;
        }

        public void setAppletInfoId(Long appletInfoId) {
            this.appletInfoId = appletInfoId;
        }

        public Integer getVersionNumber() {
            return versionNumber;
        }

        public void setVersionNumber(Integer versionNumber) {
            this.versionNumber = versionNumber;
        }

        public String getResVersionNumber() {
            return resVersionNumber;
        }

        public void setResVersionNumber(String resVersionNumber) {
            this.resVersionNumber = resVersionNumber;
        }

        public String getUpgradeUrl() {
            return upgradeUrl;
        }

        public void setUpgradeUrl(String upgradeUrl) {
            this.upgradeUrl = upgradeUrl;
        }

        public String getUpgradeContent() {
            return upgradeContent;
        }

        public void setUpgradeContent(String upgradeContent) {
            this.upgradeContent = upgradeContent;
        }

        public String getUpgradeType() {
            return upgradeType;
        }

        public void setUpgradeType(String upgradeType) {
            this.upgradeType = upgradeType;
        }

        public String getUpgradeSystem() {
            return upgradeSystem;
        }

        public void setUpgradeSystem(String upgradeSystem) {
            this.upgradeSystem = upgradeSystem;
        }

        public String getUpgradeTypeName() {
            return upgradeTypeName;
        }

        public void setUpgradeTypeName(String upgradeTypeName) {
            this.upgradeTypeName = upgradeTypeName;
        }

        public String getUpgradeSystemName() {
            return upgradeSystemName;
        }

        public void setUpgradeSystemName(String upgradeSystemName) {
            this.upgradeSystemName = upgradeSystemName;
        }
    }
}
