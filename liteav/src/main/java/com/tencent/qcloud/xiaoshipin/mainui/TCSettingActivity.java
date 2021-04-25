package com.tencent.qcloud.xiaoshipin.mainui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hjyy.liteav.R;

public class TCSettingActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lite_activity_setting);

        findViewById(R.id.layout_language).setOnClickListener(this);
        findViewById(R.id.imgBtn_back).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.layout_language) {
            Intent intent = new Intent(this, TCLanguagaSettingActivity.class);
            startActivity(intent);
        } else if (id == R.id.imgBtn_back) {
            finish();
        }
    }
}
