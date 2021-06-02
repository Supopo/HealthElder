package com.xaqinren.healthyelders.moduleHome.bean;

import com.xaqinren.healthyelders.utils.ColorsUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lee. on 2021/5/15.
 * 菜单类
 */
public class MenuBean {
    public boolean isSelect;
    public String id;//": "1",
    public String createdAt;//": null,
    public String merchantId;//": "1",
    public String menuName;//": "全部分类",
    public String subMenuName;//": "精选好物",
    public String backgroundColor;//": "#ffffff",
    public String fontColor;//": "#000000",
    public String subFontColor;//": "#000000",
    public String icon;//": "https://img.qianniux.com/BankLogo/WeChat.png",
    public String jumpUrl;//": "https://xxxx",
    public String jumpType;//": "APPLET",
    public String appId;//": "APPLET",
    public boolean onlyShowImage;//": false,
    public String imageUrl;//": "https://img.qianniux.com/BankLogo/WeChat.png",
    public int sortOrder;//": 1,
    public String showChannel;//": "commodityType"
    public List<MenuBean> menuBeans = new ArrayList<>();
    public int type;
    public int menuRes;

    public int getPlaceholderRes() {
        return ColorsUtils.randomColor();
    }
    public MenuBean(){}
    public MenuBean(String menuName,int menuRes){
        this.menuName = menuName;
        this.menuRes = menuRes;
    }
}
