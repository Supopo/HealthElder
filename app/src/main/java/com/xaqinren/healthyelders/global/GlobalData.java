package com.xaqinren.healthyelders.global;

import com.xaqinren.healthyelders.bean.SlideBarBean;

public class GlobalData {

    private static GlobalData globalData = new GlobalData();
    public static GlobalData getInstance(){
        return globalData;
    }
    private GlobalData(){}

    private SlideBarBean slideBarBean;

    public void saveSlideBar(SlideBarBean slideBarBean) {
        this.slideBarBean = slideBarBean;
    }

    public SlideBarBean getSlideBar() {
        return this.slideBarBean;
    }
}
