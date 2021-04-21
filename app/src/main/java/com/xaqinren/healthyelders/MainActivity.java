package com.xaqinren.healthyelders;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.xaqinren.healthyelders.databinding.ActivityMainBinding;
import com.xaqinren.healthyelders.moduleHome.fragment.GirlsFragment;
import com.xaqinren.healthyelders.moduleHome.fragment.XxxFragment;
import com.xaqinren.healthyelders.moduleHome.viewModel.MainViewModel;
import com.xaqinren.healthyelders.moduleZhiBo.activity.ZhiboOverActivity;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseActivity;


public class MainActivity extends BaseActivity<ActivityMainBinding, MainViewModel> {
    private List<Fragment> mFragments;
    private double firstTime;
    private TextView oldView;
    private TextView selectView;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_main;
    }

    @Override
    public int initVariableId() {
        return com.xaqinren.healthyelders.BR.viewModel;
    }

    @Override
    public void initData() {
        setStatusBarTransparent();
        rlTitle.setVisibility(View.GONE);
        ivLeft.setVisibility(View.INVISIBLE);
        //初始化Fragment
        initFragment();
    }

    private void initFragment() {
        mFragments = new ArrayList<>();
        mFragments.add(new XxxFragment());
        mFragments.add(new GirlsFragment());
        mFragments.add(new XxxFragment());
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
        binding.tvMenu4.setOnClickListener(lis -> {
            selectView = binding.tvMenu4;
            initBottomTab();
            oldView = binding.tvMenu4;
        });
        binding.ivLive.setOnClickListener(lis -> {
            startActivity(ZhiboOverActivity.class);
        });
    }


    private void initBottomTab() {

        Drawable dawable = getResources().getDrawable(R.mipmap.line_bq);
        dawable.setBounds(0, 0, dawable.getMinimumWidth(), dawable.getMinimumHeight());

        oldView.setCompoundDrawables(null, null, null, null);
        oldView.setTextColor(getResources().getColor(R.color.gray_666));
        oldView.setTextSize(16);

        selectView.setCompoundDrawables(null, null, null, dawable);
        selectView.setTextColor(getResources().getColor(R.color.color_DC3530));
        selectView.setTextSize(18);

        switch (selectView.getId()) {
            case R.id.tv_menu1:
                commitAllowingStateLoss(0);
                break;
            case R.id.tv_menu2:
                commitAllowingStateLoss(1);
                break;
            case R.id.tv_menu3:
                commitAllowingStateLoss(2);
                break;
            case R.id.tv_menu4:
                commitAllowingStateLoss(3);
                break;

        }

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

    /**
     * 二次点击（返回键）退出
     */
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                long secondTime = System.currentTimeMillis();
                if (secondTime - firstTime > 2000) {
                    //如果两次按键时间间隔大于2秒，则不退出
                    Toast.makeText(this, "再按一次退出程序~", Toast.LENGTH_SHORT).show();
                    firstTime = secondTime;//更新firstTime
                    return true;
                } else {
                    //两次按键小于2秒时，退出应用
                    System.exit(0);
                }
                break;
        }
        return super.onKeyUp(keyCode, event);
    }
}
