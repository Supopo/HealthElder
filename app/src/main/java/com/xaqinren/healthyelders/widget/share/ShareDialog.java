package com.xaqinren.healthyelders.widget.share;

import android.Manifest;
import android.app.Fragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tencent.imsdk.v2.V2TIMConversation;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMMessage;
import com.tencent.imsdk.v2.V2TIMMessageManager;
import com.tencent.imsdk.v2.V2TIMSendCallback;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.qcloud.tim.uikit.base.IBaseMessageSender;
import com.tencent.qcloud.tim.uikit.base.IUIKitCallBack;
import com.tencent.qcloud.tim.uikit.base.TUIKitListenerManager;
import com.tencent.qcloud.tim.uikit.modules.chat.C2CChatManagerKit;
import com.tencent.qcloud.tim.uikit.modules.chat.ChatLayout;
import com.tencent.qcloud.tim.uikit.modules.message.MessageInfo;
import com.tencent.qcloud.tim.uikit.modules.message.MessageInfoUtil;
import com.tencent.qcloud.tim.uikit.utils.TUIKitConstants;
import com.tencent.qcloud.ugckit.utils.ToastUtil;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.databinding.PopShareBinding;
import com.xaqinren.healthyelders.global.AppApplication;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleHome.bean.ShareBean;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;
import com.xaqinren.healthyelders.moduleMsg.activity.FriendsListActivity;
import com.xaqinren.healthyelders.moduleMsg.bean.MCustomMsgBean;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ZBUserListBean;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.TCGlobalConfig;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.roomutil.im.IMMessageMgr;
import com.xaqinren.healthyelders.uniApp.UniService;
import com.xaqinren.healthyelders.uniApp.UniUtil;
import com.xaqinren.healthyelders.uniApp.bean.UniEventBean;
import com.xaqinren.healthyelders.utils.DownloadUtil;
import com.xaqinren.healthyelders.utils.LogUtils;
import com.xaqinren.healthyelders.widget.DownLoadProgressDialog;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.ref.SoftReference;
import java.util.List;

import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.utils.ToastUtils;

public class ShareDialog {
    private PopupWindow popupWindow;
    private View contentView;
    private SoftReference<Context> context;
    private PopShareBinding binding;
    private ShareFriendAdapter shareFriendAdapter;
    private List<? extends IShareUser> userList;
    public static int VIDEO_TYPE = 1;//?????????
    public static int TP_TYPE = 2;//??????
    public static int LIVE_TYPE = 3;//??????
    public static int USER_TYPE = 4;//??????
    private int showType = VIDEO_TYPE;
    private Context mContext;
    private DownLoadProgressDialog downloadProgress;
    private OnClickListener onClickListener;
    private OnDelClickListener onDelClickListener;
    private ShareBean shareBean;
    private VideoInfo videoInfo;
    private Disposable uniSubscribe;
    private Disposable Subscribe;
    //??????/???????????????id
    private String userId;
    //??????????????????Id
    private String selfUseId;
    private boolean isMine = false;

    private float disEnable = 0.4f;
    private RxPermissions permissions;
    private FragmentActivity fragmentActivity;
    private Fragment fragment;
    private Disposable disposable;
    private IMMessageMgr imMessageMgr;
    private String shareData;
    private MCustomMsgBean messageCustom;
    private long shareTime;
    private IBaseMessageSender messageSender;
    private MessageInfo customMessage;
    private Gson gson;

    //    //????????????????????????
    //    private static final ShareDialog instance = new ShareDialog();
    //
    //    private ShareDialog() {
    //    }
    //
    //    public static ShareDialog getInstance() {
    //        return instance;
    //    }


    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setOnDelClickListener(OnDelClickListener onDelClickListener) {
        this.onDelClickListener = onDelClickListener;
    }

    public ShareDialog(Context context) {
        this.context = new SoftReference<>(context);
        mContext = context;
        init();
        showType();
    }

    public ShareDialog(Context context, int showType) {
        this.context = new SoftReference<>(context);
        this.showType = showType;
        mContext = context;
        init();
        showType();
    }

