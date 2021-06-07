package com.xaqinren.healthyelders.moduleLiteav.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;

public class FragmentViewPagerAdapter extends FragmentStatePagerAdapter {
    private List<? extends Fragment> mFragments;

    public FragmentViewPagerAdapter(@NonNull FragmentManager fm, List<? extends Fragment> mFragments) {
        super(fm);
        this.mFragments = mFragments;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }
}
