package com.xaqinren.healthyelders.moduleliteav.activity;

import android.os.Bundle;

import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivityLiteAvBinding;
import com.xaqinren.healthyelders.moduleliteav.viewModel.LiveAVViewModel;

import me.goldze.mvvmhabit.base.BaseActivity;

/**
 * 短视频
 */
public class LiveAVActivity extends BaseActivity<ActivityLiteAvBinding, LiveAVViewModel>{
    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_lite_av;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();

    }
}
