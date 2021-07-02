package com.xaqinren.healthyelders.moduleHome.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.apiserver.LiveRepository;
import com.xaqinren.healthyelders.apiserver.UserRepository;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleHome.bean.ResBean;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveInitInfo;

import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;


public class SearchAllViewModel extends BaseViewModel {
    public SearchAllViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<ResBean> dzSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> toUsers = new MutableLiveData<>();
    public MutableLiveData<Boolean> dismissDialog = new MutableLiveData<>();
    public MutableLiveData<List<VideoInfo>> allDatas = new MutableLiveData<>();
    public MutableLiveData<List<VideoInfo>> videoDatas = new MutableLiveData<>();
    public MutableLiveData<List<VideoInfo>> twDatas = new MutableLiveData<>();
    public MutableLiveData<List<VideoInfo>> userDatas = new MutableLiveData<>();
    public MutableLiveData<List<VideoInfo>> userDatas2 = new MutableLiveData<>();
    public MutableLiveData<List<VideoInfo>> goodsDatas = new MutableLiveData<>();
    public MutableLiveData<List<VideoInfo>> zbDatas = new MutableLiveData<>();
    public MutableLiveData<LiveInitInfo> liveInfo = new MutableLiveData<>();

    public String keys;
    public void searchDatas(int page, int searchType , boolean showDialog){
        if (showDialog)
            showDialog();
        switch (searchType) {
            case 0:
                //全部搜索
                String resType = "all";
                LiveRepository.getInstance().getHomeVideoList(dismissDialog, page, 10, null, allDatas, resType, "", keys);
                break;
            case 1:
                //视频
                LiveRepository.getInstance().getHomeVideoList(dismissDialog, page, 10, null, videoDatas, Constant.REQ_TAG_SP,"", keys);
                break;
            case 2:
                //用户
                searchUsers(page, 10);
                break;
            case 3:
                //商品
                LiveRepository.getInstance().getHomeVideoList(dismissDialog, page, 10, null, goodsDatas, Constant.REQ_TAG_GOODS, "", keys);
                break;
            case 4:
                //直播
                LiveRepository.getInstance().getHomeVideoList(dismissDialog, page, 10, null, zbDatas, Constant.REQ_TAG_ZB, "", keys);
                break;
            case 5:
                //图文
                LiveRepository.getInstance().getHomeVideoList(dismissDialog, page, 10, null, twDatas, Constant.REQ_TAG_TW, "", keys);
                break;
        }
    }
    public void searchDatas(int page, int searchType) {
        searchDatas(page, searchType, false);
    }

    public void joinLive(String liveRoomId) {
        showDialog();
        LiveRepository.getInstance().joinLive(dismissDialog, liveInfo, liveRoomId);
    }

    public void searchUsers(int page, int size) {
        if (size == 10) {
            UserRepository.getInstance().searchUser(dismissDialog, userDatas2, page, size, keys);
        } else {
            UserRepository.getInstance().searchUser(dismissDialog, userDatas, page, size, keys);
        }
    }

    public MutableLiveData<Boolean> followSuccess = new MutableLiveData<>();


    public void toFollow(String userId) {
        showDialog();
        UserRepository.getInstance().toFollow(followSuccess, dismissDialog, userId);
    }

    public void toLike(int type, String shortVideoId, boolean favoriteStatus, int position) {
        showDialog();
        LiveRepository.getInstance().toLikeVideo(type, shortVideoId, favoriteStatus, position, dzSuccess, dismissDialog);
    }
}
