package com.xaqinren.healthyelders.moduleMine.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.apiserver.LiveRepository;
import com.xaqinren.healthyelders.apiserver.UserRepository;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;
import com.xaqinren.healthyelders.moduleLogin.bean.UserInfoBean;

import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;

public class UserInfoViewModel extends BaseViewModel {
    public MutableLiveData<UserInfoBean> userInfo = new MutableLiveData<>();

    public UserInfoViewModel(@NonNull Application application) {
        super(application);
    }

    public void getUserInfo(String userId) {
        UserRepository.getInstance().getOtherUserInfo(userInfo, userId);
    }

    public MutableLiveData<Boolean> dismissDialog = new MutableLiveData<>();
    public MutableLiveData<Boolean> followSuccess = new MutableLiveData<>();

    public void toFollow(String userId) {
        UserRepository.getInstance().toFollow(followSuccess, dismissDialog, userId);
    }
}
