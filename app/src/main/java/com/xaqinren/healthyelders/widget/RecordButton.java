package com.xaqinren.healthyelders.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

import com.tencent.qcloud.ugckit.module.record.RecordModeView;
import com.xaqinren.healthyelders.R;

public class RecordButton extends RelativeLayout implements View.OnTouchListener {
    private Activity mActivity;
    private View translucenceView;
    private View whiteView;
    private View recodeView;
    private int tranColor;

    private OnRecordButtonListener onRecordButtonListener;


    public void setOnRecordButtonListener(OnRecordButtonListener onRecordButtonListener) {
        this.onRecordButtonListener = onRecordButtonListener;
    }

    public RecordButton(Context context) {
        super(context);
        initViews();
    }

    public RecordButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public RecordButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews();
    }

    private void initViews() {
        mActivity = (Activity) getContext();
        inflate(mActivity, R.layout.layout_record_button, this);
        setOnTouchListener(this);
        translucenceView = findViewById(R.id.translucence_view);
        whiteView = findViewById(R.id.white_view);
        recodeView = findViewById(R.id.recode_view);
        tranColor = Color.parseColor("#7f000000");
        translucenceView.setBackground(createCircleGradientDrawable(tranColor));
        whiteView.setBackground(createCircleGradientDrawable(getResources().getColor(R.color.white)));
        translucenceView.setBackground(createCircleGradientDrawable(tranColor));
//        whiteView.setVisibility(GONE);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                startRecodeAnim();

                break;
            }
            case MotionEvent.ACTION_UP: {
                endRecodeAnim();
                break;
            }
        }
        return true;
    }

    private GradientDrawable createCircleGradientDrawable(int color) {
        GradientDrawable normalDrawable = new GradientDrawable();
        normalDrawable.setShape(GradientDrawable.OVAL);
        normalDrawable.setColor(color);
        normalDrawable.setUseLevel(false);
        return normalDrawable;
    }

    /**
     * 开启录制动画
     */
    private void startRecodeAnim() {
        ObjectAnimator btnBkgZoomOutXAn = ObjectAnimator.ofFloat(translucenceView, "scaleX", 2.5f);
        ObjectAnimator btnBkgZoomOutYAn = ObjectAnimator.ofFloat(translucenceView, "scaleY",2.5f);

        ObjectAnimator whiteZoomInXAn = ObjectAnimator.ofFloat(whiteView, "scaleX", 0.65f);
        ObjectAnimator whiteZoomInYAn = ObjectAnimator.ofFloat(whiteView, "scaleY", 0.65f);


        ObjectAnimator btnZoomInXAn = ObjectAnimator.ofFloat(recodeView, "scaleX", 0.f);
        ObjectAnimator btnZoomInYAn = ObjectAnimator.ofFloat(recodeView, "scaleY", 0.f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(120);
        animatorSet.setInterpolator(new LinearInterpolator());
        animatorSet.play(btnBkgZoomOutXAn).with(btnBkgZoomOutYAn).with(whiteZoomInXAn).with(whiteZoomInYAn)
        .with(btnZoomInXAn).with(btnZoomInYAn);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
//                whiteView.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                onRecordButtonListener.onRecordStart();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorSet.start();
    }


    private void endRecodeAnim() {
        ObjectAnimator btnBkgZoomOutXAn = ObjectAnimator.ofFloat(translucenceView, "scaleX", 0.5f);
        ObjectAnimator btnBkgZoomOutYAn = ObjectAnimator.ofFloat(translucenceView, "scaleY",0.5f);

        ObjectAnimator whiteZoomInXAn = ObjectAnimator.ofFloat(whiteView, "scaleX", 1f);
        ObjectAnimator whiteZoomInYAn = ObjectAnimator.ofFloat(whiteView, "scaleY", 1f);


        ObjectAnimator btnZoomInXAn = ObjectAnimator.ofFloat(recodeView, "scaleX", 1.f);
        ObjectAnimator btnZoomInYAn = ObjectAnimator.ofFloat(recodeView, "scaleY", 1.f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(120);
        animatorSet.setInterpolator(new LinearInterpolator());
        animatorSet.play(btnBkgZoomOutXAn).with(btnBkgZoomOutYAn).with(whiteZoomInXAn).with(whiteZoomInYAn)
                .with(btnZoomInXAn).with(btnZoomInYAn);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
//                whiteView.setVisibility(GONE);
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                onRecordButtonListener.onRecordPause();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorSet.start();
    }

    public interface OnRecordButtonListener{
        /**
         * 多段录制点击开始
         */
        void onRecordStart();

        /**
         * 多段录制点击暂停
         */
        void onRecordPause();
    }
}
