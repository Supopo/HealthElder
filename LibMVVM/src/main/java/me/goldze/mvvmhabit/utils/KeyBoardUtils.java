package me.goldze.mvvmhabit.utils;

import android.app.Activity;
import android.content.Context;
import android.hardware.input.InputManager;
import android.os.IBinder;
import android.view.inputmethod.InputMethodManager;

/**
 * 键盘隐藏，显示
 */
public class KeyBoardUtils {

    public static void hideKeyBoard(Context context, IBinder binder) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(binder,InputMethodManager.HIDE_NOT_ALWAYS);
    }

}
