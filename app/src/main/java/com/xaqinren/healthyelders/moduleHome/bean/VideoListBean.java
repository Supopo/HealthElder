package com.xaqinren.healthyelders.moduleHome.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Lee. on 2021/5/18.
 */
public class VideoListBean implements Serializable {
    public List<VideoInfo> videoInfos;
    public int position;
    public int page;
    public int type;//2-从附近打开 0-从推荐打开
}