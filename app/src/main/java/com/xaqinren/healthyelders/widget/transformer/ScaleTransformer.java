package com.xaqinren.healthyelders.widget.transformer;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

public class ScaleTransformer implements ViewPager.PageTransformer {
    private static final float MIN_SCALE = 0.70f;
    private static final float MIN_ALPHA = 0.5f;

    /**
     * position取值特点：
     * 假设页面从0～1，则：
     * 第一个页面position变化为[0,-1]
     * 第二个页面position变化为[1,0]
     *
     * @param page
     * @param position
     */
    @Override
    public void transformPage(@NonNull View page, float position) {
        if (position < -1 || position > 1) {
            //退出
            page.setAlpha(MIN_ALPHA);
            page.setScaleX(MIN_SCALE);
            page.setScaleY(MIN_SCALE);
        } else if (position <= 1) { // [-1,1]
            //进入
            float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
            float scaleX;
            if (position < 0) {
                scaleX = 1 + 0.3f * position;
            } else {
                scaleX = 1 - 0.3f * position;
            }
            page.setScaleX(scaleX);
            page.setScaleY(scaleX);
            page.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA));
        }
    }
}
