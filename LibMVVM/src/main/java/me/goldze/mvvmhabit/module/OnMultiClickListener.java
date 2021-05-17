package me.goldze.mvvmhabit.module;

import android.view.View;

public abstract class OnMultiClickListener implements View.OnClickListener {
    // 两次点击按钮之间的点击间隔不能少于1000毫秒
    private  int MIN_CLICK_DELAY_TIME = 1000;
    private  long lastClickTime;

    public abstract void onMultiClick(View v);

    public OnMultiClickListener(int MIN_CLICK_DELAY_TIME) {
        this.MIN_CLICK_DELAY_TIME = MIN_CLICK_DELAY_TIME;
    }

    @Override
    public void onClick(View view) {
        long curClickTime = System.currentTimeMillis();
        if((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            // 超过点击间隔后再将lastClickTime重置为当前点击时间
            lastClickTime = curClickTime;
            onMultiClick(view);
        }
    }
}
