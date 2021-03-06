package com.xaqinren.healthyelders.moduleLogin.bean;

import java.io.Serializable;

public class WalletAccountBean implements Serializable {

    private int depositAmount;// 保证金金额
    private int outstandingAmount;// 未结算金额
    private int withdrawalAmount;//提款金额
    private int accountBalance;//账户余额

    public int getDepositAmount() {
        return depositAmount;
    }

    public void setDepositAmount(int depositAmount) {
        this.depositAmount = depositAmount;
    }

    public int getOutstandingAmount() {
        return outstandingAmount;
    }

    public void setOutstandingAmount(int outstandingAmount) {
        this.outstandingAmount = outstandingAmount;
    }

    public int getWithdrawalAmount() {
        return withdrawalAmount;
    }

    public void setWithdrawalAmount(int withdrawalAmount) {
        this.withdrawalAmount = withdrawalAmount;
    }

    public int getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(int accountBalance) {
        this.accountBalance = accountBalance;
    }
}
