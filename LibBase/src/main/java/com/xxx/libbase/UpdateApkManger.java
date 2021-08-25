package com.xxx.libbase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.alibaba.fastjson.JSONObject;
import com.xxx.libbase.dialog.DownloadDialog;
import com.xxx.libbase.dialog.NormalDialog;
import com.xxx.libbase.dialog.UpdateDialog;
import com.xxx.libbase.utils.PermissionUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Lee. on 2021/8/24.
 */
public class UpdateApkManger extends AppCompatActivity {
    private JSONObject tempOptions;
    private Context tempContext;
    private String downUrl;
    public UpdateDialog updateDialog;
    public NormalDialog installDialog;
    public DownloadDialog downDialog;
    private boolean isStop;//是否停止下载
    private Thread thread;
    public boolean haveInstallPermission;

    public void showUpdateDialog(final JSONObject options, final Context context) {
        tempOptions = options;
        tempContext = context;

        if (updateDialog == null) {
            updateDialog = new UpdateDialog(context);
        }

        if (options.containsKey("verCode")) {
            String verCode = options.getString("verCode");
            updateDialog.setVersionText(verCode);
        }

        if (options.containsKey("title")) {
            String title = options.getString("title");
            updateDialog.setTitleText(title);
        }
        if (options.containsKey("con")) {
            String content = options.getString("con");
            updateDialog.setContentText(content);
        }
        if (options.containsKey("downUrl")) {
            downUrl = options.getString("downUrl");
        }

        //是否隐藏取消按钮-判断是否强制更新
        if (options.containsKey("hidCancelbtn")) {
            Boolean hidCancelbtn = options.getBoolean("hidCancelbtn");
            if (hidCancelbtn != null) {
                updateDialog.setSingleConfirm(hidCancelbtn);
            } else {
                updateDialog.setSingleConfirm(true);
            }
        }
        if (options.containsKey("btnBgColor")) {
            String btnBgColor = options.getString("btnBgColor");
            updateDialog.setColorConfirm(btnBgColor);
        }
        if (options.containsKey("updateBtnText")) {
            String updateBtnText = options.getString("updateBtnText");
            updateDialog.setRightBtnText(updateBtnText);
        }

        updateDialog.showDialog();

        updateDialog.setLeftBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDialog.dismissDialog();
            }
        });
        updateDialog.setRightBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //检查存储权限
                String[] permissions = new String[]{
                        "android.permission.READ_EXTERNAL_STORAGE",
                        "android.permission.WRITE_EXTERNAL_STORAGE"};
                boolean check = checkStoragePermission((Activity) context, permissions);
                if (check) {
                    //有权限，开始安装应用程序
                    showDownDialog(options, context);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        ((Activity) context).requestPermissions(permissions, 10087);
                    }
                }


            }
        });
    }

    private boolean checkStoragePermission(Activity activity, String[] permissions) {
        //在进入主页面时动态获取(记得在清单文件中添加)
        return PermissionUtils.checkPermissionAllGranted(activity, permissions);
    }

    private void showInstallDialog(final Context context) {
        installDialog = new NormalDialog(context);
        installDialog.setTitleText("安装提示");
        installDialog.setMessageText("安装应用需要打开未知来源权限，请去设置中开启权限");
        installDialog.showDialog();
        installDialog.setLeftBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                installDialog.dismissDialog();
            }
        });
        installDialog.setRightBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转申请权限的页面
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startInstallPermissionSettingActivity(context);
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startInstallPermissionSettingActivity(Context context) {
        //注意这个是8.0新API
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
        Activity activity = (Activity) context;
        activity.startActivityForResult(intent, 10086);
    }

    public void showDownDialog(JSONObject options, Context context) {
        if (options == null) {
            options = tempOptions;
        }
        if (context == null) {
            context = tempContext;
        }
        updateDialog.dismissDialog();
        //开始下载
        //弹出下载弹窗
        if (downDialog == null) {
            downDialog = new DownloadDialog(context);
        }
        if (options.containsKey("downMsgTip")) {
            String downMsgTip = options.getString("downMsgTip");
            downDialog.setDownMsg(downMsgTip);
        }
        if (options.containsKey("hidCancelbtn")) {
            Boolean hidCancelbtn = options.getBoolean("hidCancelbtn");
            if (hidCancelbtn != null && hidCancelbtn) {
                downDialog.setHideCancel(hidCancelbtn);
            }
        }


        downDialog.setCancelBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStop = true;
                //取消下载
                downDialog.dismissDialog();
            }
        });

        downDialog.showDialog();
        //访问网络下载apk
        thread = new Thread(new DownloadApk(options, context));
        thread.start();
        isStop = false;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 100) {
                if (downDialog != null && downDialog.isShowing()) {
                    downDialog.setDownMsg("下载已完成。");
                    downDialog.setCancelText("立即安装");
                    //展示安装按钮
                    downDialog.setCancelBtnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //检查安装权限
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                //先获取是否有安装未知来源应用的权限
                                haveInstallPermission = tempContext.getPackageManager().canRequestPackageInstalls();
                                if (!haveInstallPermission) {
                                    //没有权限
                                    showInstallDialog(tempContext);
                                    return;
                                }
                            }
                            //安装
                            installApk(tempOptions, tempContext);
                        }
                    });
                }
            }
        }
    };

    /**
     * 访问网络下载apk
     */
    private class DownloadApk implements Runnable {
        InputStream is;
        FileOutputStream fos;
        JSONObject options;
        Context context;

        public DownloadApk(JSONObject options, Context context) {
            this.options = options;
            this.context = context;
        }

        @Override
        public void run() {
            OkHttpClient client = new OkHttpClient();
            String url = downUrl;
            Request request = new Request.Builder().get().url(url).build();
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    Log.v("======", "开始下载了。");
                    //获取内容总长度
                    long contentLength = response.body().contentLength();
                    //设置最大值(百分比)
                    //默认设置100
                    //保存到sd卡
                    String apkName = options.getString("apkName") == null ? "newApp" : options.getString("apkName");
                    String verCode = options.getString("verCode") == null ? "1.0.0" : options.getString("verCode");

                    File apkFile = new File(Environment.getExternalStorageDirectory(), apkName + verCode + ".apk");

                    try {
                        //文件存在,且大小一致就去安装
                        if (apkFile.exists() && apkFile.isFile()) {
                            FileInputStream fis = new FileInputStream(apkFile);
                            FileChannel fc = fis.getChannel();
                            if (contentLength == fc.size()) {
                                if (downDialog != null && downDialog.isShowing()) {
                                    downDialog.setProgress(100);
                                }
                                mHandler.sendEmptyMessage(100);
                                installApk(options, context);
                                return;
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    fos = new FileOutputStream(apkFile);
                    //获得输入流
                    is = response.body().byteStream();
                    //定义缓冲区大小
                    byte[] bys = new byte[1024];
                    int progress = 0;
                    int len = -1;
                    while ((len = is.read(bys)) != -1) {
                        try {
                            if (isStop) {
                                return;
                            }

                            Thread.sleep(1);
                            fos.write(bys, 0, len);
                            fos.flush();
                            progress += len;
                            //设置进度百分比
                            double n = (double) progress / contentLength;
                            if (downDialog != null && downDialog.isShowing()) {
                                downDialog.setProgress((int) (100 * n));
                            }
                            Log.v("======", "下载中：" + n);

                            if (n == 1) {
                                mHandler.sendEmptyMessage(100);
                            }
                        } catch (InterruptedException e) {
                        }
                    }
                    Log.v("======", "下载结束了。");
                    //检查安装权限
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        //先获取是否有安装未知来源应用的权限
                        haveInstallPermission = context.getPackageManager().canRequestPackageInstalls();
                        if (!haveInstallPermission) {
                            //没有权限
                            showInstallDialog(context);
                            return;
                        }
                    }
                    //先去尝试安装
                    installApk(options, context);
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
            downDialog.dismissDialog();
        }
    }

    /**
     * 下载完成,提示用户安装
     */
    public void installApk(JSONObject options, Context context) {
        if (options == null) {
            options = tempOptions;
        }
        if (context == null) {
            context = tempContext;
        }

        //先制定安装路径
        String apkName = options.getString("apkName") == null ? "newApp" : options.getString("apkName");
        String verCode = options.getString("verCode") == null ? "1.0.0" : options.getString("verCode");

        File apkFile = new File(Environment.getExternalStorageDirectory(), apkName + verCode + ".apk");
        //文件不存在就去下载
        if (!apkFile.exists()) {
            showDownDialog(options, context);
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
            apkUri = FileProvider.getUriForFile(context, "com.xxx.libbase.fileprovider", apkFile);
            // 给目标应用一个临时授权
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            apkUri = Uri.fromFile(apkFile);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        }

        context.startActivity(intent);
    }


    //复制到项目页面
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10086) {
            //开启权限回来了
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //先获取是否有安装未知来源应用的权限
                haveInstallPermission = tempContext.getPackageManager().canRequestPackageInstalls();
                if (haveInstallPermission) {
                    if (installDialog.isShowing()) {
                        installDialog.dismissDialog();
                    }
                    //去安装
                    installApk(tempOptions, tempContext);
                }
            }
        }
    }
}
