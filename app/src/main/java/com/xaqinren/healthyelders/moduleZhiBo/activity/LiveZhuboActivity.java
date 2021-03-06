
package com.xaqinren.healthyelders.moduleZhiBo.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.HttpResponseCache;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.google.gson.Gson;
import com.opensource.svgaplayer.SVGACallback;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.tencent.liteav.audio.TXCAudioEngine;
import com.tencent.liteav.audio.TXCAudioUGCRecorder;
import com.tencent.liteav.audio.impl.Record.TXCAudioSysRecord;
import com.tencent.liteav.demo.beauty.BeautyImpl;
import com.tencent.qcloud.tim.uikit.component.face.FaceManager;
import com.tencent.qcloud.ugckit.utils.ToastUtil;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.databinding.ActivityLiveZhuboBinding;
import com.xaqinren.healthyelders.global.AppApplication;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleHome.LoadGiftService;
import com.xaqinren.healthyelders.moduleZhiBo.adapter.MoreLinkAdapter;
import com.xaqinren.healthyelders.moduleZhiBo.adapter.TCChatMsgListAdapter;
import com.xaqinren.healthyelders.moduleZhiBo.adapter.TopUserHeadAdapter;
import com.xaqinren.healthyelders.moduleZhiBo.adapter.ZBLinkShowAdapter;
import com.xaqinren.healthyelders.moduleZhiBo.bean.JsonMsgBean;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveHeaderInfo;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveInitInfo;
import com.xaqinren.healthyelders.moduleZhiBo.bean.SendGiftBean;
import com.xaqinren.healthyelders.moduleZhiBo.bean.SendUserLinkBean;
import com.xaqinren.healthyelders.moduleZhiBo.bean.TCChatEntity;
import com.xaqinren.healthyelders.moduleZhiBo.bean.TCUserInfo;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ZBSettingBean;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ZBUserListBean;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.IMLVBLiveRoomListener;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.LiveConstants;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.MLVBLiveRoom;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.roomutil.commondef.AnchorInfo;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.roomutil.commondef.AudienceInfo;
import com.xaqinren.healthyelders.moduleZhiBo.popupWindow.ZBGoodsListPop;
import com.xaqinren.healthyelders.moduleZhiBo.popupWindow.ZBLinkUsersPop;
import com.xaqinren.healthyelders.moduleZhiBo.popupWindow.ZBMorePop;
import com.xaqinren.healthyelders.moduleZhiBo.popupWindow.ZBUserInfoPop;
import com.xaqinren.healthyelders.moduleZhiBo.popupWindow.ZBUserListPop;
import com.xaqinren.healthyelders.moduleZhiBo.viewModel.LiveZhuboViewModel;
import com.xaqinren.healthyelders.moduleZhiBo.widgetLike.TCFrequeControl;
import com.xaqinren.healthyelders.utils.AnimUtils;
import com.xaqinren.healthyelders.utils.LogUtils;
import com.xaqinren.healthyelders.utils.MScreenUtil;
import com.xaqinren.healthyelders.utils.SensitiveWordsUtils;
import com.xaqinren.healthyelders.widget.YesOrNoDialog;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import io.reactivex.disposables.Disposable;
import jp.wasabeef.glide.transformations.BlurTransformation;
import me.goldze.mvvmhabit.base.AppManager;
import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;
import me.goldze.mvvmhabit.utils.ToastUtils;

/**
 * Created by Lee. on 2021/4/25.
 * ????????????
 */
public class LiveZhuboActivity extends BaseActivity<ActivityLiveZhuboBinding, LiveZhuboViewModel> implements IMLVBLiveRoomListener, View.OnClickListener {

