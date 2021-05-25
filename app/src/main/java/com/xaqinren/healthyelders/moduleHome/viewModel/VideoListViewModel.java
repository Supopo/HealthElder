package com.xaqinren.healthyelders.moduleHome.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.apiserver.LiveRepository;
import com.xaqinren.healthyelders.apiserver.UserRepository;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleHome.bean.HomeRes;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;
import com.xaqinren.healthyelders.moduleMine.bean.DZVideoInfo;

import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;


public class VideoListViewModel extends BaseViewModel {
    public VideoListViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<List<VideoInfo>> datas = new MutableLiveData<>();
    public MutableLiveData<List<DZVideoInfo>> dzDatas = new MutableLiveData<>();
    public MutableLiveData<Boolean> closeRsl = new MutableLiveData<>();

    //type = 2 从附近打开 //type = 0 从推荐打开 之请求
    public void getVideoData(int page, int type) {
        String resourceType = "";
        if (type == 0) {
            resourceType = "LIVE";
            LiveRepository.getInstance().getHomeVideoList(closeRsl, page, Constant.loadVideoSize, type, datas, resourceType,"");
        } else if (type == 2) {
            resourceType = "LIVE,VIDEO,USER_DIARY";
            LiveRepository.getInstance().getHomeVideoList(closeRsl, page, Constant.loadVideoSize, type, datas, resourceType,"");
        } else if (type == 3) {
            UserRepository.getInstance().getMyVideoList(datas, page, Constant.loadVideoSize, "");
        } else if (type == 4) {
            UserRepository.getInstance().getMyVideoList(datas, page, Constant.loadVideoSize, "PRIVATE");
        } else if (type == 5) {
            UserRepository.getInstance().getMyLikeVideoList(dzDatas, page, Constant.loadVideoSize);
        }

    }
}
