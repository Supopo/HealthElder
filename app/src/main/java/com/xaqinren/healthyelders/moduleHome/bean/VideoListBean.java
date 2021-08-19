package com.xaqinren.healthyelders.moduleHome.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Lee. on 2021/5/18.
 */
public class VideoListBean implements Serializable {
    public List<VideoInfo> videoInfos;
    public int position;
    public int page = 1;
    public String tags = "";
    public int openType;//1-从首页直播列表打开 2-从附近（首页菜单视频页面）打开 3 我的-作品 4 我的-私密 5 我的-点赞
    public int isFollow;
}
