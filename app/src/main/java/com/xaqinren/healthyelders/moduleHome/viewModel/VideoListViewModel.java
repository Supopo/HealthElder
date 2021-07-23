package com.xaqinren.healthyelders.moduleHome.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.apiserver.LiveRepository;
import com.xaqinren.healthyelders.apiserver.UserRepository;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;
import com.xaqinren.healthyelders.moduleHome.bean.VideoListBean;
import com.xaqinren.healthyelders.moduleMine.bean.DZVideoInfo;

import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;


public class VideoListViewModel extends BaseViewModel {
    public VideoListViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Boolean> dismissDialog = new MutableLiveData<>();
    public MutableLiveData<List<VideoInfo>> datas = new MutableLiveData<>();
    public MutableLiveData<List<DZVideoInfo>> dzDatas = new MutableLiveData<>();
    public MutableLiveData<Boolean> closeRsl = new MutableLiveData<>();

    //1 从首页直播列表 2 从附近打开 3我的作品 4我的私密 5我的点赞作品
    public void getVideoData(int page, VideoListBean videoListBean,String userId) {

        int type = videoListBean.openType;
        String tags = videoListBean.tags;
        String resourceType = "";
        if (type == 1) {
            resourceType = "LIVE";
            LiveRepository.getInstance().getHomeVideoList(closeRsl, page, Constant.loadVideoSize, null, datas, resourceType, "");
        } else if (type == 2) {
            resourceType = "LIVE,VIDEO,USER_DIARY";
            LiveRepository.getInstance().getHomeVideoList(closeRsl, page, Constant.loadVideoSize, 2, datas, resourceType, tags);
        } else if (type == 3) {
            UserRepository.getInstance().getMyVideoList(dismissDialog, datas, page, Constant.loadVideoSize, "", userId);
        } else if (type == 4) {
            UserRepository.getInstance().getMyVideoList(dismissDialog, datas, page, Constant.loadVideoSize, "PRIVATE", userId);
        } else if (type == 5) {
            UserRepository.getInstance().getMyLikeVideoList(dzDatas, page, Constant.loadVideoSize);
        }else if (type == 6) {
            UserRepository.getInstance().getMyVideoList(dismissDialog,datas, page, Constant.loadVideoSize, "", userId);
        }else if (type == 7) {
            UserRepository.getInstance().getMyLikeVideoList(dzDatas, page, Constant.loadVideoSize, userId);

        }

    }
}