    public void setRxPermissions(RxPermissions permissions) {
        this.permissions = permissions;
    }


    public ShareDialog(Context context, ShareBean shareInfo, int showType) {
        this.context = new SoftReference<>(context);
        this.showType = showType;
        mContext = context;
        shareBean = shareInfo;
        selfUseId = UserInfoMgr.getInstance().getUserInfo().getId();
        init();
        showType();
    }

    public ShareDialog(Context context, ShareBean shareInfo, VideoInfo videoInfo, int showType) {
        this.context = new SoftReference<>(context);
        this.showType = showType;
        this.videoInfo = videoInfo;
        mContext = context;
        shareBean = shareInfo;
        shareBean.resourceId = videoInfo.resourceId;
        shareBean.userAvatar = videoInfo.avatarUrl;
        shareBean.userNickname = videoInfo.nickname;
        selfUseId = UserInfoMgr.getInstance().getUserInfo().getId();
        init();
        showType();
    }

    public ShareDialog(Context context, ShareBean shareInfo) {
        this.context = new SoftReference<>(context);
        mContext = context;
        shareBean = shareInfo;
        init();
    }

    public void isMineOpen(boolean isMine) {
        this.isMine = isMine;
        if (isMine) {
            binding.shareOperationLayout.shareDel.setVisibility(View.VISIBLE);
            binding.shareOperationLayout.shareDel.setOnClickListener(v -> {
                //??????
                if (onDelClickListener != null) {
                    popupWindow.dismiss();
                    onDelClickListener.onDelClick();
                }
            });
        } else {
            binding.shareOperationLayout.shareDel.setVisibility(View.GONE);
        }

    }

