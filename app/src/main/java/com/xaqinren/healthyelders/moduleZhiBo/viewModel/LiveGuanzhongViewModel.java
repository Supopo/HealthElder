package com.xaqinren.healthyelders.moduleZhiBo.viewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.apiserver.LiveRepository;
import com.xaqinren.healthyelders.apiserver.UserRepository;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveHeaderInfo;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveInitInfo;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ZBUserListBean;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.IMLVBLiveRoomListener;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.MLVBLiveRoom;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.roomutil.commondef.AnchorInfo;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.roomutil.commondef.LoginInfo;
import com.xaqinren.healthyelders.utils.LogUtils;

import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;

public class LiveGuanzhongViewModel extends BaseViewModel {
    public LiveGuanzhongViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Boolean> loginRoomSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> exitSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> zanSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> followSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> dismissDialog = new MutableLiveData<>();
    public MutableLiveData<LiveHeaderInfo> liveHeaderInfo = new MutableLiveData<>();
    public MutableLiveData<Integer> netSuccess = new MutableLiveData<>();//1 禁言 2加入多人连麦 21加入多人连麦失败
    public MutableLiveData<List<ZBUserListBean>> moreLinkList = new MutableLiveData<>();

    public void toLoginRoom(MLVBLiveRoom mLiveRoom) {
        //判断登录
        LoginInfo loginInfo = new LoginInfo();
        loginInfo.userAvatar = UserInfoMgr.getInstance().getUserInfo().getAvatarUrl();
        loginInfo.userName = UserInfoMgr.getInstance().getUserInfo().getNickname();
        LogUtils.v(Constant.TAG_LIVE, "loginInfo.userName: " + loginInfo.userName);
        loginInfo.sdkAppID = 1400392607;
        loginInfo.userID = UserInfoMgr.getInstance().getUserInfo().getId();
        loginInfo.userSig = "eJw1jl0LgjAYhf-Lbg3b3s1tCl0GFoKI4UV0I23GW5YyJY3ovydal*fjOZw3OSS5b8cWnSURSKUFpavZfFpHIgI*JYvuzK1sWzQkYlOHhyCpWhI09tFjhTPAuJagBedKCBUoJgL5H8DLlB-TrbuWYTDkXTo0XjHGtD6tsXbsldEz7eKs8sTOmaTYN5sf2eN9esckUNAAQn**VREzgA__";
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

    //刷新直播间顶部信息
    public void rushLiveInfo(String liveRoomRecordId) {
        LiveRepository.getInstance().rushLiveInfo(dismissDialog, liveHeaderInfo, liveRoomRecordId);
    }

    //直播间点赞
    public void toZanLive(String liveRoomRecordId, String count) {
        LiveRepository.getInstance().toZanLive(dismissDialog, zanSuccess, liveRoomRecordId, count);
    }

    //离开直播间
    public void leaveLive(String liveRoomRecordId) {
        LiveRepository.getInstance().leaveLive(dismissDialog, exitSuccess, liveRoomRecordId);
    }

    //用户关注
    public void toFollow(String userId) {
        UserRepository.getInstance().toFollow(followSuccess, dismissDialog, userId);
    }

    //新增粉丝统计
    public void addFansCount(String liveRoomRecordId) {
        LiveRepository.getInstance().addFansCount(liveRoomRecordId);
    }

    //设置麦克风是否静音
    public void setVoiceMicMute(String liveRoomRecordId, String targetId, boolean voiceMicMute) {
        LiveRepository.getInstance().setVoiceMicMute(dismissDialog, netSuccess, liveRoomRecordId, targetId, voiceMicMute);
    }

    //多人连麦列表
    public void findMicUsers(String liveRoomRecordId) {
        LiveRepository.getInstance().findMicUsers(liveRoomRecordId, moreLinkList);
    }

}
