package com.tencent.qcloud.xiaoshipin.userinfo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.hjyy.liteav.R;


public class TCAboutActivity extends Activity {
    private ImageView mIVBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lite_activity_about);
        initView();
    }

    private void initView() {
        mIVBack = (ImageView)findViewById(R.id.iv_about_back);
        mIVBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
