package com.xaqinren.healthyelders.moduleZhiBo.viewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.apiserver.LiveRepository;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveHeaderInfo;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveInitInfo;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ZBSettingBean;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ZBUserListBean;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ZhiboUserBean;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.IMLVBLiveRoomListener;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.MLVBLiveRoom;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.roomutil.commondef.AnchorInfo;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.roomutil.commondef.LoginInfo;
import com.xaqinren.healthyelders.utils.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;

public class LiveZhuboViewModel extends BaseViewModel {
    public LiveZhuboViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Boolean> dismissDialog = new MutableLiveData<>();
    public MutableLiveData<Boolean> loginRoomSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> exitSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> zanSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> setSuccess = new MutableLiveData<>();
    public MutableLiveData<LiveInitInfo> startLiveInfo = new MutableLiveData<>();
    public MutableLiveData<LiveHeaderInfo> liveHeaderInfo = new MutableLiveData<>();
    public MutableLiveData<Integer> netSuccess = new MutableLiveData<>();//1 禁言 2加入多人连麦 21加入多人连麦失败
    public MutableLiveData<AnchorInfo> micAnchorInfo = new MutableLiveData<>();
    public MutableLiveData<List<ZBUserListBean>> moreLinkList = new MutableLiveData<>();


    public void toLoginRoom(MLVBLiveRoom mLiveRoom) {
        LoginInfo loginInfo = new LoginInfo();
        loginInfo.userAvatar = UserInfoMgr.getInstance().getUserInfo().getAvatarUrl();
        loginInfo.userName = UserInfoMgr.getInstance().getUserInfo().getNickname();
        loginInfo.sdkAppID = 1400392607;
        loginInfo.userID = UserInfoMgr.getInstance().getUserInfo().getId();
        //3887
        loginInfo.userSig = UserInfoMgr.getInstance().getUserSig();
        //4545
        //        loginInfo.userSig = "eJw1jl0LgjAYhf-Lbg3b3s1tCl0GFoKI4UV0I23GW5YyJY3ovydal*fjOZw3OSS5b8cWnSURSKUFpavZfFpHIgI*JYvuzK1sWzQkYlOHhyCpWhI09tFjhTPAuJagBedKCBUoJgL5H8DLlB-TrbuWYTDkXTo0XjHGtD6tsXbsldEz7eKs8sTOmaTYN5sf2eN9esckUNAAQn**VREzgA__";
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

    //通知服务器
    public void startLive(LiveInitInfo map) {
        LiveRepository.getInstance().startLive(dismissDialog, startLiveInfo, map);
    }

    //通知服务器继续直播
    public void reStartLive(String liveRoomRecordId) {
        LiveRepository.getInstance().reStartLive(dismissDialog, startLiveInfo, liveRoomRecordId);
    }

    //结束直播
    public void closeLive(String liveRoomRecordId, String commentPeopleNum) {
        LiveRepository.getInstance().overLive(dismissDialog, exitSuccess, liveRoomRecordId, commentPeopleNum);
    }

    //刷新直播间顶部信息
    public void rushLiveInfo(String liveRoomRecordId) {
        LiveRepository.getInstance().rushLiveInfo(dismissDialog, liveHeaderInfo, liveRoomRecordId);
    }

    //直播间点赞
    public void toZanLive(String liveRoomRecordId, String count) {
        LiveRepository.getInstance().toZanLive(dismissDialog, zanSuccess, liveRoomRecordId, count);
    }

    //直播间设置
    public void setZBStatus(ZBSettingBean zbSettingBean) {
        LiveRepository.getInstance().setZBStatus(dismissDialog, setSuccess, zbSettingBean);
    }

    //通知服务器用户加入/离开多人连麦
    public void setVoiceMic(AnchorInfo anchorInfo, String liveRoomRecordId) {
        LiveRepository.getInstance().setVoiceMic(dismissDialog, netSuccess, micAnchorInfo, liveRoomRecordId, anchorInfo);
    }

    //多人连麦列表
    public void findMicUsers(String liveRoomRecordId) {
        LiveRepository.getInstance().findMicUsers(liveRoomRecordId,moreLinkList);
    }

}
