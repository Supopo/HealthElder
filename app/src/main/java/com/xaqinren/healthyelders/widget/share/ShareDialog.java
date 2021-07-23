package com.xaqinren.healthyelders.widget.share;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.dcloud.zxing2.BarcodeFormat;
import com.dcloud.zxing2.MultiFormatWriter;
import com.dcloud.zxing2.common.BitMatrix;
import com.dcloud.zxing2.qrcode.QRCodeWriter;
import com.dcloud.zxing2.qrcode.encoder.QRCode;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.qcloud.ugckit.utils.ToastUtil;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.apiserver.UserRepository;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.databinding.PopShareBinding;
import com.xaqinren.healthyelders.global.AppApplication;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleHome.bean.ShareBean;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;
import com.xaqinren.healthyelders.moduleMine.fragment.MineFragment;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ZBUserListBean;
import com.xaqinren.healthyelders.uniApp.UniService;
import com.xaqinren.healthyelders.uniApp.UniUtil;
import com.xaqinren.healthyelders.uniApp.bean.UniEventBean;
import com.xaqinren.healthyelders.utils.DownloadUtil;
import com.xaqinren.healthyelders.utils.LogUtils;
import com.xaqinren.healthyelders.utils.UrlUtils;
import com.xaqinren.healthyelders.widget.DownLoadProgressDialog;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.ref.SoftReference;
import java.util.List;

import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.utils.StringUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;
import me.goldze.mvvmhabit.utils.compression.Luban;

import static com.xaqinren.healthyelders.global.AppApplication.mWXapi;

public class ShareDialog {
    private PopupWindow popupWindow;
    private View contentView;
    private SoftReference<Context> context;
    private PopShareBinding binding;
    private ShareFriendAdapter shareFriendAdapter;
    private List<? extends IShareUser> userList;
    public static int VIDEO_TYPE = 0;//短视频
    public static int TP_TYPE = 1;//图文
    public static int LIVE_TYPE = 2;//直播
    public static int USER_TYPE = 3;//用户
    private int showType = VIDEO_TYPE;
    private Context mContext;
    private DownLoadProgressDialog downloadProgress;
    private OnClickListener onClickListener;
    private OnDelClickListener onDelClickListener;
    private ShareBean shareBean;
    private Disposable uniSubscribe;
    //视频/日记发布者id
    private String userId;
    //当前登录用户Id
    private String selfUseId;
    private boolean isMine = false;

    private float disEnable = 0.4f;

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

    public ShareDialog(Context context, ShareBean shareInfo, int showType) {
        this.context = new SoftReference<>(context);
        this.showType = showType;
        mContext = context;
        shareBean = shareInfo;
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
                //删除
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
        popupWindow = new PopupWindow(binding.getRoot(), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
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

        if (shareBean != null)
            getFriends();


        //说明未审核
        if (shareBean == null) {
            binding.atUserList.setVisibility(View.GONE);
            binding.llShare.setVisibility(View.GONE);
            if (showType == 1 || showType == 0) {
                binding.tvTitle.setText("作品审核中");
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
            //私信朋友
        });
        binding.shareClsLayout.shareWxFriend.setOnClickListener(view -> {
            //私信微信朋友
            if (shareBean == null) {
                ToastUtils.showShort("内容未审核");
                return;
            }
            if (!AppApplication.mWXapi.isWXAppInstalled()) {
                ToastUtil.toastShortMessage("您还未安装微信客户端");
                return;
            }
            shareWebPage(1);
        });
        binding.shareClsLayout.shareWxCircle.setOnClickListener(view -> {

            if (!AppApplication.mWXapi.isWXAppInstalled()) {
                ToastUtil.toastShortMessage("您还未安装微信客户端");
                return;
            }
            //私信微信朋友圈
            shareWebPage(2);
        });

        binding.shareOperationLayout.shareSaveNative.setOnClickListener(view -> {
            if (!TextUtils.isEmpty(shareBean.downUrl)) {
                //保存本地 当前下载url应该是解密之后的
                saveVideo(shareBean.downUrl);
            }

        });
        binding.shareOperationLayout.shareSaveUrl.setOnClickListener(view -> {
            //复制链接
            toCopy();
        });
        binding.shareOperationLayout.shareColl.setOnClickListener(view -> {
            //收藏
        });
        binding.shareOperationLayout.shareReport.setOnClickListener(view -> {
            //举报
            //举报视频
            if (showType == VIDEO_TYPE) {
                UniService.startService(mContext, Constant.JKZL_MINI_APP_ID, 99, Constant.VIDEO_REPORT + shareBean.id);
            } else if (showType == TP_TYPE) {//举报日记
                UniService.startService(mContext, Constant.JKZL_MINI_APP_ID, 99, Constant.USERDIARY_REPORT + shareBean.id);
            } else if (showType == LIVE_TYPE) {//举报直播
                UniService.startService(mContext, Constant.JKZL_MINI_APP_ID, 99, Constant.LIVE_REPORT + shareBean.id);
            }
        });
        binding.shareOperationLayout.sharePost.setOnClickListener(view -> {
            //生成海报
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
                    //ToastUtils.showShort("打开小程序失败");
                }
            }
        });
    }


    private ClipboardManager cm;
    private ClipData mClipData;

    private void toCopy() {
        //获取剪贴板管理器：
        cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
        mClipData = ClipData.newPlainText("Label", shareBean.url);
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData);
        ToastUtil.toastShortMessage("复制成功");
    }

    /*
     * 分享链接
     */
    private void shareWebPage(int type) {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = Constant.baseH5Url + "#" + shareBean.url;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = shareBean.title;
        msg.description = shareBean.subTitle;


        Glide.with(mContext).asBitmap().load(shareBean.coverUrl).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                //限制内容大小不超过 32KB
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

        }); //方法中设置asBitmap可以设置回调类型
    }

    /**
     * 图片质量压缩方法
     *
     * @param image
     * @return
     */
    public Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 30, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 90;
        while (baos.toByteArray().length / 1024 > 30) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset(); // 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
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

    public void setData(List<? extends IShareUser> data) {
        this.userList = data;
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
        //暂时没有分享好友功能 先隐藏，注释
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
            public void onDownloadSuccess() {
                ToastUtil.toastLongMessage("视频已下载至：" + downLoadPath.getPath());
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