    public void setShowType(int showType) {
        this.showType = showType;
        showType();
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    private void init() {
        contentView = View.inflate(context.get(), R.layout.pop_share, null);
        binding = DataBindingUtil.bind(contentView);
        if (popupWindow == null) {
            popupWindow = new PopupWindow(binding.getRoot(), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        popupWindow.setAnimationStyle(R.style.DialogBottomAnimation);
        popupWindow.setOnDismissListener(() -> {

        });
        binding.close.setOnClickListener(view -> popupWindow.dismiss());
        shareFriendAdapter = new ShareFriendAdapter(R.layout.item_share_user);

        binding.atUserList.setLayoutManager(new LinearLayoutManager(context.get(), LinearLayoutManager.HORIZONTAL, false));
        binding.atUserList.setAdapter(shareFriendAdapter);
        imMessageMgr = new IMMessageMgr(mContext);

        //?????????
        imMessageMgr.initialize(UserInfoMgr.getInstance().getUserInfo().getId(), UserInfoMgr.getInstance().getUserSig(), TCGlobalConfig.SDKAPPID, new IMMessageMgr.Callback() {
            @Override
            public void onError(final int code, final String errInfo) {
                String msg = "[IM] ???????????????[" + errInfo + ":" + code + "]";
                LogUtils.v(Constant.TAG_LIVE, msg);
            }

            @Override
            public void onSuccess(Object... args) {
            }
        });


        shareFriendAdapter.setOnItemClickListener(((adapter, view, position) -> {

        }));

        if (shareBean != null)
            getFriends();


        //???????????????
        if (shareBean == null) {

            if (showType == 1 || showType == 0) {
                binding.tvTitle.setText("???????????????");
            }


            binding.shareClsLayout.shareFriend.setEnabled(false);
            binding.shareClsLayout.shareFriend.setAlpha(disEnable);

            binding.shareClsLayout.shareWxFriend.setEnabled(false);
            binding.shareClsLayout.shareWxFriend.setAlpha(disEnable);

            binding.shareClsLayout.shareWxCircle.setEnabled(false);
            binding.shareClsLayout.shareWxCircle.setAlpha(disEnable);

            binding.shareOperationLayout.shareSaveNative.setEnabled(false);
            binding.shareOperationLayout.shareSaveNative.setAlpha(disEnable);

            binding.shareOperationLayout.shareSaveNative.setEnabled(false);
            binding.shareOperationLayout.shareSaveNative.setAlpha(disEnable);

            binding.shareOperationLayout.shareSaveUrl.setEnabled(false);
            binding.shareOperationLayout.shareSaveUrl.setAlpha(disEnable);

            binding.shareOperationLayout.shareColl.setEnabled(false);
            binding.shareOperationLayout.shareColl.setAlpha(disEnable);

            binding.shareOperationLayout.shareReport.setEnabled(false);
            binding.shareOperationLayout.shareReport.setAlpha(disEnable);

            binding.shareOperationLayout.sharePost.setEnabled(false);
            binding.shareOperationLayout.sharePost.setAlpha(disEnable);

            binding.shareOperationLayout.sharePost.setEnabled(false);
            binding.shareOperationLayout.sharePost.setAlpha(disEnable);
        }

        binding.shareClsLayout.shareFriend.setOnClickListener(view -> {
            //????????????
            //??????????????????


            gson = new Gson();

            messageCustom = new MCustomMsgBean();

            messageCustom.msgType = showType;
            messageCustom.content = shareBean.introduce;
            messageCustom.cover = shareBean.coverUrl;
            messageCustom.userAvatar = shareBean.userAvatar;
            messageCustom.userName = shareBean.userNickname;
            messageCustom.resourceId = shareBean.resourceId;
            messageCustom.hasRoomPwd = shareBean.hasRoomPwd;

            if (videoInfo != null) {
                videoInfo.resourceUrl = videoInfo.oldResourceUrl;
                messageCustom.resource = new Gson().toJson(videoInfo);
            }
            shareData = gson.toJson(messageCustom);

            Bundle bundle = new Bundle();
            Intent intent = new Intent(mContext, FriendsListActivity.class);
            bundle.putInt("type", 1);
            shareTime = System.currentTimeMillis();
            bundle.putLong("time", shareTime);
            intent.putExtras(bundle);
            mContext.startActivity(intent);

        });
        binding.shareClsLayout.shareWxFriend.setOnClickListener(view -> {
            //??????????????????
            if (shareBean == null) {
                ToastUtils.showShort("???????????????");
                return;
            }
            if (!AppApplication.mWXapi.isWXAppInstalled()) {
                ToastUtil.toastShortMessage("??????????????????????????????");
                return;
            }
            shareWebPage(1);
        });
        binding.shareClsLayout.shareWxCircle.setOnClickListener(view -> {

            if (!AppApplication.mWXapi.isWXAppInstalled()) {
                ToastUtil.toastShortMessage("??????????????????????????????");
                return;
            }
            //?????????????????????
            shareWebPage(2);
        });
        binding.shareOperationLayout.shareSaveNative.setOnClickListener(view -> {
            if (!TextUtils.isEmpty(shareBean.downUrl)) {
                if (permissions != null) {
                    //???????????? ????????????url????????????????????????
                    disposable = permissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                            .subscribe(granted -> {
                                if (granted) {
                                    saveVideo(shareBean.downUrl);
                                }
                            });
                }


            }

        });
        binding.shareOperationLayout.shareSaveUrl.setOnClickListener(view -> {
            //????????????
            toCopy();
        });
        binding.shareOperationLayout.shareColl.setOnClickListener(view -> {
            //??????
        });
        binding.shareOperationLayout.shareReport.setOnClickListener(view -> {
            //??????
            //????????????
            if (showType == VIDEO_TYPE) {
                UniService.startService(mContext, Constant.JKZL_MINI_APP_ID, 99, Constant.VIDEO_REPORT + shareBean.id);
            } else if (showType == TP_TYPE) {//????????????
                UniService.startService(mContext, Constant.JKZL_MINI_APP_ID, 99, Constant.USERDIARY_REPORT + shareBean.id);
            } else if (showType == LIVE_TYPE) {//????????????
                UniService.startService(mContext, Constant.JKZL_MINI_APP_ID, 99, Constant.LIVE_REPORT + shareBean.id);
            }
        });
        binding.shareOperationLayout.sharePost.setOnClickListener(view -> {
            //????????????
            if (onClickListener != null) {
                onClickListener.onCreatePostClick();
            }
        });
        binding.rlContainer.setOnClickListener(view -> popupWindow.dismiss());

        datas.observe((LifecycleOwner) mContext, datas -> {
            if (datas != null && datas.size() > 0) {
                binding.atUserList.setVisibility(View.VISIBLE);
                shareFriendAdapter.setNewInstance(datas);
            } else {
                binding.friendLayout.setVisibility(View.GONE);
            }
        });

        uniSubscribe = RxBus.getDefault().toObservable(UniEventBean.class).subscribe(event -> {
            if (event != null) {
                if (event.msgId == CodeTable.UNI_RELEASE) {
                    if (event.taskId == 99) {
                        UniUtil.openUniApp(mContext, Constant.JKZL_MINI_APP_ID, event.jumpUrl, null, event.isSelfUni);
                        hide();
                    }
                } else if (event.msgId == CodeTable.UNI_RELEASE_FAIL) {
                    //ToastUtils.showShort("?????????????????????");
                }
            }
        });

        Subscribe = RxBus.getDefault().toObservable(EventBean.class).subscribe(event -> {
            if (event != null) {
                //??????????????????????????? ????????????
                if (event.msgId == CodeTable.SHARE_MSG && event.time == shareTime) {
                    shareMsg(event);
                }
            }
        });
    }

    private void shareMsg(EventBean event) {
        //???????????????
        //                    imMessageMgr.sendC2CCustomMessage(event.content, shareData, new IMMessageMgr.Callback() {
        //                        @Override
        //                        public void onError(int code, String errInfo) {
        //                            LogUtils.v("im-send", "errInfo: " + errInfo);
        //                            LogUtils.v("im-send", "code: " + code);
        //                        }
        //
        //                        @Override
        //                        public void onSuccess(Object... args) {
        //                            Log.v("CustomChatController", "IM Custom Data: " + shareData);
        //                            ToastUtil.toastShortMessage("????????????");
        //                        }
        //                    });

        //??????IM??????????????? ???????????????
        V2TIMMessage v2TIMMessage = V2TIMManager.getMessageManager().createCustomMessage(shareData.getBytes(), "???????????????", null);
        V2TIMManager.getMessageManager().sendMessage(v2TIMMessage, event.content, null, V2TIMMessage.V2TIM_PRIORITY_DEFAULT, false, null, new V2TIMSendCallback<V2TIMMessage>() {
            @Override
            public void onError(int code, String desc) {
                LogUtils.v("im-send", "errInfo: " + desc);
                LogUtils.v("im-send", "code: " + code);
            }

            @Override
            public void onSuccess(V2TIMMessage v2TIMMessage) {
                ToastUtil.toastShortMessage("????????????");
            }

            @Override
            public void onProgress(int progress) {

            }
        });

        //????????????????????????????????????????????????????????????
        //                    customMessage = MessageInfoUtil.buildCustomMessage(shareData);
        //                    IBaseMessageSender messageSender = C2CChatManagerKit.getInstance();
        //                    if (messageSender != null) {
        //                        // ????????????
        //                        messageSender.sendMessage(customMessage, null, event.content,
        //                                false, false, new IUIKitCallBack() {
        //                                    @Override
        //                                    public void onSuccess(Object data) {
        //                                    }
        //
        //                                    @Override
        //                                    public void onError(String module, int errCode, String errMsg) {
        //                                    }
        //                                });
        //                    }
    }


    private ClipboardManager cm;
    private ClipData mClipData;

    private void toCopy() {
        //???????????????????????????
        cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        // ?????????????????????ClipData
        mClipData = ClipData.newPlainText("Label", Constant.baseH5Url + "#" + shareBean.url);
        // ???ClipData?????????????????????????????????
        cm.setPrimaryClip(mClipData);
        ToastUtil.toastShortMessage("????????????");
    }

    /*
     * ????????????
     */
    private void shareWebPage(int type) {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = Constant.baseH5Url + "#" + shareBean.url;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        //???????????????????????????
        if (TextUtils.isEmpty(shareBean.title)) {
            msg.title = "????????????";
        } else {
            msg.title = shareBean.title;
        }
        if (TextUtils.isEmpty(shareBean.subTitle)) {
            msg.description = shareBean.introduce;
        } else {
            msg.description = shareBean.subTitle;
        }


        Glide.with(mContext).asBitmap().load(shareBean.coverUrl).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                //??????????????????????????? 32KB
                Bitmap bitmap = compressImage(resource);

                msg.setThumbImage(bitmap);

                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.message = msg;

                if (type == 1) {
                    req.scene = SendMessageToWX.Req.WXSceneSession;
                } else if (type == 2) {
                    req.scene = SendMessageToWX.Req.WXSceneTimeline;
                }
                boolean sendReq = AppApplication.mWXapi.sendReq(req);
                if (sendReq) {
                    hide();
                }
            }

        }); //???????????????asBitmap????????????????????????
    }

