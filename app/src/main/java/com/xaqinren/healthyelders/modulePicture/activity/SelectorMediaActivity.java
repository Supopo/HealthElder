package com.xaqinren.healthyelders.modulePicture.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivitySelectorMediaBinding;
import com.xaqinren.healthyelders.modulePicture.fragment.SelectorImageFragment;
import com.xaqinren.healthyelders.modulePicture.fragment.SelectorVideoFragment;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.tatarka.bindingcollectionadapter2.BindingViewPagerAdapter;

/**
 * 选择媒体(拍摄视频)
 */
public class SelectorMediaActivity extends BaseActivity<ActivitySelectorMediaBinding, BaseViewModel> {
    private SelectorImageFragment imageFragment;
    private SelectorVideoFragment videoFragment;
    List<Fragment> fragments;
    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_selector_media;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        setTheme(com.hjyy.liteav.R.style.PickerActivityTheme);
        setTitle("所有照片");
        imageFragment = new SelectorImageFragment();
        videoFragment = new SelectorVideoFragment();
        fragments = new ArrayList<>();
        fragments.add(imageFragment);
        fragments.add(videoFragment);
        binding.viewpager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return 2;
            }
        });
        binding.viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        binding.tabLayout.setupWithViewPager(binding.viewpager);
        binding.tabLayout.getTabAt(0).setText("照片");
        binding.tabLayout.getTabAt(1).setText("视频");
    }
}