    private MLVBLiveRoom mLiveRoom;
    private LiveInitInfo mLiveInitInfo;
    private Disposable disposable;
    private List<TCChatEntity> msgList;   // ????????????
    private TCChatMsgListAdapter msgAdapter;
    private String mRoomID;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private TCFrequeControl mLikeFrequeControl;    //??????????????????
    private int mZanNum;    //??????????????????
    private TopUserHeadAdapter topHeadAdapter;
    private ZBUserListPop zbUserListPop;
    private Set<String> commentSet = new HashSet();//??????????????????
    private Dialog selectLinkTypeDialog;
    private int linkType = 0;//???????????? 0-?????? 1-??????
    private ZBLinkUsersPop userLinkPopShow;
    private ZBLinkShowAdapter waitLinkAdapter;
    private ZBLinkShowAdapter linksShowAdapter;//??????????????????adapter
    private List<AnchorInfo> mPusherList = new ArrayList<>();            // ????????????????????????
    private boolean isLianMai;
    private boolean hasLinkMsg;//???????????????????????????
    private String waitLinkUserId;//1v1 ?????? ???????????????
    private Timer toLinkTimer;//?????????????????????
    private TimerTask toLinkTask;
    private QMUITipDialog waitLinkTip;//????????????????????????
    private QMUITipDialog showLinkTip;//????????????
    private int linkStatus;//0????????? 1????????? 2?????????????????????
    private boolean mPendingRequest;//???????????????????????????
    private YesOrNoDialog showCloseLinkDialog;//??????????????????
    private MoreLinkAdapter moreLinkAdapter;
    private List<ZBUserListBean> moreLinkList;
    private int setType;//????????????????????? 1?????????
    private LinkedHashMap<Integer, ZBUserListBean> posMap = new LinkedHashMap<>();//?????????????????????
    private int selectLinkPos;//????????????-????????????????????????????????????
    private Integer linkerPos;//???????????????????????????
    private Dialog moreLinkSettingDialog;
    private ZBSettingBean zbSettingBean;
    private SVGAImageView svgaImageView;
    private SVGAParser svgaParser;
    private int screenWidth;
    private int screenHeight;
    private ZBMorePop zbMorePop;
    private ZBUserInfoPop userInfoPop;
    private ZBGoodsListPop zbGoodsListPop;
    private String mMsg;
    private QMUITipDialog tipDialog;
    private int lastNetStatus = 1;//?????????????????????
    private YesOrNoDialog quitRoomDialog;
    private LiveHeaderInfo mLiveHeaderInfo;

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
        //???????????????
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //???????????????????????????
        Bundle bundle = getIntent().getExtras();
        mLiveInitInfo = (LiveInitInfo) bundle.getSerializable(Constant.LiveInitInfo);
        mLiveInitInfo.liveRoomType = Constant.REQ_ZB_TYPE_SP;//????????????
        mRoomID = Constant.getRoomId(mLiveInitInfo.liveRoomCode);
    }

    @Override
    public void initData() {
        super.initData();
        //????????????
        setStatusBarTransparent();

        screenWidth = MScreenUtil.getScreenWidth(this);
        screenHeight = MScreenUtil.getScreenHeight(this);

        //***??????????????????????????????????????????????????????
        TXCAudioSysRecord.getInstance().stop();
        TXCAudioUGCRecorder.getInstance().stopRecord();
        TXCAudioEngine.getInstance().stopLocalAudio();

        //??????LiveRoom??????
        mLiveRoom = MLVBLiveRoom.sharedInstance(getApplication());

        showDialog("?????????...");
        //????????????????????????????????????????????????????????????????????????????????????
        viewModel.toLoginRoom(mLiveRoom);

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
        initLinkMsgManger();
    }

    private TXCloudVideoView txCloudVideoView;

    private void startLinkLayout(AnchorInfo anchorInfo) {

        if (userLinkPopShow != null && userLinkPopShow.isShowing()) {
            userLinkPopShow.dismiss();
        }

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
            txCloudVideoView = binding.anchor2TxVideoView;

        } else {
            //?????????????????????????????????
            linkerPos = getMapKey(anchorInfo.userID);
            if (linkerPos != null) {
                moreLinkAdapter.getData().get(linkerPos).userId = anchorInfo.userID;
                moreLinkAdapter.getData().get(linkerPos).nickname = anchorInfo.userName;
                moreLinkAdapter.getData().get(linkerPos).avatarUrl = anchorInfo.userAvatar;
                moreLinkAdapter.notifyItemChanged(linkerPos);

                anchorInfo.micStatus = 1;
                anchorInfo.position = linkerPos;
            } else {
                return;
            }

            txCloudVideoView = (TXCloudVideoView) moreLinkAdapter.getViewByPosition(linkerPos, R.id.anchor_video_view);
        }


        //????????????????????????
        mLiveRoom.startRemoteView(linkType == 1 ? true : false, anchorInfo, txCloudVideoView, new IMLVBLiveRoomListener.PlayCallback() {
            @Override
            public void onBegin() {
                isLianMai = true;
                binding.btnLianmai.setBackgroundResource(R.mipmap.zbj_menu_lianmaiing);
                if (linkType == 0) {
                    //??????????????????????????????????????????dialog
                    mLiveRoom.sendRoomCustomMsg(String.valueOf(LiveConstants.IMCMD_ZB_LINKING), "", null);

                    zbSettingBean.liveRoomConnection = LiveConstants.LIVE_STATUS_DOUBLE_TALK;
                    viewModel.setZBStatus(zbSettingBean);
                } else {
                    //??????????????????????????????
                    viewModel.setVoiceMic(anchorInfo, mLiveInitInfo.liveRoomRecordId);
                }
            }

            @Override
            public void onError(int errCode, String errInfo) {
                mLiveRoom.kickoutJoinAnchor(anchorInfo.userID);
                //??????????????????
                stopLinkLayout(anchorInfo);
            }

            @Override
            public void onEvent(int event, Bundle param) {

            }
        });

    }

    //??????????????????
    private void stopLinkLayout(AnchorInfo anchorInfo) {
        //????????????????????????
        if (mPusherList != null) {
            Iterator<AnchorInfo> it = mPusherList.iterator();
            while (it.hasNext()) {
                AnchorInfo item = it.next();
                if (anchorInfo.userID.equalsIgnoreCase(item.userID)) {
                    it.remove();
                    break;
                }
            }
        }

        mLiveRoom.stopRemoteView(anchorInfo);//????????????????????????
        anchorInfo.micStatus = 0;

        //????????????????????????????????????
        viewModel.setVoiceMic(anchorInfo, mLiveInitInfo.liveRoomRecordId);

        if (linkType == 0) {
            isLianMai = false;
            binding.btnLianmai.setBackgroundResource(R.mipmap.zbj_menu_lianmai);

            //??????????????????
            binding.rlAnchor2.setVisibility(View.GONE);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            lp.setMargins(0, 0, 0, (int) getResources().getDimension(R.dimen.dp_54));
            binding.llVideo.setLayoutParams(lp);

            zbSettingBean.liveRoomConnection = LiveConstants.LIVE_STATUS_FREE;
            viewModel.setZBStatus(zbSettingBean);
        } else {
            Integer linkPos = getMapKey(anchorInfo.userID);
            if (linkPos != null) {
                moreLinkAdapter.getData().get(linkPos).nickname = "????????????";
                moreLinkAdapter.getData().get(linkPos).position = 0;
                moreLinkAdapter.getData().get(linkPos).hasProsody = false;
                moreLinkAdapter.getData().get(linkPos).avatarUrl = null;
                moreLinkAdapter.getData().get(linkPos).userId = null;
                moreLinkAdapter.notifyItemChanged(linkPos);
                //????????????????????????
                addPosMap(null, linkPos);
            }

            if (mPusherList.size() == 0) {
                isLianMai = false;
                binding.btnLianmai.setBackgroundResource(R.mipmap.zbj_menu_lianmai);
            }
        }

    }

    //?????????????????????
    private void initLiveInfo() {
        binding.tvZanNum.setText(UserInfoMgr.getInstance().getUserInfo().getNickname());
        Glide.with(this).load(mLiveInitInfo.avatarUrl).diskCacheStrategy(DiskCacheStrategy.ALL).into(binding.rivPhoto);


        //?????????????????????
        if (!mLiveInitInfo.liveRoomLevel.getCanSale()) {
            binding.btnGoods.setVisibility(View.GONE);
        }
        //?????????????????????
        if (!mLiveInitInfo.liveRoomLevel.getCanMic()) {
            binding.btnLianmai.setVisibility(View.GONE);
        }


        //????????????????????????
        if (mLiveInitInfo.shieldList != null && mLiveInitInfo.shieldList.size() > 0) {
            for (String s : mLiveInitInfo.shieldList) {
                pbWords.add(s);
            }
        }
        //??????????????????
        SensitiveWordsUtils.init(pbWords);

        topHeadAdapter = new TopUserHeadAdapter(R.layout.item_top_user_head);
        binding.rvAvatar.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
        binding.rvAvatar.setAdapter(topHeadAdapter);
        topHeadAdapter.setOnItemClickListener(((adapter, view, position) -> {
            userInfoPop = new ZBUserInfoPop(this, mLiveInitInfo, topHeadAdapter.getData().get(position).userId);
            userInfoPop.showPopupWindow();
        }));


        waitLinkAdapter = new ZBLinkShowAdapter(R.layout.item_zb_link_show);
        //????????????????????????
        linksShowAdapter = new ZBLinkShowAdapter(R.layout.item_zb_link_show);
        linksShowAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                if (isLianMai && linkType == 0) {
                    ToastUtil.toastShortMessage("???????????????~");
                    return;
                }

                if (view.getId() == R.id.ll_js) {
                    if (linkType == 0) {
                        handleSingleLinkMsg(position);
                    } else if (linkType == 1) {
                        handleMoreLinkMsg(linksShowAdapter.getData().get(position), position);
                    }
                }

            }
        });

        //??????????????????????????????
        moreLinkAdapter = new MoreLinkAdapter(R.layout.item_more_link);
        //???????????? ????????????
        binding.rvMoreLink.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        binding.rvMoreLink.setAdapter(moreLinkAdapter);
        initMoreLinkData();

        moreLinkAdapter.setOnItemClickListener(((adapter, view, position) -> {
            //??????????????????????????????
            if (TextUtils.isEmpty(moreLinkAdapter.getData().get(position).userId)) {
                //?????????????????????????????????
                selectLinkPos = position;
                showUserLinkPopShow(1, 1);
            } else {
                //???????????????????????????
                showMoreLinkSettingPop(position);
            }
        }));

    }

    //????????????????????????
    private void handleSingleLinkMsg(int position) {
        //??????????????????????????????????????????
        mLiveRoom.sendC2CCustomMsg(linksShowAdapter.getData().get(position).userId, String.valueOf(LiveConstants.IMCMD_INVITE_LINK), "????????????", null);
        waitLinkUserId = linksShowAdapter.getData().get(position).userId;
        linksShowAdapter.remove(position);
        userLinkPopShow.dismiss();
        showWaitTip();
        //??????30S??????????????????
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //20S????????????????????????dialog
                if (linkStatus == 1) {
                    ToastUtils.showShort("???????????????");
                    disWaitTip();
                }
            }
        }, 1000 * 20);
    }

    //???????????????????????????
    private void initMoreLinkData() {
        moreLinkList = new ArrayList<>();
        posMap = new LinkedHashMap<>();
        for (int i = 0; i < 6; i++) {
            ZBUserListBean userInfoBean = new ZBUserListBean();
            userInfoBean.nickname = "????????????";
            moreLinkList.add(userInfoBean);
            //??????????????????
            addPosMap(null, i);
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
                    //?????????????????????
                    if (TextUtils.isEmpty(msgList.get(position).getUserId())) {
                        return;
                    }

                    if (msgList.get(position).getUserId().equals(UserInfoMgr.getInstance().getUserInfo().getId())) {
                        return;
                    }

                    //????????????
                    userInfoPop = new ZBUserInfoPop(LiveZhuboActivity.this, mLiveInitInfo, msgList.get(position).getUserId());
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

    private void startPublish() {
        mLiveRoom.setListener(this);
        //??????????????????
        mLiveRoom.startLocalPreview(!mLiveInitInfo.isBackCamera, binding.mTxVideoView);

        //????????????-?????????????????????????????????
        mLiveRoom.getBeautyManager().setBeautyStyle(mLiveInitInfo.beautyStyle);
        mLiveRoom.getBeautyManager().setBeautyLevel(mLiveInitInfo.beautyLevel);
        mLiveRoom.getBeautyManager().setWhitenessLevel(mLiveInitInfo.whitenessLevel);
        mLiveRoom.getBeautyManager().setRuddyLevel(mLiveInitInfo.ruddinessLevel);
        //????????????
        if (mLiveInitInfo.filterStyle != null) {
            BeautyImpl beauty = new BeautyImpl(this);
            Bitmap bitmap = beauty.decodeFilterResource(mLiveInitInfo.filterStyle);
            mLiveRoom.getBeautyManager().setFilter(bitmap);
            mLiveRoom.getBeautyManager().setFilterStrength(mLiveInitInfo.filterStyle.getItemLevel() / 10.0f);
        }


        //????????????
        mLiveRoom.setCameraMuteImage(R.drawable.sp_leave_bg);
        //???????????????
        mLiveRoom.createRoom(Constant.getRoomId(mLiveInitInfo.liveRoomCode), "", new IMLVBLiveRoomListener.CreateRoomCallback() {
            @Override
            public void onSuccess(String roomId) {
                isPlaying = true;
                LogUtils.v(Constant.TAG_LIVE, "?????????????????????");
                //?????????????????????????????????
                mLiveInitInfo.pushUrl = mLiveRoom.getPushUrl();
                //???????????????????????????????????????????????????????????????????????????
                showDialog("???????????????...");
                if (TextUtils.isEmpty(mLiveInitInfo.liveRoomRecordId)) {
                    viewModel.startLive(mLiveInitInfo);
                } else {
                    viewModel.reStartLive(mLiveInitInfo.liveRoomRecordId);
                }
                //???????????????????????????????????????????????????????????????
                if (mLiveInitInfo.isMirror) {
                    mLiveRoom.setMirror(true);
                }
            }


            @Override
            public void onError(int errCode, String e) {
                dismissDialog();

                //?????????
                if (errCode != -1307) {
                    ToastUtil.toastShortMessage("???????????????????????????????????????");
                } else {
                    //????????????
                    Bundle bundle = new Bundle();
                    bundle.putString("liveRoomRecordId", mLiveInitInfo.liveRoomRecordId);
                    bundle.putString("liveRoomId", mLiveInitInfo.liveRoomId);
                    startActivity(ZhiboOverActivity.class, bundle);
                }
                finish();
                Log.v(Constant.TAG_LIVE, "????????????????????????" + errCode);
                Log.v(Constant.TAG_LIVE, "????????????????????????" + e);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isPlaying) {
            Activity activity = AppManager.getAppManager().currentActivity();
            //????????????????????????????????????
            if (activity.getLocalClassName().contains("LiveZhuboActivity")) {
                mLiveRoom.setPusher(true);
            }

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isPlaying) {
            mLiveRoom.setPusher(false);
        }
    }

    private boolean isPlaying;//?????????

    private void stopPublish() {
        mLiveRoom.exitRoom(new ExitRoomCallback() {
            @Override
            public void onSuccess() {
                isPlaying = false;
                dismissDialog();
                mLiveRoom.setListener(null);
                LogUtils.v(Constant.TAG_LIVE, "?????????????????????");

                if (viewModel.exitSuccess.getValue().booleanValue()) {
                    Bundle bundle = new Bundle();
                    bundle.putString("liveRoomRecordId", mLiveInitInfo.liveRoomRecordId);
                    bundle.putString("liveRoomId", mLiveInitInfo.liveRoomId);
                    startActivity(ZhiboOverActivity.class, bundle);
                }

                finish();
            }

            @Override
            public void onError(int errCode, String e) {
                dismissDialog();
                finish();
                LogUtils.v(Constant.TAG_LIVE, "?????????????????????:" + errCode);
                LogUtils.v(Constant.TAG_LIVE, "?????????????????????:" + e);
            }
        });
    }

    //???????????????????????????????????????
    private void selectLinkTypePop() {
        if (selectLinkTypeDialog != null) {
            selectLinkTypeDialog.show();
            return;
        }

        selectLinkTypeDialog = new Dialog(this, R.style.CustomerDialog);
        //????????????????????????
        View view = LayoutInflater.from(this).inflate(R.layout.layout_zb_link_type_pop, null);
        //???????????????
        RelativeLayout rlOne = (RelativeLayout) view.findViewById(R.id.rl_one);
        RelativeLayout rlMore = (RelativeLayout) view.findViewById(R.id.rl_more);
        rlOne.setOnClickListener(lis -> {
            selectLinkTypeDialog.dismiss();
            linkType = 0;
            showUserLinkPopShow(0, 1);
        });
        rlMore.setOnClickListener(lis -> {
            //??????????????????????????????
            //1.????????????????????? 2.?????????????????? 3.????????????????????? 4.????????????????????????
            //???????????????????????????
            setType = LiveConstants.ZBJ_SET_KQLTS;
            zbSettingBean.liveRoomConnection = LiveConstants.LIVE_STATUS_CHAT_ROOM;
            viewModel.setZBStatus(zbSettingBean);
        });

        //???????????????dismiss
        selectLinkTypeDialog.setCancelable(true);
        //??????????????????Dialog
        selectLinkTypeDialog.setContentView(view);
        //????????????Activity???????????????
        Window dialogWindow = selectLinkTypeDialog.getWindow();
        //??????Dialog?????????????????????
        dialogWindow.setGravity(Gravity.BOTTOM);
        //??????????????????
        dialogWindow.setWindowAnimations(R.style.DialogBottomAnimation);
        //?????????????????????
        WindowManager.LayoutParams params = dialogWindow.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;//?????????????????????
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;//?????????????????????
        dialogWindow.setAttributes(params);
        selectLinkTypeDialog.show();

    }

    //?????????????????????????????????
    private void showUserLinkPopShow(int openType, int showType) {
        mLiveInitInfo.linkType = linkType;
        userLinkPopShow = new ZBLinkUsersPop(mLiveInitInfo, openType, this, 0, linksShowAdapter, mPusherList, isLianMai);
        userLinkPopShow.showPopupWindow();
    }

    //??????????????????
    private void showMoreLinkSettingPop(int postion) {
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

        if (voiceMicMute) {
            tvJinYin.setText("????????????");
        } else {
            tvJinYin.setText("??????");
        }

        tvName.setText("???@ " + nickName + " ????????????");
        tvCancel.setOnClickListener(lis -> {
            moreLinkSettingDialog.dismiss();
        });
        tvDKLM.setOnClickListener(lis -> {
            //??????????????????
            //??????????????????
            AnchorInfo anchorInfo = new AnchorInfo();
            anchorInfo.userID = userId;
            anchorInfo.micStatus = 0;
            viewModel.setVoiceMic(anchorInfo, mLiveInitInfo.liveRoomRecordId);
            moreLinkSettingDialog.dismiss();
        });
        tvJinYin.setOnClickListener(lis -> {
            if (voiceMicMute) {
                //?????????????????? ???????????????????????????
                mLiveRoom.sendC2CCustomMsg(userId, String.valueOf(LiveConstants.IMCMD_MORE_ANCHOR_QXJY), "????????????", null);
            } else {
                //??????????????????
                mLiveRoom.sendC2CCustomMsg(userId, String.valueOf(LiveConstants.IMCMD_MORE_ANCHOR_JY), "????????????", null);
            }
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

    private Set<String> pbWords = new HashSet<>();

    //??????????????????
    private void toSendTextMsg(String msg) {
        //??????????????????????????????
        if (SensitiveWordsUtils.contains(msg)) {
            msg = SensitiveWordsUtils.replaceSensitiveWord(msg, "***");
        }


        TCChatEntity entity = new TCChatEntity();
        entity.setSenderName("??? ");
        entity.setContent(msg);
        entity.setLevelIcon(UserInfoMgr.getInstance().getUserInfo().getLevelIcon());
        entity.setLevelName(UserInfoMgr.getInstance().getUserInfo().getLevelName());
        entity.setType(LiveConstants.IMCMD_TEXT_MSG);
        notifyMsg(entity);
        mLiveRoom.sendRoomCustomMsg(String.valueOf(LiveConstants.IMCMD_TEXT_MSG), msg, null);

    }

    //????????????????????????
    public void toRecvTextMsg(TCUserInfo userInfo, String text, int type) {
        TCChatEntity entity = new TCChatEntity();
        if (TextUtils.isEmpty(userInfo.nickname)) {
            entity.setSenderName(LiveConstants.NIKENAME + userInfo.userid);
        } else {
            entity.setSenderName(userInfo.nickname);
        }
        text = SensitiveWordsUtils.replaceSensitiveWord(text, "***");
        entity.setUserId(userInfo.userid);
        entity.setLevelName(userInfo.leaveName);
        entity.setContent(text);
        entity.setType(type);

        notifyMsg(entity);
    }

    //??????????????????
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
        RxBus.getDefault().post(new EventBean(CodeTable.CODE_SUCCESS, "overLive-zb"));
        LogUtils.v(Constant.TAG_LIVE, "????????????");
        finish();
    }

    //????????????
    @Override
    public void onAnchorEnter(AnchorInfo anchorInfo) {
        disLinkTip();

        if (mPusherList != null) {
            boolean exist = false;
            for (AnchorInfo item : mPusherList) {
                if (anchorInfo.userID.equalsIgnoreCase(item.userID)) {
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                mPusherList.add(anchorInfo);
            }
        }
        startLinkLayout(anchorInfo);
        //??????????????????????????????????????????
        removeMoreLinkList(anchorInfo.userID);
    }

    //????????????
    @Override
    public void onAnchorExit(AnchorInfo anchorInfo) {
        stopLinkLayout(anchorInfo);
    }

    @Override
    public void onAudienceEnter(AudienceInfo audienceInfo) {
        //?????????????????? ????????????
    }

    @Override
    public void onAudienceExit(AudienceInfo audienceInfo) {
        //?????????????????? ????????????
    }

    @Override
    public void onRequestJoinAnchor(AnchorInfo anchorInfo, String reason) {
        boolean hasUser = false;
        if (linkType == 0) {
            //????????????????????????????????????????????????????????????
            if (!anchorInfo.userID.equals(waitLinkUserId)) {
                mLiveRoom.responseJoinAnchor(anchorInfo.userID, false, "??????????????????????????????????????????????????????");
                return;
            }
        } else if (linkType == 1) {
            //????????????????????????????????????
            for (ZBUserListBean bean : posMap.values()) {
                if (bean != null && bean.userId.equals(anchorInfo.userID)) {
                    hasUser = true;
                }
            }
        }

        //?????????????????????
        //        if (mPendingRequest) {
        //            mLiveRoom.responseJoinAnchor(anchorInfo.userID, false, "??????????????????????????????????????????????????????");
        //            return;
        //        }
        if (mPusherList.size() > 6) {
            mLiveRoom.responseJoinAnchor(anchorInfo.userID, false, "???????????????????????????????????????");
            return;
        }
        mLiveRoom.responseJoinAnchor(anchorInfo.userID, true, "");

        disWaitTip();
        if (!hasUser) {
            showLinkTip();
        }

        mPendingRequest = true;
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
    public void onRecvRoomCustomMsg(String roomID, String userID, String userName, String userAvatar, String cmd, Object message, String userLevel, String userLevelIcon) {
        if (!roomID.equals(mRoomID)) {
            return;
        }
        TCUserInfo userInfo = new TCUserInfo(userID, userName, userAvatar, userLevel, userLevelIcon);
        int type = Integer.parseInt(cmd);
        switch (type) {
            case LiveConstants.IMCMD_TEXT_MSG:
                //??????????????????
                commentSet.add(userName);
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
                //??????????????????
                toRushLiveInfo();
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
            case LiveConstants.IMCMD_FOLLOW://??????????????????
                toRecvTextMsg(userInfo, LiveConstants.SHOW_FOLLOW, type);
                break;
            case LiveConstants.IMCMD_TO_LINK://??????????????????????????????
                SendUserLinkBean userLinkBean = new Gson().fromJson((String) message, SendUserLinkBean.class);
                ZBUserListBean zbUserListBean = new ZBUserListBean();
                zbUserListBean.position = Integer.parseInt(TextUtils.isEmpty(userLinkBean.position) ? "0" : userLinkBean.position);
                zbUserListBean.userId = userLinkBean.userId;
                zbUserListBean.nickname = userLinkBean.userName;
                zbUserListBean.avatarUrl = userLinkBean.userHeadImageUrl;
                zbUserListBean.showTime = LiveConstants.TO_LINK_TIME;

                addLinkMsg(zbUserListBean, userLinkBean.userId);
            case LiveConstants.IMCMD_RESH_MORELINK_INFO://???????????????
                viewModel.findMicUsers(mLiveInitInfo.liveRoomRecordId);
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
            default:
                break;
        }
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
            showAnim2 = AnimUtils.getAnimation(LiveZhuboActivity.this, R.anim.anim_slice_in_left);
            binding.rlGift2.setVisibility(View.VISIBLE);
            Glide.with(LiveZhuboActivity.this).load(sendGiftBean2.sendUserPhoto).into(binding.ivUserHeadPic2);
            Glide.with(LiveZhuboActivity.this).load(sendGiftBean2.giftsIcon).into(binding.ivGift2);
            binding.tvAudienceName2.setText(sendGiftBean2.sendUserName);
            binding.tvGiftName2.setText("???" + sendGiftBean2.giftsName);
            binding.rlGift2.clearAnimation();
            binding.rlGift2.setAnimation(showAnim2);
        } else {
            Animation bigAnim = AnimUtils.getAnimation(LiveZhuboActivity.this, R.anim.zbj_gift_num);
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

                        goneAnim2 = AnimUtils.getAnimation(LiveZhuboActivity.this, R.anim.anim_slice_out_left);
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
            showAnim1 = AnimUtils.getAnimation(LiveZhuboActivity.this, R.anim.anim_slice_in_left);
            binding.rlGift.setVisibility(View.VISIBLE);
            Glide.with(LiveZhuboActivity.this).load(sendGiftBean1.sendUserPhoto).into(binding.ivUserHeadPic);
            Glide.with(LiveZhuboActivity.this).load(sendGiftBean1.giftsIcon).into(binding.ivGift);
            binding.tvAudienceName.setText(sendGiftBean1.sendUserName);
            binding.tvGiftName.setText("???" + sendGiftBean1.giftsName);

            binding.rlGift.clearAnimation();
            binding.rlGift.setAnimation(showAnim1);
        } else {
            Animation bigAnim = AnimUtils.getAnimation(LiveZhuboActivity.this, R.anim.zbj_gift_num);
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
                        goneAnim1 = AnimUtils.getAnimation(LiveZhuboActivity.this, R.anim.anim_slice_out_left);
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

    @Override
    public void onRecvC2CCustomMsg(String senderId, String cmd, String message, String userName, String headPic) {
        int type = Integer.parseInt(cmd);
        switch (type) {
            case LiveConstants.IMCMD_REFUSE_LINK://??????????????????????????????
                ToastUtil.toastShortMessage("???????????????????????????");
                if (linkType == 1) {
                    //???????????????????????????????????????
                    addPosMap(null, getMapKey(senderId));
                    //?????????????????????
                    removeMoreLinkList(senderId);
                }
                disWaitTip();
                break;
            case LiveConstants.IMCMD_CANCEL_LINK://????????????????????????
                disWaitTip();
                //adapter?????????
                int temp = -1;
                for (int i = 0; i < linksShowAdapter.getData().size(); i++) {
                    if (linksShowAdapter.getData().get(i).userId.equals(senderId)) {
                        temp = i;
                    }
                }
                if (temp >= 0) {
                    linksShowAdapter.removeAt(temp);
                    updateLinkNum();
                }

                break;
            case LiveConstants.IMCMD_MORE_LINK_NUM://???????????????????????????????????????
                //?????????????????????????????????????????????
                int linkPos = Integer.parseInt(message);


                ZBUserListBean bean = new ZBUserListBean();
                bean.avatarUrl = headPic;
                bean.nickname = userName;
                bean.userId = senderId;
                bean.position = linkPos;
                handleMoreLinkMsg(bean);
                break;

        }
    }

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

    //????????????????????????
    private void addLinkMsg(ZBUserListBean bean, String userId) {
        //????????????????????????
        boolean hasUser = false;
        for (int i = 0; i < linksShowAdapter.getData().size(); i++) {
            if (linksShowAdapter.getData().get(i).userId.equals(userId)) {
                hasUser = true;
            }
        }
        if (!hasUser) {
            //???adapter??????????????????
            linksShowAdapter.addData(bean);
        }
        String num;
        if (linksShowAdapter.getData().size() > 99) {
            num = "99";
        } else {
            num = String.valueOf(linksShowAdapter.getData().size());
        }
        //???????????????????????????
        binding.tvLinkNum.setVisibility(View.VISIBLE);
        binding.tvLinkNum.setText(num);

        hasLinkMsg = true;
    }

    //???????????????????????????
    private void handleMoreLinkMsg(ZBUserListBean bean, int position) {
        linksShowAdapter.remove(position);
        userLinkPopShow.dismiss();
        handleMoreLinkMsg(bean);
    }

    //???????????????????????????
    private void handleMoreLinkMsg(ZBUserListBean bean) {

        int linkPos = bean.position;
        //-1????????????????????????????????????????????????
        if (linkPos == -1) {
            computeEmptyPos(bean);
        } else {

            //????????????????????????????????????
            if (getPosMapBean(linkPos) == null) {
                addPosMap(bean, linkPos);
                //??????????????????????????????????????????
                mLiveRoom.sendC2CCustomMsg(bean.userId, String.valueOf(LiveConstants.IMCMD_MORE_LINK_JS), String.valueOf(linkPos), null);
                addWaitMoreLinkList(bean);
            } else {
                //???????????????????????????????????????????????????????????????
                if ((getPosMapBean(linkPos)).userId.equals(bean.userId)) {
                    addPosMap(bean, linkPos);
                    //??????????????????????????????????????????
                    mLiveRoom.sendC2CCustomMsg(bean.userId, String.valueOf(LiveConstants.IMCMD_MORE_LINK_JS), String.valueOf(linkPos), null);
                    addWaitMoreLinkList(bean);
                } else {//??????????????????
                    computeEmptyPos(bean);
                }
            }
        }
    }

    //????????????
    private void computeEmptyPos(ZBUserListBean bean) {
        Integer tempKey = getEmptyPos();

        if (tempKey != null) {
            addPosMap(bean, tempKey);
            //??????????????????????????????????????????
            mLiveRoom.sendC2CCustomMsg(bean.userId, String.valueOf(LiveConstants.IMCMD_MORE_LINK_JS), String.valueOf(tempKey), null);
        } else {
            //????????????????????????
            mLiveRoom.sendC2CCustomMsg(bean.userId, String.valueOf(LiveConstants.IMCMD_MORE_LINK_JS), String.valueOf(-1), null);
        }
    }

    //?????????
    private final ReadWriteLock rwlock = new ReentrantReadWriteLock();
    private final Lock rlock = rwlock.readLock();
    private final Lock wlock = rwlock.writeLock();

    //???????????????
    private ZBUserListBean getPosMapBean(int linkPos) {
        rlock.lock();
        try {
            return posMap.get(linkPos);
        } finally {
            rlock.unlock();
        }
    }

    //???????????????
    private void addPosMap(ZBUserListBean bean, Integer tempKey) {
        wlock.lock();
        try {
            posMap.put(tempKey, bean);
        } finally {
            wlock.unlock();
        }
    }

    //?????????????????????????????????
    private Integer getEmptyPos() {
        Integer emptyPos = null;
        for (Integer key : posMap.keySet()) {
            if (getPosMapBean(key) == null) {
                LogUtils.v(Constant.TAG_LIVE, "?????????" + key.toString());
                return key;
            }
        }
        return emptyPos;
    }

    //?????????????????????
    private Integer getMapKey(String value) {
        Integer key = null;
        for (Integer getKey : posMap.keySet()) {
            if (getKey != null) {
                if (getPosMapBean(getKey) != null) {
                    ZBUserListBean bean = getPosMapBean(getKey);
                    if (bean.userId.equals(value)) {
                        return getKey;
                    }
                }
            }


        }
        return key;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null) {
            disposable.dispose();
        }
        if (isPlaying) {
            viewModel.closeLive(mLiveInitInfo.liveRoomRecordId, String.valueOf(commentSet.size()));
            stopPublish();
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

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
        //????????????
        binding.btnBack.setOnClickListener(this);
        binding.tvMsg.setOnClickListener(this);
        binding.btnLianmai.setOnClickListener(this);
        binding.btnGoods.setOnClickListener(this);
        binding.tvMembersNum.setOnClickListener(this);
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
                quitRoomDialog = new YesOrNoDialog(this);
                try {
                    if (mLiveHeaderInfo != null && !TextUtils.isEmpty(mLiveHeaderInfo.totalPeopleNum)) {
                        if (Integer.parseInt(mLiveHeaderInfo.totalPeopleNum) > 0) {
                            quitRoomDialog.setTitleText("????????????????????????????????????");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                quitRoomDialog.setMessageText("????????????????????????");
                quitRoomDialog.setRightBtnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //????????????
                        showDialog("????????????...");
                        //?????????????????????????????????
                        viewModel.closeLive(mLiveInitInfo.liveRoomRecordId, String.valueOf(commentSet.size()));

                        quitRoomDialog.dismissDialog();

                    }
                });
                quitRoomDialog.showDialog();

                break;
            case R.id.tv_msg:
                Bundle bundle = new Bundle();
                bundle.putString("content", mMsg);
                startActivity(ZBEditTextDialogActivity.class, bundle);
                break;
            case R.id.btn_lianmai:
                if (isLianMai || hasLinkMsg || linkType == 1) {
                    //???????????????
                    showUserLinkPopShow(0, hasLinkMsg ? 0 : 1);
                } else {
                    //??????????????????????????????????????????????????????pop
                    selectLinkTypePop();
                }
                break;
            case R.id.btn_more:
                zbMorePop = new ZBMorePop(this, mLiveRoom, mLiveInitInfo);
                zbMorePop.showPopupWindow();
                zbMorePop.setHandler(mHandler);
                break;
            case R.id.btn_goods:
                zbGoodsListPop = new ZBGoodsListPop(this, mLiveInitInfo.liveRoomId, 1);
                zbGoodsListPop.showPopupWindow();
                break;
        }
    }

    //?????????????????? 15S??????????????????????????????
    private void addWaitMoreLinkList(ZBUserListBean zbUserListBean) {
        //15S??????????????????????????????
        zbUserListBean.showTime = LiveConstants.WAIT_MORE_LINK;

        //????????????????????????
        boolean hasUser = false;
        for (int i = 0; i < waitLinkAdapter.getData().size(); i++) {
            if (waitLinkAdapter.getData().get(i).userId.equals(zbUserListBean.userId)) {
                hasUser = true;
            }
        }
        if (!hasUser) {
            //???????????????????????????handler??????????????????
            //???adapter??????????????????
            waitLinkAdapter.addData(zbUserListBean);
        }
    }

    //?????????????????????
    private void removeMoreLinkList(String userId) {
        Integer temp = null;
        for (int i = 0; i < waitLinkAdapter.getData().size(); i++) {
            if (waitLinkAdapter.getData().get(i).userId.equals(userId)) {
                temp = i;
            }
        }
        if (temp != null) {
            waitLinkAdapter.remove(temp);
        }
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        disposable = RxBus.getDefault().toObservable(EventBean.class).subscribe(eventBean -> {
            switch (eventBean.msgId) {
                case CodeTable.IM_LOGIN_SUCCESS:
                    if (!isPlaying) {
                        showDialog("???????????????...");
                        //????????????
                        startPublish();
                    }
                    break;

                case Constant.NET_SPEED:
                    if (eventBean.msgType == 1) {
                        binding.tvNet.setText("????????????");
                        binding.ivNet.setBackground(getResources().getDrawable(R.mipmap.wangluolh));
                        if (tipDialog != null && tipDialog.isShowing()) {
                            tipDialog.dismiss();
                        }
                        if (lastNetStatus != eventBean.msgType) {
                            tipDialog = new QMUITipDialog.Builder(getContext())
                                    .setTipWord("????????????")
                                    .create();
                            tipDialog.show();
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    tipDialog.dismiss();
                                }
                            }, 1000);
                        }
                    } else if (eventBean.msgType == 2) {
                        if (tipDialog != null && tipDialog.isShowing()) {
                            tipDialog.dismiss();
                        }
                        binding.tvNet.setText("????????????");
                        binding.ivNet.setBackground(getResources().getDrawable(R.mipmap.wangluoyb));
                    } else if (eventBean.msgType == 3) {
                        if (tipDialog != null && tipDialog.isShowing()) {
                            tipDialog.dismiss();
                        }
                        binding.tvNet.setText("????????????");
                        binding.ivNet.setBackground(getResources().getDrawable(R.mipmap.wangluokd));
                        if (lastNetStatus != eventBean.msgType) {
                            tipDialog = new QMUITipDialog.Builder(getContext())
                                    .setTipWord("??????????????????")
                                    .create();
                            tipDialog.show();
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    tipDialog.dismiss();
                                }
                            }, 1000);
                        }
                    } else if (eventBean.msgType == 4) {
                        if (tipDialog != null && tipDialog.isShowing()) {
                            tipDialog.dismiss();
                        }
                        tipDialog = new QMUITipDialog.Builder(getContext())
                                .setTipWord("????????????????????????????????????")
                                .create();
                        tipDialog.show();
                    }
                    lastNetStatus = eventBean.msgType;
                    break;
                case LiveConstants.SEND_MSG:
                    //??????????????????
                    toSendTextMsg(eventBean.content.trim());
                    break;
                case LiveConstants.ZB_USER_SET:
                    if (eventBean.msgType == LiveConstants.SETTING_JINYAN) {      //??????-????????????
                        if (eventBean.status == 1) {
                            //??????
                            mLiveRoom.sendC2CCustomMsg(eventBean.content, String.valueOf(LiveConstants.IMCMD_FORBIDDER_TALK), LiveConstants.SHOW_JINYAN, null);
                        } else {
                            //????????????
                            mLiveRoom.sendC2CCustomMsg(eventBean.content, String.valueOf(LiveConstants.IMCMD_CANCEL_FORBIDDER_TALK), LiveConstants.SHOW_QXJINYAN, null);
                        }
                    } else if (eventBean.msgType == LiveConstants.SETTING_LAHEI) {      //??????
                        if (userInfoPop != null && userInfoPop.isShowing()) {
                            userInfoPop.dismiss();
                        }
                        mLiveRoom.sendC2CCustomMsg(eventBean.content, String.valueOf(LiveConstants.IMCMD_PUT_BLACK), LiveConstants.SHOW_LAHEI, new IMLVBLiveRoomListener.SendC2CCustomMsgCallback() {

                            @Override
                            public void onError(int errCode, String errInfo) {

                            }

                            @Override
                            public void onSuccess() {
                                toRushLiveInfo();
                                //??????????????????-??????
                                String jsonMsg = JsonMsgBean.json("0", eventBean.nickname, LiveConstants.GONGGAO_TICHU);
                                mLiveRoom.sendRoomCustomMsg(String.valueOf(LiveConstants.IMCMD_GONGGAO_MSG), jsonMsg, null);
                                LogUtils.v(Constant.TAG_LIVE, "????????????");
                            }
                        });
                    } else if (eventBean.msgType == LiveConstants.SETTING_TICHU) {      //??????
                        if (userInfoPop != null && userInfoPop.isShowing()) {
                            userInfoPop.dismiss();
                        }
                        mLiveRoom.sendC2CCustomMsg(eventBean.content, String.valueOf(LiveConstants.IMCMD_PUT_BLACK), LiveConstants.SHOW_TICHU, new IMLVBLiveRoomListener.SendC2CCustomMsgCallback() {

                            @Override
                            public void onError(int errCode, String errInfo) {

                            }

                            @Override
                            public void onSuccess() {
                                toRushLiveInfo();
                                //??????????????????-??????
                                String jsonMsg = JsonMsgBean.json("1", eventBean.nickname, LiveConstants.GONGGAO_TICHU);
                                mLiveRoom.sendRoomCustomMsg(String.valueOf(LiveConstants.IMCMD_GONGGAO_MSG), jsonMsg, null);
                            }
                        });
                    }
                    break;
                case LiveConstants.ZB_LINK_YQ:
                    if (isLianMai && linkType == 0) {
                        ToastUtil.toastShortMessage("???????????????~");
                        return;
                    }
                    ZBUserListBean zbUserListBean = (ZBUserListBean) eventBean.object;

                    //1v1????????????
                    if (linkType == 0) {
                        //?????????????????????????????????????????????
                        mLiveRoom.sendC2CCustomMsg(zbUserListBean.userId, String.valueOf(LiveConstants.IMCMD_INVITE_LINK), "????????????", null);
                    } else {//????????????
                        Integer tempKey;
                        //????????????????????????
                        if (eventBean.status == 1) {
                            tempKey = selectLinkPos;
                            //??????????????????
                            if (getPosMapBean(tempKey) != null) {
                                tempKey = getEmptyPos();
                            }
                        } else {//???????????????
                            tempKey = getEmptyPos();
                        }
                        if (tempKey == null) {
                            ToastUtil.toastShortMessage("???????????????");
                            return;
                        }
                        addPosMap(zbUserListBean, tempKey);
                        //?????????????????????????????????????????????????????????
                        addWaitMoreLinkList(zbUserListBean);
                        //????????????????????? ??????????????????
                        mLiveRoom.sendC2CCustomMsg(zbUserListBean.userId, String.valueOf(LiveConstants.IMCMD_MORE_LINK_YQ), String.valueOf(tempKey), null);
                    }

                    waitLinkUserId = zbUserListBean.userId;
                    showWaitTip();
                    if (toLinkTimer != null) {
                        toLinkTimer.cancel();
                        toLinkTimer.purge();
                    }
                    toLinkTimer = new Timer();
                    if (toLinkTask != null) {
                        toLinkTask.cancel();
                    }
                    toLinkTask = new TimerTask() {
                        @Override
                        public void run() {
                            //????????????????????????
                            if (linkStatus == 1) {
                                ToastUtil.toastShortMessage("???????????????");
                                disWaitTip();
                            }

                        }
                    };
                    toLinkTimer.schedule(toLinkTask, 11000);//11???????????? ??????????????????1S
                    break;
                case LiveConstants.ZB_LINK_GB:
                    //????????????
                    if (isLianMai || linkType == 1) {
                        showCloseLinkDialog();
                    }
                    break;
                case LiveConstants.ZBJ_SET_SUCCESS://??????????????????
                    ZBSettingBean zbSettingBean = (ZBSettingBean) eventBean.data;
                    if (linkType == 0) {
                        mLiveInitInfo.onlyFansMic = zbSettingBean.chatStatusManageDto.onlyFansMic;
                        mLiveInitInfo.userApplyMic = zbSettingBean.chatStatusManageDto.userApplyMic;
                        mLiveInitInfo.onlyInviteMic = zbSettingBean.chatStatusManageDto.onlyInviteMic;
                    } else if (linkType == 1) {
                        mLiveInitInfo.chatRoomOnlyFansMic = zbSettingBean.chatStatusManageDto.onlyFansMic;
                        mLiveInitInfo.chatRoomUserApplyMic = zbSettingBean.chatStatusManageDto.userApplyMic;
                    }
                    //????????????????????????????????????
                    mLiveRoom.sendRoomCustomMsg(String.valueOf(LiveConstants.IMCMD_RESH_HOME_INFO), "", null);
                    break;
                case LiveConstants.ZBJ_MORE_SETTING://???????????? 1-???????????? 2???????????? 3???????????? 4???????????? 5???????????? 6????????????
                    moreSetting(eventBean);
                    break;
                case LiveConstants.SHOW_ET:
                    //????????????????????????
                    int height = eventBean.msgType;
                    ViewGroup.LayoutParams params = binding.view.getLayoutParams();
                    params.height = height;
                    binding.view.setLayoutParams(params);
                    break;
                case LiveConstants.DISMISS_ET:
                    //????????????????????????
                    ViewGroup.LayoutParams params1 = binding.view.getLayoutParams();
                    params1.height = 0;
                    binding.view.setLayoutParams(params1);

                    mMsg = eventBean.content;
                    if (!TextUtils.isEmpty(mMsg)) {
                        binding.tvMsg.setText(mMsg);
                        FaceManager.handlerEmojiText(binding.tvMsg, mMsg, false);
                    } else {
                        binding.tvMsg.setText("???????????????");
                    }
                    break;
                case LiveConstants.IMCMD_SHOW_GOODS://??????????????????
                    mLiveRoom.sendRoomCustomMsg(String.valueOf(LiveConstants.IMCMD_SHOW_GOODS), eventBean.content, null);
                    break;
                case LiveConstants.IMCMD_SHOW_GOODS_CANCEL://????????????????????????
                    mLiveRoom.sendRoomCustomMsg(String.valueOf(LiveConstants.IMCMD_SHOW_GOODS_CANCEL), "", null);
                    break;
                case LiveConstants.IMCMD_BLOCK_WORD_ADD:
                    //????????????????????????????????????
                    mLiveRoom.sendRoomCustomMsg(String.valueOf(LiveConstants.IMCMD_BLOCK_WORD_ADD), eventBean.content, null);
                    //????????????
                    pbWords.add(eventBean.content);
                    SensitiveWordsUtils.init(pbWords);
                    break;
                case LiveConstants.IMCMD_BLOCK_WORD_DEL:
                    mLiveRoom.sendRoomCustomMsg(String.valueOf(LiveConstants.IMCMD_BLOCK_WORD_DEL), eventBean.content, null);
                    pbWords.remove(eventBean.content);
                    SensitiveWordsUtils.init(pbWords);
                    break;
                case LiveConstants.SETTING_DES:
                    mLiveInitInfo.setHasIntroduce(eventBean.doIt);
                    break;
                default:
                    break;
            }
        });
        RxSubscriptions.add(disposable);

        viewModel.startError.observe(this, startError -> {
            if (startError != null) {
                if (startError) {
                    ToastUtil.toastShortMessage("???????????????????????????????????????");
                    finish();
                }
            }
        });
        viewModel.loginRoomSuccess.observe(this, isSuccess -> {
            showDialog("???????????????...");
            //????????????
            startPublish();
        });
        viewModel.dismissDialog.observe(this, dismiss -> {
            if (dismiss != null) {
                if (dismiss) {
                    dismissDialog();
                }
            }
        });
        //????????????-?????????????????????????????????
        viewModel.startLiveInfo.observe(this, liveInitInfo -> {
            if (liveInitInfo != null) {
                // ??????????????????????????????????????? ?????????????????????????????????????????????????????????
                // RxBus.getDefault().post(new EventBean(CodeTable.CODE_SUCCESS, "startLive"));
                if (!TextUtils.isEmpty(mLiveInitInfo.liveRoomRecordId)) {
                    //???????????????????????? ??????????????????????????????????????????????????????
                    mLiveRoom.sendRoomCustomMsg(String.valueOf(LiveConstants.IMCMD_ZB_COMEBACK), "", null);
                    if (!TextUtils.isEmpty(mLiveInitInfo.liveRoomConnection) && mLiveInitInfo.liveRoomConnection.equals("CHAT_ROOM")) {
                        //????????????????????????????????????????????????????????????????????????????????????
                        mLiveRoom.sendRoomCustomMsg(String.valueOf(LiveConstants.IMCMD_CLOSE_MORE_LINK), "", null);
                    }
                }

                mLiveInitInfo.liveRoomId = liveInitInfo.liveRoomId;
                mLiveInitInfo.liveRoomRecordId = liveInitInfo.liveRoomRecordId;
                zbSettingBean = new ZBSettingBean();
                //???????????????
                zbSettingBean.liveRoomId = mLiveInitInfo.liveRoomId;

                //??????????????????
                if (liveInitInfo.groupIds != null && liveInitInfo.groupIds.length > 0) {
                    for (String groupId : liveInitInfo.groupIds) {
                        //???????????????????????????
                        if (!Constant.getRoomId(mLiveInitInfo.liveRoomCode).equals(groupId)) {
                            //?????????????????????
                            mLiveRoom.exitGroup(groupId);
                        }

                    }
                }
            }
        });

        //????????????-?????????????????????-????????????????????????
        viewModel.exitSuccess.observe(this, exitSuccess -> {
            if (exitSuccess != null) {
                //????????????
                showDialog("????????????...");
                stopPublish();
            }
        });

        //?????????????????????????????????
        viewModel.liveHeaderInfo.observe(this, liveHeaderInfo -> {
            if (liveHeaderInfo != null) {
                mLiveHeaderInfo = liveHeaderInfo;
                //??????????????????
                if (TextUtils.isEmpty(liveHeaderInfo.totalZanNum)) {
                    binding.tvZanNum.setText(mLiveInitInfo.nickname);
                } else {
                    binding.tvZanNum.setText(liveHeaderInfo.totalZanNum);
                }
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

        viewModel.setSuccess.observe(this, setSuccess -> {
            if (setSuccess != null && setSuccess) {
                switch (setType) {
                    case LiveConstants.ZBJ_SET_KQLTS:
                        if (selectLinkTypeDialog != null && selectLinkTypeDialog.isShowing()) {
                            selectLinkTypeDialog.dismiss();
                        }
                        //??????????????????
                        linkType = 1;
                        startMoreLinkAnim();

                        //????????????
                        mLiveRoom.sendRoomCustomMsg(String.valueOf(LiveConstants.IMCMD_OPEN_MORE_LINK), "", null);
                        break;
                    case LiveConstants.ZBJ_SET_GBLTS:
                        if (userLinkPopShow != null && userLinkPopShow.isShowing()) {
                            userLinkPopShow.dismiss();
                        }
                        //??????????????????
                        if (mPusherList.size() > 0) {
                            //????????????????????????list???????????????
                            List<AnchorInfo> tempList = new ArrayList<>();
                            tempList.addAll(mPusherList);
                            for (AnchorInfo anchorInfo : tempList) {
                                //???????????????
                                mLiveRoom.kickoutJoinAnchor(anchorInfo.userID);
                                onAnchorExit(anchorInfo);
                            }
                        }

                        //??????????????????
                        linkType = 0;
                        closeMoreLinkAnim();

                        //??????????????????????????????
                        mLiveRoom.sendRoomCustomMsg(String.valueOf(LiveConstants.IMCMD_CLOSE_MORE_LINK), "", null);

                        //??????adapter??????
                        initMoreLinkData();
                        break;
                }
                setType = 0;
            }
        });

        viewModel.micAnchorInfo.observe(this, micAnchorInfo -> {
            if (micAnchorInfo != null) {
                //?????????????????????????????????
                //                boolean hasPos = false;
                //                for (ZBUserListBean bean : posMap.values()) {
                //                    if (bean != null && bean.userId.equals(micAnchorInfo.userID)) {
                //                        hasPos = true;
                //                    }
                //                }
                //                if (!hasPos) {
                //                    return;
                //                }
                //???????????? / ????????????
                if (micAnchorInfo.netStatus == 1 || micAnchorInfo.netStatus == 3) {
                    mLiveRoom.kickoutJoinAnchor(micAnchorInfo.userID);
                }

                //???????????? / ????????????
                if (micAnchorInfo.netStatus == 1 || micAnchorInfo.netStatus == 2) {
                    //??????????????????
                    mLiveRoom.sendRoomCustomMsg(String.valueOf(LiveConstants.IMCMD_RESH_MORELINK_INFO), "", null);
                    viewModel.findMicUsers(mLiveInitInfo.liveRoomRecordId);
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
                    moreLinkList.remove(bean.position);
                    moreLinkList.add(bean.position, bean);
                }

                moreLinkAdapter.setNewInstance(moreLinkList);
            }
        });
    }

    //????????????
    private void moreSetting(EventBean eventBean) {
        switch (eventBean.msgType) {
            case 1:
                mLiveRoom.sendRoomCustomMsg(String.valueOf(LiveConstants.IMCMD_SHOW_MIC), "", null);
                break;
            case 2:
                mLiveRoom.sendRoomCustomMsg(String.valueOf(LiveConstants.IMCMD_FORBIDDEN_MIC), "", null);
                break;
            case 3:
                mLiveRoom.sendRoomCustomMsg(String.valueOf(LiveConstants.IMCMD_SETTING_PL), "1", null);
                break;
            case 4:
                mLiveRoom.sendRoomCustomMsg(String.valueOf(LiveConstants.IMCMD_SETTING_PL), "0", null);
                break;
            case 5:
                mLiveRoom.sendRoomCustomMsg(String.valueOf(LiveConstants.IMCMD_SETTING_LW), "1", null);
                break;
            case 6:
                mLiveRoom.sendRoomCustomMsg(String.valueOf(LiveConstants.IMCMD_SETTING_LW), "0", null);
                break;
        }
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
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        setStatusBarTransparent();
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
        binding.rvMoreLink.setVisibility(View.INVISIBLE);

    }

    //??????????????????dialog
    public void showCloseLinkDialog() {
        if (showCloseLinkDialog == null) {
            showCloseLinkDialog = new YesOrNoDialog(this);
            showCloseLinkDialog.setMessageText("????????????????????????");
            showCloseLinkDialog.setRightBtnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCloseLinkDialog.dismissDialog();
                    //???????????????????????????????????????
                    setType = LiveConstants.ZBJ_SET_GBLTS;
                    zbSettingBean.liveRoomConnection = LiveConstants.LIVE_STATUS_FREE;
                    viewModel.setZBStatus(zbSettingBean);

                    //??????????????????
                    if (userLinkPopShow.isShowing()) {
                        userLinkPopShow.dismiss();
                    }

                    if (mPusherList.size() > 0) {
                        //????????????????????????list???????????????
                        List<AnchorInfo> tempList = new ArrayList<>();
                        tempList.addAll(mPusherList);
                        for (AnchorInfo anchorInfo : tempList) {
                            //???????????????
                            mLiveRoom.kickoutJoinAnchor(anchorInfo.userID);
                            onAnchorExit(anchorInfo);
                        }
                    }
                }
            });
        }
        showCloseLinkDialog.showDialog();

    }

    private void disWaitTip() {
        linkStatus = 0;
        waitLinkUserId = "";
        if (toLinkTask != null) {
            toLinkTask.cancel();
            toLinkTask = null;
        }
        if (toLinkTimer != null) {
            toLinkTimer.cancel();
            toLinkTimer.purge();
            toLinkTimer = null;
        }
        if (waitLinkTip != null && waitLinkTip.isShowing()) {
            waitLinkTip.dismiss();
        }
        mPendingRequest = false;
    }

    private void showWaitTip() {
        linkStatus = 1;
        waitLinkTip = new QMUITipDialog.Builder(this)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("???????????????????????????...")
                .create();
        waitLinkTip.show();
    }

    private void showLinkTip() {
        linkStatus = 2;
        showLinkTip = new QMUITipDialog.Builder(this)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("???????????????...")
                .create();
        showLinkTip.show();
    }

    private void disLinkTip() {
        linkStatus = 0;
        if (showLinkTip != null && showLinkTip.isShowing()) {
            showLinkTip.dismiss();
        }
        mPendingRequest = false;
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
                    Toast.makeText(this, "??????????????????~", Toast.LENGTH_SHORT).show();
                    firstTime = secondTime;//??????firstTime
                } else {
                    //????????????
                    showDialog("????????????...");
                    //?????????????????????????????????
                    viewModel.closeLive(mLiveInitInfo.liveRoomRecordId, String.valueOf(commentSet.size()));

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

    private Runnable runnable;

    private void initLinkMsgManger() {
        runnable = new Runnable() {
            @Override
            public void run() {
                //????????????????????????
                for (int i = 0; i < linksShowAdapter.getData().size(); i++) {
                    ZBUserListBean bean = linksShowAdapter.getData().get(i);
                    if (bean.showTime > 0) {
                        bean.showTime = bean.showTime - 1;
                        linksShowAdapter.notifyItemChanged(i, 99);
                    } else {
                        linksShowAdapter.remove(i);

                        //???????????????????????????
                        updateLinkNum();


                    }
                }
                if (linksShowAdapter.getData().size() == 0) {
                    binding.tvLinkNum.setVisibility(View.GONE);
                    hasLinkMsg = false;
                }


                if (linkType == 1) {
                    //?????????????????????????????????
                    for (int i = 0; i < waitLinkAdapter.getData().size(); i++) {
                        ZBUserListBean bean = waitLinkAdapter.getData().get(i);
                        if (bean.showTime > 0) {
                            bean.showTime = bean.showTime - 1;
                        } else {
                            waitLinkAdapter.remove(i);
                            //???????????????
                            addPosMap(null, getMapKey(bean.userId));
                        }
                    }
                }
                mHandler.postDelayed(runnable, 1000L);
            }
        };
        mHandler.postDelayed(runnable, 1000L);
    }

    private void updateLinkNum() {
        //???????????????????????????
        binding.tvLinkNum.setVisibility(View.VISIBLE);
        String num = "";
        if (linksShowAdapter.getData().size() > 99) {
            num = "99";
        } else if (linksShowAdapter.getData().size() == 0) {
            num = "";
            binding.tvLinkNum.setVisibility(View.GONE);
            hasLinkMsg = false;
        } else {
            num = String.valueOf(linksShowAdapter.getData().size());
        }
        binding.tvLinkNum.setText(num);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (zbGoodsListPop != null && zbGoodsListPop.isShowing()) {
            if (zbGoodsListPop.toUniApp) {
                zbGoodsListPop.refreshData();
            }
        }
    }
}
