package com.xaqinren.healthyelders.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.xaqinren.healthyelders.R;


/**
 * =====================================================
 * 描    述:底部Dialog
 * =====================================================
 */
public class BottomDialog extends Dialog implements View.OnClickListener {
    private Context context;
    private int layoutRes;
    private View view;
    private int[] clickIds;   //需要设置点击事件的ID.需要其他ID,在dialog实例化后在dialog上fbc.
    private boolean autoWidthHeight = false;
    private int height;
    private int width;
    private int gravity;

    public BottomDialog(Context context, int layoutRes, int[] clickIds) {
        super(context, R.style.dialog_full);    //设置主题
        this.context = context;
        this.layoutRes = layoutRes;
        this.clickIds = clickIds;
    }

    public BottomDialog(Context context, View view, int[] clickIds) {
        super(context, R.style.dialog_full);    //设置主题
        this.context = context;
        this.view = view;
        this.clickIds = clickIds;
    }

    public BottomDialog(Context context, View view, int[] clickIds, int width, int height, int gravity) {
        super(context, R.style.dialog_full);    //设置主题
        this.context = context;
        this.view = view;
        this.clickIds = clickIds;
        this.autoWidthHeight = true;
        this.width = width;
        this.height = height;
        this.gravity = gravity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        //底部弹出的Dialog
        //底部弹出的动画
        window.setWindowAnimations(R.style.DialogBottomAnimation);
        if (view != null) {
            setContentView(view);
        } else {
            setContentView(layoutRes);
        }

        if (autoWidthHeight) {
            getWindow().setLayout(width, height);
            window.setGravity(gravity);
        } else {
            window.setGravity(Gravity.BOTTOM);
            getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }
        //点击Dialog外部消失
        setCanceledOnTouchOutside(true);
        //禁用返回键
        setCancelable(true);
        //设置点击事件
        if (clickIds != null) {
            for (int id : clickIds) {
                findViewById(id).setOnClickListener(this);
            }
        }
    }

    public View getView() {
        if (view == null) {
            return getLayoutInflater().inflate(layoutRes, null);
        }
        return view;
    }

    private OnBottomItemClickListener listener;

    public interface OnBottomItemClickListener {
        void onBottomItemClick(BottomDialog dialog, View view);
    }

    public void setOnBottomItemClickListener(OnBottomItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        listener.onBottomItemClick(this, v);
    }
}