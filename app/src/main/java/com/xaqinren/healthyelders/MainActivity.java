package com.xaqinren.healthyelders;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;

import com.dmcbig.mediapicker.utils.ScreenUtils;
import com.igexin.sdk.PushManager;
import com.tencent.qcloud.tim.uikit.utils.ScreenUtil;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.databinding.ActivityMainBinding;
import com.xaqinren.healthyelders.global.AppApplication;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.global.InfoCache;
import com.xaqinren.healthyelders.moduleHome.bean.VideoEvent;
import com.xaqinren.healthyelders.moduleHome.fragment.HomeFragment;
import com.xaqinren.healthyelders.moduleLiteav.service.LocationService;
import com.xaqinren.healthyelders.moduleLogin.activity.SelectLoginActivity;
import com.xaqinren.healthyelders.moduleLogin.bean.UserInfoBean;
import com.xaqinren.healthyelders.moduleMall.fragment.MallFragment;
import com.xaqinren.healthyelders.moduleMine.fragment.MineFragment;
import com.xaqinren.healthyelders.moduleMsg.fragment.MsgFragment;
import com.xaqinren.healthyelders.moduleZhiBo.activity.StartLiveActivity;
import com.xaqinren.healthyelders.moduleZhiBo.activity.StartRenZhengActivity;
import com.xaqinren.healthyelders.utils.ColorsUtils;
import com.xaqinren.healthyelders.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;
import me.goldze.mvvmhabit.utils.PermissionUtils;
import me.goldze.mvvmhabit.utils.SPUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;


