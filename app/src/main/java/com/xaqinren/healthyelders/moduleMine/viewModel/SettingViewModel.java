package com.xaqinren.healthyelders.moduleMine.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.apiserver.UserRepository;
import com.xaqinren.healthyelders.moduleMine.bean.VersionBean;

import me.goldze.mvvmhabit.base.BaseViewModel;

public class SettingViewModel extends BaseViewModel {
    public MutableLiveData<VersionBean> versionBean = new MutableLiveData<>();
    public SettingViewModel(@NonNull Application application) {
        super(application);
    }

    public void checkVersion() {
        UserRepository.getInstance().getVersionInfo(versionBean);
    }

}
