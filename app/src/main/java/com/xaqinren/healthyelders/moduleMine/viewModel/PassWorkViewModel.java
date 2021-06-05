package com.xaqinren.healthyelders.moduleMine.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.apiserver.UserRepository;

import me.goldze.mvvmhabit.base.BaseViewModel;

public class PassWorkViewModel extends BaseViewModel {
    public MutableLiveData<Boolean> request = new MutableLiveData<>();
    public MutableLiveData<Boolean> passWord = new MutableLiveData<>();

    public PassWorkViewModel(@NonNull Application application) {
        super(application);
    }

    public void setPassWord(String pass) {
        UserRepository.getInstance().setPassWord(request,passWord,pass);
    }
}
