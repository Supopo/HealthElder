package com.xaqinren.healthyelders.moduleHome.viewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.apiserver.LiveRepository;
import com.xaqinren.healthyelders.apiserver.UserRepository;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleLogin.bean.UserInfoBean;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveInitInfo;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.IMLVBLiveRoomListener;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.MLVBLiveRoom;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.roomutil.commondef.LoginInfo;

import me.goldze.mvvmhabit.base.BaseViewModel;

public class XxxViewModel extends BaseViewModel {

    public XxxViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Boolean> loginRoomSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> joinSuccess = new MutableLiveData<>();
    public MutableLiveData<LiveInitInfo> liveInfo = new MutableLiveData<>();
    public MutableLiveData<UserInfoBean> userInfo = new MutableLiveData<>();
    private UserRepository userRepository = UserRepository.getInstance();

    public void getUserInfo(String token) {
        userRepository.getUserInfo(userInfo, Constant.API_HEADER + token);
    }

    public void joinLive(String liveRoomId) {
        LiveRepository.getInstance().joinLive(liveInfo, liveRoomId);
    }

}
