package com.xaqinren.healthyelders.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.LiveConstants;


/**
 * 确定-取消 Dialog
 */
public class YesOrNoDialog {
    private Dialog centerDialog;
    private TextView message;
    private TextView leftBtn, rightBtn;
    private Context context;
    private View.OnClickListener onClickListener;

    public YesOrNoDialog(Context context) {
        this.context = context;
        init(context);
    }

    public void init(Context context) {
        centerDialog = new Dialog(context, R.style.CustomerDialog);
        //填充对话框的布局
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_center_layout, null);
        //点击外部不可dismiss
        centerDialog.setCancelable(false);
        //将布局设置给Dialog
        centerDialog.setContentView(view);
        //获取当前Activity所在的窗体
        Window dialogWindow = centerDialog.getWindow();
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity(Gravity.CENTER);
        //设置弹出动画
        //        dialogWindow.setWindowAnimations(R.style.DialogBottomAnimation);
        //获得窗体的属性
        WindowManager.LayoutParams params = dialogWindow.getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;//设置宽高模式，
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;//设置宽高模式，
        dialogWindow.setAttributes(params);


        //初始化控件
        message = view.findViewById(R.id.message);
        leftBtn = view.findViewById(R.id.left_btn);
        rightBtn = view.findViewById(R.id.right_btn);

        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (centerDialog.isShowing()) {
                    centerDialog.dismiss();
                }
            }
        });
    }

    public void setMessageText(String str) {
        message.setText(str);
    }

    public void setLeftBtnText(String str) {
        leftBtn.setText(str);
    }

    public void setRightBtnText(String str) {
        rightBtn.setText(str);
    }

    public void setRightBtnClickListener(View.OnClickListener onClickListener) {
        rightBtn.setOnClickListener(onClickListener);
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
}
