package com.xaqinren.healthyelders.moduleZhiBo.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.http.HttpResponseCache;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.opensource.svgaplayer.SVGACallback;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.tencent.qcloud.tim.uikit.component.face.FaceManager;
import com.tencent.qcloud.tim.uikit.utils.ToastUtil;
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.databinding.ActivityLiveGuanzhunBinding;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.global.InfoCache;
import com.xaqinren.healthyelders.moduleHome.LoadGiftService;
import com.xaqinren.healthyelders.moduleLogin.bean.UserInfoBean;
import com.xaqinren.healthyelders.moduleZhiBo.adapter.MoreLinkAdapter;
import com.xaqinren.healthyelders.moduleZhiBo.adapter.TCChatMsgListAdapter;
import com.xaqinren.healthyelders.moduleZhiBo.adapter.TopUserHeadAdapter;
import com.xaqinren.healthyelders.moduleZhiBo.bean.GiftBean;
import com.xaqinren.healthyelders.moduleZhiBo.bean.JsonMsgBean;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveInitInfo;
import com.xaqinren.healthyelders.moduleZhiBo.bean.SendGiftBean;
import com.xaqinren.healthyelders.moduleZhiBo.bean.TCChatEntity;
import com.xaqinren.healthyelders.moduleZhiBo.bean.TCUserInfo;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ZBGoodsBean;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ZBUserListBean;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.IMLVBLiveRoomListener;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.LiveConstants;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.MLVBLiveRoom;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.roomutil.commondef.AnchorInfo;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.roomutil.commondef.AudienceInfo;
import com.xaqinren.healthyelders.moduleZhiBo.popupWindow.ZBGiftListPop;
import com.xaqinren.healthyelders.moduleZhiBo.popupWindow.ZBGoodsListPop;
import com.xaqinren.healthyelders.moduleZhiBo.popupWindow.ZBUserInfoPop;
import com.xaqinren.healthyelders.moduleZhiBo.popupWindow.ZBUserListPop;
import com.xaqinren.healthyelders.moduleZhiBo.viewModel.LiveGuanzhongViewModel;
import com.xaqinren.healthyelders.moduleZhiBo.widgetLike.TCFrequeControl;
import com.xaqinren.healthyelders.uniApp.UniService;
import com.xaqinren.healthyelders.uniApp.UniUtil;
import com.xaqinren.healthyelders.uniApp.bean.UniEventBean;
import com.xaqinren.healthyelders.utils.AnimUtils;
import com.xaqinren.healthyelders.utils.GlideUtil;
import com.xaqinren.healthyelders.utils.LogUtils;
import com.xaqinren.healthyelders.utils.MScreenUtil;
import com.xaqinren.healthyelders.utils.SensitiveWordsUtils;
import com.xaqinren.healthyelders.utils.UrlUtils;
import com.xaqinren.healthyelders.widget.YesOrNoDialog;
import com.xaqinren.healthyelders.widget.share.ShareDialog;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.disposables.Disposable;
import jp.wasabeef.glide.transformations.BlurTransformation;
import me.goldze.mvvmhabit.base.AppManager;
import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;
import me.goldze.mvvmhabit.utils.ToastUtils;

/**
 * Created by Lee. on 2021/4/25.
 * 直播间-观众页面
 */
public class LiveGuanzhongActivity extends BaseActivity<ActivityLiveGuanzhunBinding, LiveGuanzhongViewModel> implements IMLVBLiveRoomListener, View.OnClickListener, ITXLivePlayListener {

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
    private Animation ggShowAnimation;
    private Animation ggGoneAnimation;
    private int linkStatus = 1;//1未连麦 2申请中 3连麦中
    private int linkType;//0 双人连麦 1多人连麦
    private YesOrNoDialog closeLinkDialog;
    private Dialog waitLinkDialog;
    private Dialog selectLinkDialog;
    private QMUITipDialog linkWaitTip;
    private QMUITipDialog showLinkTip;
    private MoreLinkAdapter moreLinkAdapter;
    private List<ZBUserListBean> moreLinkList;
    private int mLinkPos;//多人连麦自己的座位号
    private Dialog moreLinkToLinkDialog;
    private Dialog moreLinkSettingDialog;
    private GiftBean selectGift;

    private SVGAImageView svgaImageView;
    private SVGAParser svgaParser;
    private ZBGiftListPop zbGiftListPop;
    private int screenWidth;
    private int screenHeight;
    private ZBGoodsListPop zbGoodsListPop;
    private Disposable uniSubscribe;
    private ZBUserListPop zbUserListPop;
    private ZBUserInfoPop userInfoPop;
    private TXLivePlayer txLivePlayer;
    private int mRenderMode;
    private ShareDialog shareDialog;
    private LinearLayoutManager moreLinkManager;
    private String mMsg;//评论框输入的内容
    private AnimationDrawable animationDrawable;


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
        screenWidth = MScreenUtil.getScreenWidth(this);
        screenHeight = MScreenUtil.getScreenHeight(this);

        //获取LiveRoom实例
        mLiveRoom = MLVBLiveRoom.sharedInstance(getApplication());
        //登录直播间IM服务
        showDialog("进入中...");
        //判断下内存里面的UserInfo为空不
        if (UserInfoMgr.getInstance().getUserInfo() == null) {
            UserInfoBean loginUser = InfoCache.getInstance().getLoginUser();
            if (loginUser == null) {
                //请求用户信息
                viewModel.getUserInfo(UserInfoMgr.getInstance().getHttpToken());
            } else {
                UserInfoMgr.getInstance().setUserInfo(loginUser);
                viewModel.toLoginRoom(mLiveRoom);
            }
        } else {
            viewModel.toLoginRoom(mLiveRoom);
        }

        LoadGiftService.startService(this);


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

        //直播间禁止送礼
        if (!mLiveInitInfo.getCanGift()) {
            binding.btnGift.setVisibility(View.GONE);
        }
        //直播间禁止带货
        if (!mLiveInitInfo.getCanSale()) {
            binding.btnGoods.setVisibility(View.GONE);
        } else {
            if (mLiveInitInfo.commodityInfoDto != null) {
                initZBGoodsBean(mLiveInitInfo.commodityInfoDto);
            }
        }
        //直播间禁止连麦
        if (!mLiveInitInfo.getCanMic()) {
            binding.btnLianmai.setVisibility(View.GONE);
        }

        //添加直播间屏蔽词
        if (mLiveInitInfo.shieldList != null && mLiveInitInfo.shieldList.size() > 0) {
            for (String s : mLiveInitInfo.shieldList) {
                pbWords.add(s);
            }
        }

        SensitiveWordsUtils.init(pbWords);


        if (mLiveInitInfo.getHasFollow() || mLiveInitInfo.userId.equals(UserInfoMgr.getInstance().getUserInfo().getId())) {
            binding.tvFollow.setText("");
            binding.tvFollow.setBackground(getResources().getDrawable(R.mipmap.guanzhu_1_00061));
        } else {
            binding.tvFollow.setText("关注");
            binding.tvFollow.setBackground(getResources().getDrawable(R.mipmap.guanzhu_1_00034));
        }

        topHeadAdapter = new TopUserHeadAdapter(R.layout.item_top_user_head);
        binding.rvAvatar.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
        binding.rvAvatar.setAdapter(topHeadAdapter);
        topHeadAdapter.setOnItemClickListener(((adapter, view, position) -> {

            if (!UserInfoMgr.getInstance().getUserInfo().getId().equals(topHeadAdapter.getData().get(position).userId)) {
                userInfoPop = new ZBUserInfoPop(this, mLiveInitInfo, topHeadAdapter.getData().get(position).userId);
                userInfoPop.showPopupWindow();
            }

        }));

        binding.rivPhoto.setOnClickListener(lis -> {
            userInfoPop = new ZBUserInfoPop(this, mLiveInitInfo, mLiveInitInfo.userId);
            userInfoPop.showPopupWindow();
        });

        //初始化多人连麦座位表
        moreLinkAdapter = new MoreLinkAdapter(R.layout.item_more_link);
        //垂直布局 禁止滑动
        moreLinkManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        binding.rvMoreLink.setLayoutManager(moreLinkManager);
        binding.rvMoreLink.setAdapter(moreLinkAdapter);
        initMoreLinkData();

        moreLinkAdapter.setOnItemClickListener(((adapter, view, position) -> {
            //用户点击请求上麦
            //主播默认同意上麦 - 主播展示用户申请消息

            if (TextUtils.isEmpty(moreLinkAdapter.getData().get(position).userId)) {
                if (linkStatus == 1) {
                    mLinkPos = position;
                    showMoreLinkToLinkPop(false);
                }
            } else {
                //打开用户资料Pop
                showMoreLinkSettingPop(position);
            }


        }));

