package com.xaqinren.healthyelders.moduleZhiBo.bean;

/**
 * Created by Lee. on 2021/5/7.
 * PK连线设置
 */
public class AnchorInteractionDto {

    public int pkDuration;
    public boolean canGiftToPkAnchor;
    public boolean canPk;
    public boolean canFriendsPk;
    public boolean canSearchPk;
    public boolean canConnection;
    public boolean canMultiplayerConnection;
    public boolean canNoFollowConnection;
    public boolean closedPkAnchorVoice;

    public void setPkDuration(int pkDuration) {
        this.pkDuration = pkDuration;
    }

    public int getPkDuration() {
        return pkDuration;
    }

    public void setCanGiftToPkAnchor(boolean canGiftToPkAnchor) {
        this.canGiftToPkAnchor = canGiftToPkAnchor;
    }

    public boolean getCanGiftToPkAnchor() {
        return canGiftToPkAnchor;
    }

    public void setCanPk(boolean canPk) {
        this.canPk = canPk;
    }

    public boolean getCanPk() {
        return canPk;
    }

    public void setCanFriendsPk(boolean canFriendsPk) {
        this.canFriendsPk = canFriendsPk;
    }

    public boolean getCanFriendsPk() {
        return canFriendsPk;
    }

    public void setCanSearchPk(boolean canSearchPk) {
        this.canSearchPk = canSearchPk;
    }

    public boolean getCanSearchPk() {
        return canSearchPk;
    }

    public void setCanConnection(boolean canConnection) {
        this.canConnection = canConnection;
    }

    public boolean getCanConnection() {
        return canConnection;
    }

    public void setCanMultiplayerConnection(boolean canMultiplayerConnection) {
        this.canMultiplayerConnection = canMultiplayerConnection;
    }

    public boolean getCanMultiplayerConnection() {
        return canMultiplayerConnection;
    }

    public void setCanNoFollowConnection(boolean canNoFollowConnection) {
        this.canNoFollowConnection = canNoFollowConnection;
    }

    public boolean getCanNoFollowConnection() {
        return canNoFollowConnection;
    }

    public void setClosedPkAnchorVoice(boolean closedPkAnchorVoice) {
        this.closedPkAnchorVoice = closedPkAnchorVoice;
    }

    public boolean getClosedPkAnchorVoice() {
        return closedPkAnchorVoice;
    }

}

