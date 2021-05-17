package com.xaqinren.healthyelders.modulePicture.activity;

import android.os.Bundle;
import android.view.View;

import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivityTackPictureBinding;
import com.xaqinren.healthyelders.moduleLiteav.fragment.StartLiteAVFragment;
import com.xaqinren.healthyelders.modulePicture.constant.Constant;
import com.xaqinren.healthyelders.widget.RecordButton;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.base.BaseViewModel;

/**
 * 拍照页
 */
public class TackPictureActivity extends BaseActivity<ActivityTackPictureBinding, BaseViewModel> {

    StartLiteAVFragment fragment;
    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_tack_picture;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        setStatusBarTransparent();
        rlTitle.setVisibility(View.GONE);
        fragment = new StartLiteAVFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constant.RECODE_MODE, RecordButton.PHOTO_MODE);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.rl_container, fragment).commit();
    }

    @Override
    protected void onStop() {
        super.onStop();
        fragment.onActivityStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        fragment.onActivityRestart();
    }

    @Override
    protected void onDestroy() {
        fragment.setOnFragmentStatusListener(null);
        super.onDestroy();
    }
}
