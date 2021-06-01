package com.xaqinren.healthyelders.moduleZhiBo.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lee. on 2021/6/1.
 * 礼物
 */
public class GiftBean {
    public String id;//": "1213213",
    public String giftName;//": "黄金车队",
    public String giftImage;//": "http://qinren.oss-cn-hangzhou.aliyuncs.com/20210330/a48f9fa85f5c40c98125946bf52e8290.png",
    public String giftUrl;//": "http://qinren.oss-cn-hangzhou.aliyuncs.com/20210330/2e80cf5cb3ae4e4991fe4279fed78322.svga",
    public String giftCode;//": "1254440",
    public String giftPrice;//": 3599,
    public String hasGroup;//": true,
    public String hasAnimation;//": true,
    public String giftGroup;//": "GIFT_GROUP"
    public List<GiftBean> giftBeans = new ArrayList<>();

    public int lastPos;
    public int nowPos;
    public boolean isSelect;

}
