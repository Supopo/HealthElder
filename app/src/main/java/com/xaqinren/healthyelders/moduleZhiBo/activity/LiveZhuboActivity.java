package com.xaqinren.healthyelders.moduleZhiBo.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivityLiveZhuboBinding;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.IMLVBLiveRoomListener;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.MLVBLiveRoom;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.roomutil.commondef.AnchorInfo;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.roomutil.commondef.AudienceInfo;
import com.xaqinren.healthyelders.moduleZhiBo.viewModel.LiveZhuboViewModel;
import com.xaqinren.healthyelders.utils.LogUtils;

import java.util.HashMap;

import me.goldze.mvvmhabit.base.BaseActivity;

/**
 * Created by Lee. on 2021/4/25.
 * 主播页面
 */
public class LiveZhuboActivity extends BaseActivity<ActivityLiveZhuboBinding, LiveZhuboViewModel> implements IMLVBLiveRoomListener {

    private MLVBLiveRoom mLiveRoom;
    private HashMap<String, Object> startParamMap;

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


        startParamMap = new HashMap<>();
        startParamMap.put("liveRoomName", "直播主题");
        startParamMap.put("liveRoomCover", "https://img.qianniux.com/missing-face.png");
        startParamMap.put("address", "陕西省 西安市");
        startParamMap.put("longitude", 116.473083);
        startParamMap.put("latitude", 39.993762);
        startParamMap.put("liveRoomType", "39.VIDEO_LIVE");
        startParamMap.put("canRecordVideo", true);
        startParamMap.put("liveRoomIntroduce", "此处省略500字");
        startParamMap.put("hasIntroduce", true);
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
                startParamMap.put("pushUrl", mLiveRoom.getPushUrl());
                LogUtils.v(Constant.TAG_LIVE, mLiveRoom.getPushUrl());
                viewModel.startLive(startParamMap);
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
        viewModel.startLiveSuccess.observe(this, isSuccess -> {
            if (isSuccess != null) {
                if (isSuccess) {
                    Log.v(Constant.TAG_LIVE, "通知服务器成功");
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
