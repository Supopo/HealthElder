package com.xaqinren.healthyelders.widget.comment;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.List;

public class ICommentBean implements MultiItemEntity {

    public String videoId;

    public String id;

    public String parentId;

    public String avatar;

    public String nickName;

    public String comment;

    public String commentTime;

    public String likeCount;

    public boolean isLike;

    public boolean canComment;

    public String commentUserId;//评论目标的人

    public String commentUserNickName;//评论目标的人

    public List<ICommentBean> childComment;

    public int commentCount;

    public int viewType;

    @Override
    public int getItemType() {
        return viewType;
    }
}
