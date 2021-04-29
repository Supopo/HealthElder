package com.xaqinren.healthyelders.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.utils.AnimUtils;

import razerdp.basepopup.BasePopup;
import razerdp.basepopup.BasePopupWindow;

public class LiteAvOpenModePopupWindow extends BasePopupWindow implements View.OnClickListener {
    LinearLayout openLayout ;
    LinearLayout friendLayout ;
    LinearLayout privateLayout ;
    LinearLayout hideLayout ;
    SwitchButton recommendBtn ;
    ImageView openIv ;
    ImageView friendIv;
    ImageView privateIv ;
    ImageView hideIv ;
    ImageView closeIv;
    RelativeLayout rlContain;

    public static final int OPEN_MODE = 0;
    public static final int FRIEND_MODE = 1;
    public static final int PRIVATE_MODE = 2;
    public static final int HIDE_MODE = 3;
    private OnItemSelListener onItemSelListener;
    private int currentMode = OPEN_MODE;



    public void setOnItemSelListener(OnItemSelListener onItemSelListener) {
        this.onItemSelListener = onItemSelListener;
    }

    public LiteAvOpenModePopupWindow(Context context) {
        super(context);
        initView();
    }

    public LiteAvOpenModePopupWindow(Context context, boolean delayInit) {
        super(context, delayInit);
        initView();
    }

    public LiteAvOpenModePopupWindow(Context context, int width, int height) {
        super(context, width, height);
        initView();
    }

    public LiteAvOpenModePopupWindow(Context context, int width, int height, boolean delayInit) {
        super(context, width, height, delayInit);
        initView();
    }

    private void initView() {
        setShowAnimation(AnimUtils.PopAnimBottom2Enter(getContext()));
        setDismissAnimation(AnimUtils.PopAnimBottom2Exit(getContext()));
        openLayout = findViewById(R.id.open_layout);
        friendLayout = findViewById(R.id.friend_layout);
        privateLayout = findViewById(R.id.private_layout);
        hideLayout = findViewById(R.id.hide_layout);
        recommendBtn = findViewById(R.id.recommend_btn);
        rlContain = findViewById(R.id.rl_container);
        openIv = findViewById(R.id.open_iv);
        friendIv = findViewById(R.id.friend_iv);
        privateIv = findViewById(R.id.private_iv);
        closeIv = findViewById(R.id.close_iv);
        hideIv = findViewById(R.id.hide_iv);
        openLayout.setOnClickListener(this);
        friendLayout.setOnClickListener(this);
        privateLayout.setOnClickListener(this);
        hideLayout.setOnClickListener(this);
        closeIv.setOnClickListener(this);


    }

    @Override
    public void showPopupWindow() {
        super.showPopupWindow();
    }

    @Override
    public void onDismiss() {
        super.onDismiss();
    }


    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.pop_lite_av_open_mode);
    }

    @Override
    public void onClick(View view) {
        openIv.setVisibility(View.GONE);
        friendIv.setVisibility(View.GONE);
        privateIv.setVisibility(View.GONE);
        hideIv.setVisibility(View.GONE);
        switch (view.getId()) {
            case R.id.open_layout:
                openIv.setVisibility(View.VISIBLE);
                onItemSelListener.onItemSel(OPEN_MODE);
                break;
            case R.id.friend_layout:
                friendIv.setVisibility(View.VISIBLE);
                onItemSelListener.onItemSel(FRIEND_MODE);
                break;
            case R.id.private_layout:
                privateIv.setVisibility(View.VISIBLE);
                onItemSelListener.onItemSel(PRIVATE_MODE);
                break;
            case R.id.hide_layout:
                hideIv.setVisibility(View.VISIBLE);
                onItemSelListener.onItemSel(HIDE_MODE);
                break;
            case R.id.close_iv:
                dismiss();
                break;
        }
    }


    public interface OnItemSelListener{
        void onItemSel(int mode);
    }
}
