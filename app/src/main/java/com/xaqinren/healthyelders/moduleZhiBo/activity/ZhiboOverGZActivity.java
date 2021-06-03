package com.xaqinren.healthyelders.moduleZhiBo.activity;

import android.os.Bundle;

import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.apiserver.LiveRepository;
import com.xaqinren.healthyelders.databinding.ActivityZhiboOverGzBinding;
import com.xaqinren.healthyelders.moduleZhiBo.viewModel.ZhiboOverGZViewModel;

import me.goldze.mvvmhabit.base.BaseActivity;

/**
 * Created by Lee. on 2021/4/30.
 * 主结束直播用户展示页面
 */
public class ZhiboOverGZActivity extends BaseActivity<ActivityZhiboOverGzBinding, ZhiboOverGZViewModel> {
    private String liveRoomRecordId;


    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_zhibo_over_gz;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initParam() {
        super.initParam();
        Bundle extras = getIntent().getExtras();
        liveRoomRecordId = (String) extras.get("liveRoomRecordId");
    }

    @Override
    public void initData() {
        super.initData();
        setStatusBarTransparent();
        viewModel.getLiveOverInfo(liveRoomRecordId);
        binding.ivClose.setOnClickListener(lis ->{
            finish();
        });
    }
}