public class MainActivity extends BaseActivity<ActivityMainBinding, MainViewModel> {
    private List<Fragment> mFragments;
    private double firstTime;
    private TextView oldView;
    private TextView selectView;
    private Disposable disposable;
    private Disposable eventDisposable;
    private String accessToken;
    private UserInfoBean userInfoBean;
    private Disposable subscribe;
    private HomeFragment homeFragment;
    private Drawable dawable2;
    private Drawable dawable;
    private boolean isTranMenu;
    private Handler handler;
    private MallFragment mallFragment;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_main;
    }

    @Override
    public int initVariableId() {
        return com.xaqinren.healthyelders.BR.viewModel;
    }


    @Override
    public void initData() {
        setStatusBarTransparent();
        //初始化Fragment
        initFragment();
        getCacheUserInfo();
        handler = new Handler();


        //设置底部背景线
        ViewGroup.LayoutParams layoutParams = binding.lineBottom.getLayoutParams();
        layoutParams.height = 1;
        binding.lineBottom.setLayoutParams(layoutParams);

        //开启定位服务
        boolean check = PermissionUtils.checkPermission(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
        });
        if (check)
            LocationService.startService(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean check = PermissionUtils.checkPermissionAllGranted(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
        });
        if (check)
            LocationService.startService(this);
    }

    private void getCacheUserInfo() {
        //获取token
        accessToken = InfoCache.getInstance().getAccessToken();
        userInfoBean = InfoCache.getInstance().getLoginUser();

        //已登陆，判断下用户信息存不存在请求用户信息接口
        if (!TextUtils.isEmpty(accessToken)) {
            if (userInfoBean == null) {
                //获取用户信息
                viewModel.getUserInfo(accessToken);
            } else {
                UserInfoMgr.getInstance().setUserInfo(userInfoBean);
                UserInfoMgr.getInstance().setAccessToken(accessToken);
                UserInfoMgr.getInstance().setHttpToken(Constant.API_HEADER + accessToken);
            }

        }

        //获取UserSig
        viewModel.getUserSig(accessToken);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.v("----------", "onNewIntent");
        getCacheUserInfo();
    }

    private void initFragment() {
        mFragments = new ArrayList<>();
        homeFragment = new HomeFragment();
        mallFragment = new MallFragment();
        mFragments.add(homeFragment);
        mFragments.add(mallFragment);
        mFragments.add(new MsgFragment());
        mFragments.add(new MineFragment());
        //默认选中第一个
        commitAllowingStateLoss(0);
        oldView = binding.tvMenu1;
        selectView = binding.tvMenu1;

        dawable = getResources().getDrawable(R.mipmap.line_bq);
        dawable.setBounds(0, 0, dawable.getMinimumWidth(), dawable.getMinimumHeight());
        dawable2 = getResources().getDrawable(R.mipmap.line_bq_white);
        dawable2.setBounds(0, 0, dawable.getMinimumWidth(), dawable.getMinimumHeight());

        initEvent();

    }

    private void initEvent() {
        binding.rlMenu1.setOnClickListener(lis -> {
            selectView = binding.tvMenu1;

            //发送HomeFragment回顶消息
            RxBus.getDefault().post(new EventBean(CodeTable.EVENT_HOME, CodeTable.SHOW_HOME1_TOP));
            //发送停止播放消息
            RxBus.getDefault().post(new VideoEvent(1, "全部停止播放"));
            //底部菜单变白色
            setBottomColors(R.color.white, dawable, R.color.color_252525, false);

            initBottomTab();
            oldView = binding.tvMenu1;
        });
        binding.rlMenu2.setOnClickListener(lis -> {
            //发送停止播放消息
            RxBus.getDefault().post(new VideoEvent(1, "全部停止播放"));
            selectView = binding.tvMenu2;
            initBottomTab();
            oldView = binding.tvMenu2;
        });
        binding.rlMenu3.setOnClickListener(lis -> {
            //发送停止播放消息
            RxBus.getDefault().post(new VideoEvent(1, "全部停止播放"));
            selectView = binding.tvMenu3;
            initBottomTab();
            oldView = binding.tvMenu3;
        });
        binding.rlMenu4.setOnClickListener(lis -> {
            //判断是否登录
            if (!InfoCache.getInstance().checkLogin()) {
                //跳转登录页面
                startActivity(SelectLoginActivity.class);
                return;
            }

            //发送停止播放消息
            RxBus.getDefault().post(new VideoEvent(1, "全部停止播放"));
            selectView = binding.tvMenu4;
            initBottomTab();
            oldView = binding.tvMenu4;
        });
        binding.ivLive.setOnClickListener(lis -> {
            disposable = permissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                    .subscribe(granted -> {
                        if (granted) {
                            startActivity(StartLiveActivity.class);
                        } else {
                            ToastUtils.showShort("访问权限已拒绝");
                        }

                    });
        });
    }

    private void setBottomColors(int p, Drawable dawable, int p2, boolean b) {
        binding.llMenu.setBackgroundColor(getResources().getColor(p));
        selectView.setCompoundDrawables(null, null, null, dawable);
        selectView.setTextColor(getResources().getColor(p2));
        isTranMenu = b;
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

                if (selectView.getId() == R.id.tv_menu1) {
                    if (scrollX <= 50 || scrollX < scrollY) {
                        //左右滑动过小，禁止滑动
                        if (homeFragment != null && homeFragment.vp2 != null) {
                            homeFragment.vp2.setUserInputEnabled(false);
                        }
                    } else {
                        if (homeFragment != null && homeFragment.vp2 != null) {
                            homeFragment.vp2.setUserInputEnabled(true);
                        }
                    }
                }

                //判断商城页面appBar处于展开，且向下滑动超过左右滑动再打开下拉刷新
                else if (selectView.getId() == R.id.tv_menu2) {
                    if (scrollX > scrollY) {
                        if (mallFragment.isTop) {
                            if (mallFragment.srl != null) {
                                mallFragment.srl.setEnabled(false);
                            }
                        }
                    } else {
                        if (mallFragment.isTop) {
                            if (mallFragment.srl != null) {
                                mallFragment.srl.setEnabled(true);
                            }
                        }
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
                before_press_Y = 0;
                before_press_X = 0;
                //恢复滑动
                if (homeFragment != null && homeFragment.vp2 != null) {
                    homeFragment.vp2.setUserInputEnabled(true);
                }
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();

        eventDisposable = RxBus.getDefault().toObservable(EventBean.class).subscribe(o -> {
            if (o.msgId == CodeTable.TOKEN_ERR) {
                SPUtils.getInstance().put(Constant.SP_KEY_LOGIN_USER, "");
                SPUtils.getInstance().put(Constant.SP_KEY_SIG_USER, "");
                SPUtils.getInstance().put(Constant.SP_KEY_TOKEN_INFO, "");
                SPUtils.getInstance().put(Constant.SP_KEY_WX_INFO, "");
                startActivity(SelectLoginActivity.class);
            } else if (o.msgId == CodeTable.NO_CARD) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(StartRenZhengActivity.class);
                    }
                }, 500);
            } else if (o.msgId == CodeTable.EVENT_HOME) {
                if (o.msgType == CodeTable.SET_MENU_TOUMING) {
                    if (selectView.getId() == R.id.tv_menu1 && AppApplication.get().getLayoutPos() == 2) {
                        return;
                    }
                    //底部菜单变透明，中心布局变全屏
                    setBottomColors(R.color.transparent, dawable2, R.color.white, true);
                    binding.lineBottom.setVisibility(View.VISIBLE);
                    //变全屏
                    binding.line.setVisibility(View.GONE);
                } else if (o.msgType == CodeTable.SET_MENU_WHITE) {
                    binding.llMenu.setBackgroundColor(getResources().getColor(R.color.white));
                    selectView.setCompoundDrawables(null, null, null, dawable);
                    selectView.setTextColor(getResources().getColor(R.color.color_252525));
                    binding.lineBottom.setVisibility(View.INVISIBLE);
                } else if (o.msgType == CodeTable.SET_MENU_COLOR) {

                    if (selectView.getId() == R.id.tv_menu1 && AppApplication.get().getLayoutPos() == 2) {
                        return;
                    }

                    if (o.status != 10) {
                        //背景色从白-透明 10-0
                        String alphaColor = ColorsUtils.getAlphaColor("ffffff", o.status);
                        binding.llMenu.setBackgroundColor(Color.parseColor(alphaColor));
                        //字体色从黑-白
                        selectView.setTextColor(Color.parseColor(textColors[o.status]));

                        //菜单下标逐渐透明
                    }

                }
            }
        });
        RxSubscriptions.add(eventDisposable);

        viewModel.userInfo.observe(this, userInfoBean -> {
            if (userInfoBean != null) {
                //调用上传clientId接口[个推]
                String clientId = PushManager.getInstance().getClientid(this);
                LogUtils.e("MainActivity", "绑定 cid -> " + clientId);
                viewModel.postClientId(clientId);
            }
        });
        viewModel.clientId.observe(this, b -> LogUtils.e("MainActivity", "绑定cid结果 -> " + b));
    }

    private String[] textColors = {
            "#FFFFFF",
            "#FFF7F7F7",
            "#FFE1E1E1",
            "#FFC5C5C5",
            "#FFA3A3A3",
            "#FF858585",
            "#FF696969",
            "#FF4D4D4D",
            "#FF2E2E2E",
            "#FF0F0F0F",
    };

    private void initBottomTab() {

        oldView.setCompoundDrawables(null, null, null, null);
        oldView.setTextColor(getResources().getColor(R.color.color_9292));
        oldView.setTextSize(ScreenUtils.px2sp(this, getResources().getDimension(R.dimen.sp_16)));


        if (selectView.getId() == R.id.tv_menu1 && isTranMenu) {
            binding.llMenu.setBackgroundColor(getResources().getColor(R.color.transparent));
            binding.lineBottom.setVisibility(View.VISIBLE);
            selectView.setCompoundDrawables(null, null, null, dawable2);
            selectView.setTextColor(getResources().getColor(R.color.white));
        } else {
            binding.llMenu.setBackgroundColor(getResources().getColor(R.color.white));
            binding.lineBottom.setVisibility(View.INVISIBLE);
            selectView.setCompoundDrawables(null, null, null, dawable);
            selectView.setTextColor(getResources().getColor(R.color.color_252525));
            selectView.setTextSize(ScreenUtils.px2sp(this, getResources().getDimension(R.dimen.sp_18)));
        }


        switch (selectView.getId()) {
            case R.id.tv_menu1:
                setStatusBarColorWhite();
                binding.line.setVisibility(View.GONE);
                commitAllowingStateLoss(0);
                break;
            case R.id.tv_menu2:
                setStatusBarColorBlack();
                //取消布局全屏
                binding.line.setVisibility(View.VISIBLE);
                commitAllowingStateLoss(1);
                break;
            case R.id.tv_menu3:
                setStatusBarColorBlack();
                binding.line.setVisibility(View.VISIBLE);
                commitAllowingStateLoss(2);
                break;
            case R.id.tv_menu4:
                setStatusBarColorWhite();
                binding.line.setVisibility(View.VISIBLE);
                commitAllowingStateLoss(3);
                break;

        }

    }

    private void commitAllowingStateLoss(int position) {
        hideAllFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(position + "");
        if (currentFragment != null) {
            transaction.show(currentFragment);
        } else {
            currentFragment = mFragments.get(position);
            transaction.add(R.id.frameLayout, currentFragment, position + "");
        }
        transaction.commitAllowingStateLoss();
    }

    //隐藏所有Fragment
    private void hideAllFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        for (int i = 0; i < mFragments.size(); i++) {
            Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(i + "");
            if (currentFragment != null) {
                transaction.hide(currentFragment);
            }
        }
        transaction.commitAllowingStateLoss();
    }

    /**
     * 二次点击（返回键）退出
     */
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                long secondTime = System.currentTimeMillis();
                if (secondTime - firstTime > 2000) {
                    //如果两次按键时间间隔大于2秒，则不退出
                    Toast.makeText(this, "再按一次退出程序~", Toast.LENGTH_SHORT).show();
                    firstTime = secondTime;//更新firstTime
                    return true;
                } else {
                    //两次按键小于2秒时，退出应用
                    System.exit(0);
                }
                break;
        }
        return super.onKeyUp(keyCode, event);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null) {
            disposable.dispose();
        }
        if (eventDisposable != null) {
            eventDisposable.dispose();
        }
        handler.removeCallbacksAndMessages(null);
    }
}
