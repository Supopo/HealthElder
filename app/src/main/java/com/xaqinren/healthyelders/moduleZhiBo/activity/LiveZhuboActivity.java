package com.xaqinren.healthyelders.moduleZhiBo.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tencent.liteav.demo.beauty.BeautyParams;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.databinding.ActivityLiveZhuboBinding;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleZhiBo.adapter.TCChatMsgListAdapter;
import com.xaqinren.healthyelders.moduleZhiBo.adapter.TopUserHeadAdapter;
import com.xaqinren.healthyelders.moduleZhiBo.bean.JsonMsgBean;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveInitInfo;
import com.xaqinren.healthyelders.moduleZhiBo.bean.TCChatEntity;
import com.xaqinren.healthyelders.moduleZhiBo.bean.TCUserInfo;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.IMLVBLiveRoomListener;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.LiveConstants;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.MLVBLiveRoom;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.roomutil.commondef.AnchorInfo;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.roomutil.commondef.AudienceInfo;
import com.xaqinren.healthyelders.moduleZhiBo.popupWindow.ZBUserListPop;
import com.xaqinren.healthyelders.moduleZhiBo.viewModel.LiveZhuboViewModel;
import com.xaqinren.healthyelders.moduleZhiBo.widgetLike.TCFrequeControl;
import com.xaqinren.healthyelders.utils.LogUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;

/**
 * Created by Lee. on 2021/4/25.
 * 主播页面
 */
public class LiveZhuboActivity extends BaseActivity<ActivityLiveZhuboBinding, LiveZhuboViewModel> implements IMLVBLiveRoomListener, View.OnClickListener {

