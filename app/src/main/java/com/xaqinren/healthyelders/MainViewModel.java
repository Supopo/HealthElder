package com.xaqinren.healthyelders;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.apiserver.LiveRepository;
import com.xaqinren.healthyelders.apiserver.UserRepository;
import com.xaqinren.healthyelders.bean.AppConfigBean;
import com.xaqinren.healthyelders.bean.SlideBarBean;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleLogin.bean.UserInfoBean;
import com.xaqinren.healthyelders.moduleMine.bean.VersionBean;
import com.xaqinren.healthyelders.moduleZhiBo.bean.GiftBean;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveInitInfo;

import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;


public class MainViewModel extends BaseViewModel {
    public MainViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<List<GiftBean>> giftList = new MutableLiveData<>();
    public MutableLiveData<UserInfoBean> userInfo = new MutableLiveData<>();
    public MutableLiveData<Boolean> clientId = new MutableLiveData<>();
    public MutableLiveData<Boolean> userSign = new MutableLiveData<>();
    public MutableLiveData<VersionBean> versionBean = new MutableLiveData<>();

    public MutableLiveData<AppConfigBean> appConfig = new MutableLiveData<>();
    public MutableLiveData<SlideBarBean> slideBarLiveData = new MutableLiveData<>();


    public void getUserInfo(String token,boolean refreshSign) {
        UserRepository.getInstance().getUserInfo(userInfo, Constant.API_HEADER + token, refreshSign);
    }

    public void getUserSig(String token) {
        UserRepository.getInstance().getUserSig(Constant.API_HEADER + token);
    }

    public void postClientId(String token) {
        UserRepository.getInstance().bindAlias(clientId, token);
    }

    public void getGiftList() {
        LiveRepository.getInstance().getGiftList(giftList);
    }

    public void checkVersion() {
        UserRepository.getInstance().getVersionInfo(versionBean);
    }

    public void getAppConfig() {
        UserRepository.getInstance().getAppConfig(appConfig);
    }

    public void getSlideBar() {
        UserRepository.getInstance().getAppSideBar(slideBarLiveData);
    }
}
