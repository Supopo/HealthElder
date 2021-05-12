
package com.xaqinren.healthyelders.moduleZhiBo.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.google.gson.Gson;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.tencent.qcloud.tim.uikit.utils.ScreenUtil;
import com.tencent.qcloud.ugckit.utils.ToastUtil;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.databinding.ActivityLiveZhuboBinding;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleZhiBo.adapter.MoreLinkAdapter;
import com.xaqinren.healthyelders.moduleZhiBo.adapter.TCChatMsgListAdapter;
import com.xaqinren.healthyelders.moduleZhiBo.adapter.TopUserHeadAdapter;
import com.xaqinren.healthyelders.moduleZhiBo.adapter.ZBLinkShowAdapter;
import com.xaqinren.healthyelders.moduleZhiBo.bean.JsonMsgBean;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveInitInfo;
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
import com.xaqinren.healthyelders.moduleZhiBo.popupWindow.ZBLinkUsersPop;
import com.xaqinren.healthyelders.moduleZhiBo.popupWindow.ZBUserListPop;
import com.xaqinren.healthyelders.moduleZhiBo.viewModel.LiveZhuboViewModel;
import com.xaqinren.healthyelders.moduleZhiBo.widgetLike.TCFrequeControl;
import com.xaqinren.healthyelders.utils.LogUtils;
import com.xaqinren.healthyelders.widget.CenterDialog;

