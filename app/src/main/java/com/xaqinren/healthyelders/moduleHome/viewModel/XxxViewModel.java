package com.xaqinren.healthyelders.moduleHome.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.apiserver.UserRepository;
import com.xaqinren.healthyelders.bean.UserInfo;
import com.xaqinren.healthyelders.global.Constant;

import me.goldze.mvvmhabit.base.BaseViewModel;

public class XxxViewModel extends BaseViewModel {

    public XxxViewModel(@NonNull Application application) {
        super(application);
    }
    public MutableLiveData<UserInfo> userInfo = new MutableLiveData<>();
    private UserRepository userRepository = UserRepository.getInstance();

    public void getUserInfo(String token) {
        userRepository.getUserInfo(userInfo, Constant.API_HEADER + token);
    }
}
