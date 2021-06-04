package com.xaqinren.healthyelders.moduleZhiBo.bean;

import java.io.Serializable;

/**
 * Created by Lee. on 2021/6/4.
 * 直播间等级设置
 */
public class LiveLevelInfo implements Serializable {
    public String id;//": "1385826311190527490",
    public String createdAt;//": "2021-04-24 13:21",
    public String merchantId;//": "1378618860613144576",
    public String levelCode;//": "1",
    public String levelName;//": "默认等级",
    public String levelIcon;//": "http:\/\/sss.png",

    public Boolean getCanSale() {
        return canSale == null ? false : canSale;
    }

    public void setCanSale(Boolean canSale) {
        this.canSale = canSale;
    }

    public Boolean getCanMic() {
        return canMic == null ? false : canMic;
    }

    public void setCanMic(Boolean canMic) {
        this.canMic = canMic;
    }

    public Boolean getCanComment() {
        return canComment == null ? false : canComment;
    }

    public void setCanComment(Boolean canComment) {
        this.canComment = canComment;
    }

    public Boolean getCanGift() {
        return canGift == null ? false : canGift;
    }

    public void setCanGift(Boolean canGift) {
        this.canGift = canGift;
    }

    public Boolean getDefaultLevel() {
        return defaultLevel == null ? false : defaultLevel;
    }

    public void setDefaultLevel(Boolean defaultLevel) {
        this.defaultLevel = defaultLevel;
    }

    public Boolean canSale;//": false,
    public Boolean canMic;//": true,
    public Boolean canComment;//": true,
    public Boolean canGift;//": true,
    public int rankBonuses;//": 0,
    public String recommendOneLevelIds;//": null,
    public int recommendOneLevelBonuses;//": 0,
    public Boolean defaultLevel;//": true


}
