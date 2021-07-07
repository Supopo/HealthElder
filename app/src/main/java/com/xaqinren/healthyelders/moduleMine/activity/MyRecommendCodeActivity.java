package com.xaqinren.healthyelders.moduleMine.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.dcloud.zxing2.WriterException;
import com.king.zxing.CameraScan;
import com.king.zxing.CaptureActivity;
import com.tencent.qcloud.tim.uikit.utils.FileUtil;
import com.tencent.qcloud.tim.uikit.utils.ScreenUtil;
import com.tencent.qcloud.ugckit.utils.BitmapUtils;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.databinding.ActivityMyRecommendBinding;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleLogin.bean.UserInfoBean;
import com.xaqinren.healthyelders.qrcode.QRCodeActivity;
import com.xaqinren.healthyelders.utils.GlideUtil;
import com.xaqinren.healthyelders.utils.LogUtils;
import com.xaqinren.healthyelders.utils.QRCodeUtils;

import java.io.File;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.utils.ToastUtils;
import me.goldze.mvvmhabit.utils.Utils;

public class MyRecommendCodeActivity extends BaseActivity <ActivityMyRecommendBinding, BaseViewModel>{
    UserInfoBean userInfoBean;
    private int requestCode = Constant.QR_CODE;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_my_recommend;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        setStatusBarTransparent();
        setStatusBarColorWhite();

        int statusBarHeight = ScreenUtil.getStatusBarHeight();
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) binding.titleLayout.getLayoutParams();
        layoutParams.topMargin = statusBarHeight;
        binding.titleLayout.setLayoutParams(layoutParams);

        binding.title.getPaint().setFakeBoldText(true);

        rlTitle.setVisibility(View.GONE);
        userInfoBean = UserInfoMgr.getInstance().getUserInfo();
        GlideUtil.intoImageView(this,userInfoBean.getAvatarUrl(),binding.avatar);
        binding.nickname.setText(userInfoBean.getNickname());
        int size = (int) getResources().getDimension(R.dimen.dp_230);
        try {
            Bitmap code = QRCodeUtils.Create2DCode(userInfoBean.getRecommendedCode(), size, size);
            binding.code.setImageBitmap(code);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        binding.back.setOnClickListener(v -> finish());
        binding.saveLayout.setOnClickListener(v -> {
            saveCanvas();
        });
        binding.scanLayout.setOnClickListener(v -> {
            //跳转的默认扫码界面
//            startActivityForResult(new Intent(context, QRCodeActivity.class),requestCode);
        });
    }

    private void saveCanvas() {
        Bitmap bitmap = BitmapUtils.createViewBitmap(binding.canvasLayout);
        File directory_pictures = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        getExternalMediaDirs();
        String path = FileUtil.saveBitmap(directory_pictures.getAbsolutePath(), bitmap);
        LogUtils.e(TAG, "保存推荐码->" + path);

        Uri localUri = Uri.fromFile(new File(path));
        Intent localIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri);
        sendBroadcast(localIntent);
        ToastUtils.showShort("保存成功");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == this.requestCode) {
                String result = CameraScan.parseScanResult(data);
                LogUtils.e(TAG, "扫码结果 -> "+result);
            }
        }
    }
}
