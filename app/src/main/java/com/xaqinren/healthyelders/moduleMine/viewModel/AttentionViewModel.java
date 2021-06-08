package com.xaqinren.healthyelders.moduleMine.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.apiserver.LiteAvRepository;
import com.xaqinren.healthyelders.apiserver.MsgRepository;
import com.xaqinren.healthyelders.apiserver.UserRepository;
import com.xaqinren.healthyelders.moduleLiteav.bean.LiteAvUserBean;

import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.utils.StringUtils;

public class AttentionViewModel extends BaseViewModel {
    public MutableLiveData<Boolean> requestSuccess = new MutableLiveData<>();
    public MutableLiveData<List<LiteAvUserBean>> userList = new MutableLiveData<>();
    public MutableLiveData<List<LiteAvUserBean>> searchUserList = new MutableLiveData<>();
    public MutableLiveData<Boolean> flow = new MutableLiveData<>();
    public MutableLiveData<Boolean> del = new MutableLiveData<>();

    public AttentionViewModel(@NonNull Application application) {
        super(application);
    }

    public void getUserList(int page, int pageSize, String identity) {
        LiteAvRepository.getInstance().getUserList(requestSuccess, userList, page, pageSize, identity);
    }

    public void getUserList(int page, int pageSize, String identity, String uid) {
        if (StringUtils.isEmpty(uid)) {
            LiteAvRepository.getInstance().getUserList(requestSuccess, userList, page, pageSize, identity);
        }else
            LiteAvRepository.getInstance().getUserList(requestSuccess, userList, uid ,page, pageSize, identity);
    }

    public void searchUserList(int page, int pageSize, String identity) {
        LiteAvRepository.getInstance().getSearchUserList(requestSuccess, searchUserList, page, pageSize, identity);
    }
    public void recommendFriend(String id) {
        MsgRepository.getInstance().toFollow(requestSuccess, flow, id);
    }
    public void delFans(String id) {
        UserRepository.getInstance().delFans(requestSuccess, del, id);
    }
}
