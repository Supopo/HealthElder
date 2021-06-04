package com.xaqinren.healthyelders.moduleMine.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import com.dcloud.zxing2.WriterException;
import com.tencent.qcloud.tim.uikit.utils.FileUtil;
import com.tencent.qcloud.ugckit.utils.BitmapUtils;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.databinding.ActivityMyRecommendBinding;
import com.xaqinren.healthyelders.moduleLogin.bean.UserInfoBean;
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
            //TODO 扫码
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
}
