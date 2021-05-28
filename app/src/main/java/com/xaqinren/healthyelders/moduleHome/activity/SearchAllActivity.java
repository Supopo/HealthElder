package com.xaqinren.healthyelders.moduleHome.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivitySearchAllBinding;
import com.xaqinren.healthyelders.moduleHome.adapter.FragmentPagerAdapter;
import com.xaqinren.healthyelders.moduleHome.fragment.SearchAllFragment;
import com.xaqinren.healthyelders.moduleHome.fragment.SearchGoodsFragment;
import com.xaqinren.healthyelders.moduleHome.fragment.SearchTuwenFragment;
import com.xaqinren.healthyelders.moduleHome.fragment.SearchUserFragment;
import com.xaqinren.healthyelders.moduleHome.fragment.SearchVideoFragment;
import com.xaqinren.healthyelders.moduleHome.fragment.SearchZhiboFragment;
import com.xaqinren.healthyelders.moduleHome.viewModel.SearchAllViewModel;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseActivity;

/**
 * Created by Lee. on 2021/5/27.
 * 全部搜索页面
 */
public class SearchAllActivity extends BaseActivity<ActivitySearchAllBinding, SearchAllViewModel> {
    private String[] titles = {"综合", "视频", "用户", "商品", "直播", "图文"};
    private String tags;
    private int fragmentPos = 0;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_search_all;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initParam() {
        super.initParam();
        tags = (String) getIntent().getExtras().get("tags");
    }

    @Override
    public void initData() {
        super.initData();
        viewModel.tags = tags;
        binding.etSearch.setText(tags);
        setStatusBarTransparentBlack();

        List<Fragment> fragments = new ArrayList<>();

        SearchAllFragment allFragment = new SearchAllFragment();
        SearchVideoFragment videoFragment = new SearchVideoFragment();
        SearchUserFragment userFragment = new SearchUserFragment();
        SearchGoodsFragment goodsFragment = new SearchGoodsFragment();
        SearchZhiboFragment zbFragment = new SearchZhiboFragment();
        SearchTuwenFragment twFragment = new SearchTuwenFragment();
        fragments.add(allFragment);
        fragments.add(videoFragment);
        fragments.add(userFragment);
        fragments.add(goodsFragment);
        fragments.add(zbFragment);
        fragments.add(twFragment);


        FragmentPagerAdapter pagerAdapter = new FragmentPagerAdapter(this, fragments);
        binding.viewPager2.setAdapter(pagerAdapter);
        binding.viewPager2.setOffscreenPageLimit(fragments.size());
        binding.tabLayout.setViewPager2(binding.viewPager2, titles);

        binding.viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                fragmentPos = position;
                viewModel.searchDatas(1, fragmentPos);
            }
        });

        binding.etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    tags = binding.etSearch.getText().toString().trim();
                    viewModel.tags = tags;
                    viewModel.searchDatas(1, fragmentPos);
                }
                return false;
            }
        });

        binding.ivBack.setOnClickListener(lis -> {
            finish();
        });
        viewModel.searchDatas(1, fragmentPos);
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.dismissDialog.observe(this, dismissDialog -> {
            if (dismissDialog != null) {
                dismissDialog();
            }
        });
    }
}
