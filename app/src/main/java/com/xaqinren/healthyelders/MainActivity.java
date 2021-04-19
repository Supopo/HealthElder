package com.xaqinren.healthyelders;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.xaqinren.healthyelders.databinding.ActivityMainBinding;
import com.xaqinren.healthyelders.moduleHome.fragment.GirlsFragment;
import com.xaqinren.healthyelders.moduleHome.fragment.XxxFragment;
import com.xaqinren.healthyelders.moduleHome.viewModel.MainViewModel;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseActivity;


public class MainActivity extends BaseActivity<ActivityMainBinding, MainViewModel> {
    private List<Fragment> mFragments;
    private double firstTime;

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
        //初始化底部Button
        initBottomTab();
    }

    private void initFragment() {
        mFragments = new ArrayList<>();
        mFragments.add(new XxxFragment());
        mFragments.add(new GirlsFragment());
        mFragments.add(new XxxFragment());
        mFragments.add(new XxxFragment());

        //默认选中第一个
        commitAllowingStateLoss(0);
    }

    private void initBottomTab() {
        closeAnimation(binding.navigation);

        binding.navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                CharSequence title = item.getTitle();
                if (title.equals(getString(R.string.home_menu1))) {
                    commitAllowingStateLoss(0);
                    return true; //必须返回true 不然无效
                } else if (title.equals(getString(R.string.home_menu2))) {
                    commitAllowingStateLoss(1);
                    return true;
                } else if (title.equals(getString(R.string.home_menu3))) {
                    commitAllowingStateLoss(2);
                    return true;
                } else if (title.equals(getString(R.string.home_menu4))) {
                    commitAllowingStateLoss(3);
                    return true;
                }
                return false;
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


    /**
     * 例用反射关闭 BottomNavigationView 的点击放大效果
     *
     * @param view ---------------------------------------------
     */

    @SuppressLint("RestrictedApi")
    public void closeAnimation(BottomNavigationView view) {
        BottomNavigationMenuView mMenuView = (BottomNavigationMenuView) view.getChildAt(0);
        for (int i = 0; i < mMenuView.getChildCount(); i++) {
            BottomNavigationItemView button = (BottomNavigationItemView) mMenuView.getChildAt(i);
            TextView mLargeLabel = getField(button.getClass(), button, "largeLabel");
            TextView mSmallLabel = getField(button.getClass(), button, "smallLabel");
            float mSmallLabelSize = mSmallLabel.getTextSize();
            setField(button.getClass(), button, "shiftAmount", 0F);
            setField(button.getClass(), button, "scaleUpFactor", 1F);
            setField(button.getClass(), button, "scaleDownFactor", 1F);
            mLargeLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, mSmallLabelSize);
        }
        mMenuView.updateMenuView();
    }


    private <T> T getField(Class targetClass, Object instance, String fieldName) {
        try {
            Field field = targetClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            return (T) field.get(instance);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }


    private void setField(Class targetClass, Object instance, String fieldName, Object value) {
        try {
            Field field = targetClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(instance, value);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     *------------------------------------------------
     */


}
