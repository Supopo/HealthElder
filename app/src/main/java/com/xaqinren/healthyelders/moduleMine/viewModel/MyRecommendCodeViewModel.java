package com.xaqinren.healthyelders.moduleMine.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.apiserver.UserRepository;

import me.goldze.mvvmhabit.base.BaseViewModel;

public class MyRecommendCodeViewModel extends BaseViewModel {
    public MutableLiveData<Boolean> requestSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> scanResponseSuccess = new MutableLiveData<>();
    public MyRecommendCodeViewModel(@NonNull Application application) {
        super(application);
    }
    public void sendScanResult(String code) {
        UserRepository.getInstance().sendScanResult(code,scanResponseSuccess,requestSuccess);
    }
}
