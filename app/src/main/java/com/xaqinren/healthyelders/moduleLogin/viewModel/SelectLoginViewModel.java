package com.xaqinren.healthyelders.moduleLogin.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.apiserver.UserRepository;
import com.xaqinren.healthyelders.moduleLogin.bean.WeChatUserInfoBean;

import me.goldze.mvvmhabit.base.BaseViewModel;


public class SelectLoginViewModel extends BaseViewModel {

    public SelectLoginViewModel(@NonNull Application application) {
        super(application);
    }

    private UserRepository userRepository = UserRepository.getInstance();
    public MutableLiveData<Integer> loginStatus = new MutableLiveData<>();

    public void toWxChatRealLogin(WeChatUserInfoBean weChatUserInfoBean) {
        userRepository.toWxChatRealLogin(loginStatus, weChatUserInfoBean);
    }
}
