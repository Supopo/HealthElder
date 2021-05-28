package com.xaqinren.healthyelders.moduleHome.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.apiserver.LiveRepository;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleHome.bean.SearchBean;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;


public class SearchAllViewModel extends BaseViewModel {
    public SearchAllViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Boolean> dismissDialog = new MutableLiveData<>();
    public MutableLiveData<List<VideoInfo>> allDatas = new MutableLiveData<>();
    public MutableLiveData<List<VideoInfo>> videoDatas = new MutableLiveData<>();
    public MutableLiveData<List<VideoInfo>> twDatas = new MutableLiveData<>();
    public MutableLiveData<List<VideoInfo>> userDatas = new MutableLiveData<>();
    public MutableLiveData<List<VideoInfo>> goodsDatas = new MutableLiveData<>();
    public MutableLiveData<List<VideoInfo>> zbDatas = new MutableLiveData<>();
    public String tags;

    public void searchDatas(int page, int searchType) {
        showDialog();
        switch (searchType) {
            case 0:
                //全部搜索
                String resType = Constant.REQ_TAG_ZB + "," + Constant.REQ_TAG_WZ + "," + Constant.REQ_TAG_SP + "," + Constant.REQ_TAG_GOODS + "," + Constant.REQ_TAG_YH;
                LiveRepository.getInstance().getHomeVideoList(dismissDialog, page, 10, 0, allDatas, resType, tags);
                break;
            case 1:
                //视频
                LiveRepository.getInstance().getHomeVideoList(dismissDialog, page, 10, 0, videoDatas, Constant.REQ_TAG_SP, tags);
                break;
            case 2:
                //用户
                LiveRepository.getInstance().getHomeVideoList(dismissDialog, page, 10, 0, userDatas, Constant.REQ_TAG_YH, tags);
                break;
            case 3:
                //商品
                LiveRepository.getInstance().getHomeVideoList(dismissDialog, page, 10, 0, goodsDatas, Constant.REQ_TAG_GOODS, tags);
                break;
            case 4:
                //直播
                LiveRepository.getInstance().getHomeVideoList(dismissDialog, page, 10, 0, zbDatas, Constant.REQ_TAG_ZB, tags);
                break;
            case 5:
                //图文
                LiveRepository.getInstance().getHomeVideoList(dismissDialog, page, 10, 0, twDatas, Constant.REQ_TAG_TW, tags);
                break;
        }
    }
}
