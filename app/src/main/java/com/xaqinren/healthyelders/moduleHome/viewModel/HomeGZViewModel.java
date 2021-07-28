package com.xaqinren.healthyelders.moduleHome.viewModel;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.apiserver.LiveRepository;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveInitInfo;

import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;


public class HomeGZViewModel extends BaseViewModel {
    public HomeGZViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<List<VideoInfo>> datas = new MutableLiveData<>();
    public MutableLiveData<List<VideoInfo>> firendDatas = new MutableLiveData<>();
    public MutableLiveData<Boolean> dismissDialog = new MutableLiveData<>();
    public MutableLiveData<LiveInitInfo> liveInfo = new MutableLiveData<>();
    public MutableLiveData<Boolean> closeRsl = new MutableLiveData<>();


    public void getVideoData(int page) {
        LiveRepository.getInstance().getHomeVideoList(closeRsl,page, Constant.loadVideoSize,1, datas);
    }

    public void getLiveFiends() {
        if (TextUtils.isEmpty(UserInfoMgr.getInstance().getHttpToken())) {
            return;
        }
        LiveRepository.getInstance().getLiveFiends(firendDatas);
    }

    public void joinLive(String liveRoomId,String liveRoomPassword) {
        showDialog();
        LiveRepository.getInstance().joinLive(dismissDialog, liveInfo, liveRoomId,liveRoomPassword);
    }
}
