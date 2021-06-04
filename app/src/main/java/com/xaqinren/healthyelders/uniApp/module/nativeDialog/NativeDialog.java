package com.xaqinren.healthyelders.uniApp.module.nativeDialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.xaqinren.healthyelders.R;

public class NativeDialog {

    private Dialog centerDialog;
    private TextView message, title;
    private TextView leftBtn, rightBtn;
    private View bottomLine;
    private CardView content;

    private boolean singleConfirm;
    private int colorTitle,colorMessage,colorCancel, colorConfirm;
    private int sizeTitle,sizeMessage, sizeBtn;

    private Context context;
    private View.OnClickListener onClickListener;

    public NativeDialog(Context context) {
        this.context = context;
        init(context);
    }

    public void init(Context context) {
        centerDialog = new Dialog(context, R.style.CustomerDialog);
        //填充对话框的布局
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_uni_center_layout, null);
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
        content = view.findViewById(R.id.content);
        message = view.findViewById(R.id.message);
        title = view.findViewById(R.id.title);
        leftBtn = view.findViewById(R.id.left_btn);
        rightBtn = view.findViewById(R.id.right_btn);
        bottomLine = view.findViewById(R.id.bottom_line);


        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (centerDialog.isShowing()) {
                    centerDialog.dismiss();
                }
            }
        });
    }

    public void setTitleText(String str) {
        if (isNull(str)) {
            title.setVisibility(View.GONE);
        }else{
            title.setVisibility(View.VISIBLE);
        }
        title.setText(str);
    }

    public void setMessageText(String str) {
        if (isNull(str)) {
            message.setVisibility(View.GONE);
        }else{
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
        this.singleConfirm = singleConfirm;
        if (singleConfirm) {
            leftBtn.setVisibility(View.GONE);
            bottomLine.setVisibility(View.GONE);
        }else{
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
            this.colorTitle = Color.parseColor(colorTitle);
            this.title.setTextColor(this.colorTitle);
        }
    }

    public void setColorMessage(String colorMessage) {
        if (!isNull(colorMessage)) {
            this.colorMessage = Color.parseColor(colorMessage);
            this.message.setTextColor(this.colorMessage);
        }
    }

    public void setColorCancel(String colorCancel) {
        if (!isNull(colorCancel)) {
            this.colorCancel = Color.parseColor(colorCancel);
            this.leftBtn.setTextColor(this.colorCancel);
        }
    }

    public void setColorConfirm(String colorConfirm) {
        if (colorConfirm != null) {
            this.colorConfirm = Color.parseColor(colorConfirm);
            this.rightBtn.setTextColor(this.colorConfirm);
        }
    }


    public void setSizeTitle(int sizeTitle) {
        this.sizeTitle = sizeTitle;
    }

    public void setSizeMessage(int sizeMessage) {
        this.sizeMessage = sizeMessage;
    }

    public void setSizeBtn(int sizeBtn) {
        this.sizeBtn = sizeBtn;
    }

    public void setBackGround(String color) {
        if (!isNull(color)) {
            content.setCardBackgroundColor(Color.parseColor(color));
        }
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

    private boolean isNull(String str){
        if (str == null || str.equals("")) {
            return true;
        }
        return false;
    }
}
