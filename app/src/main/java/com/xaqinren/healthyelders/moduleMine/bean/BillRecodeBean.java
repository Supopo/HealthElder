package com.xaqinren.healthyelders.moduleMine.bean;

import java.util.List;

public class BillRecodeBean {
    private float income;
    private float expenditure;
    private String month;
    private List<BillBean> recordList;

    public float getIncome() {
        return income;
    }

    public void setIncome(float income) {
        this.income = income;
    }

    public float getExpenditure() {
        return expenditure;
    }

    public void setExpenditure(float expenditure) {
        this.expenditure = expenditure;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public List<BillBean> getRecordList() {
        return recordList;
    }

    public void setRecordList(List<BillBean> recordList) {
        this.recordList = recordList;
    }
}
