package com.xaqinren.healthyelders.moduleZhiBo.activity;

import android.os.Bundle;

import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivityRenzhengSuccessBinding;
import com.xaqinren.healthyelders.databinding.ActivityStartRenzheng2Binding;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.base.BaseViewModel;

/**
 * Created by Lee. on 2021/4/24.
 * 身份证认证成功
 */
public class RenZhengSuccessActivity extends BaseActivity<ActivityRenzhengSuccessBinding, BaseViewModel> {

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_renzheng_success;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        tvTitle.setText("实名认证");
        binding.btnToLive.setOnClickListener(lis -> {
            startActivity(StartLiveActivity.class);
            finish();
        });
        binding.tvName.setText(getIntent().getExtras().getString("name"));
        binding.tvNum.setText(getIntent().getExtras().getString("idNumber"));
    }

}
