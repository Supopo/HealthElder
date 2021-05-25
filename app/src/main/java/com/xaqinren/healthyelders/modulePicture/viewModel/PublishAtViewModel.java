package com.xaqinren.healthyelders.modulePicture.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.apiserver.LiteAvRepository;
import com.xaqinren.healthyelders.moduleLiteav.bean.LiteAvUserBean;

import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;

public class PublishAtViewModel extends BaseViewModel {
    public MutableLiveData<Boolean> requestSuccess = new MutableLiveData<>();
    public MutableLiveData<List<LiteAvUserBean>> liteAvUserList = new MutableLiveData<>();
    public PublishAtViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * 获取用户自身好友，粉丝，
     */
    public void getMyAtList(int page ,int pageSize) {
        LiteAvRepository.getInstance().getMyAtList(requestSuccess, liteAvUserList, page, pageSize);
    }
    /**
     * 搜索用户
     */
    public void searchUserList(int page ,int pageSize,String key ) {
        LiteAvRepository.getInstance().getSearchUserList(requestSuccess, liteAvUserList, page, pageSize,key);
    }

}