    /**
     * ????????????????????????
     *
     * @param image
     * @return
     */
    public Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 100;
        image.compress(Bitmap.CompressFormat.JPEG, options, baos);// ???????????????????????????100????????????????????????????????????????????????baos???
        while (baos.toByteArray().length / 1024 > 31) { // ?????????????????????????????????????????????100kb,??????????????????
            baos.reset(); // ??????baos?????????baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// ????????????options%?????????????????????????????????baos???
            options -= 10;// ???????????????10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// ?????????????????????baos?????????ByteArrayInputStream???
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// ???ByteArrayInputStream??????????????????
        return bitmap;
    }


    private void showType() {
        if (showType == VIDEO_TYPE) {
            binding.shareOperationLayout.sharePost.setVisibility(View.GONE);
        } else if (showType == TP_TYPE) {
            binding.shareOperationLayout.shareSaveNative.setVisibility(View.GONE);
            binding.shareOperationLayout.shareColl.setVisibility(View.GONE);
            binding.shareOperationLayout.sharePost.setVisibility(View.VISIBLE);
        } else if (showType == LIVE_TYPE) {
            binding.shareOperationLayout.shareSaveNative.setVisibility(View.GONE);
            binding.shareOperationLayout.shareColl.setVisibility(View.GONE);
            binding.shareOperationLayout.sharePost.setVisibility(View.GONE);
        }
    }

