package com.xaqinren.healthyelders.moduleMine.activity;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivityLookAttentionBinding;
import com.xaqinren.healthyelders.moduleLiteav.adapter.FragmentViewPagerAdapter;
import com.xaqinren.healthyelders.moduleMine.fragment.AttentionFragment;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.base.BaseViewModel;

/**
 * 关注/粉丝
 */
public class LookAttentionActivity extends BaseActivity<ActivityLookAttentionBinding, BaseViewModel> {

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_look_attention;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        String name = getIntent().getStringExtra("name");
        String uid = getIntent().getStringExtra("uid");
        int page = getIntent().getIntExtra("page", 0);
        setTitle(name);
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new AttentionFragment(0, uid));
        fragments.add(new AttentionFragment(1, uid));
        binding.tablayout.setupWithViewPager(binding.viewpager);
        FragmentViewPagerAdapter adapter = new FragmentViewPagerAdapter(getSupportFragmentManager(),fragments);
        binding.viewpager.setAdapter(adapter);
        binding.tablayout.getTabAt(0).setText("关注");
        binding.tablayout.getTabAt(1).setText("粉丝");
        binding.viewpager.setCurrentItem(page, false);
    }

    @Override
    public void finish() {
        super.finish();
    }
}