        if (zbGiftListPop == null) {
            zbGiftListPop = new ZBGiftListPop(this, mLiveInitInfo);
        }
    }

    //多人连麦-用户申请连麦
    private void showMoreLinkToLinkPop(boolean isBottomOpen) {
        moreLinkToLinkDialog = new Dialog(this, R.style.CustomerDialog);
        //填充对话框的布局
        View view = LayoutInflater.from(this).inflate(R.layout.layout_more_link_tolink_pop, null);
        //初始化控件
        TextView tvCancel = (TextView) view.findViewById(R.id.tv_qx);
        LinearLayout llSQYYLM = (LinearLayout) view.findViewById(R.id.ll_sqyylm);

        tvCancel.setOnClickListener(lis -> {
            moreLinkToLinkDialog.dismiss();
        });

        llSQYYLM.setOnClickListener(lis -> {
            //判断是否需要主播同意
            if (!mLiveInitInfo.chatRoomUserApplyMic) {
                linkStatus = 2;
                mLiveRoom.sendC2CCustomMsg(mLiveInitInfo.userId, String.valueOf(LiveConstants.IMCMD_MORE_LINK_NUM), isBottomOpen ? "-1" : String.valueOf(mLinkPos), null);
            } else {
                toSendLinkMsg(isBottomOpen ? -1 : mLinkPos);
            }

            moreLinkToLinkDialog.dismiss();
        });

        //点击外部不可dismiss
        moreLinkToLinkDialog.setCancelable(false);
        //将布局设置给Dialog
        moreLinkToLinkDialog.setContentView(view);
        //获取当前Activity所在的窗体
        Window dialogWindow = moreLinkToLinkDialog.getWindow();
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity(Gravity.BOTTOM);
        //设置弹出动画
        dialogWindow.setWindowAnimations(R.style.DialogBottomAnimation);
        //获得窗体的属性
        WindowManager.LayoutParams params = dialogWindow.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;//设置宽高模式，
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;//设置宽高模式，
        dialogWindow.setAttributes(params);
        moreLinkToLinkDialog.show();//显示对话框

    }

    private void showMoreLinkSettingPop(int postion) {
        if (moreLinkSettingDialog != null && moreLinkSettingDialog.isShowing()) {
            return;
        }

        String userId = moreLinkAdapter.getData().get(postion).userId;
        String nickName = moreLinkAdapter.getData().get(postion).nickname;
        boolean voiceMicMute = moreLinkAdapter.getData().get(postion).hasProsody;

        moreLinkSettingDialog = new Dialog(this, R.style.CustomerDialog);
        //填充对话框的布局
        View view = LayoutInflater.from(this).inflate(R.layout.layout_more_link_setting_pop, null);
        //初始化控件
        TextView tvCancel = (TextView) view.findViewById(R.id.tv_qx);
        TextView tvName = (TextView) view.findViewById(R.id.tv_name);
        TextView tvInfo = (TextView) view.findViewById(R.id.tv_info);
        TextView tvJinYin = (TextView) view.findViewById(R.id.tv_jy);
        TextView tvDKLM = (TextView) view.findViewById(R.id.tv_dklx);

        if (userId.equals(UserInfoMgr.getInstance().getUserInfo().getId())) {
            tvJinYin.setVisibility(View.VISIBLE);
            tvDKLM.setVisibility(View.VISIBLE);
            tvInfo.setVisibility(View.GONE);
        } else {
            tvJinYin.setVisibility(View.GONE);
            tvDKLM.setVisibility(View.GONE);
            tvInfo.setVisibility(View.VISIBLE);
        }

        if (voiceMicMute) {
            tvJinYin.setText("取消静音");
        } else {
            tvJinYin.setText("静音");
        }

        tvName.setText(nickName);
        tvCancel.setOnClickListener(lis -> {
            moreLinkSettingDialog.dismiss();
        });
        tvDKLM.setOnClickListener(lis -> {
            showCloseLinkDialog();
        });
        tvJinYin.setOnClickListener(lis -> {
            viewModel.setVoiceMicMute(mLiveInitInfo.liveRoomRecordId, UserInfoMgr.getInstance().getUserInfo().getId(), !voiceMicMute);
            moreLinkSettingDialog.dismiss();
        });
        tvInfo.setOnClickListener(lis -> {
            //查看资料
            userInfoPop = new ZBUserInfoPop(this, mLiveInitInfo, moreLinkAdapter.getData().get(postion).userId);
            userInfoPop.showPopupWindow();
            moreLinkSettingDialog.dismiss();
        });

        //点击外部可dismiss
        moreLinkSettingDialog.setCancelable(true);
        //将布局设置给Dialog
        moreLinkSettingDialog.setContentView(view);
        //获取当前Activity所在的窗体
        Window dialogWindow = moreLinkSettingDialog.getWindow();
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity(Gravity.BOTTOM);
        //设置弹出动画
        dialogWindow.setWindowAnimations(R.style.DialogBottomAnimation);
        //获得窗体的属性
        WindowManager.LayoutParams params = dialogWindow.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;//设置宽高模式，
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;//设置宽高模式，
        dialogWindow.setAttributes(params);
        moreLinkSettingDialog.show();//显示对话框

    }

    //初始化多人连麦数据
    private void initMoreLinkData() {
        moreLinkList = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            ZBUserListBean userInfoBean = new ZBUserListBean();
            userInfoBean.nickname = "请求上麦";
            moreLinkList.add(userInfoBean);
        }
        moreLinkAdapter.setNewInstance(moreLinkList);
    }

    //初始化聊天列表
    private void initMsgList() {
        msgList = new ArrayList<>();
        //添加一条默认的展示文本消息
        TCChatEntity tcChatEntity = new TCChatEntity();
        tcChatEntity.setSenderName("");
        tcChatEntity.setType(LiveConstants.TYPE_SHOW);
        tcChatEntity.setContent(LiveConstants.TYPE_SHOW_TEXT);
        msgList.add(tcChatEntity);
        //再添加一条消息展示直播间介绍
        if (mLiveInitInfo.getHasIntroduce() && !TextUtils.isEmpty(mLiveInitInfo.liveRoomIntroduce)) {
            TCChatEntity tcChatEntity2 = new TCChatEntity();
            tcChatEntity2.setSenderName("");
            tcChatEntity2.setType(LiveConstants.TYPE_DES);
            tcChatEntity2.setContent(mLiveInitInfo.liveRoomIntroduce);
            msgList.add(tcChatEntity2);
        }


        msgAdapter = new TCChatMsgListAdapter(this, binding.lvMsg, msgList);
        binding.lvMsg.setAdapter(msgAdapter);
        binding.lvMsg.setSelection(0);

        binding.lvMsg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //判断当前消息是文本消息
                if (msgList.get(position).getType() == LiveConstants.IMCMD_TEXT_MSG) {
                    if (TextUtils.isEmpty(msgList.get(position).getUserId())) {
                        return;
                    }
                    //不是自己的消息
                    if (msgList.get(position).getUserId().equals(UserInfoMgr.getInstance().getUserInfo().getId())) {
                        return;
                    }

                    //查看资料
                    userInfoPop = new ZBUserInfoPop(LiveGuanzhongActivity.this, mLiveInitInfo, msgList.get(position).getUserId());
                    userInfoPop.showPopupWindow();
                }
            }
        });
        binding.lvMsg.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //判断当前消息是文本消息
                if (msgList.get(position).getType() == LiveConstants.IMCMD_TEXT_MSG) {
                    if (TextUtils.isEmpty(msgList.get(position).getUserId())) {
                        return true;
                    }
                    //不是自己的消息
                    if (msgList.get(position).getUserId().equals(UserInfoMgr.getInstance().getUserInfo().getId())) {
                        return true;
                    }

                    //长按@
                    //弹出输入dialog
                    Bundle bundle = new Bundle();
                    bundle.putString("content", "@" + msgList.get(position).getSenderName() + " ");
                    Intent intent = new Intent(getContext(), ZBEditTextDialogActivity.class);
                    intent.putExtras(bundle);
                    getContext().startActivity(intent);
                }


                return true;
            }
        });
    }

    private void startPlay() {
        showDialog("加载画面...");
        mLiveRoom.setListener(this);
        LogUtils.v(Constant.TAG_LIVE, "拉流地址：" + mLiveInitInfo.pullStreamUrl);
        LogUtils.v(Constant.TAG_LIVE, "房间号：" + Constant.getRoomId(mLiveInitInfo.liveRoomCode));

        //虚拟直播传空链接进去
        //加入直播间 mLiveInitInfo.pullStreamUrl
        if (mLiveInitInfo.liveRoomType == null) {
            return;
        }

        mLiveRoom.enterRoom(mLiveInitInfo.liveRoomType.equals(Constant.REQ_ZB_TYPE_XN) ? "" : mLiveInitInfo.pullStreamUrl, Constant.getRoomId(mLiveInitInfo.liveRoomCode), binding.mTxVideoView, new EnterRoomCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                dismissDialog();
                LogUtils.v(Constant.TAG_LIVE, "加入直播间失败：" + errCode);
                LogUtils.v(Constant.TAG_LIVE, "加入直播间失败：" + errInfo);
                ToastUtil.toastShortMessage("加入直播间失败，请重新进入");
                finish();
            }

            @Override
            public void onSuccess() {
                dismissDialog();
                LogUtils.v(Constant.TAG_LIVE, "加入直播间成功");
                //群发进入直播间的消息
                mLiveRoom.sendRoomCustomMsg(String.valueOf(LiveConstants.IMCMD_ENTER_LIVE), "", null);

                //判断虚拟直播开始加载播放
                if (mLiveInitInfo.liveRoomType.equals(Constant.REQ_ZB_TYPE_XN)) {
                    startXNPlayer();
                } else {
                    //正式直播
                    isPlaying = true;
                    if (!TextUtils.isEmpty(mLiveInitInfo.liveRoomConnection)) {
                        if (mLiveInitInfo.liveRoomConnection.equals(LiveConstants.LIVE_STATUS_CHAT_ROOM)) {
                            //判断是否开启了多人聊天
                            startMoreLinkLayout();
                            viewModel.findMicUsers(mLiveInitInfo.liveRoomRecordId);
                        }
                    }
                }

            }
        });

    }

    //初始化虚拟直播 采用直播播放器 否则文件过大播放会失败。
    public void startXNPlayer() {
        //创建 player 对象
        txLivePlayer = new TXLivePlayer(this);
        //RENDER_MODE_FULL_FILL_SCREEN 将图像等比例铺满整个屏幕，多余部分裁剪掉，此模式下画面不会留黑边，但可能因为部分区域被裁剪而显示不全。
        //RENDER_MODE_ADJUST_RESOLUTION 将图像等比例缩放，适配最长边，缩放后的宽和高都不会超过显示区域，居中显示，画面可能会留有黑边。
        txLivePlayer.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
        //RENDER_ROTATION_PORTRAIT 正常播放（Home 键在画面正下方）
        //RENDER_ROTATION_LANDSCAPE 画面顺时针旋转 270 度（Home 键在画面正左方）
        txLivePlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);
        txLivePlayer.setPlayListener(
                LiveGuanzhongActivity.this);
        txLivePlayer.setPlayerView(binding.mTxVideoView);
        txLivePlayer.startPlay(mLiveInitInfo.pullStreamUrl, TXLivePlayer.PLAY_TYPE_VOD_HLS);
    }

    public void stopXNPlayer() {
        if (txLivePlayer != null) {
            txLivePlayer.stopPlay(true);
        }
    }


    private boolean isPlaying;

    private void stopPlay() {
        mLiveRoom.exitRoom(new ExitRoomCallback() {
            @Override
            public void onSuccess() {
                LogUtils.v(Constant.TAG_LIVE, "直播间退出成功");
                dismissDialog();
                isPlaying = false;
                if (roomDestroy) {
                    //跳转结算页面
                    Bundle bundle = new Bundle();
                    bundle.putString("liveRoomRecordId", mLiveInitInfo.liveRoomRecordId);
                    bundle.putString("liveRoomId", mLiveInitInfo.liveRoomId);
                    startActivity(ZhiboOverGZActivity.class, bundle);
                } else {
                    //群发退出直播间的消息
                    mLiveRoom.sendRoomCustomMsg(String.valueOf(LiveConstants.IMCMD_EXIT_LIVE), "", null);
                }

                if (mLiveInitInfo.liveRoomType.equals(Constant.REQ_ZB_TYPE_XN)) {
                    stopXNPlayer();
                }

                mLiveRoom.setListener(null);
                finish();
            }

            @Override
            public void onError(int errCode, String e) {
                LogUtils.v(Constant.TAG_LIVE, "直播间退出失败:" + errCode);
                LogUtils.v(Constant.TAG_LIVE, "直播间退出失败:" + e);
                dismissDialog();
                finish();
            }
        });
    }

    //是否关闭连麦dialog
    public void showCloseLinkDialog() {
        if (closeLinkDialog == null) {
            closeLinkDialog = new YesOrNoDialog(this);
            closeLinkDialog.setMessageText("确定要关闭连麦吗？");
            closeLinkDialog.setRightBtnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //关闭自己的连麦
                    stopIMLink();
                    //关闭多人连麦的设置弹窗
                    if (moreLinkSettingDialog != null && moreLinkSettingDialog.isShowing()) {
                        moreLinkSettingDialog.dismiss();
                    }
                    closeLinkDialog.dismissDialog();
                }
            });
        }
        closeLinkDialog.showDialog();

    }

    Set<String> pbWords = new HashSet<>();

    //发送文字消息
    private void toSendTextMsg(String msg) {
        //检查消息是否有屏蔽词
        if (SensitiveWordsUtils.contains(msg)) {
            msg = SensitiveWordsUtils.replaceSensitiveWord(msg, "***");
        }


        addMsg2List("我 ", msg, LiveConstants.IMCMD_TEXT_MSG);
        mLiveRoom.sendRoomCustomMsg(String.valueOf(LiveConstants.IMCMD_TEXT_MSG), msg, null);
    }

    private void addMsg2List(String sendName, String msg, int type) {
        TCChatEntity entity = new TCChatEntity();
        entity.setLevelIcon(UserInfoMgr.getInstance().getUserInfo().getLevelIcon());
        entity.setLevelName(UserInfoMgr.getInstance().getUserInfo().getLevelName());
        entity.setSenderName(sendName);
        entity.setContent(msg);
        entity.setType(type);
        notifyMsg(entity);
    }

    //发送申请连麦的消息
    private void toSendLinkMsg(int position) {
        ZBUserListBean userListBean = new ZBUserListBean();
        userListBean.userId = UserInfoMgr.getInstance().getUserInfo().getId();
        userListBean.avatarUrl = UserInfoMgr.getInstance().getUserInfo().getAvatarUrl();
        userListBean.nickname = UserInfoMgr.getInstance().getUserInfo().getNickname();
        userListBean.position = position;

        //发送申请连麦自定义消息
        mLiveRoom.sendRoomCustomMsg(String.valueOf(LiveConstants.IMCMD_TO_LINK), userListBean, new SendRoomCustomMsgCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                LogUtils.v(Constant.TAG_LIVE, "申请连麦消息：" + errInfo);
            }

            @Override
            public void onSuccess() {
                LogUtils.v(Constant.TAG_LIVE, "申请连麦消息成功");
                waitLinkDialog();
            }
        });

    }

    //等待同意连麦的dialog
    private int recLenWait = LiveConstants.TO_LINK_TIME;//自己申请连麦倒计时
    private Timer waitLinkTimer;
    private TimerTask waitLinkTask;

    private void waitLinkDialog() {
        if (waitLinkDialog != null && waitLinkDialog.isShowing()) {
            return;
        }

        linkStatus = 2;
        if (waitLinkTimer != null) {
            waitLinkTimer.cancel();
            waitLinkTimer.purge();
        }
        waitLinkTimer = new Timer();
        waitLinkDialog = new Dialog(this, R.style.CustomerDialog);
        //填充对话框的布局
        View view = LayoutInflater.from(this).inflate(R.layout.layout_link_wait_pop, null);
        //初始化控件
        TextView tvCancel = (TextView) view.findViewById(R.id.tv_cancel);
        TextView tvTime = (TextView) view.findViewById(R.id.tv_time);
        tvTime.setText(LiveConstants.TO_LINK_TIME + "S");

        tvCancel.setOnClickListener(lis -> {
            //给主播发消息取消连麦
            mLiveRoom.sendC2CCustomMsg(mLiveInitInfo.userId, String.valueOf(LiveConstants.IMCMD_CANCEL_LINK), "取消连麦", new SendC2CCustomMsgCallback() {
                @Override
                public void onError(int errCode, String errInfo) {

                }

                @Override
                public void onSuccess() {
                    linkStatus = 1;
                    //取消
                    disWaitLinkDialog();
                }
            });


        });

        //点击外部不可dismiss
        waitLinkDialog.setCancelable(false);
        //将布局设置给Dialog
        waitLinkDialog.setContentView(view);
        //获取当前Activity所在的窗体
        Window dialogWindow = waitLinkDialog.getWindow();
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity(Gravity.BOTTOM);
        //设置弹出动画
        dialogWindow.setWindowAnimations(R.style.DialogBottomAnimation);
        //获得窗体的属性
        WindowManager.LayoutParams params = dialogWindow.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;//设置宽高模式，
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;//设置宽高模式，
        dialogWindow.setAttributes(params);
        waitLinkDialog.show();//显示对话框
        recLenWait = LiveConstants.TO_LINK_TIME;
        //倒计时
        if (waitLinkTask != null) {
            waitLinkTask.cancel();
        }
        waitLinkTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() { // UI thread
                    @Override
                    public void run() {
                        recLenWait--;
                        tvTime.setText(recLenWait + "S");
                        if (recLenWait < 1) {
                            //不在连麦中的状态下才让状态变回1
                            if (linkStatus != 3) {
                                linkStatus = 1;
                                ToastUtils.showShort("对方未答应");
                            }
                            //取消
                            disWaitLinkDialog();
                        }
                    }
                });
            }
        };

        waitLinkTimer.schedule(waitLinkTask, 2000, 1000);//等待时间2秒，停顿时间一秒

    }

    public boolean isMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }

    private void disWaitLinkDialog() {
        if (waitLinkTask != null) {
            waitLinkTask.cancel();
            waitLinkTask = null;
        }
        if (waitLinkTimer != null) {
            waitLinkTimer.cancel();
            waitLinkTimer.purge();
            waitLinkTimer = null;
        }

        //防止有些手机崩溃
        try {
            if (waitLinkDialog != null && waitLinkDialog.isShowing()) {
                waitLinkDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            waitLinkDialog = null;
        }


    }

    //新的同意拒绝dialog
    private int recLen = 10;//主播邀请倒计时
    private Timer selectLinkTimer;
    private TimerTask selectLinkTask;

    //接受收拒绝连麦弹窗
    private void selectLinkDialog() {
        if (selectLinkDialog != null && selectLinkDialog.isShowing()) {
            return;
        }

        if (selectLinkTimer != null) {
            selectLinkTimer.cancel();
            selectLinkTimer.purge();
        }
        selectLinkTimer = new Timer();
        selectLinkDialog = new Dialog(this, R.style.CustomerDialog);
        //填充对话框的布局
        View view = LayoutInflater.from(this).inflate(R.layout.layout_link_select_pop, null);
        //初始化控件
        TextView tvCancel = (TextView) view.findViewById(R.id.tv_cancel);
        TextView tvSure = (TextView) view.findViewById(R.id.tv_sure);
        TextView tvTime = (TextView) view.findViewById(R.id.tv_time);

        tvCancel.setOnClickListener(lis -> {
            //拒绝主播
            //向主播发消息
            mLiveRoom.sendC2CCustomMsg(mLiveInitInfo.userId, String.valueOf(LiveConstants.IMCMD_REFUSE_LINK), "用户拒绝了您的请求", new SendC2CCustomMsgCallback() {
                @Override
                public void onError(int errCode, String errInfo) {

                }

                @Override
                public void onSuccess() {
                    dismissSelectLinkDialog();
                }
            });
        });
        tvSure.setOnClickListener(lis -> {
            //同意主播
            //向主播发连麦请求
            disposable = permissions.request(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA)
                    .subscribe(granted -> {
                        if (granted) {
                            //连麦操作
                            toSQIMLink();
                            dismissSelectLinkDialog();
                        } else {
                            ToastUtils.showShort("请先打开摄像头与麦克风权限");
                        }

                    });

        });
        //点击外部不可dismiss
        selectLinkDialog.setCancelable(false);
        //将布局设置给Dialog
        selectLinkDialog.setContentView(view);
        //获取当前Activity所在的窗体
        Window dialogWindow = selectLinkDialog.getWindow();
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity(Gravity.BOTTOM);
        //设置弹出动画
        dialogWindow.setWindowAnimations(R.style.DialogBottomAnimation);
        //获得窗体的属性
        WindowManager.LayoutParams params = dialogWindow.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;//设置宽高模式，
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;//设置宽高模式，
        dialogWindow.setAttributes(params);
        selectLinkDialog.show();//显示对话框
        recLen = 10;
        //倒计时
        selectLinkTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() { // UI thread
                    @Override
                    public void run() {
                        recLen--;
                        tvTime.setText(recLen + "S");
                        if (recLen < 1) {
                            //到时间默认向主播发拒绝消息
                            mLiveRoom.sendC2CCustomMsg(mLiveInitInfo.userId, String.valueOf(LiveConstants.IMCMD_REFUSE_LINK), "用户拒绝了您的请求", null);
                            dismissSelectLinkDialog();
                        }
                    }
                });
            }
        };

        selectLinkTimer.schedule(selectLinkTask, 1000, 1000);//等待时间一秒，停顿时间一秒

    }

    private void dismissSelectLinkDialog() {
        if (selectLinkTask != null) {
            selectLinkTask.cancel();
            selectLinkTask = null;
        }

        if (selectLinkTimer != null) {
            selectLinkTimer.cancel();
            selectLinkTimer.purge();
            selectLinkTimer = null;
        }


        //防止有些手机崩溃
        try {
            if (selectLinkDialog != null && selectLinkDialog.isShowing()) {
                selectLinkDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            selectLinkDialog = null;
        }
    }

    //1主播同意连麦会走此法 2主播邀请观众接受会走此法
    private void toSQIMLink() {
        if (linkWaitTip == null) {
            linkWaitTip = new QMUITipDialog.Builder(this)
                    .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                    .setTipWord("正在连接中，请等待...")
                    .create();
        }
        linkWaitTip.show();

        //用户请求连麦
        mLiveRoom.requestJoinAnchor(mLiveInitInfo.userId, new RequestJoinAnchorCallback() {
            @Override
            public void onAccept() {
                LogUtils.v(Constant.TAG_LIVE, "主播同意了");
                if (linkWaitTip != null && linkWaitTip.isShowing()) {
                    linkWaitTip.dismiss();
                }
                Toast.makeText(LiveGuanzhongActivity.this, "主播接受了您的连麦请求，开始连麦", Toast.LENGTH_SHORT).show();
                //取消等待dialog及定时任务
                disWaitLinkDialog();
                //开启连麦
                startLinkLayout();
            }

            //拒绝连麦
            @Override
            public void onReject(String reason) {
                if (linkWaitTip != null && linkWaitTip.isShowing()) {
                    linkWaitTip.dismiss();
                }
                linkStatus = 1;
                Toast.makeText(LiveGuanzhongActivity.this, reason, Toast.LENGTH_SHORT).show();
                //取消等待dialog的定时任务
                disWaitLinkDialog();
            }

            @Override
            public void onTimeOut() {
                if (linkWaitTip != null && linkWaitTip.isShowing()) {
                    linkWaitTip.dismiss();
                }
                linkStatus = 1;
                Toast.makeText(LiveGuanzhongActivity.this, "连麦请求超时，主播没有做出回应", Toast.LENGTH_SHORT).show();
                disWaitLinkDialog();
            }

            @Override
            public void onError(int code, String errInfo) {
                if (linkWaitTip != null && linkWaitTip.isShowing()) {
                    linkWaitTip.dismiss();
                }
                linkStatus = 1;
                Toast.makeText(LiveGuanzhongActivity.this, "连麦请求发生错误，" + errInfo, Toast.LENGTH_SHORT).show();
                disWaitLinkDialog();
            }
        });
    }

    private void startLinkLayout() {
        if (linkType == 0) {
            //1v1视频连麦切分主播屏幕
            binding.rlAnchor2.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) binding.llVideo.getLayoutParams();
            lp.height = (int) getResources().getDimension(R.dimen.dp_320);
            lp.setMargins(0, (int) getResources().getDimension(R.dimen.dp_148), 0, 0);
            binding.llVideo.setLayoutParams(lp);

            //设置小主播高斯模糊背景
            Glide.with(this)
                    .load(UserInfoMgr.getInstance().getUserInfo().getAvatarUrl())
                    .apply(RequestOptions.bitmapTransform(new BlurTransformation(15, 15)))
                    .into(binding.ivAnchor2);

            mLiveRoom.startLocalPreview(true, binding.anchor2TxVideoView);
        } else { //多人连麦
            TXCloudVideoView txCloudVideoView = (TXCloudVideoView) moreLinkAdapter.getViewByPosition(mLinkPos, R.id.anchor_video_view);
            mLiveRoom.startLocalPreview(txCloudVideoView);//不传第一个参数 为纯音频
        }

        if (showLinkTip == null) {
            showLinkTip = new QMUITipDialog.Builder(this)
                    .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                    .setTipWord("正在连接中...")
                    .create();
        }
        showLinkTip.show();
        mLiveRoom.joinAnchor(linkType, new JoinAnchorCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                if (showLinkTip != null && showLinkTip.isShowing()) {
                    showLinkTip.dismiss();
                }
                Toast.makeText(LiveGuanzhongActivity.this, "连麦失败：" + errInfo, Toast.LENGTH_SHORT).show();
                //停止连麦
                stopLinkLayout();
            }

            @Override
            public void onSuccess() {
                if (showLinkTip != null && showLinkTip.isShowing()) {
                    showLinkTip.dismiss();
                }

                //给主播发消息取消连麦消息
                mLiveRoom.sendC2CCustomMsg(mLiveInitInfo.userId, String.valueOf(LiveConstants.IMCMD_CANCEL_LINK), "取消连麦", null);

                dismissSelectLinkDialog();

                linkStatus = 3;
                //如果本来禁止连麦 主播发起连麦 需要设置状态带改
                binding.btnLianmai.setVisibility(View.VISIBLE);
                binding.btnLianmai.setBackgroundResource(R.mipmap.zbj_menu_lianmaiing_gz);
                if (linkType == 0) {
                    //展示镜头翻转按钮
                    binding.btnJtfz.setVisibility(View.VISIBLE);
                } else {
                    //刷新座位列表
                    updateLinkerPos(mLinkPos, UserInfoMgr.getInstance().getUserInfo().getId(),
                            UserInfoMgr.getInstance().getUserInfo().getNickname(),
                            UserInfoMgr.getInstance().getUserInfo().getAvatarUrl());
                }


            }
        });


    }

    private void updateLinkerPos(int linkerPos, String userId, String nickName, String avatar) {
        moreLinkAdapter.getData().get(linkerPos).nickname = nickName;
        moreLinkAdapter.getData().get(linkerPos).avatarUrl = avatar;
        moreLinkAdapter.getData().get(linkerPos).userId = userId;
        moreLinkAdapter.getData().get(linkerPos).hasProsody = false;
        moreLinkAdapter.notifyItemChanged(linkerPos);
    }

    private void stopIMLink() {
        mLiveRoom.quitJoinAnchor(linkType, new QuitAnchorCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                //退出失败
            }

            @Override
            public void onSuccess() {
                stopLinkLayout();
            }
        });
    }

    private void stopLinkLayout() {
        linkStatus = 1;
        binding.btnLianmai.setBackgroundResource(R.mipmap.zbj_menu_lianmai_gz);


        if (linkType == 0) {
            //关闭1v1视频连麦，切回主播屏幕
            binding.rlAnchor2.setVisibility(View.GONE);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            lp.setMargins(0, 0, 0, (int) getResources().getDimension(R.dimen.dp_54));
            binding.llVideo.setLayoutParams(lp);
            binding.btnJtfz.setVisibility(View.GONE);
        } else {
            //关闭多人连麦时候不需要自己移除 等会会刷新列表接口
            //            updateLinkerPos(mLinkPos, null, "请求上麦", null);
        }

        //判断是否关闭连麦了
        if (!mLiveInitInfo.getCanMic()) {
            ToastUtil.toastShortMessage(LiveConstants.SHOW_JZLM);
            binding.btnLianmai.setVisibility(View.GONE);
        }
    }


    //接受处理文字消息
    public void toRecvTextMsg(TCUserInfo userInfo, String text, int type) {
        TCChatEntity entity = new TCChatEntity();
        if (TextUtils.isEmpty(userInfo.nickname)) {
            entity.setSenderName(LiveConstants.NIKENAME + userInfo.userid);
        } else {
            entity.setSenderName(userInfo.nickname);

        }
        entity.setUserId(userInfo.userid);
        entity.setLevelName(userInfo.leaveName);
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

        //这个位置开启动画之前不用clear否则别的点赞效果就没了
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

    private boolean roomDestroy;//房间是否被解散，意味着是否要跳结束页面

    @Override
    public void onRoomDestroy(String roomID) {
        if (!roomID.equals(mRoomID)) {
            return;
        }
        roomDestroy = true;
        viewModel.leaveLive(mLiveInitInfo.liveRoomRecordId);
    }

    //有麦者
    @Override
    public void onAnchorEnter(AnchorInfo anchorInfo) {
    }

    @Override
    public void onAnchorExit(AnchorInfo anchorInfo) {
        mLiveRoom.stopRemoteView(anchorInfo);
        remoteSet.remove(anchorInfo.userID);
        LogUtils.v(Constant.TAG_LIVE, "下麦者：" + anchorInfo.userID);
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
        stopIMLink();
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
    public void onRecvRoomCustomMsg(String roomID, String userID, String userName, String userAvatar, String cmd, Object message, String userLevel, String userLevelIcon) {
        if (!roomID.equals(mRoomID)) {
            return;
        }
        TCUserInfo userInfo = new TCUserInfo(userID, userName, userAvatar, userLevel, userLevelIcon);
        int type = Integer.parseInt(cmd);
        switch (type) {
            case LiveConstants.IMCMD_TEXT_MSG:
                toRecvTextMsg(userInfo, (String) message, LiveConstants.IMCMD_TEXT_MSG);
                break;
            case LiveConstants.IMCMD_ENTER_LIVE:
                //用户进入房间消息
                toRecvTextMsg(userInfo, LiveConstants.SHOW_ENTER_LIVE, type);
                //调用刷新接口
                toRushLiveInfo();
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
                //收到主播继续直播的消息重新拉流
                mLiveRoom.reStartPlay(mLiveInitInfo.pullStreamUrl, binding.mTxVideoView, null);
                break;
            case LiveConstants.IMCMD_FOLLOW:
                //展示关注消息
                toRecvTextMsg(userInfo, LiveConstants.SHOW_FOLLOW, type);
                break;
            case LiveConstants.IMCMD_GONGGAO_MSG:
                //公告消息 顶部展示
                String jsonMsg = (String) message;
                JsonMsgBean jsonMsgBean = JSON.parseObject(jsonMsg, JsonMsgBean.class);

                //加入公告队列
                ggList.add(jsonMsgBean);
                if (binding.llGonggao.getVisibility() == View.GONE) {
                    showGongGao();
                }

                break;
            case LiveConstants.IMCMD_OPEN_MORE_LINK://主播开启多人连麦
                startMoreLinkLayout();
                break;
            case LiveConstants.IMCMD_CLOSE_MORE_LINK://主播关闭多人连麦
                if (linkType == 0) {
                    return;
                }

                //判断如果在连麦状态先退出连麦
                if (linkStatus == 3) {
                    //退出连麦
                    stopIMLink();
                } else {
                    linkType = 0;
                    closeMoreLinkAnim();
                    initMoreLinkData();
                }

                break;
            case LiveConstants.IMCMD_RESH_MORELINK_INFO://刷新座位表
                viewModel.findMicUsers(mLiveInitInfo.liveRoomRecordId);
                break;
            case LiveConstants.IMCMD_RESH_HOME_INFO://刷新房间设置信息
                viewModel.getLiveStatus(mLiveInitInfo.liveRoomId);
                break;
            case LiveConstants.IMCMD_GIFT://礼物消息
                toRushLiveInfo();

                SendGiftBean sendGiftBean = (SendGiftBean) message;
                userInfo.giftIcon = sendGiftBean.giftsIcon;
                userInfo.giftName = sendGiftBean.giftsName;
                userInfo.giftIcon = sendGiftBean.giftsIcon;
                handleGiftMSg(userInfo);

                sendGiftBean.sendUserName = userName;
                sendGiftBean.sendUserPhoto = userAvatar;

                loadGiftAnimBanner(sendGiftBean, false);
                break;
            case LiveConstants.IMCMD_SHOW_MIC://开启连麦
                ToastUtil.toastShortMessage(LiveConstants.SHOW_KQLM);
                binding.btnLianmai.setVisibility(View.VISIBLE);
                mLiveInitInfo.setCanMic(true);
                break;
            case LiveConstants.IMCMD_FORBIDDEN_MIC://禁止连麦

                //判断是否在未连麦
                mLiveInitInfo.setCanMic(false);
                if (linkStatus == 1) {
                    ToastUtil.toastShortMessage(LiveConstants.SHOW_JZLM);
                    binding.btnLianmai.setVisibility(View.GONE);
                }


                break;
            case LiveConstants.IMCMD_SETTING_PL://评论设置
                if (((String) message).equals("1")) {
                    ToastUtil.toastShortMessage(LiveConstants.SHOW_KQPL);
                    mLiveInitInfo.setCanComment(true);
                } else {
                    ToastUtil.toastShortMessage(LiveConstants.SHOW_JZPL);
                    mLiveInitInfo.setCanComment(false);
                }
                break;
            case LiveConstants.IMCMD_SETTING_LW://送礼设置
                if (((String) message).equals("1")) {
                    ToastUtil.toastShortMessage(LiveConstants.SHOW_KQLW);
                    binding.btnGift.setVisibility(View.VISIBLE);
                    mLiveInitInfo.setCanGift(true);
                } else {
                    ToastUtil.toastShortMessage(LiveConstants.SHOW_JZLW);
                    binding.btnGift.setVisibility(View.GONE);
                    mLiveInitInfo.setCanGift(false);
                }
                break;
            case LiveConstants.IMCMD_SHOW_GOODS://带货消息
                //调接口获取当前带货信息
                String json = (String) message;
                ZBGoodsBean goodBean = JSON.parseObject(json, ZBGoodsBean.class);
                initZBGoodsBean(goodBean);

                break;
            case LiveConstants.IMCMD_SHOW_GOODS_CANCEL://取消带货消息
                binding.rlShowGood.setVisibility(View.GONE);
                break;
            case LiveConstants.IMCMD_BLOCK_WORD_ADD://添加屏蔽词
                pbWords.add((String) message);
                SensitiveWordsUtils.init(pbWords);
                break;
            case LiveConstants.IMCMD_BLOCK_WORD_DEL://删除屏蔽词
                pbWords.remove((String) message);
                SensitiveWordsUtils.init(pbWords);
                break;
            default:
                break;
        }
    }

    //展示公告
    public void showGongGao() {
        if (ggList.size() == 0) {
            return;
        }
        JsonMsgBean jsonMsgBean = ggList.get(0);
        ggList.remove(0);
        binding.tvGgtype.setText("用户");
        binding.tvGgname.setText(jsonMsgBean.nickname);
        binding.tvGgcontent.setText(jsonMsgBean.content);


        ggShowAnimation = AnimUtils.getAnimation(this, R.anim.anim_slice_in_left);
        ggGoneAnimation = AnimUtils.getAnimation(this, R.anim.anim_slice_out_left);


        //公告进入动画
        binding.llGonggao.clearAnimation();
        binding.llGonggao.startAnimation(ggShowAnimation);

        ggShowAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                binding.llGonggao.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                freedGGTimerTask();
                ggTimer = new Timer();
                ggAnimTask = new TimerTask() {

                    @Override
                    public void run() {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                binding.llGonggao.clearAnimation();
                                binding.llGonggao.startAnimation(ggGoneAnimation);
                                ggGoneAnimation.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        binding.llGonggao.setVisibility(View.GONE);
                                        freedGGTimerTask();
                                        handler.sendEmptyMessage(98);
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });
                            }
                        });

                    }
                };
                ggTimer.schedule(ggAnimTask, 3000);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private List<JsonMsgBean> ggList = new ArrayList<>();

    public void freedGGTimerTask() {
        if (ggAnimTask != null) {
            ggAnimTask.cancel();
            ggAnimTask = null;
        }
        if (ggTimer != null) {
            ggTimer.cancel();
            ggTimer.purge();
            ggTimer = null;
        }

    }

    private ZBGoodsBean nowGoodsBean;//当前带货

    private void initZBGoodsBean(ZBGoodsBean goodBean) {
        nowGoodsBean = goodBean;

        binding.rlShowGood.setVisibility(View.VISIBLE);
        GlideUtil.intoImageView(this, UrlUtils.resetImgUrl(goodBean.imageUrl, 400, 400), binding.rivGood);
        binding.tvGoodName.setText(goodBean.title);
        binding.tvGoodPrice.setText("" + goodBean.maxSalesPrice);
    }

    private void startMoreLinkAnim() {
        //计算x轴缩放倍率
        float xx = (float) (screenWidth - binding.rvMoreLink.getWidth() - (getResources().getDimension(R.dimen.dp_3))) / screenWidth;

        //计算y轴缩放倍率 93*6+5*3 = 573
        float yy = (float) (binding.rvMoreLink.getHeight()) / (screenHeight - (getResources().getDimension(R.dimen.dp_54)));


        ScaleAnimation animation = new ScaleAnimation(1.0F, xx, 1.0F, yy, Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 1.0F);
        animation.setDuration(500);
        animation.setFillAfter(true);

        binding.mTxVideoView.clearAnimation();
        binding.mTxVideoView.setAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.rvMoreLink.setVisibility(View.VISIBLE);
                //                mLiveRoom.setRenderMode(TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void closeMoreLinkAnim() {
        //计算x轴缩放倍率
        float xx2 = (float) (screenWidth - binding.rvMoreLink.getWidth()) / screenWidth;
        //计算y轴缩放倍率
        float yy2 = (float) (binding.rvMoreLink.getHeight()) / (screenHeight - (getResources().getDimension(R.dimen.dp_54)));
        ScaleAnimation animation2 = new ScaleAnimation(xx2, 1.0F, yy2, 1.0F, Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 1.0F);
        animation2.setDuration(500);
        animation2.setFillAfter(true);

        binding.mTxVideoView.clearAnimation();
        binding.mTxVideoView.setAnimation(animation2);
        //        mLiveRoom.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
        binding.rvMoreLink.setVisibility(View.INVISIBLE);
    }

    private void startMoreLinkLayout() {
        linkType = 1;
        initMoreLinkData();
        startMoreLinkAnim();
    }

    @Override
    public void onRecvC2CCustomMsg(String senderId, String cmd, String message, String userName, String headPic) {
        int type = Integer.parseInt(cmd);
        switch (type) {
            case LiveConstants.IMCMD_FORBIDDER_TALK://禁言
                //判断是不是主播或者管理员发的
                if (!senderId.equals(mLiveInitInfo.userId)) {
                    return;
                }
                mLiveInitInfo.setHasSpeech(true);
                ToastUtil.toastShortMessage(message);
                break;
            case LiveConstants.IMCMD_CANCEL_FORBIDDER_TALK://取消禁言
                //判断是不是主播或者管理员发的
                if (!senderId.equals(mLiveInitInfo.userId)) {
                    return;
                }
                mLiveInitInfo.setHasSpeech(false);
                ToastUtil.toastShortMessage(message);
                break;
            case LiveConstants.IMCMD_PUT_BLACK://拉黑/踢出
                //判断是不是主播或者管理员发的
                if (!senderId.equals(mLiveInitInfo.userId)) {
                    return;
                }
                ToastUtil.toastShortMessage(message);
                finish();
                break;
            case LiveConstants.IMCMD_INVITE_LINK://主播邀请连麦
                //判断是不是当前主播发的
                if (mLiveInitInfo.userId.equals(senderId)) {
                    if (message.contains("邀请连麦")) {
                        //判断是否在要在邀请中
                        if (selectLinkDialog == null || !selectLinkDialog.isShowing()) {
                            selectLinkDialog();
                        }
                    } else if (message.contains("同意连麦")) {
                        //说明是用户发起了 主播接受之后的
                        disWaitLinkDialog();
                        toSQIMLink();
                    }

                }
                break;
            case LiveConstants.IMCMD_MORE_LINK_YQ://主播邀请多人连麦
                //主播邀请来上多人语音连麦
                //储存自己的座位号
                mLinkPos = Integer.parseInt(message);
                if (selectLinkDialog == null || !selectLinkDialog.isShowing()) {
                    selectLinkDialog();
                }


                break;
            case LiveConstants.IMCMD_MORE_LINK_JS://收到主播消息判断得知有无位置
                //收到主播消息判断得知有无位置
                if (message.equals("-1")) {
                    linkStatus = 1;
                    ToastUtil.toastShortMessage("暂无合适位置");
                } else {
                    //记录主播告诉自己的座位号
                    mLinkPos = Integer.parseInt(message);
                    //去申请连麦
                    toSQIMLink();
                }
                break;
            case LiveConstants.IMCMD_MORE_ANCHOR_QXJY://收到主播取消静音的消息
            case LiveConstants.IMCMD_MORE_ANCHOR_JY://收到主播静音的消息
                viewModel.setVoiceMicMute(mLiveInitInfo.liveRoomRecordId, UserInfoMgr.getInstance().getUserInfo().getId(), !moreLinkAdapter.getData().get(mLinkPos).hasProsody);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null) {
            disposable.dispose();
        }
        if (isPlaying) {
            viewModel.leaveLive(mLiveInitInfo.liveRoomRecordId);
            stopPlay();
        }
        if (uniSubscribe != null) {
            uniSubscribe.dispose();
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    private double firstClickLMTime;

    public void initEvent() {
        //双击点赞事件
        binding.mTxVideoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        before_press_Y = event.getY();
                        before_press_X = event.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                        long secondTime = System.currentTimeMillis();
                        if (secondTime - firstClickTime < 500) {
                            double now_press_Y = event.getY();
                            double now_press_X = event.getX();
                            if (now_press_Y - before_press_Y <= 50 && now_press_X - before_press_X <= 50) {
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
        binding.btnLianmai.setOnClickListener(this);
        binding.btnJtfz.setOnClickListener(this);
        binding.btnGift.setOnClickListener(this);
        binding.btnGoods.setOnClickListener(this);
        binding.ivCloseGood.setOnClickListener(this);
        binding.llPay.setOnClickListener(this);
        binding.btnMore.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_members_num:
                if (zbUserListPop == null) {
                    zbUserListPop = new ZBUserListPop(this, mLiveInitInfo, "守护团");
                }
                zbUserListPop.showPopupWindow();
                break;
            case R.id.btn_back:
                showDialog("离开直播间...");
                viewModel.leaveLive(mLiveInitInfo.liveRoomRecordId);

                try {
                    if (mHandler != null) {
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (isPlaying) {
                                    finish();
                                }
                            }
                        }, 2000);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case R.id.tv_msg:
                //判断是否被禁言
                if (!mLiveInitInfo.getHasSpeech() && mLiveInitInfo.getCanComment()) {
                    Bundle bundle = new Bundle();
                    bundle.putString("content", mMsg);
                    startActivity(ZBEditTextDialogActivity.class, bundle);
                } else {
                    if (!mLiveInitInfo.getCanComment()) {
                        ToastUtil.toastShortMessage(LiveConstants.SHOW_JZPL);
                        return;
                    }
                    if (mLiveInitInfo.getHasSpeech()) {
                        ToastUtil.toastShortMessage(LiveConstants.SHOW_JINYAN);
                        return;
                    }
                }
                break;
            case R.id.btn_zan:
                toDianZan();
                break;
            case R.id.tv_follow:
                //关注主播 通知服务器
                showDialog();
                viewModel.toFollow(mLiveInitInfo.userId);
                break;
            case R.id.btn_lianmai:
                //按钮时间限流
                long secondTime = System.currentTimeMillis();
                if (secondTime - firstClickLMTime > 500) {
                    //如果两次时间间隔大于500毫秒，则去请求
                    firstClickLMTime = secondTime;
                    if (linkStatus == 1) {
                        //先判断是否仅接受粉丝连线 - 是不是主播粉丝
                        if (linkType == 0) {
                            if (mLiveInitInfo.onlyFansMic && !mLiveInitInfo.getHasFollow()) {
                                ToastUtil.toastShortMessage(LiveConstants.LINK_ONLY_FANS);
                                return;
                            }
                            if (mLiveInitInfo.onlyInviteMic) {
                                ToastUtil.toastShortMessage(LiveConstants.LINK_ONLY_INVITE
                                );
                                return;
                            }
                        } else if (linkType == 1) {
                            if (mLiveInitInfo.chatRoomOnlyFansMic && !mLiveInitInfo.getHasFollow()) {
                                ToastUtil.toastShortMessage(LiveConstants.LINK_ONLY_FANS);
                                return;
                            }
                        }


                        disposable = permissions.request(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA)
                                .subscribe(granted -> {
                                    if (granted) {
                                        if (linkType == 0) {
                                            toSendLinkMsg(0);
                                        } else {
                                            //申请多人连麦的pop
                                            showMoreLinkToLinkPop(true);
                                        }
                                    } else {
                                        ToastUtils.showShort("请先打开摄像头与麦克风权限");
                                    }

                                });
                    } else if (linkStatus == 3) {
                        showCloseLinkDialog();
                    }

                }
                break;
            case R.id.btn_jtfz:
                mLiveRoom.switchCamera();
                break;
            case R.id.btn_gift:
                if (zbGiftListPop == null) {
                    zbGiftListPop = new ZBGiftListPop(this, mLiveInitInfo);
                }
                zbGiftListPop.showPopupWindow();
                break;
            case R.id.btn_goods:
                zbGoodsListPop = new ZBGoodsListPop(this, mLiveInitInfo.liveRoomId, 2);
                zbGoodsListPop.showPopupWindow();
            case R.id.iv_closeGood:
                binding.rlShowGood.setVisibility(View.GONE);
                break;
            case R.id.ll_pay:
                if (nowGoodsBean != null) {
                    if (!TextUtils.isEmpty(nowGoodsBean.appId) && !TextUtils.isEmpty(nowGoodsBean.jumpUrl)) {
                        UniService.startService(getContext(), nowGoodsBean.appId, 99, nowGoodsBean.jumpUrl);
                    }
                }
                break;
            case R.id.btn_more:
                //更多设置
                //                zbMorePop = new ZBMoreGZPop(this, mLiveRoom, mLiveInitInfo);
                //                zbMorePop.showPopupWindow();
                shareDialog = new ShareDialog(getContext(), mLiveInitInfo.share, ShareDialog.LIVE_TYPE);
                shareDialog.show(binding.llMenu);
                break;
            default:
                break;
        }
    }

    private boolean isFirst = true;
    private boolean needToLink = false;//判断是否连麦中闪退
    private long lastMsgTime;
    private String lastMsg;

    @Override
    public void initViewObservable() {
        super.initViewObservable();

        disposable = RxBus.getDefault().toObservable(EventBean.class).subscribe(eventBean -> {
            if (eventBean.msgId == LiveConstants.SEND_MSG) {
                //二次判断当前是否禁言
                if (!mLiveInitInfo.getCanComment()) {
                    ToastUtil.toastShortMessage(LiveConstants.SHOW_JZPL);
                    return;
                }
                //防止重复发送
                if (eventBean.time - lastMsgTime < 200 && lastMsg.equals(eventBean.content)) {
                    return;
                }
                toSendTextMsg(eventBean.content);
                lastMsgTime = eventBean.time;
                lastMsg = eventBean.content;
            } else if (eventBean.msgId == CodeTable.ZHJ_SEND_GIFT) {
                //发送礼物二次判断
                if (!mLiveInitInfo.getCanGift()) {
                    ToastUtil.toastShortMessage(LiveConstants.SHOW_JZLW);
                    return;
                }

                selectGift = (GiftBean) eventBean.data;
                //发送礼物-接口请求
                viewModel.sendGift(mLiveInitInfo.liveRoomRecordId, mLiveInitInfo.userId, selectGift.id);
            } else if (eventBean.msgId == LiveConstants.SHOW_ET) {
                //消息列表位置变化
                int height = eventBean.msgType;
                ViewGroup.LayoutParams params = binding.view.getLayoutParams();
                params.height = height;
                binding.view.setLayoutParams(params);
            } else if (eventBean.msgId == LiveConstants.DISMISS_ET) {
                //消息列表位置变化
                ViewGroup.LayoutParams params = binding.view.getLayoutParams();
                params.height = 0;
                binding.view.setLayoutParams(params);

                mMsg = eventBean.content;
                if (!TextUtils.isEmpty(mMsg)) {
                    binding.tvMsg.setText(mMsg);
                    FaceManager.handlerEmojiText(binding.tvMsg, mMsg, false);
                } else {
                    binding.tvMsg.setText("发条评论吧");
                }
            } else if (eventBean.msgId == CodeTable.IM_LOGIN_SUCCESS) {
                if (!isPlaying) {
                    startPlay();
                    toRushLiveInfo();
                }
            } else if (eventBean.msgId == CodeTable.ZBJ_GZ) {
                if (eventBean.msgType == 1) {
                    binding.tvFollow.setText("");
                    binding.tvFollow.setBackground(getResources().getDrawable(R.mipmap.guanzhu_1_00061));
                } else {
                    binding.tvFollow.setText("关注");
                    binding.tvFollow.setBackground(getResources().getDrawable(R.mipmap.guanzhu_1_00034));
                }
            }
        });
        RxSubscriptions.add(disposable);

        viewModel.userInfo.observe(this, userInfoBean -> {
            if (userInfoBean != null) {
                viewModel.toLoginRoom(mLiveRoom);
            }
        });

        viewModel.dismissDialog.observe(this, dismissDialog -> {
            if (dismissDialog != null) {
                if (dismissDialog) {
                    dismissDialog();
                }
            }
        });
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
                    Collections.reverse(liveHeaderInfo.liveRoomUsers);
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
        viewModel.exitSuccess.observe(this, exitSuccess -> {
            if (exitSuccess != null) {
                if (exitSuccess) {
                    stopPlay();
                }
            }
        });
        viewModel.followSuccess.observe(this, isFollow -> {
            if (isFollow != null) {
                if (isFollow) {
                    mLiveInitInfo.setHasFollow(!mLiveInitInfo.getHasFollow());
                    if (mLiveInitInfo.getHasFollow()) {
                        animationDrawable = (AnimationDrawable) getResources().getDrawable(
                                R.drawable.avatar_zbj_gz_anim);
                        binding.tvFollow.setBackground(animationDrawable);
                        binding.tvFollow.setText("");
                        animationDrawable.start();

                        //群发关注消息
                        mLiveRoom.sendRoomCustomMsg(String.valueOf(LiveConstants.IMCMD_FOLLOW), "", null);
                        addMsg2List(UserInfoMgr.getInstance().getUserInfo().getNickname(), LiveConstants.SHOW_FOLLOW, LiveConstants.IMCMD_FOLLOW);
                    } else {
                        binding.tvFollow.setText("关注");
                        binding.tvFollow.setBackground(getResources().getDrawable(R.mipmap.guanzhu_1_00034));
                    }
                    viewModel.addFansCount(mLiveInitInfo.liveRoomRecordId);
                }
            }
        });
        viewModel.netSuccess.observe(this, netSuccess -> {
            if (netSuccess != null) {
                //开启静音/取消静音
                if (netSuccess == 1) {
                    mLiveRoom.muteLocalAudio(!moreLinkAdapter.getData().get(mLinkPos).hasProsody);
                    moreLinkAdapter.getData().get(mLinkPos).hasProsody = !moreLinkAdapter.getData().get(mLinkPos).hasProsody;
                    moreLinkAdapter.notifyItemChanged(mLinkPos);
                    //群发消息刷新
                    mLiveRoom.sendRoomCustomMsg(String.valueOf(LiveConstants.IMCMD_RESH_MORELINK_INFO), "", null);
                }
            }
        });
        viewModel.moreLinkList.observe(this, linkerList -> {
            if (linkerList != null) {

                moreLinkList = new ArrayList<>();
                for (int i = 0; i < 6; i++) {
                    ZBUserListBean userInfoBean = new ZBUserListBean();
                    userInfoBean.nickname = "请求上麦";
                    moreLinkList.add(userInfoBean);
                }

                for (ZBUserListBean bean : linkerList) {
                    if (bean.userId.equals(UserInfoMgr.getInstance().getUserInfo().getId())) {
                        if (isFirst) {
                            if (linkStatus == 1) {
                                needToLink = true;
                                mLinkPos = bean.position;
                            }
                            if (linkStatus == 3) {
                                mLiveRoom.muteLocalAudio(bean.hasProsody);
                            }
                        }
                    }
                    moreLinkList.remove(bean.position);
                    moreLinkList.add(bean.position, bean);
                }
                moreLinkAdapter.setList(moreLinkList);

                if (needToLink) {
                    //去重连 走主动上麦的路线
                    //告诉主播自己想要上的位置
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mLiveRoom.sendC2CCustomMsg(mLiveInitInfo.userId, String.valueOf(LiveConstants.IMCMD_MORE_LINK_NUM), String.valueOf(mLinkPos), null);
                            needToLink = false;
                        }
                    }, 2000);
                }
                isFirst = false;

                //连麦者渲染别的上麦者
                //多人连麦且自己在上麦
                if (linkType == 1 && linkStatus == 3) {
                    for (int i = 0; i < moreLinkList.size(); i++) {
                        //排除自己，空的，还有已经渲染过的
                        if (!TextUtils.isEmpty(moreLinkList.get(i).userId) &&
                                !remoteSet.equals(moreLinkList.get(i).userId) &&
                                !moreLinkList.get(i).userId.equals(UserInfoMgr.getInstance().getUserInfo().getId())) {
                            //开启远端视频渲染
                            View item = moreLinkManager.getChildAt(i);

                            AnchorInfo anchorInfo = new AnchorInfo(moreLinkList.get(i).userId, moreLinkList.get(i).nickname,
                                    moreLinkList.get(i).avatarUrl, moreLinkList.get(i).pullStreamUrl);

                            TXCloudVideoView txCView = item.findViewById(R.id.anchor_video_view);
                            LogUtils.v(Constant.TAG_LIVE, "渲染上麦者：" + anchorInfo.userID);

                            mLiveRoom.startRemoteView(true, anchorInfo, txCView, new IMLVBLiveRoomListener.PlayCallback() {
                                @Override
                                public void onBegin() {
                                    remoteSet.add(anchorInfo.userID);
                                    LogUtils.v(Constant.TAG_LIVE, "上麦者：" + anchorInfo.userID);
                                }

                                @Override
                                public void onError(int errCode, String errInfo) {
                                    LogUtils.v(Constant.TAG_LIVE, "上麦者：" + errInfo);
                                }

                                @Override
                                public void onEvent(int event, Bundle param) {

                                }
                            });
                        }

                    }
                }


            }
        });
        viewModel.setInitInfo.observe(this, setInitInfo -> {
            if (setInitInfo != null) {
                if (linkType == 0) {
                    mLiveInitInfo.onlyFansMic = setInitInfo.onlyFansMic;
                    mLiveInitInfo.userApplyMic = setInitInfo.userApplyMic;
                    mLiveInitInfo.onlyInviteMic = setInitInfo.onlyInviteMic;
                } else if (linkType == 1) {
                    mLiveInitInfo.chatRoomOnlyFansMic = setInitInfo.chatRoomOnlyFansMic;
                    mLiveInitInfo.chatRoomUserApplyMic = setInitInfo.chatRoomUserApplyMic;
                }
            }
        });
        viewModel.sendGiftSuccess.observe(this, sendSuccess -> {
            //发送礼物自定义消息
            if (sendSuccess != null && sendSuccess) {

                //测试 发送自定义送礼物消息
                toSendGiftMsg(selectGift);
                zbGiftListPop.dismiss();
                //调接口刷新金币数量
                zbGiftListPop.rushGold();
            }
        });
        uniSubscribe = RxBus.getDefault().toObservable(UniEventBean.class).subscribe(event -> {
            if (event != null) {
                if (event.msgId == CodeTable.UNI_RELEASE) {
                    if (event.taskId == 99) {
                        UniUtil.openUniApp(getContext(), event.appId, event.jumpUrl, null, event.isSelfUni);
                    }
                } else if (event.msgId == CodeTable.UNI_RELEASE_FAIL) {
                    //ToastUtils.showShort("打开小程序失败");
                }
            }
        });
    }

    private Set<String> remoteSet = new HashSet<>();//存储已经渲染的小主播

    //处理礼物信息
    protected void handleGiftMSg(TCUserInfo userInfo) {
        TCChatEntity entity = new TCChatEntity();
        if (TextUtils.isEmpty(userInfo.nickname)) {
            entity.setSenderName(LiveConstants.NIKENAME + userInfo.userid);
        } else {
            entity.setSenderName(userInfo.nickname);
        }
        entity.setContent("送出" + userInfo.giftName);
        entity.setType(LiveConstants.IMCMD_GIFT);
        entity.setLevelName(userInfo.leaveName);
        entity.setLevelIcon(userInfo.leaveIcon);
        notifyMsg(entity);
    }

    //发送礼物消息
    private void toSendGiftMsg(GiftBean giftBean) {
        //聊天框的消息回显
        TCChatEntity entity = new TCChatEntity();
        entity.setSenderName("我  ");
        entity.setContent("送出" + giftBean.giftName);
        entity.setType(LiveConstants.IMCMD_TEXT_MSG);
        notifyMsg(entity);

        SendGiftBean sendGiftBean = new SendGiftBean();
        sendGiftBean.giftsIcon = giftBean.giftImage;
        sendGiftBean.giftsName = giftBean.giftName;
        sendGiftBean.id = giftBean.id;
        sendGiftBean.userId = UserInfoMgr.getInstance().getUserInfo().getId();
        sendGiftBean.svgaUrl = giftBean.giftUrl;
        sendGiftBean.hasAnimation = giftBean.hasAnimation ? "1" : "0";

        //发送自定义消息
        mLiveRoom.sendRoomCustomMsg(String.valueOf(LiveConstants.IMCMD_GIFT), sendGiftBean, new SendRoomCustomMsgCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
            }

            @Override
            public void onSuccess() {
                sendGiftBean.sendUserName = UserInfoMgr.getInstance().getUserInfo().getNickname();
                sendGiftBean.sendUserPhoto = UserInfoMgr.getInstance().getUserInfo().getAvatarUrl();
                loadGiftAnimBanner(sendGiftBean, true);
            }
        });
    }

    //礼物相关操作
    private boolean isBanner1Showing;//是否在横幅
    private boolean isBanner2Showing;//是否在横幅
    private boolean isAnimLoading;//是否在加载动画
    private List<SendGiftBean> giftsAnimList = new ArrayList<>();
    private List<SendGiftBean> giftsBannersList = new ArrayList<>();
    private SendGiftBean sendGiftBean1;
    private SendGiftBean sendGiftBean2;
    private Animation goneAnim1;
    private Animation showAnim1;
    private Animation goneAnim2;
    private Animation showAnim2;
    private TimerTask giftContentTask;
    private TimerTask giftContentTask2;
    private Timer giftContentTimer;
    private Timer giftContentTimer2;

    private void loadGiftAnimBanner(SendGiftBean sendGiftBean, boolean isMe) {
        //存礼物消息

        //判断当前礼物有没有动画
        if (!TextUtils.isEmpty(sendGiftBean.hasAnimation) && sendGiftBean.hasAnimation.equals("1")) {
            if (isMe) {
                giftsAnimList.add(0, sendGiftBean);
            } else {
                giftsAnimList.add(sendGiftBean);
            }

            if (!isAnimLoading) {
                loadSvga();
            }
        }


        //判断展示的banner里面有没有，若有则连击
        if (isBanner1Showing && sendGiftBean1 != null) {
            if (sendGiftBean1.id.equals(sendGiftBean.id) && sendGiftBean1.userId.equals(sendGiftBean.userId)) {
                sendGiftBean1.num = sendGiftBean1.num + 1;
                showBanner1();
                return;
            }
        }
        if (isBanner2Showing && sendGiftBean2 != null) {
            if (sendGiftBean2.id.equals(sendGiftBean.id) && sendGiftBean2.userId.equals(sendGiftBean.userId)) {
                sendGiftBean2.num = sendGiftBean2.num + 1;
                showBanner2();
                return;
            }
        }


        if (isMe) {
            giftsBannersList.add(0, sendGiftBean);
        } else {
            giftsBannersList.add(sendGiftBean);
        }
        loadBanner();
    }

    //加载横幅
    private void loadBanner() {
        if ((isBanner1Showing && isBanner2Showing)) {
            return;
        }


        if (!isBanner1Showing) {
            if (giftsBannersList.size() == 0) {
                freedTimerTask1();
                return;
            }

            sendGiftBean1 = giftsBannersList.get(0);
            giftsBannersList.remove(0);
            showBanner1();
        } else {
            if (giftsBannersList.size() == 0) {
                freedTimerTask2();
                return;
            }

            sendGiftBean2 = giftsBannersList.get(0);
            giftsBannersList.remove(0);
            showBanner2();
        }


    }

    private void showBanner2() {
        if (binding.rlGift2.getVisibility() == View.GONE) {
            showAnim2 = AnimUtils.getAnimation(LiveGuanzhongActivity.this, R.anim.anim_slice_in_left);
            binding.rlGift2.setVisibility(View.VISIBLE);
            Glide.with(LiveGuanzhongActivity.this).load(sendGiftBean2.sendUserPhoto).into(binding.ivUserHeadPic2);
            Glide.with(LiveGuanzhongActivity.this).load(sendGiftBean2.giftsIcon).into(binding.ivGift2);
            binding.tvAudienceName2.setText(sendGiftBean2.sendUserName);
            binding.tvGiftName2.setText("送" + sendGiftBean2.giftsName);
            binding.rlGift2.clearAnimation();
            binding.rlGift2.setAnimation(showAnim2);
        } else {
            Animation bigAnim = AnimUtils.getAnimation(LiveGuanzhongActivity.this, R.anim.zbj_gift_num);
            binding.tvGiftNum2.clearAnimation();
            binding.tvGiftNum2.setAnimation(bigAnim);
        }

        binding.tvGiftNum2.setText("" + sendGiftBean2.num + " ");

        //礼物消息横幅3S结束

        freedTimerTask2();
        giftContentTimer2 = new Timer();
        giftContentTask2 = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        goneAnim2 = AnimUtils.getAnimation(LiveGuanzhongActivity.this, R.anim.anim_slice_out_left);
                        binding.rlGift2.clearAnimation();
                        binding.rlGift2.setAnimation(goneAnim2);
                        goneAnim2.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                binding.rlGift2.setVisibility(View.GONE);

                                isBanner2Showing = false;
                                sendGiftBean2 = null;

                                freedTimerTask2();
                                handler.sendEmptyMessage(99);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });

                    }
                });

            }
        };
        isBanner2Showing = true;
        giftContentTimer2.schedule(giftContentTask2, 4000);//3秒后关闭
    }

    private void showBanner1() {

        //送礼横幅
        if (binding.rlGift.getVisibility() == View.GONE) {
            showAnim1 = AnimUtils.getAnimation(LiveGuanzhongActivity.this, R.anim.anim_slice_in_left);
            binding.rlGift.setVisibility(View.VISIBLE);
            Glide.with(LiveGuanzhongActivity.this).load(sendGiftBean1.sendUserPhoto).into(binding.ivUserHeadPic);
            Glide.with(LiveGuanzhongActivity.this).load(sendGiftBean1.giftsIcon).into(binding.ivGift);
            binding.tvAudienceName.setText(sendGiftBean1.sendUserName);
            binding.tvGiftName.setText("送" + sendGiftBean1.giftsName);

            binding.rlGift.clearAnimation();
            binding.rlGift.setAnimation(showAnim1);
        } else {
            Animation bigAnim = AnimUtils.getAnimation(LiveGuanzhongActivity.this, R.anim.zbj_gift_num);
            binding.tvGiftNum.clearAnimation();
            binding.tvGiftNum.setAnimation(bigAnim);
        }
        binding.tvGiftNum.setText("" + sendGiftBean1.num + " ");


        //礼物消息横幅3S结束
        freedTimerTask1();
        giftContentTimer = new Timer();
        giftContentTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        goneAnim1 = AnimUtils.getAnimation(LiveGuanzhongActivity.this, R.anim.anim_slice_out_left);
                        binding.rlGift.clearAnimation();
                        binding.rlGift.setAnimation(goneAnim1);
                        goneAnim1.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {

                                binding.rlGift.setVisibility(View.GONE);

                                isBanner1Showing = false;
                                sendGiftBean1 = null;

                                freedTimerTask1();
                                handler.sendEmptyMessage(99);

                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });


                    }
                });

            }
        };
        isBanner1Showing = true;
        giftContentTimer.schedule(giftContentTask, 4000);//3秒后关闭
    }

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 99:
                    loadBanner();
                    break;
                case 98:
                    showGongGao();
                    break;
            }
        }
    };

    private void freedTimerTask2() {
        if (giftContentTask2 != null) {
            giftContentTask2.cancel();
            giftContentTask2 = null;
        }
        if (giftContentTimer2 != null) {
            giftContentTimer2.cancel();
            giftContentTimer2.purge();
            giftContentTimer2 = null;
        }
    }

    private void freedTimerTask1() {
        if (giftContentTask != null) {
            giftContentTask.cancel();
            giftContentTask = null;
        }
        if (giftContentTimer != null) {
            giftContentTimer.cancel();
            giftContentTimer.purge();
            giftContentTimer = null;
        }

    }

    //加载动画
    private void loadSvga() {
        if (isAnimLoading || giftsAnimList.size() == 0) {
            return;
        }

        showGiftAnim(giftsAnimList.get(0).svgaUrl);
        giftsAnimList.remove(0);
    }

    private void showGiftAnim(String nowUrl) {
        binding.rlShowGift.setVisibility(View.VISIBLE);
        //svga缓存设置
        File cacheDir = new File(getApplicationContext().getCacheDir().getAbsolutePath(), "svga");
        try {
            HttpResponseCache.install(cacheDir, 1024 * 1024 * 200);
        } catch (IOException e) {
            e.printStackTrace();
        }

        binding.rlShowGift.removeAllViews();
        //注意使用单例
        if (svgaImageView == null) {
            svgaImageView = new SVGAImageView(this);
        }
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        svgaImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        svgaImageView.setLayoutParams(params);
        svgaImageView.setLoops(1);
        binding.rlShowGift.addView(svgaImageView);


        svgaParser = new SVGAParser(this);
        try {
            svgaParser.decodeFromURL(new URL(nowUrl), new SVGAParser.ParseCompletion() {
                @RequiresApi(api = Build.VERSION_CODES.P)
                @Override
                public void onComplete(@NotNull SVGAVideoEntity videoItem) {

                    svgaImageView.setVideoItem(videoItem);
                    svgaImageView.startAnimation();
                    isAnimLoading = true;
                }

                @Override
                public void onError() {

                }
            });
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


        svgaImageView.setCallback(new SVGACallback() {
            @Override
            public void onPause() {
                //动画暂停
            }

            @Override
            public void onFinished() {
                //播放结束
                isAnimLoading = false;
                loadSvga();
            }

            @Override
            public void onRepeat() {
                //重复播放
            }

            @Override
            public void onStep(int i, double v) {
                //动画步骤
            }
        });
    }


    /**
     * 二次点击（返回键）退出
     */
    private double firstTime;

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                long secondTime = System.currentTimeMillis();
                if (secondTime - firstTime > 2000) {
                    //如果两次按键时间间隔大于2秒，则不退出
                    Toast.makeText(this, "再按一次退出直播间~", Toast.LENGTH_SHORT).show();
                    firstTime = secondTime;//更新firstTime
                } else {
                    //弹窗提示
                    showDialog("离开直播间...");
                    //去通知服务器退出了直播
                    viewModel.leaveLive(mLiveInitInfo.liveRoomRecordId);
                    try {
                        if (mHandler != null) {
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (isPlaying) {
                                        finish();
                                    }
                                }
                            }, 2000);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                //返回键返回判断
                return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isPlaying) {
            if (txLivePlayer != null) {
                txLivePlayer.resume();
            } else {
                mLiveRoom.setResume();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isPlaying) {
            Activity activity = AppManager.getAppManager().currentActivity();
            //判断当前栈顶是否当前页面
            if (activity.getLocalClassName().contains("LiveGuanzhongActivity")) {
                // 暂停
                if (txLivePlayer != null) {
                    txLivePlayer.pause();
                } else {
                    mLiveRoom.setPause();
                }
            }
        }
    }

    @Override
    public void onPlayEvent(int event, Bundle bundle) {
        LogUtils.v(Constant.TAG_LIVE, "onPlayEvent: " + event);
        if (event == TXLiveConstants.PLAY_EVT_PLAY_END) {//视频播放结束
            if (txLivePlayer != null) {
                txLivePlayer.resume();
            }
        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_BEGIN) {//视频播放开始
            isPlaying = true;
        }
    }

    @Override
    public void onNetStatus(Bundle status) {
        //判断横竖屏
        if (status.getInt(TXLiveConstants.NET_STATUS_VIDEO_WIDTH) > status.getInt(TXLiveConstants.NET_STATUS_VIDEO_HEIGHT)) {
            //横屏设置
            if (mRenderMode != TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION) {
                mRenderMode = TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION;
                txLivePlayer.setRenderMode(mRenderMode);
            }
        } else {
            //竖屏设置
            if (mRenderMode != TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN) {
                mRenderMode = TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN;
                txLivePlayer.setRenderMode(mRenderMode);
            }
        }
    }
}
