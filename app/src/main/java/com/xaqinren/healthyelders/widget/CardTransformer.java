package com.xaqinren.healthyelders.widget;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

/**
 * ViewPager层叠效果 让后面的View逐渐缩小、透明，从而切换时候达到层叠效果
 */
public class CardTransformer implements ViewPager.PageTransformer {
    private float mCardHeight = 30;//默认高度差

    public CardTransformer(float cardheight) {
        this.mCardHeight = cardheight;
    }

    @Override
    public void transformPage(@NonNull View view, float position) {
        if (position <= 0) {
            view.setTranslationX(0f);
            view.setClickable(true);
            view.setAlpha(1.0f);
        } else {
            //后面的View逐渐缩小透明化，在滑动过程中就能产生层叠效果
            float scale = (view.getWidth() - mCardHeight * position) / view.getWidth();
            view.setScaleX(scale);
            view.setScaleY(scale);
            view.setClickable(false);
            view.setTranslationX(-view.getWidth() * position + mCardHeight * position);
            view.setAlpha((1 - position) * 0.4f + 0.6f);
        }
    }
}