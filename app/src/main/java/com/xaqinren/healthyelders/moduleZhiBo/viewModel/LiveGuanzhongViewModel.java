package com.xaqinren.healthyelders.moduleZhiBo.viewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.apiserver.LiveRepository;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveInitInfo;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.IMLVBLiveRoomListener;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.MLVBLiveRoom;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.roomutil.commondef.LoginInfo;

import me.goldze.mvvmhabit.base.BaseViewModel;

public class LiveGuanzhongViewModel extends BaseViewModel {
    public LiveGuanzhongViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Boolean> loginRoomSuccess = new MutableLiveData<>();
    public void toLoginRoom(MLVBLiveRoom mLiveRoom) {
        //判断登录
        LoginInfo loginInfo = new LoginInfo();
        loginInfo.userAvatar = UserInfoMgr.getInstance().getUserInfo().getAvatarUrl();
        loginInfo.userName = UserInfoMgr.getInstance().getUserInfo().getNickname();
        loginInfo.sdkAppID = 1400392607;
        loginInfo.userID = UserInfoMgr.getInstance().getUserInfo().getId();
        loginInfo.userSig = "eJw1jtsKgkAYhN9lr8P25B6EbkSIMMSwAr1TXPMnsm2TDKJ3z7QuZ*abYV5ov80887TgDAoIFVJxjBeT*zAOBYh6GM36Xp9La6EeuZFhmgos5wRq0-XQwFQgTAmqOGOSc*lLwn3xH4DTmIdpBvk6j4tNm8TJEClt2a4KXdOmjkXt4Xa8qqpYdiEeVr9iD5fvO0E0x1RT*v4AMdgzHQ__";
        mLiveRoom.login(loginInfo, new IMLVBLiveRoomListener.LoginCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                Log.v(Constant.TAG_LIVE, "LiveRoom登录失败：" + errCode);
                Log.v(Constant.TAG_LIVE, "LiveRoom登录失败：" + errInfo);
            }

            @Override
            public void onSuccess() {
                Log.v(Constant.TAG_LIVE, "LiveRoom登录成功");
                loginRoomSuccess.postValue(true);
            }
        });
    }
}
