package com.xaqinren.healthyelders.moduleZhiBo.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.apiserver.UserRepository;

import java.util.Map;

import me.goldze.mvvmhabit.base.BaseViewModel;

public class StartRenZhengViewModel extends BaseViewModel {

    public MutableLiveData<Boolean> dismissDialog = new MutableLiveData<>();
    public MutableLiveData<String> fileUrl1 = new MutableLiveData<>();
    public MutableLiveData<String> fileUrl2 = new MutableLiveData<>();

    public StartRenZhengViewModel(@NonNull Application application) {
        super(application);
    }

    public void updatePhoto(String filePath, int type) {
        UserRepository.getInstance().updatePhoto(dismissDialog, type == 1 ? fileUrl1 : fileUrl2, filePath);
    }
}
