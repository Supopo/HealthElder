package com.xaqinren.healthyelders.moduleMine.bean;

import java.util.List;

public class WalletBean {
    private String wallAccountBalance;
    private String pointAccountBalance;
    private boolean hasOpenAccount;
    private List<BankCardBean> userBankCardList;

    public String getWallAccountBalance() {
        return wallAccountBalance;
    }

    public void setWallAccountBalance(String wallAccountBalance) {
        this.wallAccountBalance = wallAccountBalance;
    }

    public String getPointAccountBalance() {
        return pointAccountBalance;
    }

    public void setPointAccountBalance(String pointAccountBalance) {
        this.pointAccountBalance = pointAccountBalance;
    }

    public boolean isHasOpenAccount() {
        return hasOpenAccount;
    }

    public void setHasOpenAccount(boolean hasOpenAccount) {
        this.hasOpenAccount = hasOpenAccount;
    }

    public List<BankCardBean> getUserBankCardList() {
        return userBankCardList;
    }

    public void setUserBankCardList(List<BankCardBean> userBankCardList) {
        this.userBankCardList = userBankCardList;
    }

    public String getCardCount(){
        return this.userBankCardList == null ? "0" : userBankCardList.size()+"";
    }

}
