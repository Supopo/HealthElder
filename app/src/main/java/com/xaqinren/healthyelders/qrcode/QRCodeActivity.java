package com.xaqinren.healthyelders.qrcode;

import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.king.zxing.CaptureActivity;
import com.tencent.qcloud.tim.uikit.utils.ScreenUtil;
import com.xaqinren.healthyelders.R;

public class QRCodeActivity extends CaptureActivity {
    ImageView flashlightIV;
    boolean isTorchEnabled = false;
    RelativeLayout titleLayout;
    ImageView back;
    @Override
    public void initUI() {
        super.initUI();
        flashlightIV = findViewById(R.id.flashlightIV);
        titleLayout = findViewById(R.id.titleLayout);
        back = findViewById(R.id.back);
        back.setOnClickListener(v -> finish());
        int statusBarHeight = ScreenUtil.getStatusBarHeight();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) titleLayout.getLayoutParams();
        params.topMargin = statusBarHeight;
        titleLayout.setLayoutParams(params);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_qr_code;
    }

    @Override
    public int getFlashlightId() {
        return R.id.flashlight;
    }

    @Override
    protected void onClickFlashlight() {
        super.onClickFlashlight();
        isTorchEnabled = !isTorchEnabled;
        flashlightIV.setSelected(isTorchEnabled);
    }
}
