package com.xaqinren.healthyelders.modulePicture.bean;

import com.google.gson.annotations.SerializedName;
import com.xaqinren.healthyelders.moduleLiteav.bean.PublishSummaryBean;

import java.util.List;

public class PublishBean {

    public String title;
    public String coverUrl;
    public PublishSummaryBean summary;
    public String longitude;
    public String latitude;
    public String address;
    public List<String> bannerImages;
    public String province;
    public String city;
    public String district;
}
