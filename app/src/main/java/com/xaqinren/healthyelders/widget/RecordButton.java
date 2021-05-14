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

import com.afollestad.materialdialogs.color.CircleView;
import com.tencent.qcloud.ugckit.module.record.RecordModeView;
import com.xaqinren.healthyelders.R;

public class RecordButton extends RelativeLayout implements View.OnTouchListener {
    private Activity mActivity;
    private View translucenceView;
    private View whiteView;
    private View recodeView;
    private View photoView;
    private ArcView arcView;
    private int tranColor;
    private boolean isClickRecode;
    private int starts_idle = 0;
    private int starts_recode = 1;
    private int starts_complete = 2;
    private int currentStats = starts_idle;
    private boolean isParentRecodeComplete = false;
    public static final int PHOTO_MODE = 2;
    public static final int VIDEO_MODE = 1;
    private int mode = VIDEO_MODE;

    private OnRecordButtonListener onRecordButtonListener;

    public void setMode(int mode) {
        if (mode != this.mode) {
            this.mode = mode;
            changeMode();
        }
    }

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
        photoView = findViewById(R.id.photo_view);
        arcView = findViewById(R.id.cir_view);
        tranColor = Color.parseColor("#a5ffffff");
        translucenceView.setBackground(createCircleGradientDrawable(tranColor));
        whiteView.setBackground(createCircleGradientDrawable(getResources().getColor(R.color.white)));
        translucenceView.setBackground(createCircleGradientDrawable(tranColor));
    }
    private void changeMode() {
        if (mode == VIDEO_MODE) {
            startVideoMode();
        } else if (mode == PHOTO_MODE) {
            startPhotoMode();
        }
    }
    long downTime;
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                if (downTime != 0 && (System.currentTimeMillis() - downTime) < 100) {
                    //判断是快速双击,不处理任何
                    return true;
                }
                downTime = System.currentTimeMillis();
                if (mode == VIDEO_MODE) {
                    if (isClickRecode) {
                        //点击播放中,再次点击则认为播放完成
                        endRecodeAnim();
                    }else
                        startRecodeAnim();
                }else if (mode == PHOTO_MODE){
                    //拍照模式

                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                if (mode == VIDEO_MODE) {
                    if (System.currentTimeMillis() - downTime   < 100) {
                        //认为是点击播放
                        isClickRecode = true;
                    }
                    if (!isClickRecode) {
                        endRecodeAnim();
                    }
                } else if (mode == PHOTO_MODE) {
                    //拍照完成,执行拍照完成动画
                    photoEndAnim();
                }
                break;
            }
        }
        return true;
    }
    public void setRecodeMaxTime(int maxTime) {
        arcView.setMax(maxTime);
    }
    public void setRecodeProgress(long progress) {
        arcView.setProgress(0, progress);
    }
    public void setRecodeProgressBarWidth(int width) {
        arcView.setWidth(width);
    }

    private GradientDrawable createCircleGradientDrawable(int color) {
        GradientDrawable normalDrawable = new GradientDrawable();
        normalDrawable.setShape(GradientDrawable.OVAL);
        normalDrawable.setColor(color);
        normalDrawable.setUseLevel(false);
        return normalDrawable;
    }
    /**
     * 执行录制->拍照模式
     */
    private void startPhotoMode() {
        ObjectAnimator videoHide = ObjectAnimator.ofFloat(recodeView, "alpha", 1f, 0f);
        ObjectAnimator photoShow = ObjectAnimator.ofFloat(photoView, "alpha", 0f, 1f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(120);
        animatorSet.setInterpolator(new LinearInterpolator());
        animatorSet.play(videoHide).with(photoShow);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                photoView.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                recodeView.setVisibility(GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animatorSet.start();
    }
    /**
     * 执行拍照->录制模式
     */
    private void startVideoMode() {
        ObjectAnimator videoShow = ObjectAnimator.ofFloat(recodeView, "alpha", 0f, 1f);
        ObjectAnimator photohide = ObjectAnimator.ofFloat(photoView, "alpha", 1f, 0f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(120);
        animatorSet.setInterpolator(new LinearInterpolator());
        animatorSet.play(videoShow).with(photohide);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                recodeView.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                photoView.setVisibility(GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animatorSet.start();
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
                isParentRecodeComplete = false;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                currentStats = starts_recode;
                onRecordButtonListener.onRecordStart();
                arcView.setVisibility(View.VISIBLE);
                arcView.show();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        whiteView.setVisibility(VISIBLE);
        animatorSet.start();
    }

    /**
     * 拍照缩放动画
     */
    private void photoEndAnim() {
        ObjectAnimator btnBkgZoomOutXAn = ObjectAnimator.ofFloat(photoView, "scaleX", 1f, 1.2f, 1.0f);
        ObjectAnimator btnBkgZoomOutYAn = ObjectAnimator.ofFloat(photoView, "scaleY", 1f, 1.2f, 1.0f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(120);
        animatorSet.setInterpolator(new LinearInterpolator());
        animatorSet.play(btnBkgZoomOutXAn).with(btnBkgZoomOutYAn);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (onRecordButtonListener != null) {
                    onRecordButtonListener.onPhotoSuccess();
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

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
                currentStats = starts_complete;
                downTime = 0;
                isClickRecode = false;
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                if (!isParentRecodeComplete)
                    onRecordButtonListener.onRecordPause();
                currentStats = starts_idle;
                arcView.hide();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorSet.start();
        arcView.setVisibility(View.GONE);
        whiteView.setVisibility(GONE);
    }

    /**
     * 上层发送录制完成事件
     */
    public void recodeComplete() {
        if (currentStats == starts_recode) {
            isParentRecodeComplete = true;
            endRecodeAnim();
        }
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

        /**
         * 手指抬起拍照完成
         */
        void onPhotoSuccess();
    }
}
