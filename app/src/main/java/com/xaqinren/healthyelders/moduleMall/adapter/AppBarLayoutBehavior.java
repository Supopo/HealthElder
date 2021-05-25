package com.xaqinren.healthyelders.moduleMall.adapter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;

/**
 * Created by Lee. on 2021/5/25.
 */
public class AppBarLayoutBehavior extends AppBarLayout.Behavior {
    float mOrignBottomOfAppBarLayout;
    private AppBarLayout mAppBarLayout;
    AppBarLayoutScrollListem appBarLayoutScrollListem ;
    public AppBarLayoutBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        appBarLayoutScrollListem = new AppBarLayoutScrollListem();
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, AppBarLayout child, int layoutDirection) {
        boolean handled = super.onLayoutChild(parent, child, layoutDirection);
        if(mAppBarLayout == null) {
            mAppBarLayout = child;
            mOrignBottomOfAppBarLayout = child.getBottom();
        }
        return handled;
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout parent, AppBarLayout child, View directTargetChild, View target, int nestedScrollAxes, int type) {
        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;//表示滚动方向是垂直的才触发nest scroll。
    }


    @Override
    public boolean onNestedPreFling(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, float velocityX, float velocityY) {
        RecyclerView recyclerView = target.findViewWithTag("recyclerView");
        if (recyclerView != null) {
            recyclerView.removeOnScrollListener(appBarLayoutScrollListem);
            recyclerView.addOnScrollListener(appBarLayoutScrollListem);
            appBarLayoutScrollListem.child = child;
            appBarLayoutScrollListem.target = target;
        }
        return super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY);
    }

    public class AppBarLayoutScrollListem extends RecyclerView.OnScrollListener {
        AppBarLayout child;
        View target;
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                if (child != null && target != null) {
                    RecyclerView recyclerView1 = target.findViewWithTag("recyclerView");
                    if (recyclerView1 != null) {
                        if (!recyclerView1.canScrollVertically(-1)) {
                            child.setExpanded(true);
                        }
                        recyclerView1.removeOnScrollListener(this);
                    }
                }

            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }

    }

}
