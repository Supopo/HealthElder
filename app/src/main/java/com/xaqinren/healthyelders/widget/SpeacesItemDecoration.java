package com.xaqinren.healthyelders.widget;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

/**
 * //                       _ooOoo_
 * //                      o8888888o
 * //                      88" . "88
 * //                      (| -_- |)
 * //                       O\ = /O
 * //                   ____/`---'\____
 * //                 .   ' \\| |// `.
 * //                  / \\||| : |||// \
 * //                / _||||| -:- |||||- \
 * //                  | | \\\ - /// | |
 * //                | \_| ''\---/'' | |
 * //                 \ .-\__ `-` ___/-. /
 * //              ______`. .' /--.--\ `. . __
 * //           ."" '< `.___\_<|>_/___.' >'"".
 * //          | | : `- \`.;`\ _ /`;.`/ - ` : | |
 * //            \ \ `-. \_ __\ /__ _/ .-` / /
 * //    ======`-.____`-.___\_____/___.-`____.-'======
 * //                       `=---='
 * //
 * //    .............................................
 * //             佛祖保佑             永无BUG
 * =====================================================
 * 描    述: RecyclerView间距设置，用法rv.addItemDecoration
 * 会压缩item的距离来腾出空隙
 * =====================================================
 */
public class SpeacesItemDecoration extends RecyclerView.ItemDecoration {
    private int mBottom;
    private int mRight;
    private int mColumns = 2;//列数 默认2列
    private boolean isStaggeredGrid;
    private boolean hasHead;
    private boolean onlyOne;//2列中间只有一条间距的情况

    public SpeacesItemDecoration(Context context, int bottom) {
        this.mBottom = dp2px(context, bottom);
        this.mRight = dp2px(context, bottom);
    }

    public SpeacesItemDecoration(Context context, int bottom, boolean isStaggeredGrid) {
        this.mBottom = dp2px(context, bottom);
        this.mRight = dp2px(context, bottom);
        this.isStaggeredGrid = isStaggeredGrid;
    }

    public SpeacesItemDecoration(Context context, boolean onlyOne, int bottom, boolean isStaggeredGrid) {
        this.mBottom = dp2px(context, bottom);
        this.mRight = dp2px(context, bottom);
        this.onlyOne = onlyOne;
        this.isStaggeredGrid = isStaggeredGrid;
    }

    public SpeacesItemDecoration(Context context, int bottom, boolean isStaggeredGrid, boolean hasHead) {
        this.mBottom = dp2px(context, bottom);
        this.mRight = dp2px(context, bottom);
        this.isStaggeredGrid = isStaggeredGrid;
        this.hasHead = hasHead;
    }

    public SpeacesItemDecoration(Context context, int bottom, int columns) {
        this.mBottom = dp2px(context, bottom);
        this.mRight = dp2px(context, bottom);
        this.mColumns = columns;
    }

    public SpeacesItemDecoration(Context context, int columns, int bottom, int right) {
        this.mBottom = dp2px(context, bottom);
        this.mRight = dp2px(context, right);
        this.mColumns = columns;
    }

    public SpeacesItemDecoration(Context context, int bottom, int columns, boolean isStaggeredGrid) {
        this.mBottom = dp2px(context, bottom);
        this.mRight = dp2px(context, bottom);
        this.mColumns = columns;
        this.isStaggeredGrid = isStaggeredGrid;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        //只有一条间隙的情况下左右要对分
        if (onlyOne) {
            outRect.bottom = mBottom;
            outRect.left = mRight / 2;
            outRect.right = mRight / 2;


            int position;
            if (isStaggeredGrid) {
                StaggeredGridLayoutManager.LayoutParams lp = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
                //StaggeredGridLayoutManager中第一列是 lp.getSpanIndex() == 0；
                position = lp.getSpanIndex();
            } else {
                position = parent.getChildLayoutPosition(view);
            }

            if (hasHead) {
                position = position - 1;
            }

            if (((position + (1)) % mColumns) == 0) {
                outRect.right = 0;
            } else {
                outRect.left = 0;
            }


        } else {
            outRect.bottom = mBottom;
            outRect.right = mRight;

            int position;
            if (isStaggeredGrid) {
                StaggeredGridLayoutManager.LayoutParams lp = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
                //StaggeredGridLayoutManager中第一列是 lp.getSpanIndex() == 0；
                position = lp.getSpanIndex();
            } else {
                position = parent.getChildLayoutPosition(view);
            }

            if (hasHead) {
                position = position - 1;
            }

            if (((position + (1)) % mColumns) == 0) {
                outRect.right = 0;
            }
        }


    }

    /**
     * dp转换成px
     */
    private int dp2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
