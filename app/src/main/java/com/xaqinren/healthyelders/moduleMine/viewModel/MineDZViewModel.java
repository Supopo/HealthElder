package com.xaqinren.healthyelders.moduleMine.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.apiserver.UserRepository;
import com.xaqinren.healthyelders.bean.BaseListRes;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;
import com.xaqinren.healthyelders.moduleLogin.bean.UserInfoBean;
import com.xaqinren.healthyelders.moduleMine.bean.DZVideoInfo;

import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;

public class MineDZViewModel extends BaseViewModel {
    public MutableLiveData<UserInfoBean> userInfo = new MutableLiveData<>();
    public MutableLiveData<BaseListRes<List<DZVideoInfo>>> mVideoList = new MutableLiveData<>();
    private UserRepository userRepository = UserRepository.getInstance();

    public MineDZViewModel(@NonNull Application application) {
        super(application);
    }

    public void getMyLikeVideoList(int page, int pageSize) {
        userRepository.getMyLikeVideoList(mVideoList, page, pageSize);
    }


}
