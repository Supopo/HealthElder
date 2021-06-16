package com.xaqinren.healthyelders.moduleMsg.bean;

import com.google.gson.annotations.SerializedName;

public class GroupIconBean {

    @SerializedName("FANS")
    private String fANS;
    @SerializedName("SYSTEM")
    private String sYSTEM;
    @SerializedName("WALLET")
    private String wALLET;
    @SerializedName("INTERACTIVE_MESSAGE")
    private String iNTERACTIVE_MESSAGE;
    @SerializedName("SERVICE")
    private String sERVICE;
    @SerializedName("CUSTOMER_SERVICE")
    private String cUSTOMER_SERVICE;
    @SerializedName("LIVE")
    private String lIVE;

    public String getfANS() {
        return fANS;
    }

    public void setfANS(String fANS) {
        this.fANS = fANS;
    }

    public String getsYSTEM() {
        return sYSTEM;
    }

    public void setsYSTEM(String sYSTEM) {
        this.sYSTEM = sYSTEM;
    }

    public String getwALLET() {
        return wALLET;
    }

    public void setwALLET(String wALLET) {
        this.wALLET = wALLET;
    }

    public String getiNTERACTIVE_MESSAGE() {
        return iNTERACTIVE_MESSAGE;
    }

    public void setiNTERACTIVE_MESSAGE(String iNTERACTIVE_MESSAGE) {
        this.iNTERACTIVE_MESSAGE = iNTERACTIVE_MESSAGE;
    }

    public String getsERVICE() {
        return sERVICE;
    }

    public void setsERVICE(String sERVICE) {
        this.sERVICE = sERVICE;
    }

    public String getcUSTOMER_SERVICE() {
        return cUSTOMER_SERVICE;
    }

    public void setcUSTOMER_SERVICE(String cUSTOMER_SERVICE) {
        this.cUSTOMER_SERVICE = cUSTOMER_SERVICE;
    }

    public String getlIVE() {
        return lIVE;
    }

    public void setlIVE(String lIVE) {
        this.lIVE = lIVE;
    }
}
