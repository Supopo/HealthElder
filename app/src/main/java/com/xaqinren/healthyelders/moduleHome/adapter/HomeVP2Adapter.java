package com.xaqinren.healthyelders.moduleHome.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

/**
 * Created by Lee. on 2021/5/12.
 */
public class HomeVP2Adapter extends FragmentStateAdapter {
    private List<Fragment> fragments;

    public HomeVP2Adapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    //第一次getFragmentManager()获取到的FragmentManager，只提供给activity那一层使用。
    //在viewPager那一层只能使用getChildFragmentManager()获取FragmentManager来处理子fragment
    public HomeVP2Adapter(@NonNull Fragment fragment, List<Fragment> fragments) {
        super(fragment);
        this.fragments = fragments;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments.get(position);
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }
}
