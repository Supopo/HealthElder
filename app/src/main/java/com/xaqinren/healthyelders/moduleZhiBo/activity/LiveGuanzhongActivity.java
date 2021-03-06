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
import com.xaqinren.healthyelders.global.AppApplication;
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
 * ?????????-????????????
 */
public class LiveGuanzhongActivity extends BaseActivity<ActivityLiveGuanzhunBinding, LiveGuanzhongViewModel> implements IMLVBLiveRoomListener, View.OnClickListener, ITXLivePlayListener {

    private MLVBLiveRoom mLiveRoom;
    private LiveInitInfo mLiveInitInfo;
    private List<TCChatEntity> msgList;   // ????????????
    private TCChatMsgListAdapter msgAdapter;
    private String mRoomID;
    private Disposable disposable;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private TCFrequeControl mLikeFrequeControl;    //??????????????????
    private int mZanNum;    //??????????????????
    private TopUserHeadAdapter topHeadAdapter;
    private Timer ggTimer;
    private TimerTask ggAnimTask;
    private Animation ggShowAnimation;
    private Animation ggGoneAnimation;
    private int linkStatus = 1;//1????????? 2????????? 3?????????
    private int linkType;//0 ???????????? 1????????????
    private YesOrNoDialog closeLinkDialog;
    private Dialog waitLinkDialog;
    private Dialog selectLinkDialog;
    private QMUITipDialog linkWaitTip;
    private QMUITipDialog showLinkTip;
    private MoreLinkAdapter moreLinkAdapter;
    private List<ZBUserListBean> moreLinkList;
    private int mLinkPos;//??????????????????????????????
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
    private String mMsg;//????????????????????????
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
        //???????????????
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //???????????????????????????
        Bundle bundle = getIntent().getExtras();
        mLiveInitInfo = (LiveInitInfo) bundle.getSerializable(Constant.LiveInitInfo);
        mRoomID = Constant.getRoomId(mLiveInitInfo.liveRoomCode);
    }

    @Override
    public void initData() {
        super.initData();
        //????????????
        setStatusBarTransparent();
        screenWidth = MScreenUtil.getScreenWidth(this);
        screenHeight = MScreenUtil.getScreenHeight(this);

        //??????LiveRoom??????
        mLiveRoom = MLVBLiveRoom.sharedInstance(getApplication());
        //???????????????IM??????
        showDialog("?????????...");
        //????????????????????????UserInfo?????????
        if (UserInfoMgr.getInstance().getUserInfo() == null) {
            UserInfoBean loginUser = InfoCache.getInstance().getLoginUser();
            if (loginUser == null) {
                //??????????????????
                viewModel.getUserInfo(UserInfoMgr.getInstance().getHttpToken());
            } else {
                UserInfoMgr.getInstance().setUserInfo(loginUser);
                viewModel.toLoginRoom(mLiveRoom);
            }
        } else {
            viewModel.toLoginRoom(mLiveRoom);
        }

        //??????????????????????????????
        if (!AppApplication.get().giftLoadSuccess) {
            //????????????????????????
            if (AppApplication.get().isServiceRunning("com.xaqinren.healthyelders.moduleHome.LoadGiftService")) {
                LoadGiftService.stopService(this);
            }
            LoadGiftService.startService(this);
        }


        initEvent();
        initLiveInfo();
        initMsgList();
    }

    //?????????????????????
    private void initLiveInfo() {

        //??????????????????
        if (mLiveInitInfo.groupIds != null && mLiveInitInfo.groupIds.length > 0) {
            for (String groupId : mLiveInitInfo.groupIds) {
                //???????????????????????????
                if (!Constant.getRoomId(mLiveInitInfo.liveRoomCode).equals(groupId)) {
                    //?????????????????????
                    mLiveRoom.exitGroup(groupId);
                }

            }
        }

        Glide.with(this).load(mLiveInitInfo.avatarUrl).diskCacheStrategy(DiskCacheStrategy.ALL).into(binding.rivPhoto);
        binding.tvName.setText(mLiveInitInfo.nickname);

        //?????????????????????
        if (!mLiveInitInfo.getCanGift()) {
            binding.btnGift.setVisibility(View.GONE);
        }
        //?????????????????????
        if (!mLiveInitInfo.getCanSale()) {
            binding.btnGoods.setVisibility(View.GONE);
        } else {
            if (mLiveInitInfo.commodityInfoDto != null) {
                initZBGoodsBean(mLiveInitInfo.commodityInfoDto);
            }
        }
        //?????????????????????
        if (!mLiveInitInfo.getCanMic()) {
            binding.btnLianmai.setVisibility(View.GONE);
        }

        //????????????????????????
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
            binding.tvFollow.setText("??????");
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

        //??????????????????????????????
        moreLinkAdapter = new MoreLinkAdapter(R.layout.item_more_link);
        //???????????? ????????????
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
            //????????????????????????
            //???????????????????????? - ??????????????????????????????

            if (TextUtils.isEmpty(moreLinkAdapter.getData().get(position).userId)) {
                if (linkStatus == 1) {
                    mLinkPos = position;
                    showMoreLinkToLinkPop(false);
                }
            } else {
                //??????????????????Pop
                showMoreLinkSettingPop(position);
            }


        }));

        if (zbGiftListPop == null) {
            zbGiftListPop = new ZBGiftListPop(this, mLiveInitInfo);
        }
    }

    //????????????-??????????????????
    private void showMoreLinkToLinkPop(boolean isBottomOpen) {
        moreLinkToLinkDialog = new Dialog(this, R.style.CustomerDialog);
        //????????????????????????
        View view = LayoutInflater.from(this).inflate(R.layout.layout_more_link_tolink_pop, null);
        //???????????????
        TextView tvCancel = (TextView) view.findViewById(R.id.tv_qx);
        LinearLayout llSQYYLM = (LinearLayout) view.findViewById(R.id.ll_sqyylm);

        tvCancel.setOnClickListener(lis -> {
            moreLinkToLinkDialog.dismiss();
        });

        llSQYYLM.setOnClickListener(lis -> {
            //??????????????????????????????
            if (!mLiveInitInfo.chatRoomUserApplyMic) {
                linkStatus = 2;
                mLiveRoom.sendC2CCustomMsg(mLiveInitInfo.userId, String.valueOf(LiveConstants.IMCMD_MORE_LINK_NUM), isBottomOpen ? "-1" : String.valueOf(mLinkPos), null);
            } else {
                toSendLinkMsg(isBottomOpen ? -1 : mLinkPos);
            }

            moreLinkToLinkDialog.dismiss();
        });

        //??????????????????dismiss
        moreLinkToLinkDialog.setCancelable(false);
        //??????????????????Dialog
        moreLinkToLinkDialog.setContentView(view);
        //????????????Activity???????????????
        Window dialogWindow = moreLinkToLinkDialog.getWindow();
        //??????Dialog?????????????????????
        dialogWindow.setGravity(Gravity.BOTTOM);
        //??????????????????
        dialogWindow.setWindowAnimations(R.style.DialogBottomAnimation);
        //?????????????????????
        WindowManager.LayoutParams params = dialogWindow.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;//?????????????????????
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;//?????????????????????
        dialogWindow.setAttributes(params);
        moreLinkToLinkDialog.show();//???????????????

    }

    private void showMoreLinkSettingPop(int postion) {
        if (moreLinkSettingDialog != null && moreLinkSettingDialog.isShowing()) {
            return;
        }

        String userId = moreLinkAdapter.getData().get(postion).userId;
        String nickName = moreLinkAdapter.getData().get(postion).nickname;
        boolean voiceMicMute = moreLinkAdapter.getData().get(postion).hasProsody;

        moreLinkSettingDialog = new Dialog(this, R.style.CustomerDialog);
        //????????????????????????
        View view = LayoutInflater.from(this).inflate(R.layout.layout_more_link_setting_pop, null);
        //???????????????
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
            tvJinYin.setText("????????????");
        } else {
            tvJinYin.setText("??????");
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
            //????????????
            userInfoPop = new ZBUserInfoPop(this, mLiveInitInfo, moreLinkAdapter.getData().get(postion).userId);
            userInfoPop.showPopupWindow();
            moreLinkSettingDialog.dismiss();
        });

        //???????????????dismiss
        moreLinkSettingDialog.setCancelable(true);
        //??????????????????Dialog
        moreLinkSettingDialog.setContentView(view);
        //????????????Activity???????????????
        Window dialogWindow = moreLinkSettingDialog.getWindow();
        //??????Dialog?????????????????????
        dialogWindow.setGravity(Gravity.BOTTOM);
        //??????????????????
        dialogWindow.setWindowAnimations(R.style.DialogBottomAnimation);
        //?????????????????????
        WindowManager.LayoutParams params = dialogWindow.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;//?????????????????????
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;//?????????????????????
        dialogWindow.setAttributes(params);
        moreLinkSettingDialog.show();//???????????????

    }

    //???????????????????????????
    private void initMoreLinkData() {
        moreLinkList = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            ZBUserListBean userInfoBean = new ZBUserListBean();
            userInfoBean.nickname = "????????????";
            moreLinkList.add(userInfoBean);
        }
        moreLinkAdapter.setNewInstance(moreLinkList);
    }

    //?????????????????????
    private void initMsgList() {
        msgList = new ArrayList<>();
        //???????????????????????????????????????
        TCChatEntity tcChatEntity = new TCChatEntity();
        tcChatEntity.setSenderName("");
        tcChatEntity.setType(LiveConstants.TYPE_SHOW);
        tcChatEntity.setContent(LiveConstants.TYPE_SHOW_TEXT);
        msgList.add(tcChatEntity);
        //??????????????????????????????????????????
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
                //?????????????????????????????????
                if (msgList.get(position).getType() == LiveConstants.IMCMD_TEXT_MSG) {
                    if (TextUtils.isEmpty(msgList.get(position).getUserId())) {
                        return;
                    }
                    //?????????????????????
                    if (msgList.get(position).getUserId().equals(UserInfoMgr.getInstance().getUserInfo().getId())) {
                        return;
                    }

                    //????????????
                    userInfoPop = new ZBUserInfoPop(LiveGuanzhongActivity.this, mLiveInitInfo, msgList.get(position).getUserId());
                    userInfoPop.showPopupWindow();
                }
            }
        });
        binding.lvMsg.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //?????????????????????????????????
                if (msgList.get(position).getType() == LiveConstants.IMCMD_TEXT_MSG) {
                    if (TextUtils.isEmpty(msgList.get(position).getUserId())) {
                        return true;
                    }
                    //?????????????????????
                    if (msgList.get(position).getUserId().equals(UserInfoMgr.getInstance().getUserInfo().getId())) {
                        return true;
                    }

                    //??????@
                    //????????????dialog
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
        showDialog("????????????...");
        mLiveRoom.setListener(this);
        LogUtils.v(Constant.TAG_LIVE, "???????????????" + mLiveInitInfo.pullStreamUrl);
        LogUtils.v(Constant.TAG_LIVE, "????????????" + Constant.getRoomId(mLiveInitInfo.liveRoomCode));

        //??????????????????????????????
        //??????????????? mLiveInitInfo.pullStreamUrl
        if (mLiveInitInfo.liveRoomType == null) {
            return;
        }

        mLiveRoom.enterRoom(mLiveInitInfo.liveRoomType.equals(Constant.REQ_ZB_TYPE_XN) ? "" : mLiveInitInfo.pullStreamUrl, Constant.getRoomId(mLiveInitInfo.liveRoomCode), binding.mTxVideoView, new EnterRoomCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                dismissDialog();
                LogUtils.v(Constant.TAG_LIVE, "????????????????????????" + errCode);
                LogUtils.v(Constant.TAG_LIVE, "????????????????????????" + errInfo);
                ToastUtil.toastShortMessage("???????????????????????????????????????");
                finish();
            }

            @Override
            public void onSuccess() {
                dismissDialog();
                LogUtils.v(Constant.TAG_LIVE, "?????????????????????");
                //??????????????????????????????
                mLiveRoom.sendRoomCustomMsg(String.valueOf(LiveConstants.IMCMD_ENTER_LIVE), "", null);

                //????????????????????????????????????
                if (mLiveInitInfo.liveRoomType.equals(Constant.REQ_ZB_TYPE_XN)) {
                    startXNPlayer();
                } else {
                    //????????????
                    isPlaying = true;
                    if (!TextUtils.isEmpty(mLiveInitInfo.liveRoomConnection)) {
                        if (mLiveInitInfo.liveRoomConnection.equals(LiveConstants.LIVE_STATUS_CHAT_ROOM)) {
                            //?????????????????????????????????
                            startMoreLinkLayout();
                            viewModel.findMicUsers(mLiveInitInfo.liveRoomRecordId);
                        }
                    }
                }

            }
        });

    }

    //????????????????????? ????????????????????? ????????????????????????????????????
    public void startXNPlayer() {
        //?????? player ??????
        txLivePlayer = new TXLivePlayer(this);
        //RENDER_MODE_FULL_FILL_SCREEN ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        //RENDER_MODE_ADJUST_RESOLUTION ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        txLivePlayer.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
        //RENDER_ROTATION_PORTRAIT ???????????????Home ????????????????????????
        //RENDER_ROTATION_LANDSCAPE ????????????????????? 270 ??????Home ????????????????????????
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
                LogUtils.v(Constant.TAG_LIVE, "?????????????????????");
                dismissDialog();
                isPlaying = false;
                if (roomDestroy) {
                    //??????????????????
                    Bundle bundle = new Bundle();
                    bundle.putString("liveRoomRecordId", mLiveInitInfo.liveRoomRecordId);
                    bundle.putString("liveRoomId", mLiveInitInfo.liveRoomId);
                    startActivity(ZhiboOverGZActivity.class, bundle);
                } else {
                    //??????????????????????????????
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
                LogUtils.v(Constant.TAG_LIVE, "?????????????????????:" + errCode);
                LogUtils.v(Constant.TAG_LIVE, "?????????????????????:" + e);
                dismissDialog();
                finish();
            }
        });
    }

    //??????????????????dialog
    public void showCloseLinkDialog() {
        if (closeLinkDialog == null) {
            closeLinkDialog = new YesOrNoDialog(this);
            closeLinkDialog.setMessageText("???????????????????????????");
            closeLinkDialog.setRightBtnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //?????????????????????
                    stopIMLink();
                    //?????????????????????????????????
                    if (moreLinkSettingDialog != null && moreLinkSettingDialog.isShowing()) {
                        moreLinkSettingDialog.dismiss();
                    }
                    closeLinkDialog.dismissDialog();
                }
            });
        }
        closeLinkDialog.showDialog();

    }

    private Set<String> pbWords = new HashSet<>();

    //??????????????????
    private void toSendTextMsg(String msg) {
        //??????????????????????????????
        if (SensitiveWordsUtils.contains(msg)) {
            msg = SensitiveWordsUtils.replaceSensitiveWord(msg, "***");
        }


        addMsg2List("??? ", msg, LiveConstants.IMCMD_TEXT_MSG);
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

    //???????????????????????????
    private void toSendLinkMsg(int position) {
        ZBUserListBean userListBean = new ZBUserListBean();
        userListBean.userId = UserInfoMgr.getInstance().getUserInfo().getId();
        userListBean.avatarUrl = UserInfoMgr.getInstance().getUserInfo().getAvatarUrl();
        userListBean.nickname = UserInfoMgr.getInstance().getUserInfo().getNickname();
        userListBean.position = position;

        //?????????????????????????????????
        mLiveRoom.sendRoomCustomMsg(String.valueOf(LiveConstants.IMCMD_TO_LINK), userListBean, new SendRoomCustomMsgCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                LogUtils.v(Constant.TAG_LIVE, "?????????????????????" + errInfo);
            }

            @Override
            public void onSuccess() {
                LogUtils.v(Constant.TAG_LIVE, "????????????????????????");
                waitLinkDialog();
            }
        });

    }

    //?????????????????????dialog
    private int recLenWait = LiveConstants.TO_LINK_TIME;//???????????????????????????
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
        //????????????????????????
        View view = LayoutInflater.from(this).inflate(R.layout.layout_link_wait_pop, null);
        //???????????????
        TextView tvCancel = (TextView) view.findViewById(R.id.tv_cancel);
        TextView tvTime = (TextView) view.findViewById(R.id.tv_time);
        tvTime.setText(LiveConstants.TO_LINK_TIME + "S");

        tvCancel.setOnClickListener(lis -> {
            //??????????????????????????????
            mLiveRoom.sendC2CCustomMsg(mLiveInitInfo.userId, String.valueOf(LiveConstants.IMCMD_CANCEL_LINK), "????????????", new SendC2CCustomMsgCallback() {
                @Override
                public void onError(int errCode, String errInfo) {

                }

                @Override
                public void onSuccess() {
                    linkStatus = 1;
                    //??????
                    disWaitLinkDialog();
                }
            });


        });

        //??????????????????dismiss
        waitLinkDialog.setCancelable(false);
        //??????????????????Dialog
        waitLinkDialog.setContentView(view);
        //????????????Activity???????????????
        Window dialogWindow = waitLinkDialog.getWindow();
        //??????Dialog?????????????????????
        dialogWindow.setGravity(Gravity.BOTTOM);
        //??????????????????
        dialogWindow.setWindowAnimations(R.style.DialogBottomAnimation);
        //?????????????????????
        WindowManager.LayoutParams params = dialogWindow.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;//?????????????????????
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;//?????????????????????
        dialogWindow.setAttributes(params);
        waitLinkDialog.show();//???????????????
        recLenWait = LiveConstants.TO_LINK_TIME;
        //?????????
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
                            //?????????????????????????????????????????????1
                            if (linkStatus != 3) {
                                linkStatus = 1;
                                ToastUtils.showShort("???????????????");
                            }
                            //??????
                            disWaitLinkDialog();
                        }
                    }
                });
            }
        };

        waitLinkTimer.schedule(waitLinkTask, 2000, 1000);//????????????2????????????????????????

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

        //????????????????????????
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

    //??????????????????dialog
    private int recLen = 10;//?????????????????????
    private Timer selectLinkTimer;
    private TimerTask selectLinkTask;

    //???????????????????????????
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
        //????????????????????????
        View view = LayoutInflater.from(this).inflate(R.layout.layout_link_select_pop, null);
        //???????????????
        TextView tvCancel = (TextView) view.findViewById(R.id.tv_cancel);
        TextView tvSure = (TextView) view.findViewById(R.id.tv_sure);
        TextView tvTime = (TextView) view.findViewById(R.id.tv_time);

        tvCancel.setOnClickListener(lis -> {
            //????????????
            //??????????????????
            mLiveRoom.sendC2CCustomMsg(mLiveInitInfo.userId, String.valueOf(LiveConstants.IMCMD_REFUSE_LINK), "???????????????????????????", new SendC2CCustomMsgCallback() {
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
            //????????????
            //????????????????????????
            disposable = permissions.request(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA)
                    .subscribe(granted -> {
                        if (granted) {
                            //????????????
                            toSQIMLink();
                            dismissSelectLinkDialog();
                        } else {
                            ToastUtils.showShort("???????????????????????????????????????");
                        }

                    });

        });
        //??????????????????dismiss
        selectLinkDialog.setCancelable(false);
        //??????????????????Dialog
        selectLinkDialog.setContentView(view);
        //????????????Activity???????????????
        Window dialogWindow = selectLinkDialog.getWindow();
        //??????Dialog?????????????????????
        dialogWindow.setGravity(Gravity.BOTTOM);
        //??????????????????
        dialogWindow.setWindowAnimations(R.style.DialogBottomAnimation);
        //?????????????????????
        WindowManager.LayoutParams params = dialogWindow.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;//?????????????????????
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;//?????????????????????
        dialogWindow.setAttributes(params);
        selectLinkDialog.show();//???????????????
        recLen = 10;
        //?????????
        selectLinkTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() { // UI thread
                    @Override
                    public void run() {
                        recLen--;
                        tvTime.setText(recLen + "S");
                        if (recLen < 1) {
                            //???????????????????????????????????????
                            mLiveRoom.sendC2CCustomMsg(mLiveInitInfo.userId, String.valueOf(LiveConstants.IMCMD_REFUSE_LINK), "???????????????????????????", null);
                            dismissSelectLinkDialog();
                        }
                    }
                });
            }
        };

        selectLinkTimer.schedule(selectLinkTask, 1000, 1000);//???????????????????????????????????????

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


        //????????????????????????
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

    //1?????????????????????????????? 2????????????????????????????????????
    private void toSQIMLink() {
        if (linkWaitTip == null) {
            linkWaitTip = new QMUITipDialog.Builder(this)
                    .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                    .setTipWord("???????????????????????????...")
                    .create();
        }
        linkWaitTip.show();

        //??????????????????
        mLiveRoom.requestJoinAnchor(mLiveInitInfo.userId, new RequestJoinAnchorCallback() {
            @Override
            public void onAccept() {
                LogUtils.v(Constant.TAG_LIVE, "???????????????");
                if (linkWaitTip != null && linkWaitTip.isShowing()) {
                    linkWaitTip.dismiss();
                }
                Toast.makeText(LiveGuanzhongActivity.this, "????????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
                //????????????dialog???????????????
                disWaitLinkDialog();
                //????????????
                startLinkLayout();
            }

            //????????????
            @Override
            public void onReject(String reason) {
                if (linkWaitTip != null && linkWaitTip.isShowing()) {
                    linkWaitTip.dismiss();
                }
                linkStatus = 1;
                Toast.makeText(LiveGuanzhongActivity.this, reason, Toast.LENGTH_SHORT).show();
                //????????????dialog???????????????
                disWaitLinkDialog();
            }

            @Override
            public void onTimeOut() {
                if (linkWaitTip != null && linkWaitTip.isShowing()) {
                    linkWaitTip.dismiss();
                }
                linkStatus = 1;
                Toast.makeText(LiveGuanzhongActivity.this, "?????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
                disWaitLinkDialog();
            }

            @Override
            public void onError(int code, String errInfo) {
                if (linkWaitTip != null && linkWaitTip.isShowing()) {
                    linkWaitTip.dismiss();
                }
                linkStatus = 1;
                Toast.makeText(LiveGuanzhongActivity.this, "???????????????????????????" + errInfo, Toast.LENGTH_SHORT).show();
                disWaitLinkDialog();
            }
        });
    }

    private void startLinkLayout() {
        if (linkType == 0) {
            //1v1??????????????????????????????
            binding.rlAnchor2.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) binding.llVideo.getLayoutParams();
            lp.height = (int) getResources().getDimension(R.dimen.dp_320);
            lp.setMargins(0, (int) getResources().getDimension(R.dimen.dp_148), 0, 0);
            binding.llVideo.setLayoutParams(lp);

            //?????????????????????????????????
            Glide.with(this)
                    .load(UserInfoMgr.getInstance().getUserInfo().getAvatarUrl())
                    .apply(RequestOptions.bitmapTransform(new BlurTransformation(15, 15)))
                    .into(binding.ivAnchor2);

            mLiveRoom.startLocalPreview(true, binding.anchor2TxVideoView);
        } else { //????????????
            TXCloudVideoView txCloudVideoView = (TXCloudVideoView) moreLinkAdapter.getViewByPosition(mLinkPos, R.id.anchor_video_view);
            mLiveRoom.startLocalPreview(txCloudVideoView);//????????????????????? ????????????
        }

        if (showLinkTip == null) {
            showLinkTip = new QMUITipDialog.Builder(this)
                    .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                    .setTipWord("???????????????...")
                    .create();
        }
        showLinkTip.show();
        mLiveRoom.joinAnchor(linkType, new JoinAnchorCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                if (showLinkTip != null && showLinkTip.isShowing()) {
                    showLinkTip.dismiss();
                }
                Toast.makeText(LiveGuanzhongActivity.this, "???????????????" + errInfo, Toast.LENGTH_SHORT).show();
                //????????????
                stopLinkLayout();
            }

            @Override
            public void onSuccess() {
                if (showLinkTip != null && showLinkTip.isShowing()) {
                    showLinkTip.dismiss();
                }

                //????????????????????????????????????
                mLiveRoom.sendC2CCustomMsg(mLiveInitInfo.userId, String.valueOf(LiveConstants.IMCMD_CANCEL_LINK), "????????????", null);

                dismissSelectLinkDialog();

                linkStatus = 3;
                //???????????????????????? ?????????????????? ????????????????????????
                binding.btnLianmai.setVisibility(View.VISIBLE);
                binding.btnLianmai.setBackgroundResource(R.mipmap.zbj_menu_lianmaiing_gz);
                if (linkType == 0) {
                    //????????????????????????
                    binding.btnJtfz.setVisibility(View.VISIBLE);
                } else {
                    //??????????????????
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
                //????????????
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
            //??????1v1?????????????????????????????????
            binding.rlAnchor2.setVisibility(View.GONE);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            lp.setMargins(0, 0, 0, (int) getResources().getDimension(R.dimen.dp_54));
            binding.llVideo.setLayoutParams(lp);
            binding.btnJtfz.setVisibility(View.GONE);
        } else {
            //????????????????????????????????????????????? ???????????????????????????
            //            updateLinkerPos(mLinkPos, null, "????????????", null);
        }

        //???????????????????????????
        if (!mLiveInitInfo.getCanMic()) {
            ToastUtil.toastShortMessage(LiveConstants.SHOW_JZLM);
            binding.btnLianmai.setVisibility(View.GONE);
        }
    }


    //????????????????????????
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

    //??????????????????
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

    //????????????
    private void showDianZan() {
        //??????????????????
        if (mLikeFrequeControl == null) {
            mLikeFrequeControl = new TCFrequeControl();
            mLikeFrequeControl.init(Constant.MAX_DIAN_ZAN, 1);
        }
        if (mLikeFrequeControl.canTrigger()) {
            binding.tcHeartLayout.addFavor();
        }
    }

    private boolean isDianZaning;//?????????????????????????????? 2S??????

    //????????????
    private void toDianZan() {
        //????????????????????????
        if (mLikeFrequeControl == null) {
            mLikeFrequeControl = new TCFrequeControl();
            mLikeFrequeControl.init(Constant.MAX_DIAN_ZAN, 1);
        }
        if (mLikeFrequeControl.canTrigger()) {
            mZanNum++;
            binding.tcHeartLayout.addFavor();
            //??????????????????????????????
            mLiveRoom.sendRoomCustomMsg(String.valueOf(LiveConstants.IMCMD_LIKE), "", null);
        }
        if (!isDianZaning) {
            //??????2S?????????????????? ????????????????????????
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //??????????????????
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

    //????????????
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

        //????????????????????????????????????clear?????????????????????????????????
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

    private long lastRushTime;//??????????????????2S???????????????

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

    private boolean roomDestroy;//?????????????????????????????????????????????????????????

    @Override
    public void onRoomDestroy(String roomID) {
        if (!roomID.equals(mRoomID)) {
            return;
        }
        roomDestroy = true;
        viewModel.leaveLive(mLiveInitInfo.liveRoomRecordId);
    }

    //?????????
    @Override
    public void onAnchorEnter(AnchorInfo anchorInfo) {
    }

    @Override
    public void onAnchorExit(AnchorInfo anchorInfo) {
        mLiveRoom.stopRemoteView(anchorInfo);
        remoteSet.remove(anchorInfo.userID);
        LogUtils.v(Constant.TAG_LIVE, "????????????" + anchorInfo.userID);
    }

    @Override
    public void onAudienceEnter(AudienceInfo audienceInfo) {
        //????????????
    }

    @Override
    public void onAudienceExit(AudienceInfo audienceInfo) {
        //????????????
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
                //????????????????????????
                toRecvTextMsg(userInfo, LiveConstants.SHOW_ENTER_LIVE, type);
                //??????????????????
                toRushLiveInfo();
                break;
            case LiveConstants.IMCMD_EXIT_LIVE:
                //????????????????????????
                toRecvTextMsg(userInfo, LiveConstants.SHOW_EXIT_LIVE, type);
                break;
            case LiveConstants.IMCMD_LIKE:
                //??????????????????
                showDianZan();
                //??????3S?????????????????????????????????2S?????????
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toRushLiveInfo();
                    }
                }, Constant.TIME_DIAN_ZAN_WAIT + 1000);
                break;
            case LiveConstants.IMCMD_ZB_COMEBACK:
                //?????????????????????????????????????????????
                mLiveRoom.reStartPlay(mLiveInitInfo.pullStreamUrl, binding.mTxVideoView, null);
                break;
            case LiveConstants.IMCMD_FOLLOW:
                //??????????????????
                toRecvTextMsg(userInfo, LiveConstants.SHOW_FOLLOW, type);
                break;
            case LiveConstants.IMCMD_GONGGAO_MSG:
                //???????????? ????????????
                String jsonMsg = (String) message;
                JsonMsgBean jsonMsgBean = JSON.parseObject(jsonMsg, JsonMsgBean.class);

                //??????????????????
                ggList.add(jsonMsgBean);
                if (binding.llGonggao.getVisibility() == View.GONE) {
                    showGongGao();
                }

                break;
            case LiveConstants.IMCMD_OPEN_MORE_LINK://????????????????????????
                startMoreLinkLayout();
                break;
            case LiveConstants.IMCMD_CLOSE_MORE_LINK://????????????????????????
                if (linkType == 0) {
                    return;
                }

                //??????????????????????????????????????????
                if (linkStatus == 3) {
                    //????????????
                    stopIMLink();
                } else {
                    linkType = 0;
                    closeMoreLinkAnim();
                    initMoreLinkData();
                }

                break;
            case LiveConstants.IMCMD_RESH_MORELINK_INFO://???????????????
                viewModel.findMicUsers(mLiveInitInfo.liveRoomRecordId);
                break;
            case LiveConstants.IMCMD_RESH_HOME_INFO://????????????????????????
                viewModel.getLiveStatus(mLiveInitInfo.liveRoomId);
                break;
            case LiveConstants.IMCMD_GIFT://????????????
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
            case LiveConstants.IMCMD_SHOW_MIC://????????????
                ToastUtil.toastShortMessage(LiveConstants.SHOW_KQLM);
                binding.btnLianmai.setVisibility(View.VISIBLE);
                mLiveInitInfo.setCanMic(true);
                break;
            case LiveConstants.IMCMD_FORBIDDEN_MIC://????????????

                //????????????????????????
                mLiveInitInfo.setCanMic(false);
                if (linkStatus == 1) {
                    ToastUtil.toastShortMessage(LiveConstants.SHOW_JZLM);
                    binding.btnLianmai.setVisibility(View.GONE);
                }


                break;
            case LiveConstants.IMCMD_SETTING_PL://????????????
                if (((String) message).equals("1")) {
                    ToastUtil.toastShortMessage(LiveConstants.SHOW_KQPL);
                    mLiveInitInfo.setCanComment(true);
                } else {
                    ToastUtil.toastShortMessage(LiveConstants.SHOW_JZPL);
                    mLiveInitInfo.setCanComment(false);
                }
                break;
            case LiveConstants.IMCMD_SETTING_LW://????????????
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
            case LiveConstants.IMCMD_SHOW_GOODS://????????????
                //?????????????????????????????????
                String json = (String) message;
                ZBGoodsBean goodBean = JSON.parseObject(json, ZBGoodsBean.class);
                initZBGoodsBean(goodBean);

                break;
            case LiveConstants.IMCMD_SHOW_GOODS_CANCEL://??????????????????
                binding.rlShowGood.setVisibility(View.GONE);
                break;
            case LiveConstants.IMCMD_BLOCK_WORD_ADD://???????????????
                pbWords.add((String) message);
                SensitiveWordsUtils.init(pbWords);
                break;
            case LiveConstants.IMCMD_BLOCK_WORD_DEL://???????????????
                pbWords.remove((String) message);
                SensitiveWordsUtils.init(pbWords);
                break;
            default:
                break;
        }
    }

    //????????????
    public void showGongGao() {
        if (ggList.size() == 0) {
            return;
        }
        JsonMsgBean jsonMsgBean = ggList.get(0);
        ggList.remove(0);
        binding.tvGgtype.setText("??????");
        binding.tvGgname.setText(jsonMsgBean.nickname);
        binding.tvGgcontent.setText(jsonMsgBean.content);


        ggShowAnimation = AnimUtils.getAnimation(this, R.anim.anim_slice_in_left);
        ggGoneAnimation = AnimUtils.getAnimation(this, R.anim.anim_slice_out_left);


        //??????????????????
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

    private ZBGoodsBean nowGoodsBean;//????????????

    private void initZBGoodsBean(ZBGoodsBean goodBean) {
        nowGoodsBean = goodBean;

        binding.rlShowGood.setVisibility(View.VISIBLE);
        GlideUtil.intoImageView(this, UrlUtils.resetImgUrl(goodBean.imageUrl, 400, 400), binding.rivGood);
        binding.tvGoodName.setText(goodBean.title);
        binding.tvGoodPrice.setText("" + goodBean.maxSalesPrice);
    }

    private void startMoreLinkAnim() {
        //??????x???????????????
        float xx = (float) (screenWidth - binding.rvMoreLink.getWidth() - (getResources().getDimension(R.dimen.dp_3))) / screenWidth;

        //??????y??????????????? 93*6+5*3 = 573
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
        //??????x???????????????
        float xx2 = (float) (screenWidth - binding.rvMoreLink.getWidth()) / screenWidth;
        //??????y???????????????
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
            case LiveConstants.IMCMD_FORBIDDER_TALK://??????
                //??????????????????????????????????????????
                if (!senderId.equals(mLiveInitInfo.userId)) {
                    return;
                }
                mLiveInitInfo.setHasSpeech(true);
                ToastUtil.toastShortMessage(message);
                break;
            case LiveConstants.IMCMD_CANCEL_FORBIDDER_TALK://????????????
                //??????????????????????????????????????????
                if (!senderId.equals(mLiveInitInfo.userId)) {
                    return;
                }
                mLiveInitInfo.setHasSpeech(false);
                ToastUtil.toastShortMessage(message);
                break;
            case LiveConstants.IMCMD_PUT_BLACK://??????/??????
                //??????????????????????????????????????????
                if (!senderId.equals(mLiveInitInfo.userId)) {
                    return;
                }
                ToastUtil.toastShortMessage(message);
                finish();
                break;
            case LiveConstants.IMCMD_INVITE_LINK://??????????????????
                //?????????????????????????????????
                if (mLiveInitInfo.userId.equals(senderId)) {
                    if (message.contains("????????????")) {
                        //??????????????????????????????
                        if (selectLinkDialog == null || !selectLinkDialog.isShowing()) {
                            selectLinkDialog();
                        }
                    } else if (message.contains("????????????")) {
                        //???????????????????????? ?????????????????????
                        disWaitLinkDialog();
                        toSQIMLink();
                    }

                }
                break;
            case LiveConstants.IMCMD_MORE_LINK_YQ://????????????????????????
                //????????????????????????????????????
                //????????????????????????
                mLinkPos = Integer.parseInt(message);
                if (selectLinkDialog == null || !selectLinkDialog.isShowing()) {
                    selectLinkDialog();
                }


                break;
            case LiveConstants.IMCMD_MORE_LINK_JS://??????????????????????????????????????????
                //??????????????????????????????????????????
                if (message.equals("-1")) {
                    linkStatus = 1;
                    ToastUtil.toastShortMessage("??????????????????");
                } else {
                    //????????????????????????????????????
                    mLinkPos = Integer.parseInt(message);
                    //???????????????
                    toSQIMLink();
                }
                break;
            case LiveConstants.IMCMD_MORE_ANCHOR_QXJY://?????????????????????????????????
            case LiveConstants.IMCMD_MORE_ANCHOR_JY://???????????????????????????
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
        //??????????????????
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
                                //????????????
                                double2DianZan((int) now_press_Y + binding.mTxVideoView.getTop(), (int) now_press_X);
                            }
                        }

                        firstClickTime = secondTime;
                        break;

                }
                return true;
            }
        });

        //???????????????
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
                    zbUserListPop = new ZBUserListPop(this, mLiveInitInfo, "?????????");
                }
                zbUserListPop.showPopupWindow();
                break;
            case R.id.btn_back:
                showDialog("???????????????...");
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
                //?????????????????????
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
                //???????????? ???????????????
                showDialog();
                viewModel.toFollow(mLiveInitInfo.userId);
                break;
            case R.id.btn_lianmai:
                //??????????????????
                long secondTime = System.currentTimeMillis();
                if (secondTime - firstClickLMTime > 500) {
                    //??????????????????????????????500?????????????????????
                    firstClickLMTime = secondTime;
                    if (linkStatus == 1) {
                        //???????????????????????????????????? - ?????????????????????
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
                                            //?????????????????????pop
                                            showMoreLinkToLinkPop(true);
                                        }
                                    } else {
                                        ToastUtils.showShort("???????????????????????????????????????");
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
                //????????????
                //                zbMorePop = new ZBMoreGZPop(this, mLiveRoom, mLiveInitInfo);
                //                zbMorePop.showPopupWindow();
                if (mLiveInitInfo.share != null) {
                    mLiveInitInfo.share.resourceId = mLiveInitInfo.liveRoomId;
                    mLiveInitInfo.share.userNickname = mLiveInitInfo.nickname;
                    mLiveInitInfo.share.userAvatar = mLiveInitInfo.avatarUrl;
                    mLiveInitInfo.share.title = mLiveInitInfo.liveRoomName;
                    mLiveInitInfo.share.hasRoomPwd = mLiveInitInfo.hasPassword;
                }

                shareDialog = new ShareDialog(getContext(), mLiveInitInfo.share, ShareDialog.LIVE_TYPE);
                shareDialog.show(binding.llMenu);
                break;
            default:
                break;
        }
    }

    private boolean isFirst = true;
    private boolean needToLink = false;//???????????????????????????
    private long lastMsgTime;
    private String lastMsg;

    @Override
    public void initViewObservable() {
        super.initViewObservable();

        disposable = RxBus.getDefault().toObservable(EventBean.class).subscribe(eventBean -> {
            if (eventBean.msgId == LiveConstants.SEND_MSG) {
                //??????????????????????????????
                if (!mLiveInitInfo.getCanComment()) {
                    ToastUtil.toastShortMessage(LiveConstants.SHOW_JZPL);
                    return;
                }
                //??????????????????
                if (eventBean.time - lastMsgTime < 200 && lastMsg.equals(eventBean.content)) {
                    return;
                }
                toSendTextMsg(eventBean.content.trim());
                lastMsgTime = eventBean.time;
                lastMsg = eventBean.content;
            } else if (eventBean.msgId == CodeTable.ZHJ_SEND_GIFT) {
                //????????????????????????
                if (!mLiveInitInfo.getCanGift()) {
                    ToastUtil.toastShortMessage(LiveConstants.SHOW_JZLW);
                    return;
                }

                selectGift = (GiftBean) eventBean.data;
                //????????????-????????????
                viewModel.sendGift(mLiveInitInfo.liveRoomRecordId, mLiveInitInfo.userId, selectGift.id);
            } else if (eventBean.msgId == LiveConstants.SHOW_ET) {
                //????????????????????????
                int height = eventBean.msgType;
                ViewGroup.LayoutParams params = binding.view.getLayoutParams();
                params.height = height;
                binding.view.setLayoutParams(params);
            } else if (eventBean.msgId == LiveConstants.DISMISS_ET) {
                //????????????????????????
                ViewGroup.LayoutParams params = binding.view.getLayoutParams();
                params.height = 0;
                binding.view.setLayoutParams(params);

                mMsg = eventBean.content;
                if (!TextUtils.isEmpty(mMsg)) {
                    binding.tvMsg.setText(mMsg);
                    FaceManager.handlerEmojiText(binding.tvMsg, mMsg, false);
                } else {
                    binding.tvMsg.setText("???????????????");
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
                    mLiveInitInfo.hasFollow = true;
                } else {
                    binding.tvFollow.setText("??????");
                    binding.tvFollow.setBackground(getResources().getDrawable(R.mipmap.guanzhu_1_00034));
                    mLiveInitInfo.hasFollow = false;
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
        //?????????????????????????????????
        viewModel.liveHeaderInfo.observe(this, liveHeaderInfo -> {
            if (liveHeaderInfo != null) {
                //??????????????????
                binding.tvZanNum.setText(liveHeaderInfo.totalZanNum);
                //??????????????????
                if (liveHeaderInfo.liveRoomUsers != null) {
                    Collections.reverse(liveHeaderInfo.liveRoomUsers);
                    topHeadAdapter.setNewInstance(liveHeaderInfo.liveRoomUsers);
                }
                //???????????????
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

                        //??????????????????
                        mLiveRoom.sendRoomCustomMsg(String.valueOf(LiveConstants.IMCMD_FOLLOW), "", null);
                        addMsg2List(UserInfoMgr.getInstance().getUserInfo().getNickname(), LiveConstants.SHOW_FOLLOW, LiveConstants.IMCMD_FOLLOW);
                    } else {
                        binding.tvFollow.setText("??????");
                        binding.tvFollow.setBackground(getResources().getDrawable(R.mipmap.guanzhu_1_00034));
                    }
                    viewModel.addFansCount(mLiveInitInfo.liveRoomRecordId);
                }
            }
        });
        viewModel.netSuccess.observe(this, netSuccess -> {
            if (netSuccess != null) {
                //????????????/????????????
                if (netSuccess == 1) {
                    mLiveRoom.muteLocalAudio(!moreLinkAdapter.getData().get(mLinkPos).hasProsody);
                    moreLinkAdapter.getData().get(mLinkPos).hasProsody = !moreLinkAdapter.getData().get(mLinkPos).hasProsody;
                    moreLinkAdapter.notifyItemChanged(mLinkPos);
                    //??????????????????
                    mLiveRoom.sendRoomCustomMsg(String.valueOf(LiveConstants.IMCMD_RESH_MORELINK_INFO), "", null);
                }
            }
        });
        viewModel.moreLinkList.observe(this, linkerList -> {
            if (linkerList != null) {

                moreLinkList = new ArrayList<>();
                for (int i = 0; i < 6; i++) {
                    ZBUserListBean userInfoBean = new ZBUserListBean();
                    userInfoBean.nickname = "????????????";
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
                    //????????? ????????????????????????
                    //????????????????????????????????????
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mLiveRoom.sendC2CCustomMsg(mLiveInitInfo.userId, String.valueOf(LiveConstants.IMCMD_MORE_LINK_NUM), String.valueOf(mLinkPos), null);
                            needToLink = false;
                        }
                    }, 2000);
                }
                isFirst = false;

                //??????????????????????????????
                //??????????????????????????????
                if (linkType == 1 && linkStatus == 3) {
                    for (int i = 0; i < moreLinkList.size(); i++) {
                        //????????????????????????????????????????????????
                        if (!TextUtils.isEmpty(moreLinkList.get(i).userId) &&
                                !remoteSet.equals(moreLinkList.get(i).userId) &&
                                !moreLinkList.get(i).userId.equals(UserInfoMgr.getInstance().getUserInfo().getId())) {
                            //????????????????????????
                            View item = moreLinkManager.getChildAt(i);

                            AnchorInfo anchorInfo = new AnchorInfo(moreLinkList.get(i).userId, moreLinkList.get(i).nickname,
                                    moreLinkList.get(i).avatarUrl, moreLinkList.get(i).pullStreamUrl);

                            TXCloudVideoView txCView = item.findViewById(R.id.anchor_video_view);
                            LogUtils.v(Constant.TAG_LIVE, "??????????????????" + anchorInfo.userID);

                            mLiveRoom.startRemoteView(true, anchorInfo, txCView, new IMLVBLiveRoomListener.PlayCallback() {
                                @Override
                                public void onBegin() {
                                    remoteSet.add(anchorInfo.userID);
                                    LogUtils.v(Constant.TAG_LIVE, "????????????" + anchorInfo.userID);
                                }

                                @Override
                                public void onError(int errCode, String errInfo) {
                                    LogUtils.v(Constant.TAG_LIVE, "????????????" + errInfo);
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
            //???????????????????????????
            if (sendSuccess != null && sendSuccess) {

                //?????? ??????????????????????????????
                toSendGiftMsg(selectGift);
                zbGiftListPop.dismiss();
                //???????????????????????????
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
                    //ToastUtils.showShort("?????????????????????");
                }
            }
        });
    }

    private Set<String> remoteSet = new HashSet<>();//??????????????????????????????

    //??????????????????
    protected void handleGiftMSg(TCUserInfo userInfo) {
        TCChatEntity entity = new TCChatEntity();
        if (TextUtils.isEmpty(userInfo.nickname)) {
            entity.setSenderName(LiveConstants.NIKENAME + userInfo.userid);
        } else {
            entity.setSenderName(userInfo.nickname);
        }
        entity.setContent("??????" + userInfo.giftName);
        entity.setType(LiveConstants.IMCMD_GIFT);
        entity.setLevelName(userInfo.leaveName);
        entity.setLevelIcon(userInfo.leaveIcon);
        notifyMsg(entity);
    }

    //??????????????????
    private void toSendGiftMsg(GiftBean giftBean) {
        //????????????????????????
        TCChatEntity entity = new TCChatEntity();
        entity.setSenderName("???  ");
        entity.setContent("??????" + giftBean.giftName);
        entity.setType(LiveConstants.IMCMD_TEXT_MSG);
        notifyMsg(entity);

        SendGiftBean sendGiftBean = new SendGiftBean();
        sendGiftBean.giftsIcon = giftBean.giftImage;
        sendGiftBean.giftsName = giftBean.giftName;
        sendGiftBean.id = giftBean.id;
        sendGiftBean.userId = UserInfoMgr.getInstance().getUserInfo().getId();
        sendGiftBean.svgaUrl = giftBean.giftUrl;
        sendGiftBean.hasAnimation = giftBean.hasAnimation ? "1" : "0";

        //?????????????????????
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

    //??????????????????
    private boolean isBanner1Showing;//???????????????
    private boolean isBanner2Showing;//???????????????
    private boolean isAnimLoading;//?????????????????????
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
        //???????????????

        //?????????????????????????????????
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


        //???????????????banner?????????????????????????????????
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

    //????????????
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
            binding.tvGiftName2.setText("???" + sendGiftBean2.giftsName);
            binding.rlGift2.clearAnimation();
            binding.rlGift2.setAnimation(showAnim2);
        } else {
            Animation bigAnim = AnimUtils.getAnimation(LiveGuanzhongActivity.this, R.anim.zbj_gift_num);
            binding.tvGiftNum2.clearAnimation();
            binding.tvGiftNum2.setAnimation(bigAnim);
        }

        binding.tvGiftNum2.setText("" + sendGiftBean2.num + " ");

        //??????????????????3S??????

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
        giftContentTimer2.schedule(giftContentTask2, 4000);//3????????????
    }

    private void showBanner1() {

        //????????????
        if (binding.rlGift.getVisibility() == View.GONE) {
            showAnim1 = AnimUtils.getAnimation(LiveGuanzhongActivity.this, R.anim.anim_slice_in_left);
            binding.rlGift.setVisibility(View.VISIBLE);
            Glide.with(LiveGuanzhongActivity.this).load(sendGiftBean1.sendUserPhoto).into(binding.ivUserHeadPic);
            Glide.with(LiveGuanzhongActivity.this).load(sendGiftBean1.giftsIcon).into(binding.ivGift);
            binding.tvAudienceName.setText(sendGiftBean1.sendUserName);
            binding.tvGiftName.setText("???" + sendGiftBean1.giftsName);

            binding.rlGift.clearAnimation();
            binding.rlGift.setAnimation(showAnim1);
        } else {
            Animation bigAnim = AnimUtils.getAnimation(LiveGuanzhongActivity.this, R.anim.zbj_gift_num);
            binding.tvGiftNum.clearAnimation();
            binding.tvGiftNum.setAnimation(bigAnim);
        }
        binding.tvGiftNum.setText("" + sendGiftBean1.num + " ");


        //??????????????????3S??????
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
        giftContentTimer.schedule(giftContentTask, 4000);//3????????????
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

    //????????????
    private void loadSvga() {
        if (isAnimLoading || giftsAnimList.size() == 0) {
            return;
        }

        showGiftAnim(giftsAnimList.get(0).svgaUrl);
        giftsAnimList.remove(0);
    }

    private void showGiftAnim(String nowUrl) {
        binding.rlShowGift.setVisibility(View.VISIBLE);
        //svga????????????
        File cacheDir = new File(getApplicationContext().getCacheDir().getAbsolutePath(), "svga");
        try {
            HttpResponseCache.install(cacheDir, 1024 * 1024 * 200);
        } catch (IOException e) {
            e.printStackTrace();
        }

        binding.rlShowGift.removeAllViews();
        //??????????????????
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
                //????????????
            }

            @Override
            public void onFinished() {
                //????????????
                isAnimLoading = false;
                loadSvga();
            }

            @Override
            public void onRepeat() {
                //????????????
            }

            @Override
            public void onStep(int i, double v) {
                //????????????
            }
        });
    }


    /**
     * ?????????????????????????????????
     */
    private double firstTime;

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                long secondTime = System.currentTimeMillis();
                if (secondTime - firstTime > 2000) {
                    //????????????????????????????????????2??????????????????
                    Toast.makeText(this, "???????????????????????????~", Toast.LENGTH_SHORT).show();
                    firstTime = secondTime;//??????firstTime
                } else {
                    //????????????
                    showDialog("???????????????...");
                    //?????????????????????????????????
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
                //?????????????????????
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
            //????????????????????????????????????
            if (activity.getLocalClassName().contains("LiveGuanzhongActivity")) {
                // ??????
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
        if (event == TXLiveConstants.PLAY_EVT_PLAY_END) {//??????????????????
            if (txLivePlayer != null) {
                txLivePlayer.resume();
            }
        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_BEGIN) {//??????????????????
            isPlaying = true;
        }
    }

    @Override
    public void onNetStatus(Bundle status) {
        //???????????????
        if (status.getInt(TXLiveConstants.NET_STATUS_VIDEO_WIDTH) > status.getInt(TXLiveConstants.NET_STATUS_VIDEO_HEIGHT)) {
            //????????????
            if (mRenderMode != TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION) {
                mRenderMode = TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION;
                txLivePlayer.setRenderMode(mRenderMode);
            }
        } else {
            //????????????
            if (mRenderMode != TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN) {
                mRenderMode = TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN;
                txLivePlayer.setRenderMode(mRenderMode);
            }
        }
    }
}
