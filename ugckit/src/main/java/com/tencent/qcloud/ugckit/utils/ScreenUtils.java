package com.tencent.qcloud.ugckit.utils;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * ScreenUtils
 */
public class ScreenUtils {
    /**
     * 获取屏幕宽
     *
     * @return
     */
    public static int getScreenWidth(@Nullable Context context) {
        if (context == null)
            return 0;
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    /**
     * 获取屏幕高度
     *
     * @return
     */
    public static int getScreenHeight(@Nullable Context context) {
        if (context == null)
            return 0;
        //高度不准
        //        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        //        return dm.heightPixels;

        DisplayMetrics metric = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getRealMetrics(metric);
        return metric.heightPixels;

    }


    //获取虚拟按键的高度
    public static int getNavigationBarHeight(Context context) {
        int result = 0;
        Resources res = context.getResources();
        int resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = res.getDimensionPixelSize(resourceId);
        }
        return result;
    }


    public static float dp2px(@NonNull Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    public static float sp2px(@NonNull Context context, float sp) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return sp * scale;
    }

    /**
     * 设置window透明值，带动画
     * @param context
     * @param startalpha
     * @param endalpha
     * @param count
     */
    public static void setWindowAlpha(final Context context , final float startalpha , final float endalpha , int count){
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(startalpha, endalpha);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                setWindowAlpha(context,value);
            }
        });
        valueAnimator.setDuration(count);
        valueAnimator.start();
    }

    /**
     * 设置window透明值
     * @param context
     * @param alpha
     */
    public static void setWindowAlpha(Context context , float alpha){
        WindowManager.LayoutParams lp = ((Activity)context).getWindow().getAttributes();
        lp.alpha = alpha;
        ((Activity)context).getWindow().setAttributes(lp);
    }

}
