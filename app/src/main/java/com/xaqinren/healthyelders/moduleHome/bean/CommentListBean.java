package com.xaqinren.healthyelders.moduleHome.bean;


import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.ArrayList;
import java.util.List;

//视频评论类
public class CommentListBean implements MultiItemEntity {
    public String id;//
    public String createdAt;//
    public String merchantId;//
    public String shortVideoId;//
    public String shortVideoName;//
    public String userId;//
    public String nickname;//
    public String avatarUrl;//
    public String content;//
    public int favoriteCount;//
    public int commentCount;//
    public Object status;//
    public boolean hasShow;//
    public boolean del;//
    public int weights;//
    public boolean hasAuthor;//
    public boolean hasAuthorFavorite;//
    public List<CommentListBean> shortVideoCommentReplyList = new ArrayList<>();//
    public int viewType;
    public int lodaState;

    public int itemPage = 1;
    public int itemSize = 4;

    @Override
    public int getItemType() {
        return viewType;
    }
}
