package com.tencent.qcloud.tim.uikit.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;

import com.tencent.qcloud.tim.uikit.TUIKit;

import java.lang.reflect.Method;


public class ScreenUtil {

    private static final String TAG = ScreenUtil.class.getSimpleName();

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
        //高度不准
        //        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        //        return dm.heightPixels;

        DisplayMetrics metric = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getRealMetrics(metric);

        //判断是否开启了虚拟按键
        if (checkDeviceHasNavigationBar(context)) {
            int navBarHeight = getNavigationBarHeight(context);
            return metric.heightPixels - navBarHeight;
        }

        return metric.heightPixels;

    }

    /**
     * 获取是否有虚拟按键
     * 通过判断是否有物理返回键反向判断是否有虚拟按键
     *
     * @param context
     * @return
     */
    public static boolean checkDeviceHasNavigationBar(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
            Point size = new Point();
            Point realSize = new Point();
            display.getSize(size);
            display.getRealSize(realSize);
            boolean result = realSize.y != size.y;
            return realSize.y != size.y;
        } else {

            boolean menu = ViewConfiguration.get(((Activity) context)).hasPermanentMenuKey();
            boolean back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            if (menu || back) {
                return false;
            } else {
                return true;
            }
        }
    }

    //获取虚拟按键的高度
    public static int getNavigationBarHeight(Context context) {
        int result = 0;
        if (checkDeviceHasNavigationBar(context)) {
            Resources res = context.getResources();
            int resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = res.getDimensionPixelSize(resourceId);
            }
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
     * 获取 虚拟按键的高度
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
        TUIKitLog.i(TAG, "scaledSize  containerWidth: " + containerWidth + " containerHeight: " + containerHeight
                + " realWidth: " + realWidth + " realHeight: " + realHeight);
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