    public void setShareData(List<? extends IShareUser> shareData) {
        this.userList = shareData;
    }

    public void show(View Parent) {
        if (popupWindow == null) {
            init();
        }
        popupWindow.showAtLocation(binding.getRoot(), Gravity.BOTTOM, 0, 0);
    }


    public void hide() {
        popupWindow.dismiss();
    }


    private MutableLiveData<List<ZBUserListBean>> datas = new MutableLiveData<>();
    private int page = 1;
    private int pageSize = 10;

    public void getFriends() {
        //?????????????????????????????? ??????????????????
        binding.atUserList.setVisibility(View.GONE);
        //        UserRepository.getInstance().getFriendsList(datas, page, pageSize);
    }


    private void saveVideo(String url) {
        File downLoadPath = DownloadUtil.get().getSaveFileFromUrl(shareBean.oldUrl, mContext, "Video");
        if (downloadProgress == null) {
            downloadProgress = new DownLoadProgressDialog(mContext);
            downloadProgress.setICancelDownLoad(() -> DownloadUtil.get().cancelDownload());
        }
        downloadProgress.show();
        DownloadUtil.get().download(url, downLoadPath, new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess(File file) {
                ToastUtil.toastLongMessage("?????????????????????JKZL/Video");
                //???????????????
                mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file)));
                downloadProgress.dismiss();
            }

            @Override
            public void onDownloading(int progress) {
                downloadProgress.setProgress(progress);
            }

            @Override
            public void onDownloadFailed() {
                downloadProgress.dismiss();
            }
        });
    }

    public interface OnClickListener {
        void onCreatePostClick();
    }

    public interface OnDelClickListener {
        void onDelClick();
    }
}
