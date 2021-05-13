package com.xaqinren.healthyelders.moduleHome.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.tencent.qcloud.xiaoshipin.mainui.list.TCVideoInfo;
import com.xaqinren.healthyelders.apiserver.UserRepository;
import com.xaqinren.healthyelders.moduleHome.bean.GirlsBean;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;


public class HomeViewModel extends BaseViewModel {
    public HomeViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<List<TCVideoInfo>> datas = new MutableLiveData<>();

    public void getVideoData() {
        List<TCVideoInfo> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            TCVideoInfo info = new TCVideoInfo();
            info.frontcover = "https://p9.pstatp.com/large/4c87000639ab0f21c285.jpeg";
            info.hlsPlayUrl = "https://aweme.snssdk.com/aweme/v1/play/?video_id=97022dc18711411ead17e8dcb75bccd2&line=0&ratio=720p&media_type=4&vr_type=0";
            info.playurl = "https://aweme.snssdk.com/aweme/v1/play/?video_id=97022dc18711411ead17e8dcb75bccd2&line=0&ratio=720p&media_type=4&vr_type=0";
            list.add(info);
        }
    }
}