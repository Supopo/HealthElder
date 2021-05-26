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

    public MutableLiveData<UserInfoBean> userInfo = new MutableLiveData<>();
    public MutableLiveData<Boolean> clientId = new MutableLiveData<>();


    public void getUserInfo(String token) {
        UserRepository.getInstance().getUserInfo(userInfo, Constant.API_HEADER + token);
    }

    public void getUserSig(String token) {
        UserRepository.getInstance().getUserSig( Constant.API_HEADER + token);
    }

    public void postClientId(String token) {
        UserRepository.getInstance().bindAlias(clientId, token);
    }

}
