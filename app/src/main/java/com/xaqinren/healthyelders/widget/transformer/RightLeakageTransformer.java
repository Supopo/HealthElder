package com.xaqinren.healthyelders.widget.transformer;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import com.xaqinren.healthyelders.utils.LogUtils;

public class RightLeakageTransformer implements ViewPager.PageTransformer  {
    private static final float Leak = 0.9f;
    private static final float MIN_ALPHA = 0.7f;
    private static final float MIN_SCALE = 0.8f;
    private static float MAX_TRAN = -50;//px
    private String TAG = "RightLeakageTransformer";

    public RightLeakageTransformer(int rightLeakage) {
        this.MAX_TRAN = rightLeakage;
    }

    @Override
    public void transformPage(@NonNull View page, float position) {
        if (position < -1 || position > 1) {
            //屏幕外的
            page.setTranslationX(0);
        } else if (position <= 1) {// [-1,1]
            //进入,屏幕内

            float tran = -Math.abs(position * MAX_TRAN);
            if (position < 0) {
                tran = 0;
            }
            page.setTranslationX(tran);
        }
    }
}
