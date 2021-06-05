package com.xaqinren.healthyelders.moduleMine.activity;

import android.os.Bundle;

import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivityLookAuthBinding;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.base.BaseViewModel;

public class LookAuthActivity extends BaseActivity <ActivityLookAuthBinding, BaseViewModel>{

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_look_auth;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

}
