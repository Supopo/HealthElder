package com.xaqinren.healthyelders.moduleMine.activity;

import android.os.Bundle;

import androidx.recyclerview.widget.GridLayoutManager;

import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivityMiniProgramBinding;
import com.xaqinren.healthyelders.moduleMine.adapter.MiniProgramAdapter;
import com.xaqinren.healthyelders.moduleMine.viewModel.MiniProgramViewModel;


import me.goldze.mvvmhabit.base.BaseActivity;

public class MiniActivity extends BaseActivity <ActivityMiniProgramBinding, MiniProgramViewModel>{
    private MiniProgramAdapter adapter;
    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_mini_program;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        viewModel.getNative();
        viewModel.getRemote();
        adapter = new MiniProgramAdapter(R.layout.item_mini_program);
        binding.content.setLayoutManager(new GridLayoutManager(this, 4));
        binding.content.setAdapter(adapter);
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.nativeProgram.observe(this, miniProgramBeans -> {

        });
        viewModel.miniProgram.observe(this, miniProgramBeans -> {

        });
    }
}
