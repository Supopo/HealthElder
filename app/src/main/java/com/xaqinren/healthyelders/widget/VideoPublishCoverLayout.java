package com.xaqinren.healthyelders.widget;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.icu.util.MeasureUnit;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.material.animation.AnimationUtils;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

public class VideoPublishCoverLayout extends FrameLayout {
    private int maxSize = 10;
    private List<Bitmap> bitmaps;
    private int currentIndex = 0;

    private int layoutWidth;
    private int layoutHeight;
    private int margin;

    private List<ImageView> childList = new ArrayList<>();
    private View redBorderView;
    private OnCoverChangeListener onCoverChangeListener;

    public void setOnCoverChangeListener(OnCoverChangeListener onCoverChangeListener) {
        this.onCoverChangeListener = onCoverChangeListener;
    }

    public VideoPublishCoverLayout(Context context) {
        super(context);
        initView();
    }

    public VideoPublishCoverLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public VideoPublishCoverLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        this.layoutWidth = width;
        this.layoutHeight = height;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    private void initView() {

    }

    public void setData(List<Bitmap> bitmaps) {
        this.bitmaps = bitmaps;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public void setLiftRightMarginDp(int value) {
        margin = value;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public Bitmap getCurrentBitmap() {
        return bitmaps.get(getCurrentIndex());
    }

    public void layout() {
        layoutChild();
    }

    private void layoutChild() {
        int i = 0;
        int width = (layoutWidth - margin * 2) / maxSize;
        int height = (int) (1.34 * width);
        int right = margin;
        for (Bitmap bitmap : bitmaps) {
            ImageView imageView = new ImageView(getContext());
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
            params.leftMargin = right;
            params.topMargin = (int) getResources().getDimension(R.dimen.dp_2);
            imageView.setLayoutParams(params);
            int finalI = i;
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    selCurrentChild(currentIndex , finalI);
                    moveBorderCenter();
                }
            });
            Glide.with(imageView).asBitmap().load(bitmap).into(imageView);
            addView(imageView);
            childList.add(imageView);
            right += width;
            i++;
        }
        if (redBorderView == null) {
            addBorderView(width, height);
        }
    }

    private void addBorderView(int width, int height) {
        LogUtils.e("moveBorderCenter","addBorderView");
        redBorderView = new View(getContext());
        redBorderView.setBackground(getResources().getDrawable(R.drawable.bg_publich_cover_border));
        int dp4 = (int) getResources().getDimension(R.dimen.dp_4);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width + dp4, height + dp4);
        params.leftMargin = margin - (int) getResources().getDimension(R.dimen.dp_2);
        redBorderView.setLayoutParams(params);
        addView(redBorderView);
    }

    private void moveBorderCenter() {
        ImageView view = childList.get(currentIndex);
        float startX = redBorderView.getTranslationX();
        int endX = view.getLeft()  - margin;
        LogUtils.e("moveBorderCenter",
                        "startX\t=\t"+startX +
                        "\tendX\t=\t"+endX
        );


        ValueAnimator objectAnimator = ValueAnimator.ofFloat(startX , endX);
        objectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        float value = (float) valueAnimator.getAnimatedValue();
//                        FrameLayout.LayoutParams params = (LayoutParams) redBorderView.getLayoutParams();
//                        params.leftMargin = (int) value;
//                        redBorderView.requestLayout();
                        redBorderView.setTranslationX(value);
                    }
                });
        objectAnimator.setDuration(100);
        objectAnimator.start();
    }

    private void selCurrentChild(int currentIndex , int nexIndex) {
        if (currentIndex == nexIndex) return;
        this.currentIndex = nexIndex;
        onCoverChangeListener.onChange(nexIndex);
        moveBorderCenter();
    }

    public interface OnCoverChangeListener{
        void onChange(int index);
    }
}
