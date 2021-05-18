package com.xaqinren.healthyelders.moduleLiteav.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import me.goldze.mvvmhabit.base.BaseViewModel;

public class ChooseMusicViewModel extends BaseViewModel {
    public MutableLiveData<Boolean> requestSuccess = new MutableLiveData<>();

    public ChooseMusicViewModel(@NonNull Application application) {
        super(application);
    }

}
