package com.xaqinren.healthyelders.moduleZhiBo.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.databinding.ActivityLiveGuanzhunBinding;
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
import com.xaqinren.healthyelders.moduleZhiBo.viewModel.LiveGuanzhongViewModel;
import com.xaqinren.healthyelders.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;

/**
 * Created by Lee. on 2021/4/25.
 * 直播间-观众页面
 */
public class LiveGuanzhongActivity extends BaseActivity<ActivityLiveGuanzhunBinding, LiveGuanzhongViewModel> implements IMLVBLiveRoomListener, View.OnClickListener {

    private MLVBLiveRoom mLiveRoom;
    private LiveInitInfo mLiveInitInfo;
    private List<TCChatEntity> msgList;   // 消息列表
    private TCChatMsgListAdapter msgAdapter;
    private String mRoomID;
    private Disposable disposable;
    private Handler mHandler = new Handler(Looper.getMainLooper());

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
        //获取LiveRoom实例
        mLiveRoom = MLVBLiveRoom.sharedInstance(getApplication());
        //登录直播间IM服务
        viewModel.toLoginRoom(mLiveRoom);
        initEvent();
        initLiveInfo();
        initMsgList();
    }

    //初始化房间信息
    private void initLiveInfo() {
        Glide.with(this).load(mLiveInitInfo.avatarUrl).diskCacheStrategy(DiskCacheStrategy.ALL).into(binding.rivPhoto);
        binding.tvName.setText(mLiveInitInfo.nickname);
        //禁止送礼
        if (mLiveInitInfo.getCanGift()) {
            binding.btnGift.setVisibility(View.GONE);
        }
        //禁止连麦
        if (mLiveInitInfo.getCanMic()) {
            binding.btnLianmai.setVisibility(View.GONE);
        }
        //禁止带货
        if (mLiveInitInfo.getCanSale()) {
            binding.btnGoods.setVisibility(View.GONE);
        }
    }

    //初始化聊天列表
    private void initMsgList() {
        msgList = new ArrayList<>();
        //添加一条默认的展示文本消息
        TCChatEntity tcChatEntity = new TCChatEntity();
        tcChatEntity.setSenderName("");
        tcChatEntity.setType(LiveConstants.SHOW_TYPE);
        tcChatEntity.setContent("本平台提倡绿色健康直播，严禁在平台内外出现诱导未成年人送礼打赏、诈骗、赌博、非法转移财产、低俗色情、吸烟酗酒等不当行为， 若有违反，平台有权对您采取包括暂停支付收益、冻结或封禁帐号等措施，同时向相关部门依法追究您的法律责任。如因此给平台造成损失，有权向您全额追偿。");
        msgList.add(tcChatEntity);

        msgAdapter = new TCChatMsgListAdapter(this, binding.lvMsg, msgList);
        binding.lvMsg.setAdapter(msgAdapter);
    }

    private void startPlay() {
        mLiveRoom.setListener(this);
        LogUtils.v(Constant.TAG_LIVE, "拉流地址：" + mLiveInitInfo.pullStreamUrl);
        LogUtils.v(Constant.TAG_LIVE, "房间号：" + Constant.getRoomId(mLiveInitInfo.liveRoomCode));

        //加入直播间
        mLiveRoom.enterRoom(mLiveInitInfo.pullStreamUrl, Constant.getRoomId(mLiveInitInfo.liveRoomCode), binding.mTxVideoView, new EnterRoomCallback() {
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
                finish();
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
        binding.rlBack.setOnClickListener(this);
        binding.tvMsg.setOnClickListener(this);
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
        mHandler.post(new Runnable() {
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                stopPlay();
                break;
            case R.id.tv_msg:
                startActivity(ZBEditTextDialogActivity.class);
                break;
        }
    }
}
