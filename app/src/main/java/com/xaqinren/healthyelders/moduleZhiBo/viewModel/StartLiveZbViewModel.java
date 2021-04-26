package com.xaqinren.healthyelders.moduleZhiBo.viewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.apiserver.LiveRepository;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleZhiBo.bean.GoodsBean;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveInitInfo;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.IMLVBLiveRoomListener;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.MLVBLiveRoom;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.roomutil.commondef.LoginInfo;
import com.xaqinren.healthyelders.utils.LogUtils;

import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;

public class StartLiveZbViewModel extends BaseViewModel {
    public StartLiveZbViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Boolean> dismissDialog = new MutableLiveData<>();
    public MutableLiveData<Boolean> loginRoomSuccess = new MutableLiveData<>();
    public MutableLiveData<LiveInitInfo> liveInfo = new MutableLiveData<>();

    public void toLoginRoom(MLVBLiveRoom mLiveRoom) {
        //判断登录
        LoginInfo loginInfo = new LoginInfo();
        loginInfo.userAvatar = UserInfoMgr.getInstance().getUserInfo().getAvatarUrl();
        loginInfo.userName = UserInfoMgr.getInstance().getUserInfo().getNickname();
        loginInfo.sdkAppID = 1400392607;
        loginInfo.userID = UserInfoMgr.getInstance().getUserInfo().getId();
        loginInfo.userSig = "eJw9jksLgkAUhf-LbAudO9fuqNCiTUgE2WOhy2DGuoQxmPko*u*JRsvz*A7nLU7bo2c7x5UVsSIdBlLOR7OxlYiF8qSY9MPczs6xETEMHYwUST0lbOy95oJHADAkQB2i1JFcIIL6D-BlyJvE*rJddfna36uSUnNoc8yybgfWzXDTK*5fz2uQuDRY-sCay*EcEESIRBo*XwReMts_";
        mLiveRoom.login(loginInfo, new IMLVBLiveRoomListener.LoginCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                LogUtils.v(Constant.TAG_LIVE, "LiveRoom登录失败：" + errCode);
                LogUtils.v(Constant.TAG_LIVE, "LiveRoom登录失败：" + errInfo);
            }

            @Override
            public void onSuccess() {
                LogUtils.v(Constant.TAG_LIVE, "LiveRoom登录成功");
                loginRoomSuccess.postValue(true);
            }
        });
    }


    public void checkLiveInfo() {
        LiveRepository.getInstance().checkLiveInfo(dismissDialog, liveInfo);
    }

    public void closeLastLive(String liveRoomId) {
        LiveRepository.getInstance().overLive(dismissDialog, liveRoomId);
    }
}
