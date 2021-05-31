package com.xaqinren.healthyelders.moduleZhiBo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivitySettingRoomPwdBinding;
import com.xaqinren.healthyelders.widget.VerificationCodeView;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.base.BaseViewModel;

/**
 * Created by Lee. on 2021/4/22.
 * 设置直播间密码
 */
public class SettingRoomPwdActivity extends BaseActivity<ActivitySettingRoomPwdBinding, BaseViewModel> {

    private String code;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_setting_room_pwd;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        tvTitle.setText("私密房间");
        binding.vcvCode.setOnCodeFinishListener(new VerificationCodeView.OnCodeFinishListener() {
            @Override
            public void onTextChange(View view, String content) {

            }

            @Override
            public void onComplete(View view, String content) {
                code = content;
            }
        });
        binding.btnOk.setOnClickListener(lis -> {
            Intent intent = getIntent();
            intent.putExtra("pwd", code);
            setResult(1001, intent);
            finish();
        });
    }

    @Override
    public void finish() {
        super.finish();
    }
}
