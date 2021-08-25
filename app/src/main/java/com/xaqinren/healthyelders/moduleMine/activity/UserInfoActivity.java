package com.xaqinren.healthyelders.moduleMine.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.databinding.ActivityUserInfoBinding;
import com.xaqinren.healthyelders.global.AppApplication;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.moduleHome.adapter.FragmentPagerAdapter;
import com.xaqinren.healthyelders.moduleLogin.activity.PhoneLoginActivity;
import com.xaqinren.healthyelders.moduleLogin.bean.UserInfoBean;
import com.xaqinren.healthyelders.moduleMine.fragment.UserXHFragment;
import com.xaqinren.healthyelders.moduleMine.fragment.UserZPFragment;
import com.xaqinren.healthyelders.moduleMine.viewModel.UserInfoViewModel;
import com.xaqinren.healthyelders.utils.GlideUtil;
import com.xaqinren.healthyelders.utils.IntentUtils;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.bus.RxBus;

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
    private UserInfoBean userInfoBean;

    public static void startActivity(Context context, String uid) {
        Bundle bundle = new Bundle();
        bundle.putString("userId", uid);
        Intent intent = new Intent(context, UserInfoActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

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

        showDialog();
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
                    binding.rivPhoto2.setBorderWidth(0);
                    binding.ivBack.setVisibility(View.GONE);

                } else if (verticalOffset == 0 && oldTop != 0) {
                    binding.ivBack.setVisibility(View.VISIBLE);
                    //下拉到开始位置让头像view的属性复原
                    layoutParams.width = oldWidth;
                    layoutParams.height = oldWidth;
                    layoutParams.setMargins(oldLeft, oldTop, 0, 0);
                    binding.rivPhoto2.setLayoutParams(layoutParams);
                    binding.rivPhoto2.setBorderWidth((int) getResources().getDimension(R.dimen.dp_3));
                }


            }
        });

        binding.ivBack.setOnClickListener(lis -> {
            finish();
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
        binding.tvGzNum.setOnClickListener(lis -> {
            if (userInfoBean != null) {
                Bundle bundle = new Bundle();
                bundle.putInt("page", 0);
                bundle.putString("name", userInfoBean.getNickname());
                bundle.putString("uid", userInfoBean.getId());
                startActivity(LookAttentionActivity.class, bundle);
            }

        });
        binding.tvGz.setOnClickListener(lis -> {
            if (userInfoBean != null) {
                Bundle bundle = new Bundle();
                bundle.putInt("page", 0);
                bundle.putString("name", userInfoBean.getNickname());
                bundle.putString("uid", userInfoBean.getId());
                startActivity(LookAttentionActivity.class, bundle);
            }

        });
        binding.tvFsNum.setOnClickListener(lis -> {
            if (userInfoBean != null) {
                Bundle bundle = new Bundle();
                bundle.putInt("page", 1);
                bundle.putString("name", userInfoBean.getNickname());
                bundle.putString("uid", userInfoBean.getId());
                startActivity(LookAttentionActivity.class, bundle);
            }

        });
        binding.tvFs.setOnClickListener(lis -> {
            if (userInfoBean != null) {
                Bundle bundle = new Bundle();
                bundle.putInt("page", 1);
                bundle.putString("name", userInfoBean.getNickname());
                bundle.putString("uid", userInfoBean.getId());
                startActivity(LookAttentionActivity.class, bundle);
            }

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
            if (!UserInfoMgr.getInstance().getUserInfo().hasMobileNum()) {
                startActivity(PhoneLoginActivity.class);
                return;
            }

            //储存关注状态列表
            if (binding.llFollow.getVisibility() == View.VISIBLE) {
                AppApplication.get().followList.put(userId, true);
            } else {
                AppApplication.get().followList.put(userId, false);
            }
            viewModel.toFollow(userId);
            showDialog();
        });
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.userInfo.observe(this, userInfo -> {
            if (userInfo != null) {
                userInfoBean = userInfo;
                GlideUtil.intoImageView(this, userInfo.getAvatarUrl(), binding.rivPhoto2, R.mipmap.default_avatar);
            }
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
                    userInfoBean.hasFollow = !userInfoBean.hasFollow;
                    if (userInfoBean.hasFollow) {
                        RxBus.getDefault().post(new EventBean(CodeTable.FOLLOW_USER, 1, userInfoBean.getId()));
                        userInfoBean.setIdentity("FOLLOW");
                    } else {
                        RxBus.getDefault().post(new EventBean(CodeTable.FOLLOW_USER, 0, userInfoBean.getId()));
                        userInfoBean.setIdentity("STRANGER");
                    }
                    RxBus.getDefault().post(new EventBean(CodeTable.ZBJ_GZ, userInfoBean.hasFollow ? 1 : 0));
                    viewModel.userInfo.setValue(userInfoBean);
                }
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


                if (scrollX > scrollY) {
                    binding.vpContent.setUserInputEnabled(true);
                } else {
                    binding.vpContent.setUserInputEnabled(false);
                }
                break;
            case MotionEvent.ACTION_UP:
                before_press_Y = 0;
                before_press_X = 0;
                break;
        }
        return super.dispatchTouchEvent(event);
    }
}
