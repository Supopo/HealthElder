package com.xaqinren.healthyelders.moduleHome.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.tencent.qcloud.xiaoshipin.mainui.list.TCVideoInfo;
import com.xaqinren.healthyelders.apiserver.LiveRepository;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleHome.bean.HomeRes;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;


public class HomeTJViewModel extends BaseViewModel {
    public HomeTJViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<List<VideoInfo>> datas = new MutableLiveData<>();
    public MutableLiveData<HomeRes> homeInfo = new MutableLiveData<>();

    public void getVideoData(int page) {
        LiveRepository.getInstance().getHomeVideoList(page, Constant.loadVideoSize, 0,datas);
    }

    public void getHomeInfo() {
        LiveRepository.getInstance().getHomeInfo(homeInfo);
    }
}
