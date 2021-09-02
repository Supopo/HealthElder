package com.xaqinren.healthyelders.moduleMsg.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.apiserver.LiveRepository;
import com.xaqinren.healthyelders.moduleMsg.bean.MsgBean;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveInitInfo;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;

/**
 * Created by Lee. on 2021/5/11.
 */
public class MsgViewModel extends BaseViewModel {
    public MsgViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Boolean> dismissDialog = new MutableLiveData<>();
    public MutableLiveData<LiveInitInfo> liveInfo = new MutableLiveData<>();

    public void joinLive(String liveRoomId, String roomPassword) {
        LiveRepository.getInstance().joinLive(null, null, liveRoomId, roomPassword);
    }
}
