package com.xaqinren.healthyelders.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tencent.qcloud.tim.uikit.TUIKit;
import com.tencent.qcloud.tim.uikit.utils.TUIKitLog;
import com.xaqinren.healthyelders.global.AppApplication;

import java.lang.reflect.Method;


public class MScreenUtil {

    private static final String TAG = MScreenUtil.class.getSimpleName();

    private static int navigationBarHeight = 0;
    private static int SOFT_INPUT_HEIGHT = 0;

    public static boolean checkNavigationBarShow(@NonNull Context context, @NonNull Window window) {
        boolean show;
        Display display = window.getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getRealSize(point);

        View decorView = window.getDecorView();
        Configuration conf = context.getResources().getConfiguration();
        if (Configuration.ORIENTATION_LANDSCAPE == conf.orientation) {
            View contentView = decorView.findViewById(android.R.id.content);
            show = (point.x != contentView.getWidth());
        } else {
            Rect rect = new Rect();
            decorView.getWindowVisibleDisplayFrame(rect);
            show = (rect.bottom != point.y);
        }
        return show;
    }

    public static int getNavigationBarHeight() {
        if (navigationBarHeight != 0)
            return navigationBarHeight;
        Resources resources = TUIKit.getAppContext().getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        navigationBarHeight = height;
        return height;
    }

    public static int[] getScreenSize() {
        int size[] = new int[2];
        DisplayMetrics dm = TUIKit.getAppContext().getResources().getDisplayMetrics();
        size[0] = dm.widthPixels;
        size[1] = dm.heightPixels;
        return size;
    }

    public static int getStatusBarHeight() {
        int result = 0;
        int resourceId = TUIKit.getAppContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = TUIKit.getAppContext().getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int getScreenHeight(@Nullable Context context) {
        if (context == null)
            return 0;
        //????????????
        //        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        //        return dm.heightPixels;

        DisplayMetrics metric = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getRealMetrics(metric);

        //?????????????????????????????????
        if (AppApplication.get().isHasNavBar()) {
            int NavigationBarHeight = getNavigationBarHeight();
            return metric.heightPixels - NavigationBarHeight;
        }

        return metric.heightPixels;

    }

    //???????????????????????????
    public static int getNavigationBarHeight(Context context) {
        int result = 0;
        Resources res = context.getResources();
        int resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = res.getDimensionPixelSize(resourceId);
        }
        return result;
    }


    public static int getScreenWidth(Context context) {
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metric);
        return metric.widthPixels;
    }


    public static int getPxByDp(float dp) {
        float scale = TUIKit.getAppContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static float dp2px(@NonNull Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    public static float sp2px(@NonNull Context context, float sp) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return sp * scale;
    }

    public static int getDpi(Context context) {
        int dpi = 0;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        @SuppressWarnings("rawtypes")
        Class c;
        try {
            c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, displayMetrics);
            dpi = displayMetrics.heightPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dpi;
    }

    /**
     * ?????? ?????????????????????
     *
     * @param context
     * @return
     */
    public static int getBottomStatusHeight(Context context) {
        if (SOFT_INPUT_HEIGHT > 0)
            return SOFT_INPUT_HEIGHT;
        int totalHeight = getDpi(context);
        int contentHeight = getScreenHeight(context);
        SOFT_INPUT_HEIGHT = totalHeight - contentHeight;
        return SOFT_INPUT_HEIGHT;
    }

    public static int[] scaledSize(int containerWidth, int containerHeight, int realWidth, int realHeight) {
        float deviceRate = (float) containerWidth / (float) containerHeight;
        float rate = (float) realWidth / (float) realHeight;
        int width = 0;
        int height = 0;
        if (rate < deviceRate) {
            height = containerHeight;
            width = (int) (containerHeight * rate);
        } else {
            width = containerWidth;
            height = (int) (containerWidth / rate);
        }
        return new int[]{width, height};
    }
}
