package com.xaqinren.healthyelders.utils;

import android.graphics.Color;

import java.util.Random;

/**
 * =====================================================
 * 描    述: 随机颜色
 * =====================================================
 */
public class ColorsUtils {

    /**
     * 获取指定长度的16进制字符串.
     */
    public static String randomHexStr(int len) {
        try {
            StringBuffer result = new StringBuffer();
            for (int i = 0; i < len; i++) {
                //随机生成0-15的数值并转换成16进制
                result.append(Integer.toHexString(new Random().nextInt(16)));
            }
            return result.toString().toUpperCase();
        } catch (Exception e) {
            System.out.println("获取16进制字符串异常，返回默认...");
            return "00CCCC";
        }
    }

    public static int randomColor() {
        int color = Color.argb(255, new Random().nextInt(256), new Random().nextInt(256), new Random().nextInt(256));
        return color;
    }

    // 10-100%
    public static String[] alphas = {
            "1A",
            "33",
            "4D",
            "66",
            "80",
            "99",
            "B3",
            "CC",
            "E6",
            "FF"
    };

    //设置颜色透明度 pos 1-10
    public static String getAlphaColor(String color, int pos) {
        return "#" + alphas[pos - 1] + color;
    }
}
