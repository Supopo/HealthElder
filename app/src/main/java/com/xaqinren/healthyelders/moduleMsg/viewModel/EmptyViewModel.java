package com.xaqinren.healthyelders.moduleMsg.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.apiserver.LiveRepository;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveInitInfo;

import me.goldze.mvvmhabit.base.BaseViewModel;

/**
 * Created by Lee. on 2021/5/11.
 */
public class EmptyViewModel extends BaseViewModel {
    public EmptyViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Boolean> dismissDialog = new MutableLiveData<>();
    public MutableLiveData<LiveInitInfo> liveInfo = new MutableLiveData<>();

    public void joinLive(String liveRoomId, String roomPassword) {
        LiveRepository.getInstance().joinLive(dismissDialog, null, liveRoomId, roomPassword);
    }
}
