package com.xaqinren.healthyelders.moduleHome.bean;


import android.text.TextUtils;

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
    public boolean hasFavorite;//
    public int commentCount;//
    public Object status;//
    public boolean hasShow;//
    public boolean del;//
    public int weights;//
    public boolean hasAuthor;//
    public boolean hasAuthorFavorite;//
    public List<CommentListBean> shortVideoCommentReplyList = new ArrayList<>();//
    public List<CommentListBean> replyList = new ArrayList<>();//
    public int viewType;
    public int lodaState;//自定义加载状态

    public int itemPage = 1;
    public int itemSize = 4;

    @Override
    public int getItemType() {
        return viewType;
    }

    public int parentPos;

    //评论回复数据格式
    public String commentId;//
    public String replyTypeEnum;// REPLY_REPLY 回复回复  REPLY_COMMENT("回复评论"),
    public String targetId;//
    public String fromUserId;//
    public String fromUsername;//
    public String fromAvatarUrl;//
    public String toUserId;//
    public String toUsername;//
    public String toAvatarUrl;//

    public String getShowName() {
        if (!TextUtils.isEmpty(fromUserId) && !TextUtils.isEmpty(toUserId) &&!TextUtils.isEmpty(replyTypeEnum)) {
            if ((!fromUserId.equals(toUserId)) && replyTypeEnum.equals("REPLY_REPLY")) {
                return fromUsername + " ▶ " + toUsername;
            }
        }
        return fromUsername;
    }

}
