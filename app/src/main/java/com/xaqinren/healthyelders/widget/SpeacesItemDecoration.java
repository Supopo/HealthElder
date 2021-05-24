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
 * =====================================================
 */
public class SpeacesItemDecoration extends RecyclerView.ItemDecoration {
    private int mSpeace;
    private int mColumns = 2;//列数 默认2列
    private boolean isStaggeredGrid;

    public SpeacesItemDecoration(Context context, int speace) {
        this.mSpeace = dp2px(context, speace);
    }

    public SpeacesItemDecoration(Context context, int speace, boolean isStaggeredGrid) {
        this.mSpeace = dp2px(context, speace);
        this.isStaggeredGrid = isStaggeredGrid;
    }

    public SpeacesItemDecoration(Context context, int speace, int columns) {
        this.mSpeace = dp2px(context, speace);
        this.mColumns = columns;
    }

    public SpeacesItemDecoration(Context context, int speace, int columns, boolean isStaggeredGrid) {
        this.mSpeace = dp2px(context, speace);
        this.mColumns = columns;
        this.isStaggeredGrid = isStaggeredGrid;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        //判断列数，如果是最后一列，则outRect.left = 0;
        outRect.bottom = mSpeace;
        outRect.right = mSpeace;
        //        outRect.left = mSpeace;
        //        outRect.top = mSpeace;

        int position;
        if (isStaggeredGrid) {
            StaggeredGridLayoutManager.LayoutParams lp = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
            //StaggeredGridLayoutManager中第一列是 lp.getSpanIndex() == 0；
            position = lp.getSpanIndex();
        } else {
            position = parent.getChildLayoutPosition(view);
        }

        if (((position + (1)) % mColumns) == 0) {
            outRect.right = 0;
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
