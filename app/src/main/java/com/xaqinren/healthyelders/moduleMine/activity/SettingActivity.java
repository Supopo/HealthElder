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

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;

import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.tencent.qcloud.ugckit.utils.ScreenUtils;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.databinding.ActivitySettingBinding;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.global.InfoCache;
import com.xaqinren.healthyelders.moduleLogin.activity.SelectLoginActivity;
import com.xaqinren.healthyelders.moduleLogin.bean.UserInfoBean;
import com.xaqinren.healthyelders.moduleMine.bean.VersionBean;
import com.xaqinren.healthyelders.moduleMine.viewModel.SettingViewModel;
import com.xaqinren.healthyelders.moduleZhiBo.activity.StartRenZhengActivity;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ListPopMenuBean;
import com.xaqinren.healthyelders.uniApp.UniUtil;
import com.xaqinren.healthyelders.widget.ListBottomPopup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.AppManager;
import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.base.BaseViewModel;
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

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_setting;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        setTitle("设置");
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
                        updateDialog();
                    }else{
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
                //跳到登录页面
                AppManager.getAppManager().finishAllActivity();
                startActivity(SelectLoginActivity.class);
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
            //有权限，开始安装应用程序  让每次都下载 这样靠谱
            //            installApk();
            downloadApk();
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

    /**
     * 更新提示
     */
    private void updateDialog() {
        new QMUIDialog.MessageDialogBuilder(this)
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
        dialog.setTitle(versionName + "版本更新 (" + size + "MB)");
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
            apkUri = FileProvider.getUriForFile(this, "com.xaqinren.fileprovider", apkFile);
            // 给目标应用一个临时授权
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            apkUri = Uri.fromFile(apkFile);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        }

        this.startActivity(intent);
    }


}
