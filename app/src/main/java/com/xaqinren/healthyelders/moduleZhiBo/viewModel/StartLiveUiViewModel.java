package com.xaqinren.healthyelders.moduleZhiBo.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import me.goldze.mvvmhabit.base.BaseViewModel;

public class StartLiveUiViewModel extends ViewModel {

    MutableLiveData<Integer> currentPage = new MutableLiveData<>();
    public MutableLiveData<Boolean> onBackPress = new MutableLiveData<>();


    public MutableLiveData<Integer> getCurrentPage() {
        return currentPage;
    }



}