import java.util.ArrayList;
import java.util.HashMap;
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
import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;
import me.goldze.mvvmhabit.utils.ToastUtils;

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
    private Set<String> commentSet = new HashSet();//统计评论人数
    private Dialog selectLinkTypeDialog;
    private int linkType = 0;//连麦类型 0-双人 1-多人
    private ZBLinkUsersPop userLinkPopShow;
    private ZBLinkShowAdapter waitLinkAdapter;
    private ZBLinkShowAdapter linksShowAdapter;//连麦消息展示adapter
    private List<AnchorInfo> mPusherList = new ArrayList<>();            // 当前在麦上的主播
    private boolean isLianMai;
    private boolean hasLinkMsg;//是否有连麦申请消息
    private String waitLinkUserId;//1v1 连麦 操作连麦者
    private Timer toLinkTimer;//连麦等待计时器
    private TimerTask toLinkTask;
    private QMUITipDialog waitLinkTip;//等待用户接受连麦
    private QMUITipDialog showLinkTip;//连麦请求
    private int linkStatus;//0未操作 1邀请中 2接受邀请连接中
    private boolean mPendingRequest;//是否在操作连麦请求
    private CenterDialog showCloseLinkDialog;//关闭连麦弹窗
    private MoreLinkAdapter moreLinkAdapter;
    private List<ZBUserListBean> moreLinkList;
    private int setType;//直播间设置类型 1聊天室
    private LinkedHashMap<Integer, ZBUserListBean> posMap = new LinkedHashMap<>();//多人连麦座位表
    private int selectLinkPos;//多人连麦-主播自己邀请选择的座位号
    private Integer linkerPos;//当前上麦者所在位置
    private Dialog moreLinkSettingDialog;
    private ZBSettingBean zbSettingBean;

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
        initLinkMsgManger();
    }

    private TXCloudVideoView txCloudVideoView;

    private void startLinkLayout(AnchorInfo anchorInfo) {

        if (userLinkPopShow != null && userLinkPopShow.isShowing()) {
            userLinkPopShow.dismiss();
        }

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
            txCloudVideoView = binding.anchor2TxVideoView;

        } else {
            //查询当前上麦者所在座位
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


        //开启远端视频渲染
        mLiveRoom.startRemoteView(anchorInfo, txCloudVideoView, new IMLVBLiveRoomListener.PlayCallback() {
            @Override
            public void onBegin() {
                isLianMai = true;
                binding.btnLianmai.setBackgroundResource(R.mipmap.zbj_menu_lianmaiing);
                if (linkType == 0) {
                    //群发消息通知大家关闭连麦等待dialog
                    mLiveRoom.sendRoomCustomMsg(String.valueOf(LiveConstants.IMCMD_ZB_LINKING), "", null);

                    zbSettingBean.liveRoomConnection = LiveConstants.LIVE_STATUS_DOUBLE_TALK;
                    viewModel.setZBStatus(zbSettingBean);
                } else {
                    //通知服务器更新座位表
                    viewModel.setVoiceMic(anchorInfo, mLiveInitInfo.liveRoomRecordId);
                }
            }

            @Override
            public void onError(int errCode, String errInfo) {
                mLiveRoom.kickoutJoinAnchor(anchorInfo.userID);
                //退出连麦操作
                stopLinkLayout(anchorInfo);
            }

            @Override
            public void onEvent(int event, Bundle param) {

            }
        });

    }

    //退出连麦操作
    private void stopLinkLayout(AnchorInfo anchorInfo) {
        //从上麦者列表清除
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

        mLiveRoom.stopRemoteView(anchorInfo);//关闭远端视频渲染
        anchorInfo.micStatus = 0;

        //通知服务用户离开多人连麦
        viewModel.setVoiceMic(anchorInfo, mLiveInitInfo.liveRoomRecordId);

        if (linkType == 0) {
            isLianMai = false;
            binding.btnLianmai.setBackgroundResource(R.mipmap.zbj_menu_lianmai);

            //切回主播屏幕
            binding.rlAnchor2.setVisibility(View.GONE);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            lp.setMargins(0, 0, 0, (int) getResources().getDimension(R.dimen.dp_54));
            binding.llVideo.setLayoutParams(lp);

            zbSettingBean.liveRoomConnection = LiveConstants.LIVE_STATUS_FREE;
            viewModel.setZBStatus(zbSettingBean);
        } else {
            Integer linkPos = getMapKey(anchorInfo.userID);
            if (linkPos != null) {
                moreLinkAdapter.getData().get(linkPos).nickname = "邀请上麦";
                moreLinkAdapter.getData().get(linkPos).position = 0;
                moreLinkAdapter.getData().get(linkPos).hasProsody = false;
                moreLinkAdapter.getData().get(linkPos).avatarUrl = null;
                moreLinkAdapter.getData().get(linkPos).userId = null;
                moreLinkAdapter.notifyItemChanged(linkPos);
                //移除某个座位的人
                addPosMap(null, linkPos);
            }

            if (mPusherList.size() == 0) {
                isLianMai = false;
                binding.btnLianmai.setBackgroundResource(R.mipmap.zbj_menu_lianmai);
            }
        }

    }

    //初始化房间信息
    private void initLiveInfo() {

        Glide.with(this).load(mLiveInitInfo.avatarUrl).diskCacheStrategy(DiskCacheStrategy.ALL).into(binding.rivPhoto);

        //禁止带货
        if (!mLiveInitInfo.getCanSale()) {
            binding.btnGoods.setVisibility(View.GONE);
        }
        topHeadAdapter = new TopUserHeadAdapter(R.layout.item_top_user_head);
        binding.rvAvatar.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
        binding.rvAvatar.setAdapter(topHeadAdapter);

        waitLinkAdapter = new ZBLinkShowAdapter(R.layout.item_zb_link_show);
        //展示观众申请消息
        linksShowAdapter = new ZBLinkShowAdapter(R.layout.item_zb_link_show);
        linksShowAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                if (isLianMai && linkType == 0) {
                    ToastUtil.toastShortMessage("您正在连麦~");
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

        //初始化多人连麦座位表
        moreLinkAdapter = new MoreLinkAdapter(R.layout.item_more_link);
        //垂直布局 禁止滑动
        binding.rvMoreLink.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        binding.rvMoreLink.setAdapter(moreLinkAdapter);
        initMoreLinkData();

        moreLinkAdapter.setOnItemClickListener(((adapter, view, position) -> {
            //判断当前位置是否有人
            if (TextUtils.isEmpty(moreLinkAdapter.getData().get(position).userId)) {
                //没人则打开用户连麦弹窗
                selectLinkPos = position;
                showUserLinkPopShow(1, 1);
            } else {
                //已上麦头像点击设置
                showMoreLinkSettingPop(position);
            }
        }));
    }

    //处理单人连麦消息
    private void handleSingleLinkMsg(int position) {
        //向某个用户发送同意连麦的消息
        mLiveRoom.sendC2CCustomMsg(linksShowAdapter.getData().get(position).userId, String.valueOf(LiveConstants.IMCMD_INVITE_LINK), "同意连麦", null);
        waitLinkUserId = linksShowAdapter.getData().get(position).userId;
        linksShowAdapter.remove(position);
        userLinkPopShow.dismiss();
        showWaitTip();
        //设置30S连接不上关闭
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //20S没有答复关闭等待dialog
                if (linkStatus == 1) {
                    ToastUtils.showShort("对方未答应");
                    disWaitTip();
                }
            }
        }, 1000 * 20);
    }

    //初始化多人连麦数据
    private void initMoreLinkData() {
        moreLinkList = new ArrayList<>();
        posMap = new LinkedHashMap<>();
        for (int i = 0; i < 6; i++) {
            ZBUserListBean userInfoBean = new ZBUserListBean();
            userInfoBean.nickname = "邀请上麦";
            moreLinkList.add(userInfoBean);
            //初始化座位表
            addPosMap(null, i);
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
                isPlaying = true;
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

    private boolean isPlaying;//直播中

    private void stopPublish() {
        mLiveRoom.exitRoom(new ExitRoomCallback() {
            @Override
            public void onSuccess() {
                isPlaying = false;
                dismissDialog();
                mLiveRoom.setListener(null);
                LogUtils.v(Constant.TAG_LIVE, "直播间退出成功");
                Bundle bundle = new Bundle();
                bundle.putString("liveRoomRecordId", mLiveInitInfo.liveRoomRecordId);
                startActivity(ZhiboOverActivity.class, bundle);
                finish();
            }

            @Override
            public void onError(int errCode, String e) {
                dismissDialog();
                LogUtils.v(Constant.TAG_LIVE, "直播间退出失败:" + errCode);
                LogUtils.v(Constant.TAG_LIVE, "直播间退出失败:" + e);
            }
        });
    }

    //选择双人连麦或者多人聊天室
    private void selectLinkTypePop() {
        if (selectLinkTypeDialog != null) {
            selectLinkTypeDialog.show();
            return;
        }

        selectLinkTypeDialog = new Dialog(this, R.style.CustomerDialog);
        //填充对话框的布局
        View view = LayoutInflater.from(this).inflate(R.layout.layout_zb_link_type_pop, null);
        //初始化控件
        RelativeLayout rlOne = (RelativeLayout) view.findViewById(R.id.rl_one);
        RelativeLayout rlMore = (RelativeLayout) view.findViewById(R.id.rl_more);
        rlOne.setOnClickListener(lis -> {
            selectLinkTypeDialog.dismiss();
            linkType = 0;
            showUserLinkPopShow(0, 1);
        });
        rlMore.setOnClickListener(lis -> {
            //开启多人语音聊天模式
            //1.用户选位置上号 2.用户随机上位 3.主播按位置邀请 4.主播随机邀请上位
            //先向服务器发送请求
            setType = LiveConstants.ZBJ_SET_KQLTS;
            zbSettingBean.liveRoomConnection = LiveConstants.LIVE_STATUS_CHAT_ROOM;
            viewModel.setZBStatus(zbSettingBean);
        });

        //点击外部不dismiss
        selectLinkTypeDialog.setCancelable(true);
        //将布局设置给Dialog
        selectLinkTypeDialog.setContentView(view);
        //获取当前Activity所在的窗体
        Window dialogWindow = selectLinkTypeDialog.getWindow();
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity(Gravity.BOTTOM);
        //设置弹出动画
        dialogWindow.setWindowAnimations(R.style.DialogBottomAnimation);
        //获得窗体的属性
        WindowManager.LayoutParams params = dialogWindow.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;//设置宽高模式，
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;//设置宽高模式，
        dialogWindow.setAttributes(params);
        selectLinkTypeDialog.show();

    }

    //展示邀请用户连麦的列表
    private void showUserLinkPopShow(int openType, int showType) {
        mLiveInitInfo.linkType = linkType;
        userLinkPopShow = new ZBLinkUsersPop(mLiveInitInfo, openType, this, showType, linksShowAdapter, mPusherList, isLianMai);
        userLinkPopShow.showPopupWindow();
    }

    //用户连麦设置
    private void showMoreLinkSettingPop(int postion) {
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

        if (voiceMicMute) {
            tvJinYin.setText("取消静音");
        } else {
            tvJinYin.setText("静音");
        }

        tvName.setText("对@ " + nickName + " 进行操作");
        tvCancel.setOnClickListener(lis -> {
            moreLinkSettingDialog.dismiss();
        });
        tvDKLM.setOnClickListener(lis -> {
            //关闭某人连麦
            //先通知服务器
            AnchorInfo anchorInfo = new AnchorInfo();
            anchorInfo.userID = userId;
            anchorInfo.micStatus = 0;
            viewModel.setVoiceMic(anchorInfo, mLiveInitInfo.liveRoomRecordId);
            moreLinkSettingDialog.dismiss();
        });
        tvJinYin.setOnClickListener(lis -> {
            if (!voiceMicMute) {
                //取消静音操作 发消息让用户去操作
                mLiveRoom.sendC2CCustomMsg(userId, String.valueOf(LiveConstants.IMCMD_MORE_ANCHOR_QXJY), "取消静音", null);
            } else {
                //设置静音操作
                mLiveRoom.sendC2CCustomMsg(userId, String.valueOf(LiveConstants.IMCMD_MORE_ANCHOR_JY), "开启静音", null);
            }
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

    //观众上麦
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
        //从等待列表移除，不用取消座位
        removeMoreLinkList(anchorInfo.userID);
    }

    //观众下麦
    @Override
    public void onAnchorExit(AnchorInfo anchorInfo) {
        stopLinkLayout(anchorInfo);
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
        boolean hasUser = false;
        if (linkType == 0) {
            //判断下当前是不是刚才主播同意或者邀请的人
            if (!anchorInfo.userID.equals(waitLinkUserId)) {
                mLiveRoom.responseJoinAnchor(anchorInfo.userID, false, "请稍后，主播正在处理其它人的连麦请求");
                return;
            }
        } else if (linkType == 1) {
            //判断座位表有当前用户没有
            for (ZBUserListBean bean : posMap.values()) {
                if (bean != null && bean.userId.equals(anchorInfo.userID)) {
                    hasUser = true;
                }
            }
        }

        if (mPendingRequest) {
            mLiveRoom.responseJoinAnchor(anchorInfo.userID, false, "请稍后，主播正在处理其它人的连麦请求");
            return;
        }
        if (mPusherList.size() > 6) {
            mLiveRoom.responseJoinAnchor(anchorInfo.userID, false, "主播端连麦人数超过最大限制");
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
        if (!roomID.equals(mRoomID)) {
            return;
        }
        //统计评论人数
        commentSet.add(userName);
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
            case LiveConstants.IMCMD_TO_LINK://用户来申请连麦的消息
                SendUserLinkBean userLinkBean = new Gson().fromJson((String) message, SendUserLinkBean.class);
                ZBUserListBean zbUserListBean = new ZBUserListBean();
                zbUserListBean.position = Integer.parseInt(userLinkBean.position);
                zbUserListBean.userId = userLinkBean.userId;
                zbUserListBean.nickname = userLinkBean.userName;
                zbUserListBean.avatarUrl = userLinkBean.userHeadImageUrl;
                zbUserListBean.showTime = LiveConstants.TO_LINK_TIME;

                addLinkMsg(zbUserListBean, userLinkBean.userId);
            case LiveConstants.IMCMD_RESH_MORELINK_INFO://刷新座位表
                viewModel.findMicUsers(mLiveInitInfo.liveRoomRecordId);
                break;
            default:
                break;
        }
    }

    @Override
    public void onRecvC2CCustomMsg(String senderId, String cmd, String message, String userName, String headPic) {
        int type = Integer.parseInt(cmd);
        switch (type) {
            case LiveConstants.IMCMD_REFUSE_LINK://观众拒绝主播邀请连麦
                ToastUtil.toastShortMessage("对方拒绝了您的请求");
                if (linkType == 1) {
                    //用户拒绝了上麦从座位表清除
                    addPosMap(null, getMapKey(senderId));
                    //从等待列表移除
                    removeMoreLinkList(senderId);
                }
                disWaitTip();
                break;
            case LiveConstants.IMCMD_CANCEL_LINK://观众取消连麦申请
                disWaitTip();
                //adapter中移除
                int temp = 0;
                for (int i = 0; i < linksShowAdapter.getData().size(); i++) {
                    if (linksShowAdapter.getData().get(i).userId.equals(senderId)) {
                        temp = i;
                    }
                }
                linksShowAdapter.removeAt(temp);
                updateLinkNum();
                break;
            case LiveConstants.IMCMD_MORE_LINK_NUM://用户申请多人语音连麦的请求
                //收到用户申请多人语音连麦的请求
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

    //添加连麦申请消息
    private void addLinkMsg(ZBUserListBean bean, String userId) {
        //判断是否在列表中
        boolean hasUser = false;
        for (int i = 0; i < linksShowAdapter.getData().size(); i++) {
            if (linksShowAdapter.getData().get(i).userId.equals(userId)) {
                hasUser = true;
            }
        }
        if (!hasUser) {
            //向adapter添加一条数据
            linksShowAdapter.addData(bean);
        }
        String num;
        if (linksShowAdapter.getData().size() > 99) {
            num = "99";
        } else {
            num = String.valueOf(linksShowAdapter.getData().size());
        }
        //展示小红点显示数字
        binding.tvLinkNum.setVisibility(View.VISIBLE);
        binding.tvLinkNum.setText(num);

        hasLinkMsg = true;
    }

    private void handleMoreLinkMsg(ZBUserListBean bean, int position) {
        linksShowAdapter.remove(position);
        userLinkPopShow.dismiss();
        handleMoreLinkMsg(bean);
    }

    //处理多人连麦的消息
    private void handleMoreLinkMsg(ZBUserListBean bean) {

        int linkPos = bean.position;
        //-1说明用户点的是下面的连麦随机座位
        if (linkPos == -1) {
            computeEmptyPos(bean);
        } else {

            //先判断下当前位置有人没有
            if (getPosMapBean(linkPos) == null) {
                addPosMap(bean, linkPos);
                //告诉对方有位置，可以开启连麦
                mLiveRoom.sendC2CCustomMsg(bean.userId, String.valueOf(LiveConstants.IMCMD_MORE_LINK_JS), String.valueOf(linkPos), null);
                addWaitMoreLinkList(bean);
            } else {
                //判断用户发来的位置是否和当前座位上的人一致
                if ((getPosMapBean(linkPos)).userId.equals(bean.userId)) {
                    addPosMap(bean, linkPos);
                    //告诉对方有位置，可以开启连麦
                    mLiveRoom.sendC2CCustomMsg(bean.userId, String.valueOf(LiveConstants.IMCMD_MORE_LINK_JS), String.valueOf(linkPos), null);
                    addWaitMoreLinkList(bean);
                } else {//计算一次空位
                    computeEmptyPos(bean);
                }
            }
        }
    }

    //计算空位
    private void computeEmptyPos(ZBUserListBean bean) {
        Integer tempKey = getEmptyPos();

        if (tempKey != null) {
            addPosMap(bean, tempKey);
            //告诉对方有位置，可以开启连麦
            mLiveRoom.sendC2CCustomMsg(bean.userId, String.valueOf(LiveConstants.IMCMD_MORE_LINK_JS), String.valueOf(tempKey), null);
        } else {
            //告诉对方没有位置
            mLiveRoom.sendC2CCustomMsg(bean.userId, String.valueOf(LiveConstants.IMCMD_MORE_LINK_JS), String.valueOf(-1), null);
        }
    }

    //读写锁
    private final ReadWriteLock rwlock = new ReentrantReadWriteLock();
    private final Lock rlock = rwlock.readLock();
    private final Lock wlock = rwlock.writeLock();

    //读取座位表
    private ZBUserListBean getPosMapBean(int linkPos) {
        rlock.lock();
        try {
            return posMap.get(linkPos);
        } finally {
            rlock.unlock();
        }
    }

    //操作座位表
    private void addPosMap(ZBUserListBean bean, Integer tempKey) {
        wlock.lock();
        try {
            posMap.put(tempKey, bean);
        } finally {
            wlock.unlock();
        }
    }

    //计算多人连麦当前空位置
    private Integer getEmptyPos() {
        Integer emptyPos = null;
        for (Integer key : posMap.keySet()) {
            if (getPosMapBean(key) == null) {
                LogUtils.v(Constant.TAG_LIVE, "空位：" + key.toString());
                return key;
            }
        }
        return emptyPos;
    }

    //通过值获取座位
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
        disposable.dispose();
        if (isPlaying) {
            viewModel.closeLive(mLiveInitInfo.liveRoomRecordId, String.valueOf(commentSet.size()));
            stopPublish();
        }
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
        binding.btnLianmai.setOnClickListener(this);
        binding.tvMembersNum.setOnClickListener(this);
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
                //弹窗提示
                showDialog("结束直播...");
                //去通知服务器退出了直播
                viewModel.closeLive(mLiveInitInfo.liveRoomRecordId, String.valueOf(commentSet.size()));
                break;
            case R.id.tv_msg:
                startActivity(ZBEditTextDialogActivity.class);
                break;
            case R.id.btn_lianmai:
                if (isLianMai || hasLinkMsg || linkType == 1) {
                    //有申请消息
                    showUserLinkPopShow(0, hasLinkMsg ? 0 : 1);
                } else {
                    //没连麦消息或者没连麦时候展示选择类型pop
                    selectLinkTypePop();
                }
                break;
        }
    }

    //加入等待列表 15S未上麦者会从座位清掉
    private void addWaitMoreLinkList(ZBUserListBean zbUserListBean) {
        //15S未上麦会自动踢出座位
        zbUserListBean.showTime = LiveConstants.WAIT_MORE_LINK;

        //判断是否在列表中
        boolean hasUser = false;
        for (int i = 0; i < waitLinkAdapter.getData().size(); i++) {
            if (waitLinkAdapter.getData().get(i).userId.equals(zbUserListBean.userId)) {
                hasUser = true;
            }
        }
        if (!hasUser) {
            //通过申请消息列表的handler一并统计时间
            //向adapter添加一条数据
            waitLinkAdapter.addData(zbUserListBean);
        }
    }

    //从等待席位移除
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
                case Constant.NET_SPEED:
                    if (eventBean.msgType == 1) {
                        binding.tvNet.setText("网络良好");
                        binding.ivNet.setBackground(getResources().getDrawable(R.mipmap.wangluolh));
                    } else if (eventBean.msgType == 2) {
                        binding.tvNet.setText("网络一般");
                        binding.ivNet.setBackground(getResources().getDrawable(R.mipmap.wangluoyb));
                    } else if (eventBean.msgType == 3) {
                        binding.tvNet.setText("网络卡顿");
                        binding.ivNet.setBackground(getResources().getDrawable(R.mipmap.wangluokd));
                    }
                    break;
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
                case LiveConstants.ZB_LINK_YQ:
                    if (isLianMai && linkType == 0) {
                        ToastUtil.toastShortMessage("您正在连麦~");
                        return;
                    }
                    ZBUserListBean zbUserListBean = (ZBUserListBean) eventBean.object;

                    //1v1视频连麦
                    if (linkType == 0) {
                        //发自定义消息通知用户来申请连麦
                        mLiveRoom.sendC2CCustomMsg(zbUserListBean.userId, String.valueOf(LiveConstants.IMCMD_INVITE_LINK), "邀请连麦", null);
                    } else {//多人连麦
                        Integer tempKey;
                        //从座位列表邀请的
                        if (eventBean.status == 1) {
                            tempKey = selectLinkPos;
                            //说明位置没人
                            if (getPosMapBean(tempKey) != null) {
                                tempKey = getEmptyPos();
                            }
                        } else {//从底部邀请
                            tempKey = getEmptyPos();
                        }
                        if (tempKey == null) {
                            ToastUtil.toastShortMessage("没有位置啦");
                            return;
                        }
                        addPosMap(zbUserListBean, tempKey);
                        //加入占座队列时间到了还未上麦从座位踢出
                        addWaitMoreLinkList(zbUserListBean);
                        //通知用户来上麦 并发送座位号
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
                            //主播邀请时间到了
                            if (linkStatus == 1) {
                                ToastUtil.toastShortMessage("对方未答应");
                                disWaitTip();
                            }

                        }
                    };
                    toLinkTimer.schedule(toLinkTask, 11000);//11秒后关闭 比观众端延迟1S
                    break;
                case LiveConstants.ZB_LINK_GB:
                    //关闭连麦
                    if (isLianMai || linkType == 1) {
                        showCloseLinkDialog();
                    }
                    break;
                case LiveConstants.ZBJ_SET_SUCCESS://聊天设置成功
                    ZBSettingBean zbSettingBean = (ZBSettingBean) eventBean.data;
                    if (linkType == 0) {
                        mLiveInitInfo.onlyFansMic = zbSettingBean.chatStatusManageDto.onlyFansMic;
                        mLiveInitInfo.userApplyMic = zbSettingBean.chatStatusManageDto.userApplyMic;
                        mLiveInitInfo.onlyInviteMic = zbSettingBean.chatStatusManageDto.onlyInviteMic;
                    } else if (linkType == 1) {
                        mLiveInitInfo.chatRoomOnlyFansMic = zbSettingBean.chatStatusManageDto.onlyFansMic;
                        mLiveInitInfo.chatRoomUserApplyMic = zbSettingBean.chatStatusManageDto.userApplyMic;
                    }
                    //群发消息通知大家刷新接口
                    mLiveRoom.sendRoomCustomMsg(String.valueOf(LiveConstants.IMCMD_RESH_HOME_INFO), "", null);
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
                zbSettingBean = new ZBSettingBean();
                zbSettingBean.liveRoomId = mLiveInitInfo.liveRoomId;
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
                    //退出房间
                    showDialog("退出房间...");
                    stopPublish();
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

        viewModel.setSuccess.observe(this, setSuccess -> {
            if (setSuccess != null && setSuccess) {
                switch (setType) {
                    case LiveConstants.ZBJ_SET_KQLTS:
                        if (selectLinkTypeDialog != null && selectLinkTypeDialog.isShowing()) {
                            selectLinkTypeDialog.dismiss();
                        }
                        //开启多人聊天
                        linkType = 1;


                         Animation scaleAnimation1 = AnimationUtils.loadAnimation(this, R.anim.zbj_more_link_enter2);
                        // int screenWidth = ScreenUtil.getScreenWidth(this);
                        // int screenHeight = ScreenUtil.getScreenHeight(this);
                        // //计算x轴缩放倍率
                        // float xx = (float) (screenWidth - getResources().getDimension(R.dimen.dp_96)) / screenWidth;
                        // //计算y轴缩放倍率
                        // float yy = (float) (getResources().getDimension(R.dimen.dp_573)) / (screenHeight);
                        // ScaleAnimation animation = new ScaleAnimation(1.0F, xx, 1.0F, yy, Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 1.0F);
                        // animation.setDuration(500);
                        // animation.setFillAfter(true);
                        // Animation scaleAnimation3 = AnimationUtils.loadAnimation(this, R.anim.zbj_more_link_exit);
                        // binding.mTxVideoView.clearAnimation();
                        // binding.mTxVideoView.setAnimation(animation);

                        // binding.rvMoreLink.setVisibility(View.VISIBLE);
                        // binding.rvMoreLink.clearAnimation();
                        // binding.rvMoreLink.setAnimation(scaleAnimation1);

                        binding.rvMoreLink.clearAnimation();
                        binding.rvMoreLink.setAnimation(scaleAnimation1);
                        binding.rvMoreLink.setVisibility(View.VISIBLE);

//                        animation.setAnimationListener(new Animation.AnimationListener() {
//                            @Override
//                            public void onAnimationStart(Animation animation) {
//
//                            }
//
//                            @Override
//                            public void onAnimationEnd(Animation animation) {
//
//                            }
//
//                            @Override
//                            public void onAnimationRepeat(Animation animation) {
//
//                            }
//                        });


                        //群发消息
                        mLiveRoom.sendRoomCustomMsg(String.valueOf(LiveConstants.IMCMD_OPEN_MORE_LINK), "", null);
                        break;
                    case LiveConstants.ZBJ_SET_GBLTS:
                        if (userLinkPopShow != null && userLinkPopShow.isShowing()) {
                            userLinkPopShow.dismiss();
                        }
                        //关闭连麦操作
                        if (mPusherList.size() > 0) {
                            //不能再便利中移除list故此用新的
                            List<AnchorInfo> tempList = new ArrayList<>();
                            tempList.addAll(mPusherList);
                            for (AnchorInfo anchorInfo : tempList) {
                                //踢掉连麦者
                                mLiveRoom.kickoutJoinAnchor(anchorInfo.userID);
                                onAnchorExit(anchorInfo);
                            }
                        }

                        //关闭多人聊天
                        linkType = 0;

                        Animation scaleAnimation2 = AnimationUtils.loadAnimation(this, R.anim.zbj_more_link_exit2);
                        //                        binding.rvMoreLink.setAnimation(scaleAnimation2);
                        binding.rvMoreLink.setVisibility(View.GONE);
                        scaleAnimation2.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });


                        //群发消息关闭多人连麦
                        mLiveRoom.sendRoomCustomMsg(String.valueOf(LiveConstants.IMCMD_CLOSE_MORE_LINK), "", null);

                        //刷新adapter状态
                        initMoreLinkData();
                        break;
                }
                setType = 0;
            }
        });

        viewModel.micAnchorInfo.observe(this, micAnchorInfo -> {
            if (micAnchorInfo != null) {
                //判断用户还在不在座位中
                //                boolean hasPos = false;
                //                for (ZBUserListBean bean : posMap.values()) {
                //                    if (bean != null && bean.userId.equals(micAnchorInfo.userID)) {
                //                        hasPos = true;
                //                    }
                //                }
                //                if (!hasPos) {
                //                    return;
                //                }

                //断开成功 / 加入失败
                if (micAnchorInfo.netStatus == 1 || micAnchorInfo.netStatus == 3) {
                    mLiveRoom.kickoutJoinAnchor(micAnchorInfo.userID);
                    onAnchorExit(micAnchorInfo);
                }

                //断开成功 / 加入成功
                if (micAnchorInfo.netStatus == 1 || micAnchorInfo.netStatus == 2) {
                    //群发消息刷新
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
                    userInfoBean.nickname = "邀请上麦";
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

    //是否关闭连麦dialog
    public void showCloseLinkDialog() {
        if (showCloseLinkDialog == null) {
            showCloseLinkDialog = new CenterDialog(this);
            showCloseLinkDialog.setMessageText("确定关闭连线吗？");
            showCloseLinkDialog.setRightBtnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCloseLinkDialog.dismissDialog();
                    //关闭连麦通知服务器更改状态
                    setType = LiveConstants.ZBJ_SET_GBLTS;
                    zbSettingBean.liveRoomConnection = LiveConstants.LIVE_STATUS_FREE;
                    viewModel.setZBStatus(zbSettingBean);

                    //关闭连麦操作
                    if (userLinkPopShow.isShowing()) {
                        userLinkPopShow.dismiss();
                    }

                    if (mPusherList.size() > 0) {
                        //不能再遍历中移除list故此用新的
                        List<AnchorInfo> tempList = new ArrayList<>();
                        tempList.addAll(mPusherList);
                        for (AnchorInfo anchorInfo : tempList) {
                            //踢掉连麦者
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
                .setTipWord("正在连接中，请等待...")
                .create();
        waitLinkTip.show();
    }

    private void showLinkTip() {
        linkStatus = 2;
        showLinkTip = new QMUITipDialog.Builder(this)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("正在连接中...")
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
     * 二次点击（返回键）退出
     */
    private double firstTime;

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                long secondTime = System.currentTimeMillis();
                if (secondTime - firstTime > 2000) {
                    //如果两次按键时间间隔大于2秒，则不退出
                    Toast.makeText(this, "再按一次直播间~", Toast.LENGTH_SHORT).show();
                    firstTime = secondTime;//更新firstTime
                    return true;
                } else {
                    //弹窗提示
                    showDialog("结束直播...");
                    //去通知服务器退出了直播
                    viewModel.closeLive(mLiveInitInfo.liveRoomRecordId, String.valueOf(commentSet.size()));
                }
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    private Runnable runnable;

    private void initLinkMsgManger() {
        runnable = new Runnable() {
            @Override
            public void run() {
                //连麦消息列表操作
                for (int i = 0; i < linksShowAdapter.getData().size(); i++) {
                    ZBUserListBean bean = linksShowAdapter.getData().get(i);
                    if (bean.showTime > 0) {
                        bean.showTime = bean.showTime - 1;
                        linksShowAdapter.notifyItemChanged(i, 99);
                    } else {
                        linksShowAdapter.remove(i);

                        //展示小红点显示数字
                        updateLinkNum();


                    }
                }
                if (linksShowAdapter.getData().size() == 0) {
                    binding.tvLinkNum.setVisibility(View.GONE);
                    hasLinkMsg = false;
                }


                if (linkType == 1) {
                    //等待用户连麦操作倒计时
                    for (int i = 0; i < waitLinkAdapter.getData().size(); i++) {
                        ZBUserListBean bean = waitLinkAdapter.getData().get(i);
                        if (bean.showTime > 0) {
                            bean.showTime = bean.showTime - 1;
                        } else {
                            waitLinkAdapter.remove(i);
                            //清除座位表
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
        //展示小红点显示数字
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
}
