package com.xaqinren.healthyelders;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.apiserver.UserRepository;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleLogin.bean.UserInfoBean;

import me.goldze.mvvmhabit.base.BaseViewModel;


public class MainViewModel extends BaseViewModel {
    public MainViewModel(@NonNull Application application) {
        super(application);
    }
    public MutableLiveData<UserInfoBean> userInfo = new MutableLiveData<>();
    private UserRepository userRepository = UserRepository.getInstance();

    public void getUserInfo(String token) {
        userRepository.getUserInfo(userInfo, Constant.API_HEADER + token);
    }
}
