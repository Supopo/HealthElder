package com.xaqinren.healthyelders.bean;

import java.util.HashMap;

public class SlideParamBean {
    private String function;
    private String page;
    private HashMap<String,Object> param;

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public HashMap<String, Object> getParam() {
        return param;
    }

    public void setParam(HashMap<String, Object> param) {
        this.param = param;
    }
}
