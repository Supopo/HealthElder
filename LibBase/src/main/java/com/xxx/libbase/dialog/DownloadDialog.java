package com.xxx.libbase.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xxx.libbase.R;
import com.xxx.libbase.utils.ScreenUtils;
import com.xxx.libbase.weiget.CustomLayout;


//更新-下载Dialog
public class DownloadDialog {

    private Dialog centerDialog;
    private TextView tvCancel, tvDownMsg;
    private CustomLayout content;
    private ProgressBar progressBar;
    private Context context;

    public DownloadDialog(Context context) {
        this.context = context;
        init(context);
    }

    public void init(Context context) {
        centerDialog = new Dialog(context, R.style.CustomerDialog);
        //填充对话框的布局
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_download, null);

        //将布局设置给Dialog
        centerDialog.setContentView(view);
        //获取当前Activity所在的窗体
        Window dialogWindow = centerDialog.getWindow();
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity(Gravity.CENTER);
        //获得窗体的属性
        //点击外部不可dismiss
        centerDialog.setCancelable(false);

        WindowManager.LayoutParams params = dialogWindow.getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;//设置宽高模式，
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;//设置宽高模式，
        dialogWindow.setAttributes(params);


        //初始化控件
        content = view.findViewById(R.id.content);
        progressBar = view.findViewById(R.id.progress);
        tvCancel = view.findViewById(R.id.tv_cancel);
        tvDownMsg = view.findViewById(R.id.tv_downMsg);
        content.setCirValue(30);

        int screenWidth = ScreenUtils.getScreenWidth(context);
        int screenHeight = ScreenUtils.getScreenHeight(context);
        if (screenWidth > screenHeight) {
            //横屏
            screenWidth = screenHeight;
        }
        ViewGroup.LayoutParams layoutParams = content.getLayoutParams();
        layoutParams.width = (int) (screenWidth * 0.8f);
        content.setLayoutParams(layoutParams);
    }

    public boolean isShowing() {
        return centerDialog.isShowing();
    }

    public void setProgress(int progress) {
        progressBar.setProgress(progress);
    }

    public void setCancelBtnClickListener(View.OnClickListener onClickListener) {
        tvCancel.setOnClickListener(onClickListener);
    }

    public void setCancelText(String cancelText) {
        if (!isNull(cancelText)) {
            tvCancel.setVisibility(View.VISIBLE);
            tvCancel.setText(cancelText);
        }
    }

    public void setDownMsg(String msg) {
        if (!isNull(msg)) {
            tvDownMsg.setText(msg);
        }
    }

    public void setHideCancel(boolean show) {
        tvCancel.setVisibility(show ? View.GONE : View.VISIBLE);
        //点击外部不可dismiss
        centerDialog.setCancelable(!show);
    }

    public void showDialog() {
        if (centerDialog != null && centerDialog.isShowing()) {
            return;
        }
        centerDialog.show();

    }

    public void dismissDialog() {
        if (centerDialog != null) {
            centerDialog.dismiss();
        }

    }

    private boolean isNull(String str) {
        if (str == null || str.equals("")) {
            return true;
        }
        return false;
    }
}
