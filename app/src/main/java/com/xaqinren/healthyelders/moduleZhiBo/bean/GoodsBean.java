package com.xaqinren.healthyelders.moduleZhiBo.bean;

import com.xaqinren.healthyelders.utils.ColorsUtils;
import com.xaqinren.healthyelders.utils.UrlUtils;
import com.xaqinren.healthyelders.widget.pickerView.cityPicker.utils.StringUtils;

import java.math.BigDecimal;

/**
 * Created by Lee. on 2021/4/21.
 */
public class GoodsBean {
    public int pos;//
    public String content;//
    public String cover;//
    public String name;//
    public boolean isSelect;//
    public int type;//0-开启直播 1-主播页面 2-观众页面

    public int getPlaceholderRes() {
        return ColorsUtils.randomColor();
    }

    public String id;//": "1397124741906571264",
    public String createdAt;//": "2021-05-25 17:37:50",
    public String merchantId;//": "1378618860613144576",
    public String storeId;//": "1329045982251323392",
    public String storeName;//": null,
    public String storeLogo;//": null,
    public String brandId;//": "1262971655190224896",
    public String brandName;//": null,
    public String commodityTypeId;//": "1395619916844900352",
    public String commodityCategory;//": "女装,妈妈装",
    public String commodityTags;//": "",
    public String imageUrls;//": "http://oss.hjyiyuanjiankang.com/qnx0/M00/00/0D/rBBcQmCsxRKAQGb1AAK35_yfleU118.jpg",
    public String imageUrl;//": "http://oss.hjyiyuanjiankang.com/qnx0/M00/00/0D/rBBcQmCsxRKAQGb1AAK35_yfleU118.jpg",
    public String commodityCode;//": "C00000163",
    public String spellCode;//": "zhong lao nian jia fei da ma nv zhuang xia ji xin kuan shi shang tao zhuang ma ma zhuang kuan song shang yi kuo tui ku liang jian tao",
    public String title;//": "中老年加肥大码女装夏季新款时尚套装妈妈装宽松上衣阔腿裤两件套",
    public String subtitle;//": "妈妈装宽松女装",
    public int sortOrder;//": 1,
    public String userLevelIds;//": "",
    public String status;//": null,
    public boolean hasDiscount;//": false,
    public boolean hasRebate;//": false,
    public boolean hasFree;//": false,
    public boolean consumptionAccumulation;//": false,
    public int favoriteCount;//": 0,
    public int messageCount;//": 0,
    public int totalSoldCount;//": 0,
    public double minSalesPrice;//": 89.1,
    public double maxSalesPrice;//": 99

    public String appId;//": 99
    public String jumpUrl;//": 99

    public Boolean getCanExplain() {
        return canExplain == null ? false : canExplain;
    }

    public void setCanExplain(Boolean canExplain) {
        this.canExplain = canExplain;
    }

    public Boolean canExplain;//是否在讲解


    public String getMaxSalesPrice() {
        BigDecimal maxPrice = new BigDecimal(maxSalesPrice).setScale(2, 1);
        return String.valueOf(maxPrice);
    }

    public String getMinSalesPrice() {
        BigDecimal minPrice = new BigDecimal(minSalesPrice).setScale(2, 1);
        return String.valueOf(minPrice);
    }

    public String getImageUrl() {
        return UrlUtils.resetImgUrl(imageUrl, 400, 400);
    }
}
