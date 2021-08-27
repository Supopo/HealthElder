package com.xaqinren.healthyelders.moduleZhiBo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.databinding.ActivityStartLiveBinding;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.moduleLiteav.fragment.StartLiteAVFragment;
import com.xaqinren.healthyelders.moduleZhiBo.fragment.StartLiveFragment;
import com.xaqinren.healthyelders.moduleZhiBo.viewModel.StartLiveUiViewModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.bus.RxBus;

/**
 * 开启直播-发小视频页面
 */
public class StartLiveActivity extends BaseActivity<ActivityStartLiveBinding, BaseViewModel> implements StartLiteAVFragment.OnFragmentStatusListener {
    private List<Fragment> mFragments;
    public StartLiteAVFragment startLiteAVFragment;
    private StartLiveUiViewModel liveUiViewModel;
    private int currentFragmentPosition = 0;
    private boolean isLiteAVRecode = false;
    private String TAG = getClass().getSimpleName();
    private Disposable subscribe;
    private int po;


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
        liveUiViewModel = createViewModel(this, StartLiveUiViewModel.class);
        super.onCreate(savedInstanceState);
        // 必须在代码中设置主题(setTheme)或者在AndroidManifest中设置主题(android:theme)
        setTheme(com.hjyy.liteav.R.style.RecordActivityTheme);
    }

    @Override
    public void initData() {
        setStatusBarTransparent();
        //初始化Fragment
        initFragment();

        subscribe = RxBus.getDefault().toObservable(EventBean.class).subscribe(o -> {
            if (o.msgId == CodeTable.NO_CARD) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startLiteAVFragment.onActivityStop();
                        finish();
                    }
                }, 500);
            } else if (o.msgId == CodeTable.CODE_SUCCESS && o.content.equals("overLive-zb")) {
                finish();
            }
        });
    }

    private void initFragment() {
        startLiteAVFragment = new StartLiteAVFragment();
        startLiteAVFragment.setOnFragmentStatusListener(this);
        mFragments = new ArrayList<>();
        mFragments.add(startLiteAVFragment);

        currentFragmentPosition = 1;
        commitAllowingStateLoss(0);
        liveUiViewModel.getCurrentPage().setValue(currentFragmentPosition);
        //默认选中第一个
        binding.llMenu.selectTab(binding.llMenu.getTabAt(currentFragmentPosition));
        binding.llMenu.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                po = tab.getPosition();
                /*if (po == 2) {
                    PictureSelector.create(StartLiveActivity.this)
                            .openCamera(PictureMimeType.ofImage())
                            .isAndroidQTransform(false)//开启沙盒 高版本必须选择不然拿不到小图
                            .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
                    return;
                }*/

                //                if (po == 2) {
                //                    po = 1;
                //                    commitAllowingStateLoss(po);
                //                    liveUiViewModel.getCurrentPage().setValue(2);
                //                } else {
                //                    commitAllowingStateLoss(po);
                //                    liveUiViewModel.getCurrentPage().setValue(po);
                //                }

                commitAllowingStateLoss(0);
                liveUiViewModel.getCurrentPage().setValue(po);

                currentFragmentPosition = po;
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

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
    protected void onPause() {
        super.onPause();

        if (currentFragmentPosition == 1) {
            startLiteAVFragment.onActivityStop();
        }
    }

    boolean isMusicRestart = false;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        boolean music = intent.getBooleanExtra("music", false);
        if (music) {
            if (currentFragmentPosition == 1) {
                isMusicRestart = true;
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (currentFragmentPosition == 1) {
            startLiteAVFragment.onActivityRestart();
            if (isMusicRestart)
                startLiteAVFragment.onMusicSelActivityBack();
        }
        isMusicRestart = false;
    }

    @Override
    protected void onDestroy() {
        startLiteAVFragment.setOnFragmentStatusListener(null);
        super.onDestroy();
        subscribe.dispose();
    }

    @Override
    public void isRecode(boolean is) {
        isLiteAVRecode = is;
        if (isLiteAVRecode) {
            //隐藏
            binding.llMenu.setVisibility(View.INVISIBLE);
        } else {
            //显示
            binding.llMenu.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CodeTable.MUSIC_BACK) {
                onRestart();
                if (currentFragmentPosition == 1) {
                    startLiteAVFragment.onMusicSelActivityBack();
                }
            }
        }
    }
}
