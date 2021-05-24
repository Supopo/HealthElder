package com.xaqinren.healthyelders.modulePicture.viewModel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.apiserver.LiteAvRepository;
import com.xaqinren.healthyelders.apiserver.LiveRepository;
import com.xaqinren.healthyelders.apiserver.UserRepository;
import com.xaqinren.healthyelders.moduleHome.bean.CommentListBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.LiteAvUserBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.PublishBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.SaveDraftBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.TopicBean;
import com.xaqinren.healthyelders.modulePicture.bean.DiaryInfoBean;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveInitInfo;

import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;

public class TextPhotoDetailViewModel extends BaseViewModel {
    public TextPhotoDetailViewModel(@NonNull Application application) {
        super(application);
    }
    public MutableLiveData<Boolean> requestSuccess = new MutableLiveData<>();
    public MutableLiveData<CommentListBean> commentSuccess = new MutableLiveData<>();
    public MutableLiveData<DiaryInfoBean> diaryInfo = new MutableLiveData<>();


    public MutableLiveData<List<CommentListBean>> commentList = new MutableLiveData<>();
    public MutableLiveData<CommentListBean> commentReplyList = new MutableLiveData<>();

    public MutableLiveData<Boolean> follow = new MutableLiveData<>();

    public MutableLiveData<Boolean> favorite = new MutableLiveData<>();

    //获取评论
    public void getCommentList(int page , String videoId) {
        LiteAvRepository.getInstance().getCommentList(commentList, page, videoId);
    }
    //获取对评论的评论
    public void getCommentReplyList(CommentListBean commentListBean) {
        LiteAvRepository.getInstance().getCommentReplyList(commentListBean, commentReplyList);
    }
    //点赞
    public void setCommentLike(String shortVideoId, String commentId, boolean favoriteStatus, boolean notRoot) {
        LiteAvRepository.getInstance().toLikeComment(shortVideoId, commentId, favoriteStatus, notRoot);
    }
    //评论日志本体
    public void toComment(String id, String content) {
        LiteAvRepository.getInstance().toComment(id, content, commentSuccess);
    }
    //评论评论
    public void toCommentReply(CommentListBean mCommentListBean, String content, int type) {
        LiteAvRepository.getInstance().toCommentReply(mCommentListBean, content, type, commentSuccess);
    }
    //日记详情
    public void diaryInfo(String id) {
        LiteAvRepository.getInstance().getDiaryInfo(requestSuccess, diaryInfo, id);
    }
    //关注用户
    public void toFollow(String userId) {
        LiteAvRepository.getInstance().toFollow(follow, null, userId);
    }
    //点赞日记
    public void toFavorite(String id , boolean isF) {
        LiteAvRepository.getInstance().toFavorite(favorite, requestSuccess, id , isF);
    }

}
