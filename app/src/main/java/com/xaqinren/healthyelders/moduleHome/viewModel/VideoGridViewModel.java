package com.xaqinren.healthyelders.moduleHome.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.apiserver.LiveRepository;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;

import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;


public class VideoGridViewModel extends BaseViewModel {
    public VideoGridViewModel(@NonNull Application application) {
        super(application);
    }


    public MutableLiveData<List<VideoInfo>> datas = new MutableLiveData<>();
    public MutableLiveData<Boolean> closeRsl = new MutableLiveData<>();

    public void getVideoData(int page, String tags) {
        LiveRepository.getInstance().getHomeVideoList(closeRsl, page, 6, 2, tags, datas);
    }

}
