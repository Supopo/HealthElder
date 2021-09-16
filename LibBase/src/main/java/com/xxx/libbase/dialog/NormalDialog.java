package com.xxx.libbase.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.xxx.libbase.R;
import com.xxx.libbase.utils.ScreenUtils;
import com.xxx.libbase.weiget.CustomLayout;

public class NormalDialog {

    private Dialog centerDialog;
    private TextView message, title;
    private TextView leftBtn, rightBtn;
    private View bottomLine;
    private CustomLayout content;
    private int colorTitle, colorMessage, colorCancel, colorConfirm;

    private Context context;

    public NormalDialog(Context context) {
        this.context = context;
        init(context);
    }

    public void init(Context context) {
        centerDialog = new Dialog(context, R.style.CustomerDialog);
        //填充对话框的布局
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_normal, null);
        //点击外部不可dismiss
        centerDialog.setCancelable(true);
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
        content = view.findViewById(R.id.content);
        message = view.findViewById(R.id.tv_message);
        title = view.findViewById(R.id.tv_title);
        leftBtn = view.findViewById(R.id.btn_left);
        rightBtn = view.findViewById(R.id.btn_right);
        bottomLine = view.findViewById(R.id.bottom_line);

        int screenWidth = ScreenUtils.getScreenWidth(context);
        int screenHeight = ScreenUtils.getScreenHeight(context);
        if (screenWidth > screenHeight) {
            //横屏
            screenWidth = screenHeight;
        }
        ViewGroup.LayoutParams layoutParams = content.getLayoutParams();
        layoutParams.width = (int) (screenWidth * 0.7f);
        content.setLayoutParams(layoutParams);
    }

    public void setTitleText(String str) {
        if (isNull(str)) {
            title.setVisibility(View.GONE);
        } else {
            title.setVisibility(View.VISIBLE);
        }
        title.setText(str);
    }

    public void setMessageText(String str) {
        if (isNull(str)) {
            message.setVisibility(View.GONE);
        } else {
            message.setVisibility(View.VISIBLE);
        }
        message.setText(str);
    }

    public void setLeftBtnText(String str) {
        if (isNull(str)) {
            str = "取消";
        }
        leftBtn.setText(str);
    }

    public void setRightBtnText(String str) {
        if (isNull(str)) {
            str = "确定";
        }
        rightBtn.setText(str);
    }

    public void setRightBtnClickListener(View.OnClickListener onClickListener) {
        rightBtn.setOnClickListener(onClickListener);
    }

    public void setLeftBtnClickListener(View.OnClickListener onClickListener) {
        leftBtn.setOnClickListener(onClickListener);
    }

    public void setSingleConfirm(boolean singleConfirm) {
        if (!singleConfirm) {
            leftBtn.setVisibility(View.GONE);
            bottomLine.setVisibility(View.GONE);
        } else {
            leftBtn.setVisibility(View.VISIBLE);
            bottomLine.setVisibility(View.VISIBLE);
        }
    }

    public void setTextAlign(String align) {
        ///left居左，center居中  ，right
        if (isNull(align)) {
            return;
        }
        if (align.equals("left")) {
            message.setGravity(Gravity.LEFT);
        } else if (align.equals("right")) {
            message.setGravity(Gravity.RIGHT);
        } else if (align.equals("center")) {
            message.setGravity(Gravity.CENTER);
        }

    }

    public void setColorTitle(String colorTitle) {
        if (!isNull(colorTitle)) {
            title.setTextColor(Color.parseColor(colorTitle));
        }
    }

    public void setColorMessage(String colorMessage) {
        if (!isNull(colorMessage)) {
            message.setTextColor(Color.parseColor(colorMessage));
        }
    }

    public void setColorCancel(String colorCancel) {
        if (!isNull(colorCancel)) {
            leftBtn.setTextColor(Color.parseColor(colorCancel));
        }
    }

    public void setColorConfirm(String colorConfirm) {
        if (colorConfirm != null) {
            rightBtn.setTextColor(Color.parseColor(colorConfirm));
        }
    }

    public void setBorderRadius(int borderRadius) {
        content.setCirValue(borderRadius);
    }

    public void setBackGround(String color) {
        if (!isNull(color)) {
            content.setBgColor(Color.parseColor(color));
        }
    }

    public boolean isShowing() {
        if (centerDialog != null) {
            return centerDialog.isShowing();
        }
        return false;
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
