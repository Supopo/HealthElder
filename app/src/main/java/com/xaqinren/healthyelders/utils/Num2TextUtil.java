package com.xaqinren.healthyelders.utils;

import android.text.TextUtils;

import java.text.DecimalFormat;

/**
 * Created by Lee. on 2021/3/25.
 * 数字转化为文字
 */
public class Num2TextUtil {
    public static String num2Text(int num) {
        if (num < 10000) {
            return String.valueOf(num);

        }
        String numStr = new DecimalFormat("#.00").format(num / 10000d);

        String[] ss = numStr.split("\\.");

        if ("00".equals(ss[1])) {
            return ss[0] + "万";

        } else if ('0' == (ss[1].charAt(1))) {
            return ss[0] + "." + ss[1].charAt(0) + "万";

        } else {
            return numStr + "万";
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
            return ss[0] + "万";

        } else if ('0' == (ss[1].charAt(1))) {
            return ss[0] + "." + ss[1].charAt(0) + "万";

        } else {
            return numStr + "万";
        }
    }
}
