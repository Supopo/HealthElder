package com.xaqinren.healthyelders.moduleZhiBo.viewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.apiserver.LiveRepository;
import com.xaqinren.healthyelders.apiserver.UserRepository;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleLogin.bean.UserInfoBean;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveHeaderInfo;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveInitInfo;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ZBUserListBean;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.IMLVBLiveRoomListener;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.MLVBLiveRoom;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.roomutil.commondef.LoginInfo;

import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;

public class LiveGuanzhongViewModel extends BaseViewModel {
    public LiveGuanzhongViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Boolean> sendGiftSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> loginRoomSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> exitSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> zanSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> followSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> dismissDialog = new MutableLiveData<>();
    public MutableLiveData<LiveHeaderInfo> liveHeaderInfo = new MutableLiveData<>();
    public MutableLiveData<LiveInitInfo> setInitInfo = new MutableLiveData<>();
    public MutableLiveData<UserInfoBean> userInfo = new MutableLiveData<>();
    public MutableLiveData<Integer> netSuccess = new MutableLiveData<>();//1 禁言 2加入多人连麦 21加入多人连麦失败
    public MutableLiveData<List<ZBUserListBean>> moreLinkList = new MutableLiveData<>();

    public void getUserInfo(String token){
        UserRepository.getInstance().getUserInfo(userInfo,token);
    }

    public void toLoginRoom(MLVBLiveRoom mLiveRoom) {
        //判断登录
        LoginInfo loginInfo = new LoginInfo();
        UserInfoBean userInfo = UserInfoMgr.getInstance().getUserInfo();
        if (userInfo == null) {
            return;
        }
        loginInfo.userAvatar = userInfo.getAvatarUrl();
        loginInfo.userName = userInfo.getNickname();
        loginInfo.sdkAppID = 1400392607;
        loginInfo.userID = userInfo.getId();
        loginInfo.userSig = UserInfoMgr.getInstance().getUserSig();
        loginInfo.userLevel = userInfo.getLevelName();
        loginInfo.userLevelIcon = userInfo.getLevelIcon();

        mLiveRoom.login(false, loginInfo, new IMLVBLiveRoomListener.LoginCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                Log.v(Constant.TAG_LIVE, "LiveRoom登录失败：" + errCode);
                Log.v(Constant.TAG_LIVE, "LiveRoom登录失败：" + errInfo);
                //登陆失败操作
                UserRepository.getInstance().getUserSig(UserInfoMgr.getInstance().getHttpToken());
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

    public void getLiveStatus(String liveRoomId) {
        LiveRepository.getInstance().getLiveStatus(liveRoomId, setInitInfo);
    }


    public void sendGift(String liveRoomRecordId, String targetId, String giftId) {
        LiveRepository.getInstance().sendGift(sendGiftSuccess, liveRoomRecordId, targetId, giftId);
    }
}
