package com.xaqinren.healthyelders.widget.comment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Instrumentation;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.tencent.qcloud.tim.uikit.TUIKit;
import com.tencent.qcloud.tim.uikit.utils.ScreenUtil;
import com.tencent.qcloud.tim.uikit.utils.SoftKeyBoardUtil;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.PopCommentPublishBinding;
import com.xaqinren.healthyelders.utils.LogUtils;

import java.lang.ref.SoftReference;

/**
 * 带输入框
 */
public class CommentPublishDialog {
    private final String TAG = this.getClass().getSimpleName();

    private Dialog dialog;
    private View contentView;
    private SoftReference<Context> context;
    private String videoId;
    private PopCommentPublishBinding binding;
    private OnOperationListener onOperationListener;

    public CommentPublishDialog(Context context, String videoId) {
        this.context = new SoftReference<>(context);
        this.videoId = videoId;
        init();
    }

    public void setOnOperationListener(OnOperationListener onOperationListener) {
        this.onOperationListener = onOperationListener;
    }

    private void init() {

        contentView = View.inflate(context.get(), R.layout.pop_comment_publish, null);
        binding = DataBindingUtil.bind(contentView);
        dialog = new Dialog(context.get(),R.style.FullWidthDialog);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        dialog.setContentView(binding.getRoot());
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        binding.rlContainer.setOnClickListener(view -> {
            LogUtils.e(TAG, "关闭键盘");

            dialog.dismiss();
        });SoftKeyBoardUtil.hideKeyBoard(binding.inputEt);
        binding.iconBtn.setOnClickListener(view -> {
            if (onOperationListener != null) {
                onOperationListener.onEmojiBtnClick();
            }
        });
        binding.publishBtn.setOnClickListener(view -> {
            if (onOperationListener != null) {
                onOperationListener.onPublish(binding.inputEt.getText().toString());
            }
        });
        binding.inputEt.setAtEnable(false);
        binding.inputEt.setTopicEnable(false);
    }


    public void show(View Parent) {
        if (dialog == null) {
            init();
        }
        dialog.show();
        dialog.getWindow().getDecorView().setPadding(0, 0, 0, 0); // 在底部，宽度撑满
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        int screenWidth = QMUIDisplayHelper.getScreenWidth(context.get());
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.width = screenWidth;
        params.gravity = Gravity.BOTTOM | Gravity.CENTER;//dialog从哪里弹出 //弹出窗口的宽高
        dialog.getWindow().setAttributes(params);
        binding.inputEt.setFocusable(true);
        binding.inputEt.setFocusableInTouchMode(true);
        binding.inputEt.requestFocus();
    }
    public boolean isShow(){
        if (dialog == null) return false;
        return dialog.isShowing();
    }
    public void keyBoardClosed() {
        dialog.dismiss();
    }
    public interface OnOperationListener{
        void onEmojiBtnClick();
        void onPublish(String content);
    }
}
