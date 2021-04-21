package com.xaqinren.healthyelders.moduleZhiBo.bean;

import com.xaqinren.healthyelders.utils.ColorsUtils;

/**
 * Created by Lee. on 2021/4/21.
 */
public class GoodsBean {
    public int id;//: Any?,
    public String content;//: Any?,
    public String cover;//: String?,
    public double curPrice;//: Double?,
    public String name;//: String?,
    public double oldPrice;//: Double?,
    public boolean isSelect;//:
    public int getPlaceholderRes() {
        return ColorsUtils.randomColor();
    }
}
