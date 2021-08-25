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
        setTitle("设置");
        updateApkManger = new UpdateApkManger();
        tvTitle.setOnClickListener(lis -> {
            count++;
            if (count == 3) {
                ToastUtil.toastShortMessage("再点2下获取版本号");
            } else if (count == 4) {
                ToastUtil.toastShortMessage("再点1下获取版本号");
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
                //查看认证
                startActivity(LookAuthActivity.class);
            } else {
                //去认证
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
            //清理内存
            showClearDialog();
        });
        binding.versionLayout.setOnClickListener(v -> {
            //版本更细
            showVersion();
        });
        binding.logoutLayout.setOnClickListener(v -> {
            //退出登录
            showLogout();
        });
        binding.zhuxiaoLayout.setOnClickListener(v -> {
            //注销
            YesOrNoDialog yesOrNoDialog = new YesOrNoDialog(this);
            yesOrNoDialog.setMessageText("确定注销账号吗？");
            yesOrNoDialog.showDialog();
            yesOrNoDialog.setRightBtnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    yesOrNoDialog.dismissDialog();
                    ToastUtil.toastShortMessage("您已提交申请！");
                }
            });
        });
        binding.versionName.setText(BuildConfig.VERSION_NAME);
    }

    private void showClearDialog() {
        long size = getCacheDir().length();
        long sizeMb = size / 1024 / 1024;
        List<ListPopMenuBean> menus = new ArrayList<>();
        menus.add(new ListPopMenuBean("清理空间: " + sizeMb));
        ListBottomPopup listBottomPopup = new ListBottomPopup(this, menus);
        listBottomPopup.setOnItemClickListener((adapter, view, position) -> {
            //掉接口
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
                //版本号比较
                if (versionBean != null) {
                    if (versionBean.newAppVersion.versionNumber != getVersionCode(this)) {
                        updateInfo = versionBean.newAppVersion.upgradeContent;
                        versionName = versionBean.newAppVersion.resVersionNumber;
                        downUrl = versionBean.newAppVersion.upgradeUrl;
                        mustUpdate = versionBean.autoUpdateApplet;

                        //版本更新修改
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("hidCancelbtn", mustUpdate);
                        jsonObject.put("verCode", versionName);
                        jsonObject.put("con", updateInfo);
                        jsonObject.put("downUrl", downUrl);
                        updateApkManger.showUpdateDialog(jsonObject, this);
                    } else {
                        ToastUtils.showShort("已是最新版本");
                    }
                }
            }
        });
    }

    private void showLogout() {
        long size = getCacheDir().length();
        long sizeMb = size / 1024 / 1024;
        List<ListPopMenuBean> menus = new ArrayList<>();
        ListPopMenuBean listPopMenuBean = new ListPopMenuBean("是否退出当前账号?");
        //        listPopMenuBean.subTitle = "@" + userInfoBean.getNickname();
        //        listPopMenuBean.subTitleColor = Color.parseColor("#999999");
        menus.add(listPopMenuBean);
        menus.add(new ListPopMenuBean("退出登录", Color.parseColor("#F81E4D")));
        ListBottomPopup listBottomPopup = new ListBottomPopup(this, menus);
        listBottomPopup.setOnItemClickListener((adapter, view, position) -> {
            //掉接口
            if (position == 1) {
                listBottomPopup.dismiss();
                //清除缓存
                InfoCache.getInstance().clearLogin();
                ACache.get(this).clear();
                //给主页发送消息回到首页模块
                RxBus.getDefault().post(new EventBean(CodeTable.JUMP_ACT, "main-act"));
                //跳到登录页面
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
                //认证成功,更新个人信息
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10086) {
            //开启权限回来了
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //先获取是否有安装未知来源应用的权限
                updateApkManger.haveInstallPermission = this.getPackageManager().canRequestPackageInstalls();
                if (updateApkManger.haveInstallPermission) {
                    if (updateApkManger.installDialog.isShowing()) {
                        updateApkManger.installDialog.dismissDialog();
                    }
                    //去安装
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

    //    获取本地软件版本号
    private int getVersionCode(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (Exception e) {
            return 0;
        }

    }


}
