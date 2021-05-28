package com.xaqinren.healthyelders.moduleHome.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.apiserver.LiveRepository;
import com.xaqinren.healthyelders.apiserver.UserRepository;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;

import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;


public class SearchUserViewModel extends BaseViewModel {
    public SearchUserViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Boolean> dismissDialog = new MutableLiveData<>();
    public MutableLiveData<Boolean> followSuccess = new MutableLiveData<>();


    public void toFollow(String userId) {
        UserRepository.getInstance().toFollow(followSuccess, dismissDialog, userId);
    }
}
