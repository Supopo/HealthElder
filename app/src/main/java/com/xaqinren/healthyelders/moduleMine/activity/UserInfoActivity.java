package com.xaqinren.healthyelders.moduleMine.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivityUserInfoBinding;
import com.xaqinren.healthyelders.moduleHome.adapter.FragmentPagerAdapter;
import com.xaqinren.healthyelders.moduleLogin.bean.UserInfoBean;
import com.xaqinren.healthyelders.moduleMine.fragment.UserXHFragment;
import com.xaqinren.healthyelders.moduleMine.fragment.UserZPFragment;
import com.xaqinren.healthyelders.moduleMine.viewModel.UserInfoViewModel;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseActivity;

/**
 * Created by Lee. on 2021/6/5.
 * 用户信息页面
 */
public class UserInfoActivity extends BaseActivity<ActivityUserInfoBinding, UserInfoViewModel> {

    private RelativeLayout.LayoutParams layoutParams;
    private int oldLeft;
    private int oldTop; //初始位置
    private int oldTop3;//目标位置
    private int oldWidth;//初始宽度
    private int oldWidth3;//目标宽度
    private double lv = 1.0;
    private int textNameStartY;
    private int textNameEndY;
    private int textCodeEndY;
    private int llInfoEndY;

    private List<Fragment> fragmentList = new ArrayList<>();
    private FragmentPagerAdapter pagerAdapter;
    private UserZPFragment userZPFragment;
    private UserXHFragment userXHFragment;
    public boolean isTop = true;
    public SwipeRefreshLayout srl;
    private String userId;

    @Override
    public void initParam() {
        super.initParam();
        userId = getIntent().getExtras().getString("userId");
    }

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_user_info;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        setStatusBarTransparent();
        srl = binding.srlTop;
        userZPFragment = new UserZPFragment(userId);
        userXHFragment = new UserXHFragment(userId);
        fragmentList.add(userZPFragment);
        fragmentList.add(userXHFragment);

        pagerAdapter = new FragmentPagerAdapter(this, fragmentList);
        binding.vpContent.setOffscreenPageLimit(2);
        binding.vpContent.setAdapter(pagerAdapter);

