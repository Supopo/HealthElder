package com.xaqinren.healthyelders.moduleLiteav.activity;

import android.os.Bundle;

import com.tencent.qcloud.ugckit.UGCKitConstants;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivityChooseVideoCoverBinding;
import com.xaqinren.healthyelders.moduleLiteav.viewModel.ChooseVideoCoverViewModel;

import me.goldze.mvvmhabit.base.BaseActivity;

public class ChooseVideoCoverActivity extends BaseActivity<ActivityChooseVideoCoverBinding, ChooseVideoCoverViewModel> {
    private String mVideoPath = null;
    private String mCoverPath = null;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_choose_video_cover;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        mVideoPath = getIntent().getStringExtra(UGCKitConstants.VIDEO_PATH);
        mCoverPath = getIntent().getStringExtra(UGCKitConstants.VIDEO_COVERPATH);

        binding.cancel.setOnClickListener(view -> finish());
        binding.save.setOnClickListener(view -> {
            //选择成功
        });


    }
}
