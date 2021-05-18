package com.xaqinren.healthyelders.moduleLiteav.activity;

import android.os.Bundle;

import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivityChooseMusicBinding;
import com.xaqinren.healthyelders.moduleLiteav.viewModel.ChooseMusicViewModel;



import me.goldze.mvvmhabit.base.BaseActivity;

public class ChooseMusicActivity extends BaseActivity<ActivityChooseMusicBinding, ChooseMusicViewModel> {

    private final String TAG = "ChooseMusicActivity";

    private int cSheetPage = 1, cSheetPageSize = 10;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_choose_music;
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
