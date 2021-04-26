package com.xaqinren.healthyelders.moduleZhiBo.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivityLiveGuanzhunBinding;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveInitInfo;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.IMLVBLiveRoomListener;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.MLVBLiveRoom;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.roomutil.commondef.AnchorInfo;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.roomutil.commondef.AudienceInfo;
import com.xaqinren.healthyelders.moduleZhiBo.viewModel.LiveGuanzhongViewModel;
import com.xaqinren.healthyelders.utils.LogUtils;

import me.goldze.mvvmhabit.base.BaseActivity;

/**
 * Created by Lee. on 2021/4/25.
 * 直播间-观众页面
 */
public class LiveGuanzhongActivity extends BaseActivity<ActivityLiveGuanzhunBinding, LiveGuanzhongViewModel> implements IMLVBLiveRoomListener {

    private MLVBLiveRoom mLiveRoom;
    private String playUrl;
    private String roomId;
    private LiveInitInfo liveInitInfo;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_live_guanzhun;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initParam() {
        super.initParam();
        //设置不熄屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Bundle bundle = getIntent().getExtras();
        liveInitInfo = (LiveInitInfo) bundle.getSerializable(Constant.LiveInitInfo);
    }

    @Override
    public void initData() {
        super.initData();
        //设置全屏
        setStatusBarTransparent();
        initEvent();
        //获取LiveRoom实例
        mLiveRoom = MLVBLiveRoom.sharedInstance(getApplication());
        viewModel.toLoginRoom(mLiveRoom);
    }


    private void startPlay() {
        mLiveRoom.setListener(this);
        LogUtils.v(Constant.TAG_LIVE, "拉流地址：" + liveInitInfo.pullStreamUrl);
        LogUtils.v(Constant.TAG_LIVE, "房间号：" + Constant.getRoomId(liveInitInfo.userId));

        //加入直播间
        mLiveRoom.enterRoom(liveInitInfo.pullStreamUrl, Constant.getRoomId(liveInitInfo.userId), binding.mTxVideoView, new EnterRoomCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                LogUtils.v(Constant.TAG_LIVE, "加入直播间失败：" + errCode);
                LogUtils.v(Constant.TAG_LIVE, "加入直播间失败：" + errInfo);
            }

            @Override
            public void onSuccess() {
                LogUtils.v(Constant.TAG_LIVE, "加入直播间成功");
                //群发进入直播间的消息
            }
        });

    }

    private void stopPlay() {
        mLiveRoom.exitRoom(new ExitRoomCallback() {
            @Override
            public void onSuccess() {
                Log.v(Constant.TAG_LIVE, "直播间退出成功");
                //群发退出直播间的消息
                //去通知服务器离开直播间
                mLiveRoom.setListener(null);
            }

            @Override
            public void onError(int errCode, String e) {
                Log.v(Constant.TAG_LIVE, "直播间退出失败:" + errCode);
                Log.v(Constant.TAG_LIVE, "直播间退出失败:" + e);
            }
        });
    }

    public void initEvent() {
        //退出直播间
        binding.btnBack.setOnClickListener(lis -> {

        });
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.loginRoomSuccess.observe(this, loginSuccess -> {
            if (loginSuccess) {
                startPlay();
            }
        });
    }

    @Override
    public void onError(int errCode, String errMsg, Bundle extraInfo) {

    }

    @Override
    public void onWarning(int warningCode, String warningMsg, Bundle extraInfo) {

    }

    @Override
    public void onDebugLog(String log) {

    }

    @Override
    public void onRoomDestroy(String roomID) {

    }

    @Override
    public void onAnchorEnter(AnchorInfo anchorInfo) {

    }

    @Override
    public void onAnchorExit(AnchorInfo anchorInfo) {

    }

    @Override
    public void onAudienceEnter(AudienceInfo audienceInfo) {

    }

    @Override
    public void onAudienceExit(AudienceInfo audienceInfo) {

    }

    @Override
    public void onRequestJoinAnchor(AnchorInfo anchorInfo, String reason) {

    }

    @Override
    public void onKickoutJoinAnchor() {

    }

    @Override
    public void onRequestRoomPK(AnchorInfo anchorInfo) {

    }

    @Override
    public void onQuitRoomPK(AnchorInfo anchorInfo) {

    }

    @Override
    public void onRecvRoomTextMsg(String roomID, String userID, String userName, String userAvatar, String message) {

    }

    @Override
    public void onRecvRoomCustomMsg(String roomID, String userID, String userName, String userAvatar, String cmd, Object message, String userLevel) {

    }

    @Override
    public void onRecvC2CCustonMsg(String senderId, String cmd, String message) {

    }
}
