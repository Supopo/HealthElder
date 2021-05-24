package com.xaqinren.healthyelders.modulePicture.dialog;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.ViewUtils;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.dcloud.zxing2.BarcodeFormat;
import com.dcloud.zxing2.EncodeHintType;
import com.dcloud.zxing2.MultiFormatWriter;
import com.dcloud.zxing2.WriterException;
import com.dcloud.zxing2.common.BitMatrix;
import com.dcloud.zxing2.qrcode.decoder.ErrorCorrectionLevel;
import com.tencent.bugly.proguard.C;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.qcloud.tim.uikit.utils.FileUtil;
import com.tencent.qcloud.tim.uikit.utils.ScreenUtil;
import com.tencent.qcloud.ugckit.utils.BitmapUtils;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.DialogPostBinding;
import com.xaqinren.healthyelders.global.AppApplication;
import com.xaqinren.healthyelders.moduleLiteav.dialog.CreatePostBean;
import com.xaqinren.healthyelders.utils.LogUtils;
import com.xaqinren.healthyelders.utils.QRCodeUtils;
import com.xaqinren.healthyelders.widget.BottomDialog;

import java.io.File;
import java.util.Hashtable;

import jp.wasabeef.glide.transformations.BlurTransformation;
import me.goldze.mvvmhabit.utils.ToastUtils;

public class PostPop extends BottomDialog {
    DialogPostBinding binding;
    private CreatePostBean postBean;
    private String TAG = "PostPop";


    public PostPop(Context context, CreatePostBean postBean,int w , int h) {
        super(context, View.inflate(context, R.layout.dialog_post, null), null, w, h, Gravity.BOTTOM);
        this.postBean = postBean;
    }

    public void refreshData(CreatePostBean postBean) {
        this.postBean = postBean;
        upUI();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.bind(getView());
        binding.shareWxCircle.setOnClickListener(view -> {

        });
        binding.shareWxFriend.setOnClickListener(view -> {
            shareWebPage();
        });
        binding.shareSaveLocal.setOnClickListener(view -> {
            saveCanvas();
        });
        upUI();
    }

    private void saveCanvas() {
        Bitmap bitmap = BitmapUtils.createViewBitmap(binding.saveLayout);
        File directory_pictures = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        getContext().getExternalMediaDirs();
        String path = FileUtil.saveBitmap(directory_pictures.getAbsolutePath(), bitmap);
        LogUtils.e(TAG, "保存海报->" + path);

        Uri localUri = Uri.fromFile(new File(path));
        Intent localIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri);
        getContext().sendBroadcast(localIntent);
        ToastUtils.showShort("保存成功");
    }

    private void upUI() {
        Glide.with(getContext())
                .asBitmap()
                .load(postBean.getUrl())
                .apply(RequestOptions.bitmapTransform(new BlurTransformation()))
                .into(binding.bgIv);

        Glide.with(getContext())
                .load(postBean.getUrl())
                .into(binding.photoIv);

        Glide.with(getContext())
                .load(postBean.getAvatar())
                .into(binding.avatar);

        binding.nickname.setText(postBean.getNickName());
        binding.content.setText(postBean.getContent());
        int size = (int) getContext().getResources().getDimension(R.dimen.dp_60);
        try {
            Bitmap code = QRCodeUtils.Create2DCode("测试", size, size);
            binding.qrCode.setImageBitmap(code);
        } catch (WriterException e) {
            e.printStackTrace();
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

        Glide.with(getContext()).asBitmap().load(url).into(new SimpleTarget<Bitmap>() {
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


}
