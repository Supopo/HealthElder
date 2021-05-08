package com.xaqinren.healthyelders.moduleLiteav.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.apiserver.LiteAvRepository;
import com.xaqinren.healthyelders.apiserver.LiveRepository;
import com.xaqinren.healthyelders.moduleLiteav.bean.LiteAvUserBean;

import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;

public class VideoPublishViewModel extends BaseViewModel {

    public VideoPublishViewModel(@NonNull Application application) {
        super(application);
    }
    public MutableLiveData<Boolean> requestSuccess = new MutableLiveData<>();

    public MutableLiveData<List<LiteAvUserBean>> liteAvUserList = new MutableLiveData<>();

    /**
     * 获取用户自身好友，粉丝，
     */
    public void getMyAtList(int page ,int pageSize) {
        LiteAvRepository.getInstance().getMyAtList(requestSuccess, liteAvUserList, page, pageSize);
    }

}
