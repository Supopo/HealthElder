package com.xaqinren.healthyelders.moduleMine.bean;

import android.text.TextUtils;

/**
 * Created by Lee. on 2021/4/5.
 * 我的页面信息
 */
public class MinePageInfo {
    public Integer followCount;
    public String nature;// "和善,宽容,忠诚,热情,直率",
    public Integer sex;
    public Integer goldCoin;
    public Integer fansCount;
    public String synopsis;
    public Integer type;
    public Integer userId;
    public Integer zanCount;
    public String mineBackgroundImage;//null,
    public String birthplace;//"浙江省杭州市萧山区",
    public String nickname;
    public String headPortrait;
    public String shortCode;
    public String hobby;//: "旅游,看书,跳舞"


    public String getJKHCode() {
        return "健康号：" + shortCode;
    }

    public String getGRJJ() {
        if (!TextUtils.isEmpty(synopsis)) {
            return synopsis;
        }
        return "用一句话介绍自己";
    }

    public String getSexString() {
        if (sex == null || sex == 0)
            return "男";
        else
            return "女";
    }


}
