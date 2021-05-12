package com.xaqinren.healthyelders.moduleHome.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.tencent.qcloud.xiaoshipin.mainui.list.TCVideoInfo;
import com.xaqinren.healthyelders.apiserver.LiveRepository;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;


public class HomeTJViewModel extends BaseViewModel {
    public HomeTJViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<List<VideoInfo>> datas = new MutableLiveData<>();

    public void getVideoData(int page, int pageSize) {
        LiveRepository.getInstance().getHomeVideoList(page, pageSize, datas);
    }


}
