package com.xaqinren.healthyelders.moduleZhiBo.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tencent.qcloud.tim.uikit.utils.ToastUtil;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.databinding.ActivityLiveGuanzhunBinding;
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
import com.xaqinren.healthyelders.moduleZhiBo.viewModel.LiveGuanzhongViewModel;
import com.xaqinren.healthyelders.moduleZhiBo.widgetLike.TCFrequeControl;
import com.xaqinren.healthyelders.utils.AnimUtils;
import com.xaqinren.healthyelders.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

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
    private TCFrequeControl mLikeFrequeControl;    //点赞频率控制
    private int mZanNum;    //本次点赞数量
    private TopUserHeadAdapter topHeadAdapter;
    private Timer ggTimer;
    private TimerTask ggAnimTask;

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
        showDialog("进入中...");
        viewModel.toLoginRoom(mLiveRoom);
        initEvent();
        initLiveInfo();
        initMsgList();
    }

    //初始化房间信息
    private void initLiveInfo() {

        //退掉之前的群
        if (mLiveInitInfo.groupIds != null && mLiveInitInfo.groupIds.length > 0) {
            for (String groupId : mLiveInitInfo.groupIds) {
                //判断下不是当前的群
                if (!Constant.getRoomId(mLiveInitInfo.liveRoomCode).equals(groupId)) {
                    //退出之前的的群
                    mLiveRoom.exitGroup(groupId);
                }

            }
        }

        Glide.with(this).load(mLiveInitInfo.avatarUrl).diskCacheStrategy(DiskCacheStrategy.ALL).into(binding.rivPhoto);
        binding.tvName.setText(mLiveInitInfo.nickname);
        //禁止送礼
        if (!mLiveInitInfo.getCanGift()) {
            binding.btnGift.setVisibility(View.GONE);
        }
        //禁止连麦
        if (!mLiveInitInfo.getCanMic()) {
            binding.btnLianmai.setVisibility(View.GONE);
        }
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

    private void startPlay() {
        showDialog("加载画面...");
        mLiveRoom.setListener(this);
        LogUtils.v(Constant.TAG_LIVE, "拉流地址：" + mLiveInitInfo.pullStreamUrl);
        LogUtils.v(Constant.TAG_LIVE, "房间号：" + Constant.getRoomId(mLiveInitInfo.liveRoomCode));

        //加入直播间
        mLiveRoom.enterRoom(mLiveInitInfo.pullStreamUrl, Constant.getRoomId(mLiveInitInfo.liveRoomCode), binding.mTxVideoView, new EnterRoomCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                dismissDialog();
                LogUtils.v(Constant.TAG_LIVE, "加入直播间失败：" + errCode);
                LogUtils.v(Constant.TAG_LIVE, "加入直播间失败：" + errInfo);
            }

            @Override
            public void onSuccess() {
                dismissDialog();
                LogUtils.v(Constant.TAG_LIVE, "加入直播间成功");
                //群发进入直播间的消息
                mLiveRoom.sendRoomCustomMsg(String.valueOf(LiveConstants.IMCMD_ENTER_LIVE), "", null);
                isPlaying = true;
            }
        });

    }

    private boolean isPlaying;

    private void stopPlay() {
        mLiveRoom.exitRoom(new ExitRoomCallback() {
            @Override
            public void onSuccess() {
                LogUtils.v(Constant.TAG_LIVE, "直播间退出成功");
                //群发退出直播间的消息
                mLiveRoom.sendRoomCustomMsg(String.valueOf(LiveConstants.IMCMD_EXIT_LIVE), "", null);
                //去通知服务器离开直播间
                mLiveRoom.setListener(null);
                isPlaying = false;
            }

            @Override
            public void onError(int errCode, String e) {
                LogUtils.v(Constant.TAG_LIVE, "直播间退出失败:" + errCode);
                LogUtils.v(Constant.TAG_LIVE, "直播间退出失败:" + e);
            }
        });
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

        //退出直播间
        binding.btnBack.setOnClickListener(this);
        binding.tvMsg.setOnClickListener(this);
        binding.btnZan.setOnClickListener(this);
        binding.tvFollow.setOnClickListener(this);
    }

    //发送文字消息
    private void toSendTextMsg(String msg) {
        addMsg2List("我 ", msg, LiveConstants.IMCMD_TEXT_MSG);
        mLiveRoom.sendRoomTextMsg(msg, null);
    }

    private void addMsg2List(String sendName, String msg, int type) {
        TCChatEntity entity = new TCChatEntity();
        entity.setSenderName(sendName);
        entity.setContent(msg);
        entity.setType(type);
        notifyMsg(entity);
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
        if (!roomID.equals(mRoomID)) {
            return;
        }
        //主播关闭直播，解散了群
        //跳转结算页面
        startActivity(ZhiboOverActivity.class);
        finish();
    }

    @Override
    public void onAnchorEnter(AnchorInfo anchorInfo) {

    }

    @Override
    public void onAnchorExit(AnchorInfo anchorInfo) {

    }

    @Override
    public void onAudienceEnter(AudienceInfo audienceInfo) {
        //暂时没用
    }

    @Override
    public void onAudienceExit(AudienceInfo audienceInfo) {
        //暂时没用
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
                break;
            case LiveConstants.IMCMD_EXIT_LIVE:
                //用户退出房间消息
                toRecvTextMsg(userInfo, LiveConstants.SHOW_EXIT_LIVE, type);
                break;
            case LiveConstants.IMCMD_LIKE:
                //用户点赞消息
                showDianZan();
                //延迟3S再去刷新，用户端是延迟2S刷新的
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toRushLiveInfo();
                    }
                }, Constant.TIME_DIAN_ZAN_WAIT + 1000);
                break;
            case LiveConstants.IMCMD_ZB_COMEBACK:
                //收到主播继续直播的消息
                //重新拉流
                mLiveRoom.reStartPlay(mLiveInitInfo.pullStreamUrl, binding.mTxVideoView, null);
                break;
            case LiveConstants.IMCMD_FOLLOW:
                //展示关注消息
                toRecvTextMsg(userInfo, LiveConstants.SHOW_FOLLOW, type);
                break;
            case LiveConstants.IMCMD_GONGGAO_MSG:
                //公告消息 顶部展示
                String jsonMsg = (String) message;
                LogUtils.v(Constant.TAG_LIVE, "jsonMsg: " + jsonMsg);
                JsonMsgBean jsonMsgBean = JSON.parseObject(jsonMsg, JsonMsgBean.class);
                binding.tvGgtype.setText("用户");
                binding.tvGgname.setText(jsonMsgBean.nickname);
                binding.tvGgcontent.setText(jsonMsgBean.content);

                binding.llGonggao.setVisibility(View.VISIBLE);
                //公告进入动画
                binding.llGonggao.startAnimation(AnimUtils.getAnimation(this, R.anim.anim_slice_in_left));

                if (ggTimer == null) {
                    ggTimer = new Timer();
                }
                if (ggAnimTask != null) {
                    ggAnimTask.cancel();
                }
                ggAnimTask = new TimerTask() {

                    @Override
                    public void run() {
                        //公告退出动画
                        Animation hideAnim = AnimUtils.getAnimation(LiveGuanzhongActivity.this, R.anim.anim_slice_out_left);
                        binding.llGonggao.startAnimation(hideAnim);

                        hideAnim.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                binding.llGonggao.setVisibility(View.GONE);
                                ggAnimTask.cancel();
                                ggTimer.cancel();
                                ggTimer.purge();
                                ggAnimTask = null;
                                ggTimer = null;
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                    }
                };
                ggTimer.schedule(ggAnimTask, 3000);

                break;
            default:
                break;
        }
    }

    @Override
    public void onRecvC2CCustomMsg(String senderId, String cmd, String message) {
        LogUtils.v(Constant.TAG_LIVE, cmd + "-" + message);
        int type = Integer.parseInt(cmd);
        switch (type) {
            case LiveConstants.IMCMD_FORBIDDER_TALK://禁言
                //判断是不是主播或者管理员发的
                mLiveInitInfo.setHasSpeech(true);
                ToastUtil.toastShortMessage(message);
                break;
            case LiveConstants.IMCMD_CANCEL_FORBIDDER_TALK://取消禁言
                //判断是不是主播或者管理员发的
                mLiveInitInfo.setHasSpeech(false);
                ToastUtil.toastShortMessage(message);
                break;
            case LiveConstants.IMCMD_PUT_BLACK://拉黑/踢出
                //判断是不是主播或者管理员发的
                ToastUtil.toastShortMessage(message);
                finish();
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
        if (isPlaying) {
            stopPlay();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.tv_msg:
                //判断是否被禁言
                if (!mLiveInitInfo.hasSpeech) {
                    startActivity(ZBEditTextDialogActivity.class);
                } else {
                    ToastUtil.toastShortMessage(LiveConstants.SHOW_JINYAN);
                }
                break;
            case R.id.btn_zan:
                toDianZan();
                break;
            case R.id.tv_follow:
                //关注主播
                //通知服务器-成功
                binding.tvFollow.setText("已关注");
                //群发关注消息
                mLiveRoom.sendRoomCustomMsg(String.valueOf(LiveConstants.IMCMD_FOLLOW), "", null);
                addMsg2List(UserInfoMgr.getInstance().getUserInfo().getNickname(), LiveConstants.SHOW_FOLLOW, LiveConstants.IMCMD_FOLLOW);
                break;
            default:
                break;
        }
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        disposable = RxBus.getDefault().toObservable(EventBean.class).subscribe(eventBean -> {
            if (eventBean.msgId == LiveConstants.SEND_MSG) {
                toSendTextMsg(eventBean.content);
            }
        });
        RxSubscriptions.add(disposable);
        viewModel.loginRoomSuccess.observe(this, loginSuccess -> {
            if (loginSuccess) {
                startPlay();
                toRushLiveInfo();
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
