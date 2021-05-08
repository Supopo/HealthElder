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
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.moduleLiteav.bean.LiteAvUserBean;
import com.xaqinren.healthyelders.utils.AnimUtils;

import java.util.ArrayList;
import java.util.List;

import razerdp.basepopup.BasePopup;
import razerdp.basepopup.BasePopupWindow;

/**
 * 选择公开,私有,朋友,不给谁看,弹窗
 */
public class LiteAvOpenModePopupWindow extends BasePopupWindow implements View.OnClickListener {
    LinearLayout openLayout ;
    LinearLayout friendLayout ;
    LinearLayout privateLayout ;
    LinearLayout hideLayout ;
    SwitchButton recommendBtn ;
    TextView unLookTv;
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
    public static final int SWITCH_MODE = 4;
    private OnItemSelListener onItemSelListener;
    private int currentMode = OPEN_MODE;

    //屏蔽用户列表
    private List<LiteAvUserBean> unLookUserList;


    public void setUnLookUserList(List<LiteAvUserBean> unLookUserList) {
        this.unLookUserList = unLookUserList;
    }

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
        unLookTv = findViewById(R.id.unlook_tv);
        openLayout.setOnClickListener(this);
        friendLayout.setOnClickListener(this);
        privateLayout.setOnClickListener(this);
        hideLayout.setOnClickListener(this);
        closeIv.setOnClickListener(this);
        recommendBtn.setOnCheckedChangeListener((view, isChecked) -> {
            if (onItemSelListener != null) {
                onItemSelListener.onSwitchChange(isChecked);
            }
        });
    }

    public void setComment(boolean isComm) {
        recommendBtn.setChecked(isComm);
    }
    public void setMode(int mode) {
        this.currentMode = mode;
    }

    @Override
    public void showPopupWindow() {
        super.showPopupWindow();
        refreshUnLook();
        openIv.setVisibility(View.GONE);
        friendIv.setVisibility(View.GONE);
        privateIv.setVisibility(View.GONE);
        hideIv.setVisibility(View.GONE);
        switch (currentMode) {
            case OPEN_MODE:
                openIv.setVisibility(View.VISIBLE);
                break;
            case FRIEND_MODE:
                friendIv.setVisibility(View.VISIBLE);
                break;
            case PRIVATE_MODE:
                privateIv.setVisibility(View.VISIBLE);
                break;
            case HIDE_MODE:
                hideIv.setVisibility(View.VISIBLE);
                break;

            case R.id.close_iv:
                dismiss();
                break;
        }
    }

    public void refreshUnLook() {
        if (unLookUserList != null && !unLookUserList.isEmpty()) {
            String name = unLookUserList.get(0).nickname;
            int size = unLookUserList.size();
            if (size > 1) {
                unLookTv.setText(name+"等"+size+"人");
            }else{
                unLookTv.setText(name);
            }
        }
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
                currentMode = OPEN_MODE;
                dismiss();
                break;
            case R.id.friend_layout:
                friendIv.setVisibility(View.VISIBLE);
                onItemSelListener.onItemSel(FRIEND_MODE);
                currentMode = FRIEND_MODE;
                dismiss();
                break;
            case R.id.private_layout:
                privateIv.setVisibility(View.VISIBLE);
                onItemSelListener.onItemSel(PRIVATE_MODE);
                currentMode = PRIVATE_MODE;
                dismiss();
                break;
            case R.id.hide_layout:
                hideIv.setVisibility(View.VISIBLE);
                onItemSelListener.onItemSel(HIDE_MODE);
                currentMode = HIDE_MODE;
                break;
            case R.id.close_iv:
                dismiss();
                break;
        }
    }


    public interface OnItemSelListener{
        void onItemSel(int mode);
        void onSwitchChange(boolean isComment);
    }
}
