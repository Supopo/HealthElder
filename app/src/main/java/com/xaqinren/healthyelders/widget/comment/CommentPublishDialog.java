package com.xaqinren.healthyelders.widget.comment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.databinding.DataBindingUtil;

import com.qmuiteam.qmui.util.QMUIDisplayHelper;
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
    private Activity activity;

    public CommentPublishDialog(Context context, String videoId) {
        this.context = new SoftReference<>(context);
        this.videoId = videoId;
        activity = (Activity) context;
        init();
    }

    public void setOnOperationListener(OnOperationListener onOperationListener) {
        this.onOperationListener = onOperationListener;
    }

    private void init() {

        contentView = View.inflate(context.get(), R.layout.pop_comment_publish, null);
        binding = DataBindingUtil.bind(contentView);
        dialog = new Dialog(context.get(), R.style.FullWidthDialog);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        dialog.setContentView(binding.getRoot());
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        binding.rlContainer.setOnClickListener(view -> {
            LogUtils.e(TAG, "关闭键盘");

            dialog.dismiss();
        });
        SoftKeyBoardUtil.hideKeyBoard(binding.inputEt);
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

    public void setReplyHint(String hintText) {
        binding.inputEt.setHint(hintText);
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
        binding.inputEt.setText("");
    }

    public boolean isShow() {
        if (dialog == null)
            return false;
        return dialog.isShowing();
    }

    public void keyBoardClosed() {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
        dialog.dismiss();
    }

    public interface OnOperationListener {
        void onEmojiBtnClick();

        void onPublish(String content);
    }
}
