package com.xaqinren.healthyelders;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
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
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.dmcbig.mediapicker.utils.ScreenUtils;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.tbruyelle.rxpermissions2.RxPermissions;
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
import com.xaqinren.healthyelders.moduleLogin.activity.PhoneLoginActivity;
import com.xaqinren.healthyelders.moduleLogin.activity.SelectLoginActivity;
import com.xaqinren.healthyelders.moduleLogin.bean.LoginTokenBean;
import com.xaqinren.healthyelders.moduleLogin.bean.UserInfoBean;
import com.xaqinren.healthyelders.moduleMall.fragment.MallFragment;
import com.xaqinren.healthyelders.moduleMine.fragment.MineFragment;
import com.xaqinren.healthyelders.moduleMsg.ImManager;
import com.xaqinren.healthyelders.moduleMsg.fragment.MsgFragment;
import com.xaqinren.healthyelders.moduleZhiBo.activity.StartLiveActivity;
import com.xaqinren.healthyelders.moduleZhiBo.activity.StartRenZhengActivity;
import com.xaqinren.healthyelders.push.PayLoadBean;
import com.xaqinren.healthyelders.uniApp.UniService;
import com.xaqinren.healthyelders.uniApp.UniUtil;
import com.xaqinren.healthyelders.uniApp.bean.UniEventBean;
import com.xaqinren.healthyelders.utils.ColorsUtils;
import com.xaqinren.healthyelders.utils.IntentUtils;
import com.xaqinren.healthyelders.utils.LogUtils;
import com.xaqinren.healthyelders.widget.MyProgressDialog;
import com.xxx.libbase.UpdateApkManger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
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
    private Handler mHandler;
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
    private Runnable runnable;
    private Disposable disposable1;
    private UpdateApkManger updateApkManger;

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
        updateApkManger = new UpdateApkManger();
        SPUtils.getInstance().put(Constant.PAY_WAY, "uni");
        ImManager.getInstance().setOnUnReadWatch(this);
        setStatusBarTransparent();
        getCacheUserInfo();

        //?????????Fragment
        initFragment();
        mHandler = new Handler();

        //?????????????????????
        ViewGroup.LayoutParams layoutParams = binding.lineBottom.getLayoutParams();
        layoutParams.height = 1;
        binding.lineBottom.setLayoutParams(layoutParams);

        UniService.startService(this);


        //???????????????????????????
        DCUniMPSDK.getInstance().setOnUniMPEventCallBack((s, o, dcUniMPJSCallback) -> {
            callBack = dcUniMPJSCallback;
            //????????????????????????
            //callback.invoke( "????????????");  ????????????
            if (s.equals(Constant.UNI_LOGIN)) {
                //??????????????????
                if (!InfoCache.getInstance().checkLogin()) {
                    //??????????????????
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

        DCUniMPSDK.getInstance().setOnUniMPEventCallBack(new DCUniMPSDK.IOnUniMPEventCallBack() {
            @Override
            public void onUniMPEventReceive(String s, Object o, DCUniMPJSCallback dcUniMPJSCallback) {
                LogUtils.e("UNIAPP", s);
                if (s.equals("loginSuccess")) {
                    //????????? wx/mobile ????????????,??????????????????
                    try {
                        LoginTokenBean tokenBean = JSON.parseObject(o.toString(), LoginTokenBean.class);
                        tokenBean.saveTime = System.currentTimeMillis();
                        InfoCache.getInstance().setTokenInfo(tokenBean);
                        UserInfoMgr.getInstance().setAccessToken(tokenBean.access_token);
                        UserInfoMgr.getInstance().setHttpToken(Constant.API_HEADER + tokenBean.access_token);
                        LogUtils.e("UNIAPP", "token ????????????");
                        viewModel.getUserInfo(tokenBean.access_token, false);
                    } catch (Exception e) {
                        LogUtils.e("UNIAPP", "uni ?????? ????????????");
                    }

                }
            }
        });

    }


    private void getCacheUserInfo() {
        //??????token
        accessToken = InfoCache.getInstance().getAccessToken();
        userInfoBean = InfoCache.getInstance().getLoginUser();


        //?????????????????????????????????????????????????????????????????????
        if (!TextUtils.isEmpty(accessToken)) {
            //??????????????????????????????icon???????????????????????????
            if (UserInfoMgr.getInstance().getUserInfo() == null) {

                //?????????????????????????????????????????????uid?????????
                if (userInfoBean != null) {
                    UserInfoMgr.getInstance().setUserInfo(userInfoBean);
                    UserInfoMgr.getInstance().setAccessToken(accessToken);
                    UserInfoMgr.getInstance().setHttpToken(Constant.API_HEADER + accessToken);

                    if (callBack != null) {
                        callBack.invoke(accessToken);
                        callBack = null;
                    }

                    ImManager.getInstance().init(new File(getFilesDir(), "msg").getAbsolutePath());
                    onUnReadWatch(ImManager.getInstance().getUnreadCount());
                }

                //????????????????????????
                viewModel.getUserInfo(accessToken, true);
            } else {
                viewModel.getUserSig(accessToken);
            }

        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        boolean booleanExtra = intent.getBooleanExtra(Constant.PUBLISH_SUCCESS, false);
        if (booleanExtra) {
            binding.rlMenu4.performClick();
            mHandler.postDelayed(() -> {
                mineFragment.refreshData();
            }, 500);
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
        //?????????????????????
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
            if (mallFragment != null) {
                mallFragment.stopHandler();
            }
            selectView = binding.tvMenu1;
            AppApplication.get().bottomMenu = 0;
            if (oldView.getId() == selectView.getId()) {
                toTop();
            } else {
                RxBus.getDefault().post(new VideoEvent(3, "????????????"));
            }
            initBottomTab();
            oldView = binding.tvMenu1;
        });
        binding.rlMenu2.setOnClickListener(lis -> {
            if (mallFragment != null) {
                mallFragment.startHandler();
            }
            //????????????????????????
            RxBus.getDefault().post(new VideoEvent(2, "????????????"));
            selectView = binding.tvMenu2;
            initBottomTab();
            oldView = binding.tvMenu2;
            AppApplication.get().bottomMenu = 1;
        });
        binding.rlMenu3.setOnClickListener(lis -> {
            if (mallFragment != null) {
                mallFragment.stopHandler();
            }
            if (!InfoCache.getInstance().checkLogin()) {
                //??????????????????
                startActivity(SelectLoginActivity.class);
                return;
            }
            //????????????????????????
            RxBus.getDefault().post(new VideoEvent(2, "????????????"));
            selectView = binding.tvMenu3;
            initBottomTab();
            oldView = binding.tvMenu3;
            AppApplication.get().bottomMenu = 2;
        });
        binding.rlMenu4.setOnClickListener(lis -> {
            if (mallFragment != null) {
                mallFragment.stopHandler();
            }
            //??????????????????
            if (!InfoCache.getInstance().checkLogin()) {
                //??????????????????
                startActivity(SelectLoginActivity.class);
                return;
            }
            if (currentIndex == 3) {
                return;
            }
            AppApplication.get().bottomMenu = 3;
            //????????????????????????
            RxBus.getDefault().post(new VideoEvent(2, "????????????"));
            selectView = binding.tvMenu4;
            initBottomTab();
            oldView = binding.tvMenu4;
            //????????????????????????
            mineFragment.refreshUserInfo();
        });
        binding.ivLive.setOnClickListener(lis -> {
            //?????????????????????
            if (!InfoCache.getInstance().checkLogin()) {
                startActivity(SelectLoginActivity.class);
                return;
            }
            if (UserInfoMgr.getInstance().getUserInfo() == null) {
                startActivity(SelectLoginActivity.class);
                return;
            }

            //????????????????????????
            if (!UserInfoMgr.getInstance().getUserInfo().hasMobileNum()) {
                startActivity(PhoneLoginActivity.class);
                return;
            }
            //????????????????????????
            if (!UserInfoMgr.getInstance().getUserInfo().getHasRealName()) {
                startActivity(StartRenZhengActivity.class);
                return;
            }


            disposable = permissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.MODIFY_AUDIO_SETTINGS)
                    .subscribe(granted -> {
                        if (granted) {

                            disposable1 = permissions.request(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                                    .subscribe(granted1 -> {
                                        if (granted1) {
                                            startActivity(StartLiveActivity.class);
                                        } else {
                                            startActivity(StartLiveActivity.class);
                                        }

                                    });


                        } else {
                            ToastUtils.showShort("?????????????????????");
                        }

                    });
        });
    }

    private void toTop() {
        //??????HomeFragment????????????
        RxBus.getDefault().post(new EventBean(CodeTable.EVENT_HOME, CodeTable.SHOW_HOME1_TOP));
        //?????????????????????
        setBottomColors(R.color.white, dawable, R.color.color_252525, false);
        //????????????????????????
        RxBus.getDefault().post(new VideoEvent(1, "??????????????????"));
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
                    if (scrollX <= scrollY) {
                        if (homeFragment != null && homeFragment.vp2 != null) {
                            homeFragment.vp2.setUserInputEnabled(false);
                        }
                    } else {
                        if (homeFragment != null && homeFragment.vp2 != null) {
                            homeFragment.vp2.setUserInputEnabled(true);
                        }
                    }
                }

                //??????????????????appBar?????????????????????????????????????????????????????????????????????
                else if (selectView.getId() == R.id.tv_menu2) {
                    if (mallFragment != null) {
                        if (scrollX > scrollY) {
                            mallFragment.viewPager2.setUserInputEnabled(true);
                            if (mallFragment.isTop) {
                                if (mallFragment.srl != null) {
                                    mallFragment.srl.setEnabled(false);
                                }
                            }
                        } else {
                            mallFragment.viewPager2.setUserInputEnabled(false);
                            if (mallFragment.isTop) {
                                if (mallFragment.srl != null) {
                                    mallFragment.srl.setEnabled(true);
                                }
                            }
                        }
                    }

                } else if (selectView.getId() == R.id.tv_menu4) {
                    if (mineFragment != null) {
                        if (scrollX > scrollY) {
                            mineFragment.vpContent.setUserInputEnabled(true);
                            if (mineFragment.isTop) {
                                if (mineFragment.srl != null) {
                                    mineFragment.srl.setEnabled(false);
                                }
                            }
                        } else {
                            mineFragment.vpContent.setUserInputEnabled(false);
                            if (mineFragment.isTop) {
                                if (mineFragment.srl != null) {
                                    mineFragment.srl.setEnabled(true);
                                }
                            }
                        }
                    }

                }
                break;
            case MotionEvent.ACTION_UP:
                double now_press_Y2 = event.getY();
                double now_press_X2 = event.getX();
                double scrollX2 = Math.abs(now_press_X2 - before_press_X);
                double scrollY2 = Math.abs(now_press_Y2 - before_press_Y);


                before_press_Y = 0;
                before_press_X = 0;
                //????????????
                if (homeFragment != null && homeFragment.vp2 != null) {
                    homeFragment.vp2.setUserInputEnabled(true);
                }

                if (selectView.getId() == R.id.tv_menu1 && AppApplication.get().isShowTopMenu()) {
                    //??????????????????
                    if (scrollY2 < ((int) getResources().getDimension(R.dimen.dp_234) / 2)) {
                        //??????HomeFragment????????????
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                RxBus.getDefault().post(new EventBean(CodeTable.EVENT_HOME, CodeTable.SHOW_HOME1_TOP_HT));
                            }
                        }, 50);
                    } else {
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                RxBus.getDefault().post(new EventBean(CodeTable.EVENT_HOME, CodeTable.SHOW_HOME1_TOP_ZK));
                            }
                        }, 50);
                    }
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
            if (o.msgId == CodeTable.JUMP_ACT && o.content.equals("main-act")) {
                //?????????????????????????????????????????????
                selectView = binding.tvMenu1;
                AppApplication.get().bottomMenu = 0;
                initBottomTab();
                oldView = binding.tvMenu1;


            } else if (o.msgId == CodeTable.TOKEN_ERR) {
                SPUtils.getInstance().put(Constant.SP_KEY_LOGIN_USER, "");
                SPUtils.getInstance().put(Constant.SP_KEY_SIG_USER, "");
                SPUtils.getInstance().put(Constant.SP_KEY_TOKEN_INFO, "");
                SPUtils.getInstance().put(Constant.SP_KEY_WX_INFO, "");
                startActivity(SelectLoginActivity.class);
            } else if (o.msgId == CodeTable.NO_CARD) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(StartRenZhengActivity.class);
                    }
                }, 500);
            } else if (o.msgId == CodeTable.MSG_NO_PHONE) {
                startActivity(PhoneLoginActivity.class);
            } else if (o.msgId == CodeTable.EVENT_HOME) {
                if (o.msgType == CodeTable.SET_MENU_TOUMING) {
                    if (selectView.getId() == R.id.tv_menu1 && AppApplication.get().getLayoutPos() == 2) {
                        return;
                    }
                    //?????????????????????????????????????????????
                    setBottomColors(R.color.transparent, dawable2, R.color.white, true);
                    binding.lineBottom.setVisibility(View.VISIBLE);
                    //?????????
                    binding.line.setVisibility(View.GONE);
                } else if (o.msgType == CodeTable.SET_MENU_WHITE) {

                    binding.llMenu.setBackgroundColor(getResources().getColor(R.color.white));
                    selectView.setCompoundDrawables(null, null, null, dawable);
                    selectView.setTextColor(getResources().getColor(R.color.color_252525));
                    binding.lineBottom.setVisibility(View.VISIBLE);
                } else if (o.msgType == CodeTable.SET_MENU_BLACK) {

                    binding.llMenu.setBackgroundColor(getResources().getColor(R.color.black));
                    selectView.setCompoundDrawables(null, null, null, dawable2);
                    selectView.setTextColor(getResources().getColor(R.color.white));
                    binding.lineBottom.setVisibility(View.VISIBLE);
                } else if (o.msgType == CodeTable.SET_MENU_COLOR) {

                    if (selectView.getId() == R.id.tv_menu1 && AppApplication.get().getLayoutPos() == 2) {
                        return;
                    }

                    if (o.status != 10) {

                        //???????????????-?????? 10-0
                        String alphaColor = ColorsUtils.getAlphaColor("ffffff", o.status);
                        binding.llMenu.setBackgroundColor(Color.parseColor(alphaColor));
                        //???????????????-???
                        selectView.setTextColor(Color.parseColor(textColors[o.status]));

                    }

                }
            }
        });
        RxSubscriptions.add(eventDisposable);

        viewModel.versionBean.observe(this, versionBean -> {
            //???????????????
            if (versionBean != null) {
                if (versionBean.newAppVersion.versionNumber != getVersionCode(this)) {
                    updateInfo = versionBean.newAppVersion.upgradeContent;
                    versionName = versionBean.newAppVersion.resVersionNumber;
                    downUrl = versionBean.newAppVersion.upgradeUrl;
                    mustUpdate = versionBean.autoUpdateApplet;

                    //??????????????????
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("hidCancelbtn", mustUpdate);
                    jsonObject.put("verCode", versionName);
                    jsonObject.put("con", updateInfo);
                    jsonObject.put("downUrl", downUrl);
                    updateApkManger.showUpdateDialog(jsonObject, this);
                }
            }
        });
        viewModel.userInfo.observe(this, userInfoBean -> {
            //??????IM????????????
            UserInfoMgr.getInstance().setHttpToken(Constant.API_HEADER + accessToken);
            onUnReadWatch(ImManager.getInstance().getUnreadCount());
            ImManager.getInstance().init(new File(getFilesDir(), "msg").getAbsolutePath());
        });





        //??????????????????
        mSubscription = RxBus.getDefault().toObservable(PayLoadBean.class)
                .observeOn(AndroidSchedulers.mainThread()) //????????????????????????UI
                .subscribe(new Consumer<PayLoadBean>() {
                    @Override
                    public void accept(final PayLoadBean progressLoadBean) throws Exception {
                        ImManager.getInstance().pushMessage(progressLoadBean);
                    }
                });
        //???????????????????????????
        RxSubscriptions.add(mSubscription);

        uniSubscribe = RxBus.getDefault().toObservable(UniEventBean.class).subscribe(event -> {
            if (event != null) {
                if (event.msgId == CodeTable.UNI_RELEASE) {
                    if (event.taskId == 0x10056) {
                        UniUtil.openUniApp(getContext(), event.appId, event.jumpUrl, null, event.isSelfUni);
                    }
                } else if (event.msgId == CodeTable.UNI_RELEASE_FAIL) {
                    ToastUtils.showShort("?????????????????????");
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
                //??????????????????
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

    //????????????Fragment
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
     * ?????????????????????????????????
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
                    //????????????????????????????????????2??????????????????
                    Toast.makeText(this, "????????????????????????~", Toast.LENGTH_SHORT).show();
                    firstTime = secondTime;//??????firstTime
                    return true;
                } else {
                    //??????????????????2?????????????????????
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
        if (disposable1 != null) {
            disposable1.dispose();
        }

        if (eventDisposable != null) {
            eventDisposable.dispose();
        }
        mHandler.removeCallbacksAndMessages(null);
        RxSubscriptions.remove(mSubscription);
        RxSubscriptions.remove(eventDisposable);
        RxSubscriptions.remove(uniSubscribe);
        RxSubscriptions.clear();
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
            //splash??????????????????
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
     * @param menuInfoListDTO
     */
    public void jumpMenu(SlideBarBean.MenuInfoListDTO menuInfoListDTO) {
        if (menuInfoListDTO.getEventType().equals("ORIGIN")) {
            //??????
            String event = menuInfoListDTO.getEvent();
            String jumpUrl = menuInfoListDTO.getJumpUrl();
            if (event.equals("page")) {
                //????????????
                SlideParamBean slideParamBean = JSON.parseObject(jumpUrl, SlideParamBean.class);
                HashMap<String, Object> param = slideParamBean.getParam();
                Bundle bundle = mackBundleByMap(param);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.putExtras(bundle);
                intent.setData(Uri.parse("jkzl://app_open/" + slideParamBean.getPage()));
                getActivity().startActivity(intent);
            } else if (event.equals("method")) {
                //????????????
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
            //?????????
            String appid = menuInfoListDTO.getEvent();
            String page = menuInfoListDTO.getJumpUrl();
            UniService.startService(getContext(), appid, 0x10056, page);
        }
    }

    public SlideBarBean.MenuInfoListDTO convertToSlideBarMenu(MenuBean menuBean) {
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

    public Bundle mackBundleByMap(Map<String, Object> param) {
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

    //    ???????????????????????????
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
        if (requestCode == 10086) {//???????????????????????????
            //?????????????????????
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //???????????????????????????????????????????????????
                updateApkManger.haveInstallPermission = this.getPackageManager().canRequestPackageInstalls();
                if (updateApkManger.haveInstallPermission) {
                    if (updateApkManger.installDialog.isShowing()) {
                        updateApkManger.installDialog.dismissDialog();
                    }
                    //?????????
                    updateApkManger.installApk(null, null);
                }
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        //        getCurrentProcessNameByActivityManager(this);
    }


}
