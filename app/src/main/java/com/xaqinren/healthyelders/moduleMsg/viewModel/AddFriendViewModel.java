package com.xaqinren.healthyelders.moduleMsg.viewModel;

import android.app.Application;
import android.text.BoringLayout;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.apiserver.LiteAvRepository;
import com.xaqinren.healthyelders.apiserver.MsgRepository;
import com.xaqinren.healthyelders.moduleLiteav.bean.LiteAvUserBean;
import com.xaqinren.healthyelders.moduleMsg.bean.FriendBean;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;

public class AddFriendViewModel extends BaseViewModel {
    public MutableLiveData<List<FriendBean>> friendLiveData = new MutableLiveData<>();
    public MutableLiveData<Boolean> request = new MutableLiveData<>();
    public MutableLiveData<Boolean> flow = new MutableLiveData<>();

    public MutableLiveData<List<LiteAvUserBean>> liteAvUserList = new MutableLiveData<>();

    public AddFriendViewModel(@NonNull Application application) {
        super(application);
    }

    public void getRecommendFriend() {
        MsgRepository.getInstance().getUserContactF(request, friendLiveData);
    }

    public void recommendFriend(String id) {
        MsgRepository.getInstance().toFollow(request, flow, id);
    }

    public void searchUserList(int page ,int pageSize,String key ) {
        LiteAvRepository.getInstance().getSearchUserList(request, liteAvUserList, page, pageSize,key);
    }
}

