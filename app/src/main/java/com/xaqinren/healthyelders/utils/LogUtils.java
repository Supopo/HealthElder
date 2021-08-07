package com.xaqinren.healthyelders.utils;

import android.util.Log;

import com.xaqinren.healthyelders.global.Constant;

/**
 * Created by Lee. on 2021/4/25.
 * 日志工具类
 */
public class LogUtils {
    public static void log(String type, String tag, String content) {
        if (Constant.DEBUG) {
            if (type.equals("v")) {
                Log.v(tag, content);
            } else if (type.equals("d")) {
                Log.d(tag, content);
            } else if (type.equals("i")) {
                Log.i(tag, content);
            } else if (type.equals("e")) {
                Log.e(tag, content);
            }
        }
    }

    public static void v(String tag, String content) {
        if (!Constant.DEBUG) {
            Log.v(tag, content);
        }
    }
    public static void e(String tag, String content) {
        if (Constant.DEBUG) {
            Log.e(tag, content);
        }
    }
    public static void i(String tag, String content) {
        if (Constant.DEBUG) {
            Log.i(tag, content);
        }
    }
    public static void d(String tag, String content) {
        if (Constant.DEBUG) {
            Log.d(tag, content);
        }
    }
}
