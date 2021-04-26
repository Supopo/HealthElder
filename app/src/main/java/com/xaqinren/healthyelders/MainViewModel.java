package com.xaqinren.healthyelders;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.apiserver.LiveRepository;
import com.xaqinren.healthyelders.apiserver.UserRepository;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleLogin.bean.UserInfoBean;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveInitInfo;

import me.goldze.mvvmhabit.base.BaseViewModel;


public class MainViewModel extends BaseViewModel {
    public MainViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Boolean> overSuccess = new MutableLiveData<>();
    public MutableLiveData<LiveInitInfo> liveInfo = new MutableLiveData<>();
    public MutableLiveData<UserInfoBean> userInfo = new MutableLiveData<>();

    public void getUserInfo(String token) {
        UserRepository.getInstance().getUserInfo(userInfo, Constant.API_HEADER + token);
    }

    public void checkLiveInfo() {
        LiveRepository.getInstance().checkLiveInfo(liveInfo);
    }

    public void closeLastLive(String liveRoomId) {
        LiveRepository.getInstance().overLive(overSuccess, liveRoomId);
    }
}
