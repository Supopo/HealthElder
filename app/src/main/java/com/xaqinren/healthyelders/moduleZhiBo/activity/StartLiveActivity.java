package com.xaqinren.healthyelders.moduleZhiBo.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;


import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivityStartLiveBinding;
import com.xaqinren.healthyelders.moduleHome.fragment.XxxFragment;
import com.xaqinren.healthyelders.moduleZhiBo.fragment.StartLiveFragment;
import com.xaqinren.healthyelders.moduleLiteav.fragment.StartLiteAVFragment;
import com.xaqinren.healthyelders.moduleZhiBo.viewModel.StartLiveUiViewModel;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.base.BaseViewModel;

/**
 * 开启直播-发小视频页面
 */
public class StartLiveActivity extends BaseActivity<ActivityStartLiveBinding, BaseViewModel> implements StartLiteAVFragment.OnFragmentStatusListener {
    private List<Fragment> mFragments;
    private TextView oldView;
    private TextView selectView;
    private StartLiveFragment startLiveFragment;
    private StartLiteAVFragment startLiteAVFragment;
    private StartLiveUiViewModel liveUiViewModel;
    private int currentFragmentPosition = 0;
    private boolean isLiteAVRecode = false;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_start_live;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        liveUiViewModel = createViewModel(this,StartLiveUiViewModel.class);
        super.onCreate(savedInstanceState);
        // 必须在代码中设置主题(setTheme)或者在AndroidManifest中设置主题(android:theme)
        setTheme(com.hjyy.liteav.R.style.RecordActivityTheme);

    }

    @Override
    public void initData() {
        setStatusBarTransparent();
        //初始化Fragment
        initFragment();
    }

    private void initFragment() {
        startLiveFragment = new StartLiveFragment();
        startLiteAVFragment = new StartLiteAVFragment();
        startLiteAVFragment.setOnFragmentStatusListener(this);
        mFragments = new ArrayList<>();
        mFragments.add(startLiveFragment);
        mFragments.add(startLiteAVFragment);
        mFragments.add(new XxxFragment());
        //默认选中第一个
        commitAllowingStateLoss(0);
        oldView = binding.tvMenu1;

        initEvent();
    }

    private void initEvent() {
        binding.tvMenu1.setOnClickListener(lis -> {
            selectView = binding.tvMenu1;
            initBottomTab();
            oldView = binding.tvMenu1;
        });
        binding.tvMenu2.setOnClickListener(lis -> {
            selectView = binding.tvMenu2;
            initBottomTab();
            oldView = binding.tvMenu2;
        });
        binding.tvMenu3.setOnClickListener(lis -> {
            selectView = binding.tvMenu3;
            initBottomTab();
            oldView = binding.tvMenu3;
        });
    }


    private void initBottomTab() {

        Drawable dawable = getResources().getDrawable(R.mipmap.line_white);
        dawable.setBounds(0, 0, dawable.getMinimumWidth(), dawable.getMinimumHeight());

        oldView.setCompoundDrawables(null, null, null, null);
        oldView.setTextColor(getResources().getColor(R.color.color_FF9C9C9C));

        selectView.setCompoundDrawables(null, null, null, dawable);
        selectView.setTextColor(getResources().getColor(R.color.white));

        switch (selectView.getId()) {
            case R.id.tv_menu1:
                commitAllowingStateLoss(0);
                currentFragmentPosition = 0;
                break;
            case R.id.tv_menu2:
                commitAllowingStateLoss(1);
                currentFragmentPosition = 1;
                break;
            case R.id.tv_menu3:
                commitAllowingStateLoss(2);
                currentFragmentPosition = 2;
                break;
        }

    }

    private boolean canChangePage() {
        if (isLiteAVRecode) {
            startLiteAVFragment.onFragmentChange();
            return false;
        }
        return true;
    }

    private void commitAllowingStateLoss(int position) {
        hideAllFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(position + "");
        if (currentFragment != null) {
            transaction.show(currentFragment);
        } else {
            currentFragment = mFragments.get(position);
            transaction.add(R.id.frameLayout, currentFragment, position + "");
        }
        transaction.commitAllowingStateLoss();
        liveUiViewModel.getCurrentPage().setValue(position);
    }

    //隐藏所有Fragment
    private void hideAllFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        for (int i = 0; i < mFragments.size(); i++) {
            Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(i + "");
            if (currentFragment != null) {
                transaction.hide(currentFragment);
            }
        }
        transaction.commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
        if (currentFragmentPosition == 1) {
            //短视频
            if (startLiteAVFragment.onBackPress()) {
                return;
            }
        }
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (currentFragmentPosition == 1) {
            startLiteAVFragment.onActivityStop();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (currentFragmentPosition == 1) {
            startLiteAVFragment.onActivityRestart();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void isRecode(boolean is) {
        isLiteAVRecode = is;
        if (isLiteAVRecode) {
            //隐藏
            binding.llMenu.setVisibility(View.INVISIBLE);
        }else{
            //显示
            binding.llMenu.setVisibility(View.VISIBLE);
        }
    }
}
