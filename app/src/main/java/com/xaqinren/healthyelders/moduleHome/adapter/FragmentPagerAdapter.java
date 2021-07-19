package com.xaqinren.healthyelders.moduleHome.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

@SuppressWarnings("ALL")
public class FragmentPagerAdapter extends FragmentStateAdapter {

    private List<Fragment> mFragments;

    public FragmentPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<Fragment> fragments) {
        super(fragmentActivity);
        this.mFragments = fragments;

    }

    //第一次getFragmentManager()获取到的FragmentManager，只提供给activity那一层使用。
    //在viewPager那一层只能使用getChildFragmentManager()获取FragmentManager来处理子fragment
    public FragmentPagerAdapter(@NonNull Fragment fragment, List<Fragment> fragments) {
        super(fragment);
        this.mFragments = fragments;

    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        return mFragments.get(position);

    }

    @Override
    public int getItemCount() {

        return mFragments.size();

    }

}
