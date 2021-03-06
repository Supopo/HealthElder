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
        return super.onTouchEvent(ev);
    }

    long downTime = 0;
    long upTime = 0;
    float downX, downY;
    float moveX, moveY;
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            downTime = System.currentTimeMillis();
            downX = ev.getX();
            downY = ev.getY();
        }
        if (action == MotionEvent.ACTION_MOVE) {
            moveX = ev.getX();
            moveY = ev.getY();
            if (scrollable) {
                long l1 = System.currentTimeMillis();
                float offsetX = Math.abs(moveX - downX);
                float offsetY = Math.abs(moveY - downY);
                float offset = Math.max(offsetX, offsetY);
                if (l1 - downTime < 100) {
                    LogUtils.e(TAG, "onInterceptTouchEvent  判断拦截 offset = " + offset);
                    if (offset < 10) {
                        return super.onInterceptTouchEvent(ev);
                    }
                }
                //不处理左右滑动事件
                if (offsetX > offsetY) {
                    //判断未左右滑动
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
