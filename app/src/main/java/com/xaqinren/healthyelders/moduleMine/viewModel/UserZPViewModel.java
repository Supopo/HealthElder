package com.xaqinren.healthyelders.moduleMine.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.apiserver.UserRepository;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;
import com.xaqinren.healthyelders.moduleLogin.bean.UserInfoBean;

import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;

public class UserZPViewModel extends BaseViewModel {
    public MutableLiveData<Boolean> dismissDialog = new MutableLiveData<>();
    public MutableLiveData<UserInfoBean> userInfo = new MutableLiveData<>();
    public MutableLiveData<List<VideoInfo>> mVideoList = new MutableLiveData<>();
    private UserRepository userRepository = UserRepository.getInstance();

    public UserZPViewModel(@NonNull Application application) {
        super(application);
    }


    public void getMyVideoList(int page, int pageSize, String tagerId) {
        userRepository.getMyVideoList(dismissDialog,mVideoList, page, pageSize, "", tagerId);
    }


}
