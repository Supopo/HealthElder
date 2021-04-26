package com.xaqinren.healthyelders.moduleZhiBo.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivityLiveZhuboBinding;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveInitInfo;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.IMLVBLiveRoomListener;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.MLVBLiveRoom;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.roomutil.commondef.AnchorInfo;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.roomutil.commondef.AudienceInfo;
import com.xaqinren.healthyelders.moduleZhiBo.viewModel.LiveZhuboViewModel;
import com.xaqinren.healthyelders.utils.LogUtils;

import me.goldze.mvvmhabit.base.BaseActivity;

/**
 * Created by Lee. on 2021/4/25.
 * 主播页面
 */
public class LiveZhuboActivity extends BaseActivity<ActivityLiveZhuboBinding, LiveZhuboViewModel> implements IMLVBLiveRoomListener {

    private MLVBLiveRoom mLiveRoom;
    private LiveInitInfo mLiveInitInfo;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_live_zhubo;
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
        mLiveInitInfo = (LiveInitInfo) bundle.getSerializable(Constant.LiveInitInfo);
    }

    @Override
    public void initData() {
        super.initData();
        //设置全屏
        setStatusBarTransparent();
        initEvent();
        //获取LiveRoom实例
        mLiveRoom = MLVBLiveRoom.sharedInstance(getApplication());
        //开启推流
        startPublish();
    }

    private void startPublish() {
        mLiveRoom.setListener(this);
        //启动本地预览
        //打开本地预览
        mLiveRoom.startLocalPreview(true, binding.mTxVideoView);
        //创建直播间
        mLiveRoom.createRoom("", "", new IMLVBLiveRoomListener.CreateRoomCallback() {
            @Override
            public void onSuccess(String roomId) {
                Log.v(Constant.TAG_LIVE, "直播间创建成功");
                //去通知服务器开启直播间
                LogUtils.v(Constant.TAG_LIVE, mLiveRoom.getPushUrl());
                mLiveInitInfo.pushUrl = mLiveRoom.getPushUrl();
                viewModel.startLive(mLiveInitInfo);
            }


            @Override
            public void onError(int errCode, String e) {
                Log.v(Constant.TAG_LIVE, "直播间创建失败：" + errCode);
                Log.v(Constant.TAG_LIVE, "直播间创建失败：" + e);
            }
        });

    }

    private void stopPublish() {
        mLiveRoom.exitRoom(new ExitRoomCallback() {
            @Override
            public void onSuccess() {
                //去通知服务器退出了直播
                Log.v(Constant.TAG_LIVE, "直播间退出成功");
                finish();
            }

            @Override
            public void onError(int errCode, String e) {
                Log.v(Constant.TAG_LIVE, "直播间退出失败:" + errCode);
                Log.v(Constant.TAG_LIVE, "直播间退出失败:" + e);
            }
        });
        mLiveRoom.setListener(null);
    }

    public void initEvent() {
        //退出直播
        binding.btnBack.setOnClickListener(lis -> {
            //弹窗提示
            stopPublish();
        });
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.dismissDialog.observe(this, dismiss -> {
            if (dismiss != null) {
                if (dismiss) {
                    dismissDialog();
                }
            }
        });
        viewModel.liveInitInfo.observe(this, liveInitInfo -> {
            mLiveInitInfo.liveRoomRecordId = liveInitInfo.liveRoomRecordId;
            if (liveInitInfo.groupIds != null && liveInitInfo.groupIds.length > 0) {
                for (String groupId : liveInitInfo.groupIds) {
                    //判断下不是当前的群
                    if (!Constant.getRoomId(mLiveInitInfo.liveRoomCode).equals(groupId)) {
                        //退出之前的的群
                        mLiveRoom.exitGroup(groupId);
                    }

                }
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
