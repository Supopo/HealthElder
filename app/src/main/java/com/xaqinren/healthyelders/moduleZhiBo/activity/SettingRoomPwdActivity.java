package com.xaqinren.healthyelders.moduleZhiBo.activity;

import android.os.Bundle;

import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivitySettingRoomPwdBinding;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.base.BaseViewModel;

/**
 * Created by Lee. on 2021/4/22.
 * 设置直播间密码
 */
public class SettingRoomPwdActivity extends BaseActivity<ActivitySettingRoomPwdBinding, BaseViewModel> {
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
        setResult(1001);
    }

    @Override
    public void finish() {
        super.finish();
    }
}
