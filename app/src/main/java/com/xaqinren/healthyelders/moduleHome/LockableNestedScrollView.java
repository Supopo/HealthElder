package com.xaqinren.healthyelders.moduleHome;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.widget.NestedScrollView;

import com.xaqinren.healthyelders.utils.LogUtils;

import com.xaqinren.healthyelders.R;

/**
 * Created by Lee. on 2021/5/11.
 */
public class LockableNestedScrollView extends NestedScrollView {
    // by default is scrollable
    private boolean scrollable = true;
    private String TAG = "LockableNestedScrollView";

    public LockableNestedScrollView(@NonNull Context context) {
        super(context);
    }

    public LockableNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LockableNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        super.onTouchEvent(ev);
        return false;
    }

    long l = 0;
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            l = System.currentTimeMillis();
        }
        if (action == MotionEvent.ACTION_MOVE) {
            if (scrollable) {
                long l1 = System.currentTimeMillis();
                if (l1 - l < 50) {
                    return super.onInterceptTouchEvent(ev);
                }
                LogUtils.e(TAG, "onInterceptTouchEvent  拦截 move = " + ev.getAction());
                return true;
            }
        }
        LogUtils.e(TAG, "onInterceptTouchEvent  未拦截 action = " + ev.getAction());
        return super.onInterceptTouchEvent(ev);
    }

    public void setScrollingEnabled(boolean enabled) {
        scrollable = enabled;
    }


}
