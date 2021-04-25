package com.xaqinren.healthyelders.moduleZhiBo.viewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ZhiboUserBean;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.IMLVBLiveRoomListener;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.MLVBLiveRoom;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.roomutil.commondef.LoginInfo;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;

public class LiveZhuboViewModel extends BaseViewModel {
    public LiveZhuboViewModel(@NonNull Application application) {
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
        loginInfo.userSig = "eJw9jksLgkAUhf-LbAudO9fuqNCiTUgE2WOhy2DGuoQxmPko*u*JRsvz*A7nLU7bo2c7x5UVsSIdBlLOR7OxlYiF8qSY9MPczs6xETEMHYwUST0lbOy95oJHADAkQB2i1JFcIIL6D-BlyJvE*rJddfna36uSUnNoc8yybgfWzXDTK*5fz2uQuDRY-sCay*EcEESIRBo*XwReMts_";
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
