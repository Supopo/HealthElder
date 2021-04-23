package com.xaqinren.healthyelders.utils;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.AnimRes;

import com.xaqinren.healthyelders.R;

/**
 * Created by Lee. on 2021/4/23.
 * 动画管理类-获取自定义动画
 */
public class AnimUtils {
    public static Animation getAnimation(Context context, @AnimRes int id) {
        return AnimationUtils.loadAnimation(context, id);
    }

    public static Animation PopAnimBottom2Enter(Context context) {
        return AnimationUtils.loadAnimation(context, R.anim.pop_bottom_2enter);
    }

    public static Animation PopAnimBottom2Exit(Context context) {
        return AnimationUtils.loadAnimation(context, R.anim.pop_bottom_2exit);
    }

    public static Animation PopAnimRight2Enter(Context context) {
        return AnimationUtils.loadAnimation(context, R.anim.pop_right_2enter);
    }

    public static Animation PopAnimRight2Exit(Context context) {
        return AnimationUtils.loadAnimation(context, R.anim.pop_right_2exit);
    }
}
