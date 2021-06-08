package com.xaqinren.healthyelders.moduleMine.bean;

import com.xaqinren.healthyelders.moduleLogin.bean.WalletAccountBean;

import java.io.Serializable;
import java.util.List;

public class WalletBean implements Serializable {

    public int getPointBalance() {
        return pointBalance;
    }

    public void setPointBalance(int pointBalance) {
        this.pointBalance = pointBalance;
    }

    public WalletAccountBean getWalletAccount() {
        return walletAccount;
    }

    public void setWalletAccount(WalletAccountBean walletAccount) {
        this.walletAccount = walletAccount;
    }

    private WalletAccountBean walletAccount;
    private int pointBalance;//金币余额
    private boolean hasOpenAccount;
    private List<BankCardBean> userBankCardList;

    private String wallAccountBalance;
    private String pointAccountBalance;


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

    public String getCardCount() {
        return this.userBankCardList == null ? "0" : userBankCardList.size() + "";
    }

}
