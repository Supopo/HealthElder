package com.xaqinren.healthyelders.moduleHome.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.apiserver.LiveRepository;
import com.xaqinren.healthyelders.apiserver.MallRepository;
import com.xaqinren.healthyelders.bean.SlideBarBean;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;

import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;


public class MenuSearchViewModel extends BaseViewModel {
    public MenuSearchViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<SlideBarBean> searchHistoryList = new MutableLiveData<>();
    public MutableLiveData<SlideBarBean> searchHotList = new MutableLiveData<>();
    public MutableLiveData<Boolean> dismissDialog = new MutableLiveData<>();
    public MutableLiveData<List<VideoInfo>> datas = new MutableLiveData<>();

    public void getAllWordsList(String type) {
        MallRepository.getInstance().getMenuAllWords(searchHotList, type);
    }

    public void getVideoData(int page, String tags, String key) {
        LiveRepository.getInstance().getHomeVideoList(dismissDialog, page, 6, null, datas, "LIVE,VIDEO,USER_DIARY", tags, key);
    }

}
