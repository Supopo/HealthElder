package com.xaqinren.healthyelders.utils;

import android.text.TextUtils;

import java.text.DecimalFormat;

/**
 * Created by Lee. on 2021/3/25.
 * 数字转化为文字
 */
public class Num2TextUtil {
    private static String dd = "万";
    private static String dd2 = "w";

    public static String num2Text(int num) {
        if (num < 10000) {
            return String.valueOf(num);

        }
        String numStr = new DecimalFormat("#.00").format(num / 10000d);

        String[] ss = numStr.split("\\.");

        if ("00".equals(ss[1])) {
            return ss[0] + dd;

        } else if ('0' == (ss[1].charAt(1))) {
            return ss[0] + "." + ss[1].charAt(0) + dd;

        } else {
            return numStr + dd;
        }
    }

    public static String sNum2Text(String snum) {
        int num;
        if (TextUtils.isEmpty(snum)) {
            num = 0;
        } else {
            num = Integer.parseInt(snum);
        }
        if (num < 10000) {
            return String.valueOf(num);

        }
        String numStr = new DecimalFormat("#.00").format(num / 10000d);

        String[] ss = numStr.split("\\.");

        if ("00".equals(ss[1])) {
            return ss[0] + dd;

        } else if ('0' == (ss[1].charAt(1))) {
            return ss[0] + "." + ss[1].charAt(0) + dd;

        } else {
            return numStr + dd;
        }
    }

    public static String sNum2Text2(String snum) {
        int num;
        if (TextUtils.isEmpty(snum)) {
            num = 0;
        } else {
            num = Integer.parseInt(snum);
        }
        if (num < 10000) {
            return String.valueOf(num);

        }
        String numStr = new DecimalFormat("#.00").format(num / 10000d);

        String[] ss = numStr.split("\\.");

        if ("00".equals(ss[1])) {
            return ss[0] + dd2;

        } else if ('0' == (ss[1].charAt(1))) {
            return ss[0] + "." + ss[1].charAt(0) + dd2;

        } else {
            return numStr + dd2;
        }
    }

    public static String m2Km(int m) {
        DecimalFormat decimalFormat = new DecimalFormat(".0");
        float distances = Float.valueOf(m);
        if (distances < 1000) {
            return distances + "m";
        } else {
            String dis = decimalFormat.format(distances / 1000);
            return dis + "km";
        }
    }
}
