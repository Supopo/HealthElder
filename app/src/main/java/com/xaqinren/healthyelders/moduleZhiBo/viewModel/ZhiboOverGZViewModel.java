package com.xaqinren.healthyelders.moduleZhiBo.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.apiserver.LiveRepository;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveInitInfo;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveOverInfo;

import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;

public class ZhiboOverGZViewModel extends BaseViewModel {
    public ZhiboOverGZViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<LiveOverInfo> liveOverInfo = new MutableLiveData<>();
    public MutableLiveData<Boolean> dismissDialog = new MutableLiveData<>();
    public MutableLiveData<List<VideoInfo>> videoList = new MutableLiveData<>();
    public MutableLiveData<LiveInitInfo> liveInfo = new MutableLiveData<>();

    public void getLiveOverInfo(String liveRoomRecordId) {
        LiveRepository.getInstance().liveOverInfoGZ(dismissDialog, liveOverInfo, liveRoomRecordId);
    }

    public void getMoreLives(int page) {
        LiveRepository.getInstance().getSomeLikeList(dismissDialog, page, 10, videoList, Constant.REQ_TAG_ZB);
    }

    public void joinLive(String liveRoomId) {
        showDialog();
        LiveRepository.getInstance().joinLive(dismissDialog, liveInfo, liveRoomId);
    }
}