        viewModel.getUserInfo(userId);
        initEvent();
    }

    private int menuPosition;

    private void initTabMenu() {
        if (menuPosition == 0) {
            binding.tvZp.setTextColor(getResources().getColor(R.color.color_252525));
            binding.tvXh.setTextColor(getResources().getColor(R.color.gray_666));
            binding.tvZp.setTextSize(16);
            binding.tvXh.setTextSize(14);
            binding.tvZp.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            binding.tvXh.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            Glide.with(this).load(R.mipmap.wode_sm_nor).into(binding.ivSm);
        } else if (menuPosition == 1) {
            binding.tvXh.setTextColor(getResources().getColor(R.color.color_252525));
            binding.tvZp.setTextColor(getResources().getColor(R.color.gray_666));
            binding.tvXh.setTextSize(16);
            binding.tvZp.setTextSize(14);
            binding.tvXh.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            binding.tvZp.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            Glide.with(this).load(R.mipmap.wode_sm_sel).into(binding.ivSm);
        }
    }

    private boolean isFirst = true;

    private void initEvent() {
        binding.srlTop.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (menuPosition == 0) {
                    userZPFragment.toRefresh();
                } else if (menuPosition == 1) {
                    userXHFragment.toRefresh();
                }
                binding.srlTop.setRefreshing(false);
            }
        });

        binding.vpContent.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                if (!isFirst) {
                    menuPosition = position;
                    if (menuPosition == 0) {
                        userZPFragment.getVideoList();
                    } else if (menuPosition == 1) {
                        userXHFragment.getVideoList();
                    }
                    initTabMenu();
                }
                isFirst = false;
            }
        });

        oldWidth = binding.rivPhoto2.getLayoutParams().width;
        oldWidth3 = binding.rivPhoto3.getLayoutParams().width;
        binding.tvName2.setAlpha(0);

        layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        //当滑动到顶部的时候，顶部布局变化
        binding.appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                //判单只有滑倒最顶部才能下拉刷新
                if (verticalOffset == 0) {
                    isTop = true;
                    binding.srlTop.setEnabled(true);
                } else {
                    isTop = false;
                    binding.srlTop.setEnabled(false);
                }

                if (oldTop == 0 && verticalOffset == 0) {
                    oldTop3 = binding.rivPhoto3.getTop();//拿到最终位置的top
                    oldTop = binding.rivPhoto2.getTop();
                    oldLeft = binding.rivPhoto2.getLeft();
                    //计算缩放倍率   缩小的距离/移动距离
                    lv = (double) (oldWidth - oldWidth3) / (oldTop - oldTop3);

                    textNameStartY = binding.tvName.getTop();
                    textNameEndY = binding.tvName.getBottom();
                    textCodeEndY = binding.tvUserId.getBottom();
                    llInfoEndY = binding.llInfo.getBottom();
                }

                if (verticalOffset < 0 && oldTop != 0) {
                    if (verticalOffset < -textNameStartY) {
                        //计算出透明度变化倍率
                        binding.tvName.setAlpha((1f - (float) (Math.abs(verticalOffset) - textNameStartY) / (textNameEndY - textNameStartY)));
                        binding.tvName2.setAlpha((float) (Math.abs(verticalOffset) - textNameStartY) / (textNameEndY - textNameStartY));
                    } else {
                        binding.tvName.setAlpha(1);
                        binding.tvName2.setAlpha(0);
                    }

                    if (verticalOffset < -textNameEndY) {
                        binding.tvUserId.setAlpha((1f - (float) (Math.abs(verticalOffset) - textNameEndY) / (textCodeEndY - textNameEndY)));
                    } else {
                        binding.tvUserId.setAlpha(1);
                    }

                    if (verticalOffset < -textCodeEndY) {
                        binding.llInfo.setAlpha((1f - (float) (Math.abs(verticalOffset) - textCodeEndY) / (llInfoEndY - textCodeEndY)));
                    } else {
                        binding.llInfo.setAlpha(1);
                    }


                    //向上滑动距离超过缩放距离 则不再让变化值
                    if (verticalOffset < (oldTop3 - oldTop)) {
                        verticalOffset = oldTop3 - oldTop;
                    }


                    //根据滑动距离计算新的宽度
                    layoutParams.width = oldWidth + (int) (verticalOffset * Math.abs(lv));
                    layoutParams.height = oldWidth + (int) (verticalOffset * Math.abs(lv));

                    //设置新的属性
                    layoutParams.setMargins(oldLeft, oldTop + verticalOffset, 0, 0);
                    binding.rivPhoto2.setLayoutParams(layoutParams);

                } else if (verticalOffset == 0 && oldTop != 0) {
                    //下拉到开始位置让头像view的属性复原
                    layoutParams.width = oldWidth;
                    layoutParams.height = oldWidth;
                    layoutParams.setMargins(oldLeft, oldTop, 0, 0);
                    binding.rivPhoto2.setLayoutParams(layoutParams);
                }


            }
        });

        binding.tvZp.setOnClickListener(lis -> {
            menuPosition = 0;
            initTabMenu();
            binding.vpContent.setCurrentItem(menuPosition);
        });
        binding.llXh.setOnClickListener(lis -> {
            menuPosition = 1;
            initTabMenu();
            binding.vpContent.setCurrentItem(menuPosition);
        });
        binding.tvGz.setOnClickListener(lis -> {
        });
        binding.tvFs.setOnClickListener(lis -> {
        });
        binding.ivSetting.setOnClickListener(lis -> {
        });
        binding.tvName.setOnClickListener(lis -> {
        });
        binding.tvUserId.setOnClickListener(lis -> {
        });
        binding.tvSbj.setOnClickListener(lis -> {
        });
        binding.llTag.setOnClickListener(lis -> {
        });

        binding.rlFollow.setOnClickListener(lis -> {
            viewModel.toFollow(userId);
            showDialog();
        });
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.userInfo.observe(this, userInfo -> {
            dismissDialog();
        });
        viewModel.dismissDialog.observe(this, isDis -> {
            if (isDis != null) {
                if (isDis) {
                    dismissDialog();
                }
            }
        });
        viewModel.followSuccess.observe(this, followSuccess -> {
            if (followSuccess != null) {
                if (followSuccess) {
                    UserInfoBean userInfoBean = viewModel.userInfo.getValue();
                    userInfoBean.setIdentity("FOLLOW");
                    viewModel.userInfo.setValue(userInfoBean);
                }
            }
        });
    }

}
