package com.xaqinren.healthyelders.moduleZhiBo.bean;

/**
 * Created by Lee. on 2021/4/24.
 * 底部列表pop菜单bean
 */
public class ListPopMenuBean {
    public String menuName;
    public float textSize;
    public int textStyle;
    public int textColor;

    public String subTitle;
    public float subTitleSize;
    public int subTitleStyle;
    public int subTitleColor;

    public ListPopMenuBean() {
    }
    public ListPopMenuBean(String menuName) {
        this.menuName = menuName;
    }
    public ListPopMenuBean(String menuName, int textColor) {
        this.menuName = menuName;
        this.textColor = textColor;
    }
    public ListPopMenuBean(String menuName, int textColor,float textSize) {
        this.menuName = menuName;
        this.textColor = textColor;
        this.textSize = textSize;
    }


}
