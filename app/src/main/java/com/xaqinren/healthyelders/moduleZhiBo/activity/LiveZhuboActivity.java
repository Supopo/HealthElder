package com.xaqinren.healthyelders.moduleZhiBo.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;

import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.databinding.ActivityLiveZhuboBinding;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleZhiBo.adapter.TCChatMsgListAdapter;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveInitInfo;
import com.xaqinren.healthyelders.moduleZhiBo.bean.TCChatEntity;
import com.xaqinren.healthyelders.moduleZhiBo.bean.TCUserInfo;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.IMLVBLiveRoomListener;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.LiveConstants;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.MLVBLiveRoom;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.roomutil.commondef.AnchorInfo;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.roomutil.commondef.AudienceInfo;
import com.xaqinren.healthyelders.moduleZhiBo.viewModel.LiveZhuboViewModel;
import com.xaqinren.healthyelders.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;

/**
 * Created by Lee. on 2021/4/25.
 * 主播页面
 */
public class LiveZhuboActivity extends BaseActivity<ActivityLiveZhuboBinding, LiveZhuboViewModel> implements IMLVBLiveRoomListener {

    private MLVBLiveRoom mLiveRoom;
    private LiveInitInfo mLiveInitInfo;
    private Disposable disposable;
    private List<TCChatEntity> msgList = new ArrayList<>();   // 消息列表
    private TCChatMsgListAdapter msgAdapter;
    private String mRoomID;
    private Handler mHandler = new Handler(Looper.getMainLooper());


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
        //接受房间初始化信息
        Bundle bundle = getIntent().getExtras();
        mLiveInitInfo = (LiveInitInfo) bundle.getSerializable(Constant.LiveInitInfo);
        mRoomID = Constant.getRoomId(mLiveInitInfo.liveRoomCode);
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

        //添加一条默认的展示文本消息
        TCChatEntity tcChatEntity = new TCChatEntity();
        tcChatEntity.setSenderName("");
        tcChatEntity.setType(LiveConstants.SHOW_TYPE);
        tcChatEntity.setContent("本平台提倡绿色健康直播，严禁在平台内外出现诱导未成年人送礼打赏、诈骗、赌博、非法转移财产、低俗色情、吸烟酗酒等不当行为， 若有违反，平台有权对您采取包括暂停支付收益、冻结或封禁帐号等措施，同时向相关部门依法追究您的法律责任。如因此给平台造成损失，有权向您全额追偿。");
        msgList.add(tcChatEntity);

        msgAdapter = new TCChatMsgListAdapter(this, binding.lvMsg, msgList);
        binding.lvMsg.setAdapter(msgAdapter);
    }

    private void startPublish() {
        mLiveRoom.setListener(this);
        //启动本地预览
        //打开本地预览
        mLiveRoom.startLocalPreview(true, binding.mTxVideoView);
        //创建直播间
        mLiveRoom.createRoom(Constant.getRoomId(mLiveInitInfo.liveRoomCode), "", new IMLVBLiveRoomListener.CreateRoomCallback() {
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
            //通知服务器结束直播
            //弹窗提示
            showDialog();
            viewModel.closeLive(mLiveInitInfo.liveRoomRecordId);
            stopPublish();
        });
        binding.tvMsg.setOnClickListener(lis -> {
            startActivity(ZBEditTextDialogActivity.class);
        });
        disposable = RxBus.getDefault().toObservable(EventBean.class).subscribe(eventBean -> {
            if (eventBean.msgId == LiveConstants.SEND_MSG) {
                toSendTextMsg(eventBean.content);
            }
        });
        RxSubscriptions.add(disposable);
    }

    //发送文字消息
    private void toSendTextMsg(String msg) {
        TCChatEntity entity = new TCChatEntity();
        entity.setSenderName("我 : ");
        entity.setContent(msg);
        entity.setType(LiveConstants.TEXT_TYPE);
        notifyMsg(entity);
        mLiveRoom.sendRoomTextMsg(msg, null);
    }

    //接受处理文字消息
    public void toRecvTextMsg(TCUserInfo userInfo, String text) {
        TCChatEntity entity = new TCChatEntity();
        if (TextUtils.isEmpty(userInfo.nickname)) {
            entity.setSenderName(LiveConstants.NIKENAME + userInfo.userid + ": ");
        } else {
            entity.setSenderName(userInfo.nickname + ": ");

        }
        entity.setContent(text);
        entity.setType(LiveConstants.TEXT_TYPE);

        notifyMsg(entity);
    }

    //刷新消息列表
    private void notifyMsg(final TCChatEntity entity) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (msgList.size() > 1000) {
                    while (msgList.size() > 900) {
                        msgList.remove(0);
                    }
                }
                msgList.add(entity);
                msgAdapter.notifyDataSetChanged();
            }
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
        if (!roomID.equals(mRoomID)) {
            return;
        }
        TCUserInfo userInfo = new TCUserInfo(userID, userName, userAvatar);
        toRecvTextMsg(userInfo, message);
    }

    @Override
    public void onRecvRoomCustomMsg(String roomID, String userID, String userName, String userAvatar, String cmd, Object message, String userLevel) {

    }

    @Override
    public void onRecvC2CCustonMsg(String senderId, String cmd, String message) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }
}
