package com.xaqinren.healthyelders.moduleMine.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;

import com.alibaba.fastjson.JSONObject;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.tencent.qcloud.ugckit.utils.ScreenUtils;
import com.tencent.qcloud.ugckit.utils.ToastUtil;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.BuildConfig;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.databinding.ActivitySettingBinding;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.global.InfoCache;
import com.xaqinren.healthyelders.moduleLogin.activity.SelectLoginActivity;
import com.xaqinren.healthyelders.moduleLogin.bean.UserInfoBean;
import com.xaqinren.healthyelders.moduleMine.bean.VersionBean;
import com.xaqinren.healthyelders.moduleMine.viewModel.SettingViewModel;
import com.xaqinren.healthyelders.moduleZhiBo.activity.StartRenZhengActivity;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ListPopMenuBean;
import com.xaqinren.healthyelders.uniApp.UniUtil;
import com.xaqinren.healthyelders.utils.ACache;
import com.xaqinren.healthyelders.widget.ListBottomPopup;
import com.xaqinren.healthyelders.widget.MyProgressDialog;
import com.xaqinren.healthyelders.widget.YesOrNoDialog;
import com.xxx.libbase.UpdateApkManger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.AppManager;
import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.utils.SPUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import razerdp.basepopup.BasePopupWindow;

public class SettingActivity extends BaseActivity<ActivitySettingBinding, SettingViewModel> {
    private UserInfoBean userInfoBean;
    private QMUIDialog mInstallDialog;

    private String versionName;
    private String downUrl;
    private String updateInfo;
    private double size;
    private boolean mustUpdate;
    private UpdateApkManger updateApkManger;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_setting;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    private int count;

    @Override
    public void initData() {
        super.initData();
        setTitle("??????");
        updateApkManger = new UpdateApkManger();
        tvTitle.setOnClickListener(lis -> {
            count++;
            if (count == 3) {
                ToastUtil.toastShortMessage("??????2??????????????????");
            } else if (count == 4) {
                ToastUtil.toastShortMessage("??????1??????????????????");
            } else if (count == 5) {
                ToastUtil.toastShortMessage(getVersionName(this));
                count = 0;
            }
        });
        userInfoBean = UserInfoMgr.getInstance().getUserInfo();
        binding.setData(userInfoBean);
        binding.phoneLayout.setOnClickListener(v -> {

        });
        binding.authLayout.setOnClickListener(v -> {
            if (userInfoBean.getHasRealName()) {
                //????????????
                startActivity(LookAuthActivity.class);
            } else {
                //?????????
                Bundle bundle = new Bundle();
                bundle.putInt(Constant.REN_ZHENG_TYPE, 2);
                startActivity(StartRenZhengActivity.class, bundle);
            }
        });
        binding.agreeLayout.setOnClickListener(v -> {
            UniUtil.openUniApp(this, Constant.JKZL_MINI_APP_ID, Constant.MINI_AGREEMENT, null, true);
        });
        binding.privacyLayout.setOnClickListener(v -> {
            UniUtil.openUniApp(this, Constant.JKZL_MINI_APP_ID, Constant.MINI_PRIVACY, null, true);
        });
        binding.aboutLayout.setOnClickListener(v -> {
            UniUtil.openUniApp(this, Constant.JKZL_MINI_APP_ID, Constant.MINI_ABOUT_US, null, true);
        });
        binding.memoryLayout.setOnClickListener(v -> {
            //????????????
            showClearDialog();
        });
        binding.versionLayout.setOnClickListener(v -> {
            //????????????
            showVersion();
        });
        binding.logoutLayout.setOnClickListener(v -> {
            //????????????
            showLogout();
        });
        binding.zhuxiaoLayout.setOnClickListener(v -> {
            //??????
            YesOrNoDialog yesOrNoDialog = new YesOrNoDialog(this);
            yesOrNoDialog.setMessageText("????????????????????????");
            yesOrNoDialog.showDialog();
            yesOrNoDialog.setRightBtnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    yesOrNoDialog.dismissDialog();
                    ToastUtil.toastShortMessage("?????????????????????");
                }
            });
        });
        binding.versionName.setText(BuildConfig.VERSION_NAME);
    }

    private void showClearDialog() {
        long size = getCacheDir().length();
        long sizeMb = size / 1024 / 1024;
        List<ListPopMenuBean> menus = new ArrayList<>();
        menus.add(new ListPopMenuBean("????????????: " + sizeMb));
        ListBottomPopup listBottomPopup = new ListBottomPopup(this, menus);
        listBottomPopup.setOnItemClickListener((adapter, view, position) -> {
            //?????????
            if (position == 0) {
                getCacheDir().deleteOnExit();
                listBottomPopup.dismiss();
            }
        });
        listBottomPopup.showPopupWindow();
        ScreenUtils.setWindowAlpha(getContext(), 1.0f, 0.6f, 400);
        listBottomPopup.setOnDismissListener(new BasePopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                ScreenUtils.setWindowAlpha(getContext(), 0.6f, 1f, 200);
            }
        });
    }

    private void showVersion() {
        viewModel.checkVersion();
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.versionBean.observe(this, versionBean -> {
            if (versionBean != null) {
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
                    } else {
                        ToastUtils.showShort("??????????????????");
                    }
                }
            }
        });
    }

    private void showLogout() {
        long size = getCacheDir().length();
        long sizeMb = size / 1024 / 1024;
        List<ListPopMenuBean> menus = new ArrayList<>();
        ListPopMenuBean listPopMenuBean = new ListPopMenuBean("?????????????????????????");
        //        listPopMenuBean.subTitle = "@" + userInfoBean.getNickname();
        //        listPopMenuBean.subTitleColor = Color.parseColor("#999999");
        menus.add(listPopMenuBean);
        menus.add(new ListPopMenuBean("????????????", Color.parseColor("#F81E4D")));
        ListBottomPopup listBottomPopup = new ListBottomPopup(this, menus);
        listBottomPopup.setOnItemClickListener((adapter, view, position) -> {
            //?????????
            if (position == 1) {
                listBottomPopup.dismiss();
                //????????????
                InfoCache.getInstance().clearLogin();
                ACache.get(this).clear();
                //???????????????????????????????????????
                RxBus.getDefault().post(new EventBean(CodeTable.JUMP_ACT, "main-act"));
                //??????????????????
                startActivity(SelectLoginActivity.class);
                finish();
            }
        });
        listBottomPopup.showPopupWindow();
        ScreenUtils.setWindowAlpha(getContext(), 1.0f, 0.6f, 400);
        listBottomPopup.setOnDismissListener(new BasePopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                ScreenUtils.setWindowAlpha(getContext(), 0.6f, 1f, 200);
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            boolean status = intent.getBooleanExtra("status", false);
            if (status) {
                //????????????,??????????????????
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10086) {
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

    private String getVersionName(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (Exception e) {
            return "";
        }

    }

    //    ???????????????????????????
    private int getVersionCode(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (Exception e) {
            return 0;
        }

    }


}
