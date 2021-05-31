package com.xaqinren.healthyelders.moduleHome.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.databinding.ActivitySearchAllBinding;
import com.xaqinren.healthyelders.global.CodeTable;
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
import me.goldze.mvvmhabit.bus.RxBus;

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

        showDialog();
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
                if (fragmentPos == 0) {
                    viewModel.searchUsers(1, 3);
                }
                viewModel.searchDatas(1, fragmentPos);
                RxBus.getDefault().post(new EventBean(CodeTable.SEARCH_TAG, 1));
            }
        });

        binding.etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    tags = binding.etSearch.getText().toString().trim();
                    viewModel.tags = tags;
                    viewModel.searchUsers(1, 3);
                    viewModel.searchDatas(1, fragmentPos);
                }
                return false;
            }
        });

        binding.ivBack.setOnClickListener(lis -> {
            finish();
        });

        binding.ivDel.setOnClickListener(lis -> {
            binding.etSearch.setText("");
            binding.etSearch.setHint("请输入需要搜索的内容");
        });

    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.dismissDialog.observe(this, dismissDialog -> {
            if (dismissDialog != null) {
                dismissDialog();
            }
        });
        viewModel.toUsers.observe(this, toUsers -> {
            if (toUsers != null && toUsers) {
                binding.tabLayout.setCurrentTab(2);

            }
        });
    }

    private float before_press_Y;
    private float before_press_X;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                before_press_Y = event.getY();
                before_press_X = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                double now_press_Y = event.getY();
                double now_press_X = event.getX();

                double scrollX = Math.abs(now_press_X - before_press_X);
                double scrollY = Math.abs(now_press_Y - before_press_Y);

                if (scrollX < scrollY) {
                    //禁止Viewpager2左右滑动
                    binding.viewPager2.setUserInputEnabled(false);
                } else {
                    binding.viewPager2.setUserInputEnabled(true);
                }

                break;
            case MotionEvent.ACTION_UP:
                before_press_Y = 0;
                before_press_X = 0;
                //恢复滑动
                binding.viewPager2.setUserInputEnabled(true);
                break;
        }
        return super.dispatchTouchEvent(event);
    }
}
