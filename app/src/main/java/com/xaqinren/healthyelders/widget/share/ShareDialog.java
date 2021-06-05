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
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.qcloud.ugckit.utils.ToastUtil;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.apiserver.UserRepository;
import com.xaqinren.healthyelders.databinding.PopShareBinding;
import com.xaqinren.healthyelders.global.AppApplication;
import com.xaqinren.healthyelders.moduleHome.bean.ShareBean;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ZBUserListBean;
import com.xaqinren.healthyelders.utils.DownloadUtil;
import com.xaqinren.healthyelders.utils.UrlUtils;
import com.xaqinren.healthyelders.widget.DownLoadProgressDialog;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.List;

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
    private int showType = VIDEO_TYPE;
    private Context mContext;
    private DownLoadProgressDialog downloadProgress;
    private OnClickListener onClickListener;
    private ShareBean shareBean;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public ShareDialog(Context context) {
        this.context = new SoftReference<>(context);
        mContext = context;
        init();
        showType();
    }

    public ShareDialog(Context context,int showType) {
        this.context = new SoftReference<>(context);
        this.showType = showType;
        mContext = context;
        init();
        showType();
    }

    public ShareDialog(Context context, ShareBean shareInfo,int showType) {
        this.context = new SoftReference<>(context);
        this.showType = showType;
        mContext = context;
        shareBean = shareInfo;
        init();
        showType();
    }
    public ShareDialog(Context context, ShareBean shareInfo) {
        this.context = new SoftReference<>(context);
        mContext = context;
        shareBean = shareInfo;
        init();
    }

    public void setShowType(int showType) {
        this.showType = showType;
        showType();
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

        getFriends();

        binding.shareClsLayout.shareFriend.setOnClickListener(view -> {
            //私信朋友
        });
        binding.shareClsLayout.shareWxFriend.setOnClickListener(view -> {
            //私信微信朋友
            shareWebPage(1);
        });
        binding.shareClsLayout.shareWxCircle.setOnClickListener(view -> {
            //私信微信朋友圈
            shareWebPage(2);
        });
        binding.shareOperationLayout.shareSaveNative.setOnClickListener(view -> {
            if (!TextUtils.isEmpty(shareBean.downUrl)) {
                //保存本地
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
        });
        binding.shareOperationLayout.sharePost.setOnClickListener(view -> {
            //生成海报
            if (onClickListener != null) {
                onClickListener.onCreatePostClick();
            }
        });
        binding.rlContainer.setOnClickListener(view -> popupWindow.dismiss());

        datas.observe((LifecycleOwner) mContext, datas -> {
            if (datas != null) {
                shareFriendAdapter.setNewInstance(datas);
            }
        });
    }

    private ClipboardManager cm;
    private ClipData mClipData;

    private void toCopy() {
        //获取剪贴板管理器：
        cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
        mClipData = ClipData.newPlainText("Label", "复制内容");
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData);
        ToastUtil.toastShortMessage("复制成功");
    }

    /*
     * 分享链接
     */
    private void shareWebPage(int type) {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = shareBean.url;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = shareBean.title;
        msg.description = shareBean.subTitle;

        Glide.with(mContext).asBitmap().load(UrlUtils.resetImgUrl(shareBean.coverUrl,100,100)).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                msg.setThumbImage(resource);

                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.message = msg;

                if (type == 1) {
                    req.scene = SendMessageToWX.Req.WXSceneSession;
                } else if (type == 2) {
                    req.scene = SendMessageToWX.Req.WXSceneTimeline;
                }
                AppApplication.mWXapi.sendReq(req);
            }

        }); //方法中设置asBitmap可以设置回调类型
    }


    private void showType() {
        if (showType == VIDEO_TYPE) {
            binding.shareOperationLayout.sharePost.setVisibility(View.GONE);
        } else if (showType == TP_TYPE) {
            binding.shareOperationLayout.shareSaveNative.setVisibility(View.GONE);
            binding.shareOperationLayout.shareColl.setVisibility(View.GONE);
            binding.shareOperationLayout.shareReport.setVisibility(View.GONE);
            binding.shareOperationLayout.sharePost.setVisibility(View.VISIBLE);
        }else if (showType == LIVE_TYPE) {
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
        UserRepository.getInstance().getFriendsList(datas, page, pageSize);
    }

    private void saveVideo(String url) {
        File downLoadUrl = DownloadUtil.get().getSaveFileFromUrl(url, mContext, "video");
        if (downloadProgress == null) {
            downloadProgress = new DownLoadProgressDialog(mContext);
            downloadProgress.setICancelDownLoad(() -> DownloadUtil.get().cancelDownload());
        }
        downloadProgress.show();
        DownloadUtil.get().download(url, downLoadUrl, new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess() {
                ToastUtil.toastLongMessage("视频已下载至：" + downLoadUrl.getPath());
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
}
