package com.xaqinren.healthyelders.moduleMine.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.databinding.FragmentMineBinding;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.global.InfoCache;
import com.xaqinren.healthyelders.moduleHome.adapter.FragmentPagerAdapter;
import com.xaqinren.healthyelders.moduleLogin.bean.LoginTokenBean;
import com.xaqinren.healthyelders.moduleMine.viewModel.MineViewModel;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.utils.SPUtils;

/**
 * Created by Lee. on 2021/4/24.
 * 我的页面
 */
public class MineFragment extends BaseFragment<FragmentMineBinding, MineViewModel> {
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
    private MineZPFragment mineZPFragment;
    private MineSMFragment mineSMFragment;
    private MineDZFragment mineDZFragment;

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_mine;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        mineZPFragment = new MineZPFragment();
        mineSMFragment = new MineSMFragment();
        mineDZFragment = new MineDZFragment();
        fragmentList.add(mineZPFragment);
        fragmentList.add(mineSMFragment);
        fragmentList.add(mineDZFragment);

        pagerAdapter = new FragmentPagerAdapter(getActivity(), fragmentList);
        binding.vpContent.setOffscreenPageLimit(3);
        binding.vpContent.setAdapter(pagerAdapter);

        //获取内存中的信息，如果没有调接口
        if (UserInfoMgr.getInstance().getUserInfo() != null) {
            viewModel.userInfo.postValue(UserInfoMgr.getInstance().getUserInfo());
        } else {
            String accessToken = InfoCache.getInstance().getAccessToken();
            viewModel.getUserInfo(accessToken);
        }

        initEvent();
    }

    private int menuPosition;

    private void initTabMenu() {
        if (menuPosition == 0) {
            binding.tvZp.setTextColor(getResources().getColor(R.color.color_252525));
            binding.tvSm.setTextColor(getResources().getColor(R.color.gray_666));
            binding.tvZg.setTextColor(getResources().getColor(R.color.gray_666));
            binding.tvZp.setTextSize(16);
            binding.tvSm.setTextSize(14);
            binding.tvZg.setTextSize(14);
            binding.tvZp.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            binding.tvSm.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            binding.tvZg.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            Glide.with(this).load(R.mipmap.wode_sm_nor).into(binding.ivSm);
        } else if (menuPosition == 1) {
            binding.tvSm.setTextColor(getResources().getColor(R.color.color_252525));
            binding.tvZp.setTextColor(getResources().getColor(R.color.gray_666));
            binding.tvZg.setTextColor(getResources().getColor(R.color.gray_666));
            binding.tvSm.setTextSize(16);
            binding.tvZp.setTextSize(14);
            binding.tvZg.setTextSize(14);
            binding.tvSm.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            binding.tvZp.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            binding.tvZg.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            Glide.with(this).load(R.mipmap.wode_sm_sel).into(binding.ivSm);
        } else {
            binding.tvZp.setTextColor(getResources().getColor(R.color.gray_666));
            binding.tvSm.setTextColor(getResources().getColor(R.color.gray_666));
            binding.tvZg.setTextColor(getResources().getColor(R.color.color_252525));
            binding.tvZp.setTextSize(14);
            binding.tvSm.setTextSize(14);
            binding.tvZg.setTextSize(16);
            binding.tvZp.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            binding.tvSm.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            binding.tvZg.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            Glide.with(this).load(R.mipmap.wode_sm_nor).into(binding.ivSm);
        }
    }


    private boolean isFirst = true;

    private void initEvent() {

        binding.vpContent.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                if (!isFirst) {
                    menuPosition = position;

                    if (menuPosition == 0) {
                        mineZPFragment.getVideoList();
                    } else if (menuPosition == 1) {
                        mineSMFragment.getVideoList();
                    } else {
                        mineDZFragment.getVideoList();
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

                    //                    binding.refreshLayout.setEnabled(false);

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
        binding.llSm.setOnClickListener(lis -> {
            menuPosition = 1;
            initTabMenu();
            binding.vpContent.setCurrentItem(menuPosition);
        });
        binding.tvZg.setOnClickListener(lis -> {
            menuPosition = 2;
            initTabMenu();
            binding.vpContent.setCurrentItem(menuPosition);
        });
        binding.tvGz.setOnClickListener(lis -> {
        });
        binding.tvFs.setOnClickListener(lis -> {
        });
        binding.tvOrder.setOnClickListener(v -> {
        });
        binding.tvFriends.setOnClickListener(lis -> {
        });
        binding.ivSetting.setOnClickListener(lis -> {
        });
        binding.rivPhoto2.setOnClickListener(lis -> {
        });
        binding.tvName.setOnClickListener(lis -> {
        });
        binding.tvUserId.setOnClickListener(lis -> {
        });
        binding.tvSbj.setOnClickListener(lis -> {
        });
        binding.llTag.setOnClickListener(lis -> {
        });
        binding.tvEdit.setOnClickListener(lis -> {
        });
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.userInfo.observe(this, userInfo -> {
            dismissDialog();
        });

        viewModel.mVideoList.observe(this, dataList -> {

        });
    }
}
