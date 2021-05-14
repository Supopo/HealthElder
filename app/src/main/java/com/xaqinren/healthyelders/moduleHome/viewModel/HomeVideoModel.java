package com.xaqinren.healthyelders.moduleHome.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.apiserver.LiveRepository;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;

import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;


public class HomeVideoModel extends BaseViewModel {
    public HomeVideoModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<List<VideoInfo>> datas = new MutableLiveData<>();
    public MutableLiveData<VideoInfo> videoInfo = new MutableLiveData<>();



    public void getVideoData(int page, int pageSize) {
        LiveRepository.getInstance().getHomeVideoList(page, pageSize, datas);
    }


}
