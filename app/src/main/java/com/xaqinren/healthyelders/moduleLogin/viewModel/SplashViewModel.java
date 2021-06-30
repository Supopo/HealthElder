package com.xaqinren.healthyelders.moduleLogin.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;

import com.xaqinren.healthyelders.apiserver.UserRepository;

import me.goldze.mvvmhabit.base.BaseViewModel;


public class SplashViewModel extends BaseViewModel {

    public SplashViewModel(@NonNull Application application) {
        super(application);
    }

    public void refreshToken(String token){
        UserRepository.getInstance().refreshToken(token);
    }
}