    private MLVBLiveRoom mLiveRoom;
    private LiveInitInfo mLiveInitInfo;
    private Disposable disposable;
    private List<TCChatEntity> msgList;   // 消息列表
    private TCChatMsgListAdapter msgAdapter;
    private String mRoomID;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private TCFrequeControl mLikeFrequeControl;    //点赞频率控制
    private int mZanNum;    //本次点赞数量
    private TopUserHeadAdapter topHeadAdapter;
    private ZBUserListPop zbUserListPop;
    private int commentPeopleNum = 0;//评论人数

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
        //获取LiveRoom实例
        mLiveRoom = MLVBLiveRoom.sharedInstance(getApplication());
        showDialog("进入中...");
        //后期判断是否登录，如果已经则登录注入用户信息一定要注入的
        viewModel.toLoginRoom(mLiveRoom);
        initEvent();
        initLiveInfo();
        initMsgList();


    }

    //初始化房间信息
    private void initLiveInfo() {
        Glide.with(this).load(mLiveInitInfo.avatarUrl).diskCacheStrategy(DiskCacheStrategy.ALL).into(binding.rivPhoto);
        binding.tvName.setText(mLiveInitInfo.nickname);

        //禁止带货
        if (!mLiveInitInfo.getCanSale()) {
            binding.btnGoods.setVisibility(View.GONE);
        }
        topHeadAdapter = new TopUserHeadAdapter(R.layout.item_top_user_head);
        binding.rvAvatar.setLayoutManager(new LinearLayoutManager(this));
        binding.rvAvatar.setAdapter(topHeadAdapter);
    }

    //初始化聊天列表
    private void initMsgList() {
        msgList = new ArrayList<>();
        //添加一条默认的展示文本消息
        TCChatEntity tcChatEntity = new TCChatEntity();
        tcChatEntity.setSenderName("");
        tcChatEntity.setType(LiveConstants.TYPE_SHOW);
        tcChatEntity.setContent("本平台提倡绿色健康直播，严禁在平台内外出现诱导未成年人送礼打赏、诈骗、赌博、非法转移财产、低俗色情、吸烟酗酒等不当行为， 若有违反，平台有权对您采取包括暂停支付收益、冻结或封禁帐号等措施，同时向相关部门依法追究您的法律责任。如因此给平台造成损失，有权向您全额追偿。");
        msgList.add(tcChatEntity);
        //再添加一条消息展示直播间介绍
        TCChatEntity tcChatEntity2 = new TCChatEntity();
        tcChatEntity2.setSenderName("");
        tcChatEntity2.setType(LiveConstants.TYPE_DES);
        tcChatEntity2.setContent(mLiveInitInfo.liveRoomIntroduce);
        msgList.add(tcChatEntity2);

        msgAdapter = new TCChatMsgListAdapter(this, binding.lvMsg, msgList);
        binding.lvMsg.setAdapter(msgAdapter);
    }

    private void startPublish() {
        mLiveRoom.setListener(this);
        //启动本地预览
        mLiveRoom.startLocalPreview(true, binding.mTxVideoView);

        //美颜参数-开启本地预览之后再设置
        mLiveRoom.getBeautyManager().setBeautyStyle(mLiveInitInfo.beautyStyle);
        mLiveRoom.getBeautyManager().setBeautyLevel(mLiveInitInfo.beautyLevel);
        mLiveRoom.getBeautyManager().setWhitenessLevel(mLiveInitInfo.whitenessLevel);
        mLiveRoom.getBeautyManager().setRuddyLevel(mLiveInitInfo.ruddinessLevel);

        //创建直播间
        mLiveRoom.createRoom(Constant.getRoomId(mLiveInitInfo.liveRoomCode), "", new IMLVBLiveRoomListener.CreateRoomCallback() {
            @Override
            public void onSuccess(String roomId) {
                LogUtils.v(Constant.TAG_LIVE, "直播间创建成功");
                //去通知服务器开启直播间
                LogUtils.v(Constant.TAG_LIVE, mLiveRoom.getPushUrl());
                mLiveInitInfo.pushUrl = mLiveRoom.getPushUrl();
                //判断如果有上次记录就是继续直播，没有就重新开启直播
                showDialog("连接服务器...");
                if (TextUtils.isEmpty(mLiveInitInfo.liveRoomRecordId)) {
                    viewModel.startLive(mLiveInitInfo);
                } else {
                    viewModel.reStartLive(mLiveInitInfo.liveRoomRecordId);
                }
                //如果外面开启了观众端镜像，这里需要设置一下
                if (mLiveInitInfo.isMirror) {
                    mLiveRoom.setMirror(true);
                }
            }


            @Override
            public void onError(int errCode, String e) {
                dismissDialog();
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
                viewModel.closeLive(mLiveInitInfo.liveRoomRecordId, String.valueOf(commentPeopleNum));
                LogUtils.v(Constant.TAG_LIVE, "直播间退出成功");
            }

            @Override
            public void onError(int errCode, String e) {
                LogUtils.v(Constant.TAG_LIVE, "直播间退出失败:" + errCode);
                LogUtils.v(Constant.TAG_LIVE, "直播间退出失败:" + e);
            }
        });
        mLiveRoom.setListener(null);
    }

    public void initEvent() {
        //双击点赞事件
        binding.mTxVideoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        System.out.println("---------------按下了-------------------");
                        before_press_Y = event.getY();
                        before_press_X = event.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                        System.out.println("--------------抬起了------------------");
                        long secondTime = System.currentTimeMillis();
                        if (secondTime - firstClickTime < 500) {
                            double now_press_Y = event.getY();
                            double now_press_X = event.getX();
                            if (now_press_Y - before_press_Y <= 50 && now_press_X - before_press_X <= 50) {
                                System.out.println("-------------------处理双击事件----------------------");
                                //双击点赞
                                double2DianZan((int) now_press_Y + binding.mTxVideoView.getTop(), (int) now_press_X);
                            }
                        }

                        firstClickTime = secondTime;
                        break;

                }
                return true;
            }
        });
        //退出直播
        binding.btnBack.setOnClickListener(this);
        binding.tvMsg.setOnClickListener(this);
        binding.tvMembersNum.setOnClickListener(this);
    }

    //发送文字消息
    private void toSendTextMsg(String msg) {
        TCChatEntity entity = new TCChatEntity();
        entity.setSenderName("我 ");
        entity.setContent(msg);
        entity.setType(LiveConstants.IMCMD_TEXT_MSG);
        notifyMsg(entity);
        mLiveRoom.sendRoomTextMsg(msg, null);
    }

    //接受处理文字消息
    public void toRecvTextMsg(TCUserInfo userInfo, String text, int type) {
        TCChatEntity entity = new TCChatEntity();
        if (TextUtils.isEmpty(userInfo.nickname)) {
            entity.setSenderName(LiveConstants.NIKENAME + userInfo.userid);
        } else {
            entity.setSenderName(userInfo.nickname);

        }
        entity.setContent(text);
        entity.setType(type);

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

    //点赞展示
    private void showDianZan() {
        //点赞请求限制
        if (mLikeFrequeControl == null) {
            mLikeFrequeControl = new TCFrequeControl();
            mLikeFrequeControl.init(Constant.MAX_DIAN_ZAN, 1);
        }
        if (mLikeFrequeControl.canTrigger()) {
            binding.tcHeartLayout.addFavor();
        }
    }

    private boolean isDianZaning;//表示正在点赞请求操作 2S保护

    //点赞操作
    private void toDianZan() {

        //点赞发送请求限制
        if (mLikeFrequeControl == null) {
            mLikeFrequeControl = new TCFrequeControl();
            mLikeFrequeControl.init(Constant.MAX_DIAN_ZAN, 1);
        }
        if (mLikeFrequeControl.canTrigger()) {
            mZanNum++;
            binding.tcHeartLayout.addFavor();
            //向直播间发送点赞消息
            mLiveRoom.sendRoomCustomMsg(String.valueOf(LiveConstants.IMCMD_LIKE), "", null);
        }
        if (!isDianZaning) {
            //统计2S内的点赞次数 统一向服务器发送
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //向服务器发送
                    if (mZanNum == 0) {
                        return;
                    }
                    viewModel.toZanLive(mLiveInitInfo.liveRoomRecordId, String.valueOf(mZanNum));
                    mZanNum = 0;
                    isDianZaning = false;
                }
            }, Constant.TIME_DIAN_ZAN_WAIT);
        }
        isDianZaning = true;

    }

    //双击点赞
    private double before_press_Y;
    private double before_press_X;
    private long firstClickTime;
    private int[] drawableIds = new int[]{R.mipmap.dianzan_icon01, R.mipmap.dianzan_icon02, R.mipmap.dianzan_icon03, R.mipmap.dianzan_icon04, R.mipmap.dianzan_icon05};
    private Random mRandom = new Random();

    private void double2DianZan(int now_press_Y, int now_press_X) {
        ImageView iv = new ImageView(this);
        int i = mRandom.nextInt(drawableIds.length - 1);
        iv.setBackground(getResources().getDrawable(drawableIds[i]));

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.width = (int) getResources().getDimension(R.dimen.dp_40);
        lp.height = lp.width;
        lp.setMargins(now_press_X - lp.width / 2, now_press_Y - lp.height / 2, 0, 0);
        iv.setLayoutParams(lp);
        iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        binding.rlShowLike.removeAllViews();
        binding.rlShowLike.addView(iv);

        Animation scaleAnimation1 = AnimationUtils.loadAnimation(this, R.anim.zbj_double_zan_enter);
        Animation scaleAnimation2 = AnimationUtils.loadAnimation(this, R.anim.zbj_double_zan_exit);
        iv.startAnimation(scaleAnimation1);

        scaleAnimation1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                iv.setAnimation(scaleAnimation2);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        scaleAnimation2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.rlShowLike.removeAllViews();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        toDianZan();
    }


    private long lastRushTime;//上次刷新接口2S内不再刷新

    private void toRushLiveInfo() {
        long secondTime = System.currentTimeMillis();
        if (secondTime - lastRushTime < Constant.TIME_RUSH_LIVEINFO) {
            return;
        }
        viewModel.rushLiveInfo(mLiveInitInfo.liveRoomRecordId);
        lastRushTime = secondTime;
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
        //用户进群提醒 暂时没用
    }

    @Override
    public void onAudienceExit(AudienceInfo audienceInfo) {
        //用户退群提醒 暂时没用
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
        toRecvTextMsg(userInfo, message, LiveConstants.IMCMD_TEXT_MSG);
    }

    @Override
    public void onRecvRoomCustomMsg(String roomID, String userID, String userName, String userAvatar, String cmd, Object message, String userLevel) {
        if (!roomID.equals(mRoomID)) {
            return;
        }
        TCUserInfo userInfo = new TCUserInfo(userID, userName, userAvatar);
        int type = Integer.parseInt(cmd);
        switch (type) {
            case LiveConstants.IMCMD_ENTER_LIVE:
                //用户进入房间消息
                toRecvTextMsg(userInfo, LiveConstants.SHOW_ENTER_LIVE, type);
                //调用刷新接口
                toRushLiveInfo();
                break;
            case LiveConstants.IMCMD_EXIT_LIVE:
                //用户退出房间消息
                toRecvTextMsg(userInfo, LiveConstants.SHOW_EXIT_LIVE, type);
                //调用刷新接口
                toRushLiveInfo();
                break;
            case LiveConstants.IMCMD_LIKE:
                //展示用户点赞
                showDianZan();
                //延迟3S再去刷新，用户端是延迟2S刷新的
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toRushLiveInfo();
                    }
                }, Constant.TIME_DIAN_ZAN_WAIT + 1000);
                break;
            case LiveConstants.IMCMD_FOLLOW:
                //展示关注消息
                toRecvTextMsg(userInfo, LiveConstants.SHOW_FOLLOW, type);
                break;
            default:
                break;
        }
    }


    @Override
    public void onRecvC2CCustomMsg(String senderId, String cmd, String message) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_members_num:
                if (zbUserListPop == null) {
                    zbUserListPop = new ZBUserListPop(this, mLiveInitInfo.liveRoomRecordId, mLiveInitInfo.liveRoomId, "婆婆酥的守护团");
                }
                zbUserListPop.showPopupWindow();
                break;
            case R.id.btn_back:
                //通知服务器结束直播
                //弹窗提示
                showDialog("结束直播...");
                stopPublish();
                break;
            case R.id.tv_msg:
                startActivity(ZBEditTextDialogActivity.class);
                break;
        }
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        disposable = RxBus.getDefault().toObservable(EventBean.class).subscribe(eventBean -> {
            switch (eventBean.msgId) {
                case LiveConstants.SEND_MSG:
                    toSendTextMsg(eventBean.content);
                    break;
                case LiveConstants.ZB_USER_SET:
                    if (eventBean.msgType == LiveConstants.SETTING_JINYAN) {      //禁言-取消禁言
                        if (eventBean.status == 1) {
                            //禁言
                            mLiveRoom.sendC2CCustomMsg(eventBean.content, String.valueOf(LiveConstants.IMCMD_FORBIDDER_TALK), LiveConstants.SHOW_JINYAN, null);
                        } else {
                            //取消禁言
                            mLiveRoom.sendC2CCustomMsg(eventBean.content, String.valueOf(LiveConstants.IMCMD_CANCEL_FORBIDDER_TALK), LiveConstants.SHOW_QXJINYAN, null);
                        }
                    } else if (eventBean.msgType == LiveConstants.SETTING_LAHEI) {      //拉黑
                        mLiveRoom.sendC2CCustomMsg(eventBean.content, String.valueOf(LiveConstants.IMCMD_PUT_BLACK), LiveConstants.SHOW_LAHEI, new IMLVBLiveRoomListener.SendC2CCustomMsgCallback() {

                            @Override
                            public void onError(int errCode, String errInfo) {

                            }

                            @Override
                            public void onSuccess() {
                                toRushLiveInfo();
                                //群发公告消息-拉黑
                                String jsonMsg = JsonMsgBean.json("0", eventBean.nickname, LiveConstants.GONGGAO_TICHU);
                                mLiveRoom.sendRoomCustomMsg(String.valueOf(LiveConstants.IMCMD_GONGGAO_MSG), jsonMsg, null);
                                LogUtils.v(Constant.TAG_LIVE, "拉黑成功");
                            }
                        });
                    } else if (eventBean.msgType == LiveConstants.SETTING_TICHU) {      //踢出
                        mLiveRoom.sendC2CCustomMsg(eventBean.content, String.valueOf(LiveConstants.IMCMD_PUT_BLACK), LiveConstants.SHOW_TICHU, new IMLVBLiveRoomListener.SendC2CCustomMsgCallback() {

                            @Override
                            public void onError(int errCode, String errInfo) {

                            }

                            @Override
                            public void onSuccess() {
                                toRushLiveInfo();
                                //群发公告消息-拉黑
                                String jsonMsg = JsonMsgBean.json("1", eventBean.nickname, LiveConstants.GONGGAO_TICHU);
                                mLiveRoom.sendRoomCustomMsg(String.valueOf(LiveConstants.IMCMD_GONGGAO_MSG), jsonMsg, null);
                                LogUtils.v(Constant.TAG_LIVE, "踢出成功");
                            }
                        });
                    }
                    break;
            }
        });
        RxSubscriptions.add(disposable);

        viewModel.loginRoomSuccess.observe(this, isSuccess -> {
            showDialog("创建直播间...");
            //开启推流
            startPublish();
        });
        viewModel.dismissDialog.observe(this, dismiss -> {
            if (dismiss != null) {
                if (dismiss) {
                    dismissDialog();
                }
            }
        });
        //开启直播-继续直播通知服务器回调
        viewModel.startLiveInfo.observe(this, liveInitInfo -> {
            if (liveInitInfo != null) {
                if (!TextUtils.isEmpty(mLiveInitInfo.liveRoomRecordId)) {
                    //主播继续直播消息 通知大家主播回来了，最好重新拉一下流
                    mLiveRoom.sendRoomCustomMsg(String.valueOf(LiveConstants.IMCMD_ZB_COMEBACK), "", null);
                }

                mLiveInitInfo.liveRoomId = liveInitInfo.liveRoomId;
                mLiveInitInfo.liveRoomRecordId = liveInitInfo.liveRoomRecordId;
                //退掉之前的群
                if (liveInitInfo.groupIds != null && liveInitInfo.groupIds.length > 0) {
                    for (String groupId : liveInitInfo.groupIds) {
                        //判断下不是当前的群
                        if (!Constant.getRoomId(mLiveInitInfo.liveRoomCode).equals(groupId)) {
                            //退出之前的的群
                            mLiveRoom.exitGroup(groupId);
                        }

                    }
                }
            }
        });

        //关闭直播-通知服务器回调-跳转直播结算页面
        viewModel.exitSuccess.observe(this, exitSuccess -> {
            if (exitSuccess != null) {
                if (exitSuccess) {
                    Bundle bundle = new Bundle();
                    bundle.putString("liveRoomRecordId", mLiveInitInfo.liveRoomRecordId);
                    startActivity(ZhiboOverActivity.class, bundle);
                    finish();
                }
            }
        });

        //更新点赞数和直播间人数
        viewModel.liveHeaderInfo.observe(this, liveHeaderInfo -> {
            if (liveHeaderInfo != null) {
                //更新点赞人数
                binding.tvZanNum.setText(liveHeaderInfo.totalZanNum);
                //更新榜单头像
                if (liveHeaderInfo.liveRoomUsers != null) {
                    topHeadAdapter.setNewInstance(liveHeaderInfo.liveRoomUsers);
                }
                //更新总人数
                binding.tvMembersNum.setText(liveHeaderInfo.totalPeopleNum);
            }
        });

        viewModel.zanSuccess.observe(this, zanSuccess -> {
            if (zanSuccess != null) {
                if (zanSuccess) {
                    toRushLiveInfo();
                }
            }
        });
    }
}
