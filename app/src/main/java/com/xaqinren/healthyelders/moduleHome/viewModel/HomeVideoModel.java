package com.xaqinren.healthyelders.moduleHome.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.apiserver.LiveRepository;
import com.xaqinren.healthyelders.apiserver.UserRepository;
import com.xaqinren.healthyelders.moduleHome.bean.CommentListBean;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveInitInfo;

import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;


public class HomeVideoModel extends BaseViewModel {
    public HomeVideoModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<VideoInfo> videoInfo = new MutableLiveData<>();
    public MutableLiveData<CommentListBean> commentSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> dismissDialog = new MutableLiveData<>();
    public MutableLiveData<LiveInitInfo> liveInfo = new MutableLiveData<>();

    public void toLikeVideo(String videoId, boolean isLike) {
        LiveRepository.getInstance().toLikeVideo(videoId, isLike);
    }

    public void toFollow(String userId) {
        UserRepository.getInstance().toFollow(null, null, userId);
    }

    public void toComment(String id, String content) {
        LiveRepository.getInstance().toComment(id, content, commentSuccess);
    }

    public void toCommentReply(CommentListBean mCommentListBean, String content, int type) {
        LiveRepository.getInstance().toCommentReply(mCommentListBean, content, type, commentSuccess);
    }

    public void joinLive(String liveRoomId) {
        showDialog();
        LiveRepository.getInstance().joinLive(dismissDialog, liveInfo, liveRoomId);
    }
}
