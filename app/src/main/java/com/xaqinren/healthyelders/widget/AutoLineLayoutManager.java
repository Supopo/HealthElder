package com.xaqinren.healthyelders.widget;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * =====================================================
 * 描    述: 自动换行的LayoutManager 打造标签
 * =====================================================
 */
public class AutoLineLayoutManager extends RecyclerView.LayoutManager {

    public AutoLineLayoutManager(){
        //不设置测量会无效
        setAutoMeasureEnabled(true);
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.WRAP_CONTENT,
                RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
        //RecyclerView的宽度
        int rvWidth = getWidth();

        int curLineWidth = 0;
        int curLineTop = 0;
        int lastLineMaxHeight = 0;//前一行所有view的最大高度
        for (int i = 0; i < getItemCount(); i++) {
            View view = recycler.getViewForPosition(i);
            addView(view);
            //测量view
            measureChild(view, 0, 0);

            //测量当前view的宽和高
            int width = getDecoratedMeasuredWidth(view);
            int height = getDecoratedMeasuredHeight(view);

            //累计记算view的宽
            curLineWidth += width;

            //如果超过RecyclerView的宽度，则需要换行
            if (curLineWidth > rvWidth) {
                //换行
                if (lastLineMaxHeight == 0) {
                    lastLineMaxHeight = height;
                }
                //换行之后的高度
                curLineTop += lastLineMaxHeight;
                //装饰放置
                layoutDecorated(view, 0, curLineTop, width, curLineTop + height );
                //换行，重置累计宽度
                curLineWidth = width;
                lastLineMaxHeight = height;
            }else {
                //不需要换行
                //装饰放置
                layoutDecorated(view, curLineWidth - width, curLineTop, curLineWidth, curLineTop + height);
                //比较当前行item的最大高度
                lastLineMaxHeight = Math.max(lastLineMaxHeight, height);
            }

        }
    }
}
