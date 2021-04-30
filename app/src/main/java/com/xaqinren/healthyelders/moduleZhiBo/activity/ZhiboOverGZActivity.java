package com.xaqinren.healthyelders.moduleZhiBo.activity;

import android.os.Bundle;

import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivityZhiboOverGzBinding;
import com.xaqinren.healthyelders.moduleZhiBo.viewModel.ZhiboOverGZViewModel;

import me.goldze.mvvmhabit.base.BaseActivity;

/**
 * Created by Lee. on 2021/4/30.
 */
public class ZhiboOverGZActivity extends BaseActivity<ActivityZhiboOverGzBinding, ZhiboOverGZViewModel> {
    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_zhibo_over_gz;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }
}
