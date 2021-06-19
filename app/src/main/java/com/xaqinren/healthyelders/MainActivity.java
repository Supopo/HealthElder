package com.xaqinren.healthyelders;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.http.HttpResponseCache;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.content.FileProvider;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.dmcbig.mediapicker.utils.ScreenUtils;
import com.google.gson.Gson;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.bean.SlideBarBean;
import com.xaqinren.healthyelders.bean.SlideParamBean;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.databinding.ActivityMainBinding;
import com.xaqinren.healthyelders.global.AppApplication;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.global.GlobalData;
import com.xaqinren.healthyelders.global.InfoCache;
import com.xaqinren.healthyelders.moduleHome.bean.MenuBean;
import com.xaqinren.healthyelders.moduleHome.bean.VideoEvent;
import com.xaqinren.healthyelders.moduleHome.fragment.HomeFragment;
import com.xaqinren.healthyelders.moduleLiteav.service.LocationService;
import com.xaqinren.healthyelders.moduleLogin.activity.SelectLoginActivity;
import com.xaqinren.healthyelders.moduleLogin.bean.UserInfoBean;
import com.xaqinren.healthyelders.moduleMall.fragment.MallFragment;
import com.xaqinren.healthyelders.moduleMine.activity.SettingActivity;
import com.xaqinren.healthyelders.moduleMine.activity.WalletActivity;
import com.xaqinren.healthyelders.moduleMine.fragment.MineFragment;
import com.xaqinren.healthyelders.moduleMsg.ImManager;
import com.xaqinren.healthyelders.moduleMsg.fragment.MsgFragment;
import com.xaqinren.healthyelders.moduleZhiBo.activity.StartLiveActivity;
import com.xaqinren.healthyelders.moduleZhiBo.activity.StartRenZhengActivity;
import com.xaqinren.healthyelders.moduleZhiBo.bean.GiftBean;
import com.xaqinren.healthyelders.push.PayLoadBean;
import com.xaqinren.healthyelders.uniApp.UniService;
import com.xaqinren.healthyelders.uniApp.UniUtil;
import com.xaqinren.healthyelders.uniApp.bean.UniEventBean;
import com.xaqinren.healthyelders.utils.ColorsUtils;
import com.xaqinren.healthyelders.utils.GlideUtil;
import com.xaqinren.healthyelders.utils.IntentUtils;
import com.xaqinren.healthyelders.utils.LogUtils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.dcloud.feature.sdk.DCUniMPJSCallback;
import io.dcloud.feature.sdk.DCUniMPSDK;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import kotlin.jvm.internal.FunctionBase;
import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;
import me.goldze.mvvmhabit.utils.PermissionUtils;
import me.goldze.mvvmhabit.utils.SPUtils;
import me.goldze.mvvmhabit.utils.StringUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends BaseActivity<ActivityMainBinding, MainViewModel> implements ImManager.OnUnReadWatch {
    private List<Fragment> mFragments;
    private double firstTime;
    private TextView oldView;
    private TextView selectView;
    private Disposable disposable;
    private Disposable eventDisposable;
    private String accessToken;
    private UserInfoBean userInfoBean;
    private HomeFragment homeFragment;
    private Drawable dawable2;
    private Drawable dawable;
    private boolean isTranMenu;
    private Handler handler;
    private MallFragment mallFragment;
    private MineFragment mineFragment;
    private MsgFragment msgFragment;
    private Disposable mSubscription;

    private DCUniMPJSCallback callBack;

    private int currentIndex;
    private ActionBarDrawerToggle toggle;
    private QMUIDialog mInstallDialog;
    private SlideBarAdapter slideBarAdapter;
    private SlideBarBean slideBarBean;
    private Disposable uniSubscribe;

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
        SPUtils.getInstance().put(Constant.PAY_WAY, "uni");
        ImManager.getInstance().setOnUnReadWatch(this);
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

        UniService.startService(this);


        DCUniMPSDK.getInstance().setOnUniMPEventCallBack((s, o, dcUniMPJSCallback) -> {
            callBack = dcUniMPJSCallback;
            //小程序下发的消息
            //callback.invoke( "测试数据");  回传数据
            if (s.equals(Constant.UNI_LOGIN)) {
                //小程序掉登录
                if (!InfoCache.getInstance().checkLogin()) {
                    //跳转登录页面
                    startActivity(SelectLoginActivity.class);
                    return;
                }
            }
        });
        slideBarBean = GlobalData.getInstance().getSlideBar();
        initDrawer();
        disableDrawer();
        //        viewModel.getAppConfig();
        viewModel.checkVersion();

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
            if (userInfoBean == null || TextUtils.isEmpty(userInfoBean.getId())) {
                //获取用户信息
                viewModel.getUserInfo(accessToken);
            } else {
                if (callBack != null) {
                    callBack.invoke(accessToken);
                    callBack = null;
                }
                UserInfoMgr.getInstance().setUserInfo(userInfoBean);
                UserInfoMgr.getInstance().setAccessToken(accessToken);
                UserInfoMgr.getInstance().setHttpToken(Constant.API_HEADER + accessToken);
                onUnReadWatch(ImManager.getInstance().getUnreadCount());
                ImManager.getInstance().init(new File(getFilesDir(), "msg").getAbsolutePath());
            }

            //获取UserSig
            viewModel.getUserSig(accessToken);
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        boolean booleanExtra = intent.getBooleanExtra(Constant.PUBLISH_SUCCESS, false);
        if (booleanExtra) {
            binding.rlMenu4.performClick();

            runOnUiThread(()->{
                mineFragment.refreshData();
            });
        }
        getCacheUserInfo();
    }

    private void initFragment() {
        mFragments = new ArrayList<>();
        homeFragment = new HomeFragment();
        mallFragment = new MallFragment();
        mineFragment = new MineFragment();
        msgFragment = new MsgFragment();
        mFragments.add(homeFragment);
        mFragments.add(mallFragment);
        mFragments.add(msgFragment);
        mFragments.add(mineFragment);
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
            AppApplication.get().bottomMenu = 0;
            selectView = binding.tvMenu1;

            if (oldView.getId() == selectView.getId()) {
                //发送HomeFragment回顶消息
                RxBus.getDefault().post(new EventBean(CodeTable.EVENT_HOME, CodeTable.SHOW_HOME1_TOP));
                //底部菜单变白色
                setBottomColors(R.color.white, dawable, R.color.color_252525, false);
                //发送停止播放消息
                RxBus.getDefault().post(new VideoEvent(1, "全部停止播放"));
            } else {
                RxBus.getDefault().post(new VideoEvent(3, "继续播放"));
            }
            initBottomTab();
            oldView = binding.tvMenu1;
        });
        binding.rlMenu2.setOnClickListener(lis -> {
            AppApplication.get().bottomMenu = 1;
            //发送停止播放消息
            RxBus.getDefault().post(new VideoEvent(2, "暂停播放"));
            selectView = binding.tvMenu2;
            initBottomTab();
            oldView = binding.tvMenu2;
        });
        binding.rlMenu3.setOnClickListener(lis -> {
            AppApplication.get().bottomMenu = 2;

            if (!InfoCache.getInstance().checkLogin()) {
                //跳转登录页面
                startActivity(SelectLoginActivity.class);
                return;
            }
            //发送停止播放消息
            RxBus.getDefault().post(new VideoEvent(2, "暂停播放"));
            selectView = binding.tvMenu3;
            initBottomTab();
            oldView = binding.tvMenu3;
        });
        binding.rlMenu4.setOnClickListener(lis -> {
            AppApplication.get().bottomMenu = 3;

            //判断是否登录
            if (!InfoCache.getInstance().checkLogin()) {
                //跳转登录页面
                startActivity(SelectLoginActivity.class);
                return;
            }

            //发送停止播放消息
            RxBus.getDefault().post(new VideoEvent(2, "暂停播放"));
            selectView = binding.tvMenu4;
            initBottomTab();
            oldView = binding.tvMenu4;
        });
        binding.ivLive.setOnClickListener(lis -> {
            //先判断是否登录
            if (!InfoCache.getInstance().checkLogin()) {
                startActivity(SelectLoginActivity.class);
                return;
            }

            disposable = permissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO,Manifest.permission.MODIFY_AUDIO_SETTINGS)
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

                            if (!homeFragment.isShowTop) {
                                homeFragment.vp2.setUserInputEnabled(true);
                            }
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
                } else if (selectView.getId() == R.id.tv_menu4) {
                    if (scrollX > scrollY) {
                        if (mineFragment.isTop) {
                            if (mineFragment.srl != null) {
                                mineFragment.srl.setEnabled(false);
                            }
                        }
                    } else {
                        if (mineFragment.isTop) {
                            if (mineFragment.srl != null) {
                                mineFragment.srl.setEnabled(true);
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
        viewModel.appConfig.observe(this, o -> {
            Constant.SERVICE_PHONE = o.getTelephone();
        });
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
                    binding.lineBottom.setVisibility(View.VISIBLE);
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

        viewModel.versionBean.observe(this, versionBean -> {
            //版本号比较
            if (versionBean != null) {
                if (versionBean.newAppVersion.versionNumber != getVersionCode(this)) {
                    updateInfo = versionBean.newAppVersion.upgradeContent;
                    versionName = versionBean.newAppVersion.resVersionNumber;
                    downUrl = versionBean.newAppVersion.upgradeUrl;
                    mustUpdate = versionBean.autoUpdateApplet;
                    updateDialog();
                }
            }
        });
        viewModel.userInfo.observe(this, userInfoBean -> {
            //重置IM消息列表
            UserInfoMgr.getInstance().setHttpToken(Constant.API_HEADER + accessToken);
            onUnReadWatch(ImManager.getInstance().getUnreadCount());
            ImManager.getInstance().init(new File(getFilesDir(), "msg").getAbsolutePath());
        });


        mSubscription = RxBus.getDefault().toObservable(PayLoadBean.class)
                .observeOn(AndroidSchedulers.mainThread()) //回调到主线程更新UI
                .subscribe(new Consumer<PayLoadBean>() {
                    @Override
                    public void accept(final PayLoadBean progressLoadBean) throws Exception {
                        ImManager.getInstance().pushMessage(progressLoadBean);
                    }
                });
        //将订阅者加入管理站
        RxSubscriptions.add(mSubscription);

        uniSubscribe = RxBus.getDefault().toObservable(UniEventBean.class).subscribe(event -> {
            if (event != null) {
                if (event.msgId == CodeTable.UNI_RELEASE) {
                    if (event.taskId == 0x10056) {
                        UniUtil.openUniApp(getContext(), event.appId, event.jumpUrl, null, event.isSelfUni);
                    }
                } else if (event.msgId == CodeTable.UNI_RELEASE_FAIL) {
                    ToastUtils.showShort("打开小程序失败");
                }
            }
        });
        RxSubscriptions.add(uniSubscribe);

        viewModel.slideBarLiveData.observe(this, s -> {
            if (s != null) {
                slideBarBean = s;
                GlobalData.getInstance().saveSlideBar(slideBarBean);
                if (slideBarBean.getMenuInfoList() != null) {
                    slideBarAdapter.setList(slideBarBean.getMenuInfoList());
                }
            }

        });
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
            binding.lineBottom.setVisibility(View.VISIBLE);
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
        if (position == 3) {
            enableDrawer();
        } else {
            disableDrawer();
        }
        currentIndex = position;
        hideAllFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(position + "");
        if (currentFragment != null) {
            transaction.show(currentFragment);
        } else {
            getSupportFragmentManager().executePendingTransactions();
            currentFragment = mFragments.get(position);
            if (!currentFragment.isAdded())
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
        if (DCUniMPSDK.getInstance().getRuningAppid() != null) {
            return super.onKeyUp(keyCode, event);
        }
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (binding.drawer.isDrawerOpen(Gravity.RIGHT)) {
                    closeDrawer();
                    return true;
                }
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
        RxSubscriptions.remove(mSubscription);
        RxSubscriptions.remove(uniSubscribe);
    }

    @Override
    public void onUnReadWatch(int count) {
        if (count > 0) {
            binding.unread.setVisibility(View.VISIBLE);
            if (count > 99) {
                binding.unread.setText("99");
            } else {
                binding.unread.setText(count + "");
            }
        } else {
            binding.unread.setVisibility(View.GONE);
        }
    }

    private void disableDrawer() {
        binding.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    private void enableDrawer() {
        binding.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    private void initDrawer() {
        toggle = new ActionBarDrawerToggle(
                this,
                binding.drawer,
                null,
                R.string.app_name,
                R.string.app_name
        );
        float max = getResources().getDimension(R.dimen.dp_259);
        binding.drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                LogUtils.e(TAG, "slideOffset " + slideOffset);
                float tran = max * slideOffset;
                binding.content.setTranslationX(-tran);
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        slideBarAdapter = new SlideBarAdapter();
        binding.slideBarList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.slideBarList.setAdapter(slideBarAdapter);
        if (slideBarBean == null) {
            //splash未请求到数据
            viewModel.getSlideBar();
        } else {
            slideBarAdapter.setList(slideBarBean.getMenuInfoList());
        }
        slideBarAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                SlideBarBean.MenuInfoListDTO menuInfoListDTO = slideBarAdapter.getData().get(position);
                closeDelay();
                jumpMenu(menuInfoListDTO);
            }
        });
    }

    /**
     *
     * @param menuInfoListDTO
     */
    public void jumpMenu(SlideBarBean.MenuInfoListDTO menuInfoListDTO) {
        if (menuInfoListDTO.getEventType().equals("ORIGIN")) {
            //原生
            String event = menuInfoListDTO.getEvent();
            String jumpUrl = menuInfoListDTO.getJumpUrl();
            if (event.equals("page")) {
                //跳转页面
                SlideParamBean slideParamBean = JSON.parseObject(jumpUrl, SlideParamBean.class);
                HashMap<String, Object> param = slideParamBean.getParam();
                Bundle bundle = mackBundleByMap(param);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.putExtras(bundle);
                intent.setData(Uri.parse("jkzl://app_open/" + slideParamBean.getPage()));
                getActivity().startActivity(intent);
            } else if (event.equals("method")) {
                //打开方法
                SlideParamBean slideParamBean = JSON.parseObject(jumpUrl, SlideParamBean.class);
                String function = slideParamBean.getFunction();
                try {
                    Method fun = MainActivity.class.getDeclaredMethod(function, Map.class);
                    fun.setAccessible(true);
                    fun.invoke(MainActivity.this, slideParamBean.getParam());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (menuInfoListDTO.getEventType().equals("APPLET")) {
            //小程序
            String appid = menuInfoListDTO.getEvent();
            String page = menuInfoListDTO.getJumpUrl();
            UniService.startService(getContext(), appid, 0x10056, page);
        }
    }

    public SlideBarBean.MenuInfoListDTO convertToSlideBarMenu( MenuBean menuBean) {
        SlideBarBean.MenuInfoListDTO menuInfoListDTO = new SlideBarBean.MenuInfoListDTO();
        menuInfoListDTO.setMenuName(menuBean.menuName);
        menuInfoListDTO.setSubMenuName(menuBean.subMenuName);
        menuInfoListDTO.setSubFontColor(menuBean.subFontColor);
        menuInfoListDTO.setBackgroundColor(menuBean.backgroundColor);
        menuInfoListDTO.setFontColor(menuBean.fontColor);
        menuInfoListDTO.setIcon(menuBean.icon);
        menuInfoListDTO.setJumpUrl(menuBean.jumpUrl);
        menuInfoListDTO.setEventType(menuBean.eventType);
        menuInfoListDTO.setEvent(menuBean.event);
        menuInfoListDTO.setOnlyShowImage(menuBean.onlyShowImage);
        menuInfoListDTO.setImageUrl(menuBean.imageUrl);
        menuInfoListDTO.setSortOrder(menuBean.sortOrder);
        return menuInfoListDTO;
    }

    public void makeCall(Map<String, Object> param) {
        IntentUtils.sendPhone(getContext(), (String) param.get("phone"));
    }

    public void makeActivity(Map<String, Object> param) {
        String name = (String) param.get("activityName");
        param.remove("activityName");
        String code = (String) param.get("code");
        param.remove("code");
        Bundle bundle = mackBundleByMap(param);
        try {
            Class cls = Class.forName(name);
            Intent intent = new Intent(getActivity(), cls);
            intent.putExtras(bundle);
            if (!StringUtils.isEmpty(code)) {
                int code1 = Integer.parseInt(code);
                startActivityForResult(intent, code1);
            } else {
                startActivity(intent);
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Bundle mackBundleByMap(Map<String, Object> param){
        Set<String> strings = param.keySet();
        Iterator<String> iterator = strings.iterator();
        Bundle bundle = new Bundle();
        while (iterator.hasNext()) {
            String key = iterator.next();
            Object value = param.get(key);
            if (value instanceof String) {
                bundle.putString(key, (String) value);
            } else if (value instanceof Integer) {
                bundle.putInt(key, ((Integer) value));
            } else if (value instanceof Boolean) {
                bundle.putBoolean(key, (Boolean) value);
            } else if (value instanceof Float) {
                bundle.putFloat(key, (Float) value);
            } else if (value instanceof Double) {
                bundle.putDouble(key, (Double) value);
            } else if (value instanceof Long) {
                bundle.putLong(key, (Long) value);
            }
        }
        return bundle;
    }

    public void openDrawer() {
        binding.drawer.openDrawer(Gravity.RIGHT);
    }

    public void closeDrawer() {
        binding.drawer.closeDrawer(Gravity.RIGHT);
    }

    public void closeDelay() {
        Observable.timer(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> closeDrawer());
    }


    private String updateInfo;
    private double size;
    private boolean mustUpdate;
    private String versionName;
    private String downUrl;

    //    获取本地软件版本号
    private int getVersionCode(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (Exception e) {
            return 0;
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10086) {
            if (mInstallDialog.isShowing()) {
                mInstallDialog.dismiss();
            }
            //开启权限回来了
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //先获取是否有安装未知来源应用的权限
                haveInstallPermission = getPackageManager().canRequestPackageInstalls();
                if (!haveInstallPermission) {
                    //没有权限
                    installDialog();
                    return;
                }
            }
            //有权限下载
            downloadApk();

        }
    }


    /**
     * 更新提示
     */
    private void updateDialog() {
        new QMUIDialog.MessageDialogBuilder(MainActivity.this)
                .setTitle("发现新版本" + versionName)
                .setMessage(updateInfo)
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                        if (mustUpdate) {
                            finish();
                        }
                    }
                })
                .addAction(0, "确定", QMUIDialogAction.ACTION_PROP_NEGATIVE, new QMUIDialogAction.ActionListener() {

                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        //                        Intent mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(downUrl));
                        //                        startActivity(mIntent);
                        dialog.dismiss();


                        //先根据下载路径去安装，如果文件为空会自动下载。
                        //安装应用的流程
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            //先获取是否有安装未知来源应用的权限
                            haveInstallPermission = getPackageManager().canRequestPackageInstalls();
                            if (!haveInstallPermission) {
                                //没有权限
                                installDialog();
                                return;
                            }
                        }
                        //有权限，开始安装应用程序
                        //                        installApk();
                        downloadApk();
                    }
                })
                .show();
    }

    private void installDialog() {
        mInstallDialog = new QMUIDialog.MessageDialogBuilder(this)
                .setTitle("安装提示")
                .setMessage("安装应用需要打开未知来源权限，请去设置中开启权限")
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction(0, "确定", QMUIDialogAction.ACTION_PROP_NEGATIVE, new QMUIDialogAction.ActionListener() {

                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        startInstallPermissionSettingActivity();
                    }
                })
                .show();
    }

    /**
     * 从服务器端下载最新apk
     */
    private void downloadApk() {
        //显示下载进度
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle(versionName + "版本更新");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setMax(100);
        dialog.setCancelable(false);
        dialog.show();

        //访问网络下载apk
        new Thread(new DownloadApk(dialog)).start();
    }

    /**
     * 访问网络下载apk
     */
    private class DownloadApk implements Runnable {
        private ProgressDialog dialog;
        InputStream is;
        FileOutputStream fos;

        public DownloadApk(ProgressDialog dialog) {
            this.dialog = dialog;
        }

        @Override
        public void run() {
            OkHttpClient client = new OkHttpClient();
            String url = downUrl;
            Request request = new Request.Builder().get().url(url).build();
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    //获取内容总长度
                    long contentLength = response.body().contentLength();
                    //设置最大值(百分比)
                    dialog.setMax(100);
                    //保存到sd卡
                    File apkFile = new File(Environment.getExternalStorageDirectory(), "jkzl" + versionName + ".apk");

                    fos = new FileOutputStream(apkFile);
                    //获得输入流
                    is = response.body().byteStream();
                    //定义缓冲区大小
                    byte[] bys = new byte[1024];
                    int progress = 0;
                    int len = -1;
                    while ((len = is.read(bys)) != -1) {
                        try {
                            Thread.sleep(1);
                            fos.write(bys, 0, len);
                            fos.flush();
                            progress += len;
                            //设置进度百分比
                            double n = (double) progress / contentLength;
                            dialog.setProgress((int) (100 * n));
                        } catch (InterruptedException e) {
                        }
                    }
                    //先去尝试安装
                    installApk();
                }
            } catch (IOException e) {
            } finally {
                //关闭io流
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    is = null;
                }
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    fos = null;
                }
            }
            dialog.dismiss();
        }

    }

    private boolean haveInstallPermission;

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startInstallPermissionSettingActivity() {
        //注意这个是8.0新API
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
        startActivityForResult(intent, 10086);
    }

    /**
     * 下载完成,提示用户安装
     */
    private void installApk() {
        //先制定安装路径
        File apkFile = new File(Environment.getExternalStorageDirectory(), "jkzl" + versionName + ".apk");
        //文件不存在就去下载
        if (!apkFile.exists()) {
            downloadApk();
            return;
        }
        //调用系统安装程序
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri apkUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            // 7.0以上要通过fileprovider获取安装包位置
            // UpdateConfig.FILE_PROVIDER_AUTH 即是在清单文件中配置的authorities
            apkUri = FileProvider.getUriForFile(MainActivity.this, "com.xaqinren.fileprovider", apkFile);
            // 给目标应用一个临时授权
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            apkUri = Uri.fromFile(apkFile);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        }

        MainActivity.this.startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //        getCurrentProcessNameByActivityManager(this);
    }


}
