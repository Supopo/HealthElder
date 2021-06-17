package com.xaqinren.healthyelders.moduleLiteav.widget;

import android.content.Context;
import android.icu.text.UFormat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.xaqinren.healthyelders.utils.LogUtils;

public class InterceptLinearLayout extends LinearLayout {
    private String TAG = getClass().getSimpleName();

    //正常下拉限界
    private int marginLimit = 300;
    //快速下拉时间限界
    private int strikeTimeLimit = 100;
    //快速下拉距离限界
    private int strikeMarginLimit = 50;
    private float downX, downY;
    private float moveX, moveY;
    private long downTime;
    private boolean isChildCanScroll = false;
    private boolean isIntercept;

    public void setChildCanScroll(boolean childCanScroll) {
        isChildCanScroll = childCanScroll;
    }

    public InterceptLinearLayout(Context context) {
        super(context);
    }

    public InterceptLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public InterceptLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_MOVE:
                moveX = ev.getX();
                moveY = ev.getY();
                float offsetX = Math.abs(moveX - downX);
                float offsetY = Math.abs(moveY - downY);
                float max = Math.max(offsetX, offsetY);
                if (offsetX > offsetY) {
                    //左右滑动,不处理
                    isIntercept = false;
                    return super.onInterceptTouchEvent(ev);
                }
                if (!isChildCanScroll) {
                    //仅当上下滑动时,且子控件不消费上下滑动时
                    boolean b = super.onInterceptTouchEvent(ev);
                    if (ev.getAction() == MotionEvent.ACTION_DOWN
                            && ev.isButtonPressed(MotionEvent.BUTTON_PRIMARY)) {
                        //存在被下面的控件处理的可能
                        return b;
                    }
                    if (onScrollListener != null) {
                        b = onScrollListener.isChildIntercept(moveY - downY);
                    }
                    isIntercept = b;
                    LogUtils.e(TAG, "是否拦截 -> " + b);
                    return b;
                }
                break;
        }

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        isIntercept = true;
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            moveX = event.getX();
            moveY = event.getY();
            float offsetX = Math.abs(moveX - downX);
            float offsetY = Math.abs(moveY - downY);
            if (offsetX > offsetY) {
                //左右滑动,不处理
                return super.onTouchEvent(event);
            }
            if (moveY > downY) {
                if (onScrollListener != null) {
                    onScrollListener.onMove(offsetY);
                }
            }
        }
        if (event.getAction() == MotionEvent.ACTION_UP){
            float y = event.getY();
            long cuTime = System.currentTimeMillis();
            if (cuTime - downTime <= strikeTimeLimit) {
                if (y - downY >= strikeMarginLimit) {
                    if (onScrollListener != null) {
                        onScrollListener.onLimitUp();
                    }
                }
            }else{
                float offsetY = Math.abs(y - downY);
                if (onScrollListener != null) {
                    onScrollListener.onUp(offsetY);
                }
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        if (action == MotionEvent.ACTION_DOWN){
            downX = ev.getX();
            downY = ev.getY();
            downTime = System.currentTimeMillis();
            isIntercept = false;
        }
        if (action == MotionEvent.ACTION_MOVE) {
            if (onScrollListener != null) {
                moveY = ev.getY();
                boolean b = onScrollListener.isChildIntercept(moveY - downY);
                LogUtils.e(TAG, "dispatch  -> " + b + "\t" + isIntercept);
                if (!b && isIntercept) {
                    super.dispatchTouchEvent(ev);
                    LogUtils.e(TAG, "dispatch  -> 判断拦截");
                    return true;
                }
            }
        }

        return super.dispatchTouchEvent(ev);
    }

    private OnScrollListener onScrollListener;

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    public interface OnScrollListener{
        void onMove(float y);
        void onUp(float y);
        void onLimitUp();
        boolean isChildIntercept(float offsetY);
    }
}
