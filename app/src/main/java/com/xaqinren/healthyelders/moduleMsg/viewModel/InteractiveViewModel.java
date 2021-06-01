package com.xaqinren.healthyelders.moduleMsg.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.apiserver.LiteAvRepository;
import com.xaqinren.healthyelders.apiserver.MsgRepository;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;
import com.xaqinren.healthyelders.moduleLiteav.bean.MMusicItemBean;
import com.xaqinren.healthyelders.moduleMsg.bean.FriendBean;
import com.xaqinren.healthyelders.moduleMsg.bean.InteractiveBean;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;

public class InteractiveViewModel extends BaseViewModel {

    public MutableLiveData<Boolean> requestSuccess = new MutableLiveData<>();
    public MutableLiveData<List<InteractiveBean>> musicListData = new MutableLiveData<>();
    public MutableLiveData<List<FriendBean>> friendListData = new MutableLiveData<>();
    public MutableLiveData<VideoInfo> videoInfoLiveData = new MutableLiveData<>();

    public MutableLiveData<Boolean> request = new MutableLiveData<>();
    public MutableLiveData<Boolean> flow = new MutableLiveData<>();


    public InteractiveViewModel(@NonNull Application application) {
        super(application);
    }

    public void getMessage(int page, int pageSize, String messageGroup, String messageType) {
        MsgRepository.getInstance().getMessage(requestSuccess, musicListData, page, pageSize, messageGroup, messageType);
    }

    public void getRecommendFriend() {
        MsgRepository.getInstance().getRecommendF(requestSuccess, friendListData);
    }

    public void getVideoInfo(String id) {
        LiteAvRepository.getInstance().videoDetail(requestSuccess,videoInfoLiveData,id);
    }

    public void recommendFriend(String id) {
        MsgRepository.getInstance().toFollow(request, flow, id);
    }

}
