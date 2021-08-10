package me.goldze.mvvmhabit.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import me.goldze.mvvmhabit.R;


/**
 * 加载Dialog
 */
public class LoadingDialog {
    private Dialog centerDialog;
    private TextView message;
    private TextView leftBtn, rightBtn;
    private Context context;
    private View.OnClickListener onClickListener;
    private LottieAnimationView loadView;
    private boolean cancel = true;
    public LoadingDialog(Context context) {
        this.context = context;
        init(context);
    }
    public LoadingDialog(Context context,boolean cancel) {
        this.context = context;
        this.cancel = cancel;
        init(context);
    }

    public void init(Context context) {
        centerDialog = new Dialog(context, R.style.CustomerTipDialog);
        //填充对话框的布局
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_tip_load, null);
        //点击外部不可dismiss
        centerDialog.setCancelable(cancel);
        //将布局设置给Dialog
        centerDialog.setContentView(view);
        //获取当前Activity所在的窗体
        Window dialogWindow = centerDialog.getWindow();
        //设置Dialog从窗体底部弹出
//        dialogWindow.setGravity(Gravity.CENTER);
        //设置弹出动画
        //        dialogWindow.setWindowAnimations(R.style.DialogBottomAnimation);
        //获得窗体的属性
        WindowManager.LayoutParams params = dialogWindow.getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;//设置宽高模式，
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;//设置宽高模式，
        dialogWindow.setAttributes(params);

        loadView = view.findViewById(R.id.loadView);
    }


    public void show() {
        if (centerDialog != null && centerDialog.isShowing()) {
            return;
        }
        centerDialog.show();
        loadView.playAnimation();
    }

    public void dismiss() {
        if (centerDialog != null) {
            centerDialog.dismiss();
        }
        loadView.cancelAnimation();
    }

    public boolean isShowing() {
        return centerDialog.isShowing();
    }
}
