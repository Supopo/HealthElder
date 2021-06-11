package com.xaqinren.healthyelders.bean;

import java.util.HashMap;

public class SlideParamBean {
    private String function;
    private HashMap<String,String> param;

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public HashMap<String, String> getParam() {
        return param;
    }

    public void setParam(HashMap<String, String> param) {
        this.param = param;
    }
}
