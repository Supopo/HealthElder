package com.xaqinren.healthyelders.moduleZhiBo.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.apiserver.LiveRepository;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveOverInfo;

import me.goldze.mvvmhabit.base.BaseViewModel;

public class ZhiboOverGZViewModel extends BaseViewModel {
    public ZhiboOverGZViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<LiveOverInfo> liveOverInfo = new MutableLiveData<>();
    public MutableLiveData<Boolean> dismissDialog = new MutableLiveData<>();

    public void getLiveOverInfo(String liveRoomRecordId) {
        LiveRepository.getInstance().liveOverInfoGZ(dismissDialog, liveOverInfo, liveRoomRecordId);
    }
}
