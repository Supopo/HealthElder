package com.xaqinren.healthyelders.moduleHome.bean;

import java.io.Serializable;

/**
 * Created by Lee. on 2021/5/31.
 */
public class ShareBean implements Serializable {
    public String id;
    public String coverUrl;
    public String title;
    public String subTitle;
    public String introduce;
    public String logoUrl;
    public String url;
    public String downUrl;//解密之后的地址
    public String oldUrl;//解密之前地址

    public String userNickname;
    public String userAvatar;
    public String resourceId;
    public boolean hasRoomPwd;

}
