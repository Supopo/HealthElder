package com.xaqinren.healthyelders.widget.share;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
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
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.qcloud.ugckit.utils.ToastUtil;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.apiserver.UserRepository;
import com.xaqinren.healthyelders.databinding.PopShareBinding;
import com.xaqinren.healthyelders.global.AppApplication;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ZBUserListBean;

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
    private int showType = VIDEO_TYPE;
    private Context mContext;

    public ShareDialog(Context context) {
        this.context = new SoftReference<>(context);
        mContext = context;
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
            shareWebPage();
            //            CommonExtKt.shareWxPage(mContext, 2, 0, "item.title", "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fhbimg.b0.upaiyun.com%2F754601d80986bd88e7ee18d14dbd17aa3b78897b27565-YPQ5qp_fw658&refer=http%3A%2F%2Fhbimg.b0.upaiyun.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1624266373&t=398646e20bbf7d28a01d0e24fc0758be", "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fhbimg.b0.upaiyun.com%2F754601d80986bd88e7ee18d14dbd17aa3b78897b27565-YPQ5qp_fw658&refer=http%3A%2F%2Fhbimg.b0.upaiyun.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1624266373&t=398646e20bbf7d28a01d0e24fc0758be");
            //            shareWx();
        });
        binding.shareClsLayout.shareWxCircle.setOnClickListener(view -> {
            //私信微信朋友圈
        });
        binding.shareOperationLayout.shareSaveNative.setOnClickListener(view -> {
            //保存本地
        });
        binding.shareOperationLayout.shareSaveUrl.setOnClickListener(view -> {
            //复制链接
        });
        binding.shareOperationLayout.shareColl.setOnClickListener(view -> {
            //收藏
        });
        binding.shareOperationLayout.shareReport.setOnClickListener(view -> {
            //举报
        });
        binding.shareOperationLayout.sharePost.setOnClickListener(view -> {
            //生成海报
        });
        binding.rlContainer.setOnClickListener(view -> popupWindow.dismiss());

        datas.observe((LifecycleOwner) mContext, datas -> {
            if (datas != null) {
                shareFriendAdapter.setNewInstance(datas);
            }
        });
    }

    private void shareWx() {
        if (AppApplication.mWXapi.isWXAppInstalled()) {

            //初始化一个 WXTextObject 对象，填写分享的文本内容
            WXTextObject textObj = new WXTextObject();
            textObj.text = "text";

            //用 WXTextObject 对象初始化一个 WXMediaMessage 对象
            WXMediaMessage msg = new WXMediaMessage();
            msg.mediaObject = textObj;
            msg.description = "text";


            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.message = msg;

            //            分享到对话:
            //            SendMessageToWX.Req.WXSceneSession
            //            分享到朋友圈:
            //            SendMessageToWX.Req.WXSceneTimeline ;
            //            分享到收藏:
            //            SendMessageToWX.Req.WXSceneFavorite
            req.scene = SendMessageToWX.Req.WXSceneSession;

            //发送给微信客户端

            AppApplication.mWXapi.sendReq(req);
        } else {
            ToastUtil.toastShortMessage("您未安装微信");
            return;
        }
    }

    /*
     * 分享链接
     */
    private void shareWebPage() {
        String url = "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fhbimg.b0.upaiyun.com%2F754601d80986bd88e7ee18d14dbd17aa3b78897b27565-YPQ5qp_fw658&refer=http%3A%2F%2Fhbimg.b0.upaiyun.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1624266373&t=398646e20bbf7d28a01d0e24fc0758be";

        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = "www.baidu.com";
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = "分享链接";
        msg.description = "分享描述";

        Glide.with(mContext).asBitmap().load(url).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                msg.setThumbImage(resource);

                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.message = msg;
                req.scene = SendMessageToWX.Req.WXSceneSession;
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
        }
    }

    public void setData(List<? extends IShareUser> data) {
        this.userList = data;
        //        shareFriendAdapter.setList(userList);
    }

    public void show(View Parent) {
        if (popupWindow == null) {
            init();
        }
        popupWindow.showAsDropDown(Parent, Gravity.BOTTOM, 0, 0);
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
}
