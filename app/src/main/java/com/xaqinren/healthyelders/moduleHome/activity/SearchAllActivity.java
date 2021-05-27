package com.xaqinren.healthyelders.moduleHome.activity;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivitySearchAllBinding;
import com.xaqinren.healthyelders.moduleHome.adapter.FragmentPagerAdapter;
import com.xaqinren.healthyelders.moduleHome.fragment.SearchAllFragment;
import com.xaqinren.healthyelders.moduleHome.viewModel.SearchAllViewModel;
import com.xaqinren.healthyelders.moduleMall.fragment.GoodsListFragment;
import com.xaqinren.healthyelders.moduleMall.fragment.MallFragment;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseActivity;

/**
 * Created by Lee. on 2021/5/27.
 * 全部搜索页面
 */
public class SearchAllActivity extends BaseActivity<ActivitySearchAllBinding, SearchAllViewModel> {
    private String[] titles = {"综合", "视频", "用户", "商品", "直播", "图文"};

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_search_all;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        setStatusBarTransparentBlack();

        List<Fragment> fragments = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            SearchAllFragment fragment = new SearchAllFragment();
            fragments.add(fragment);
        }

        FragmentPagerAdapter pagerAdapter = new FragmentPagerAdapter(this, fragments);
        binding.viewPager2.setAdapter(pagerAdapter);

        binding.tabLayout.setViewPager2(binding.viewPager2, titles);

    }
}
