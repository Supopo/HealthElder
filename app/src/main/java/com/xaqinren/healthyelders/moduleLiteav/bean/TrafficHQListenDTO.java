package com.xaqinren.healthyelders.moduleLiteav.bean;

import com.google.gson.annotations.SerializedName;

public class TrafficHQListenDTO {

    @SerializedName("fileUrl")
    public String fileUrl;
    @SerializedName("expires")
    public Long expires;
    @SerializedName("waveUrl")
    public String waveUrl;
    @SerializedName("musicId")
    public String musicId;
    @SerializedName("dynamicLyricUrl")
    public String dynamicLyricUrl;
    @SerializedName("staticLyricUrl")
    public String staticLyricUrl;
    @SerializedName("fileSize")
    public Integer fileSize;
}
