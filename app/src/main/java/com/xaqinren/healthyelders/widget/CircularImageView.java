package com.xaqinren.healthyelders.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.xaqinren.healthyelders.R;

/**
 * 带圆角的imageview
 */
public class CircularImageView extends androidx.appcompat.widget.AppCompatImageView {
    /**
     * 圆角左上角值（px）
     */
    private int cirTopLeftPxValue = 0;
    /**
     * 圆角右上角值（px）
     */
    private int cirTopRightPxValue = 0;
    /**
     * 圆角左下角值（px）
     */
    private int cirBottomLeftPxValue = 0;
    /**
     * 圆角右下角值（px）
     */
    private int cirBottomRightPxValue = 0;
    /**
     * 圆角设置类型
     * 1 全部
     * 2 上
     * 3 下
     * 4 左
     * 5 右
     * 6 4角，4值
     * 7 圆形图片 圆角不适用 半径为自动计算
     */
    private static final int TYPE_ALL = 1;
    private static final int TYPE_TOP = 2;
    private static final int TYPE_BOTTOM = 3;
    private static final int TYPE_LEFT = 4;
    private static final int TYPE_RIGHT = 5;
    private static final int TYPE_FOUR = 6;
    private static final int TYPE_CIR = 7;

    private int cirType = 1;


    public void  setCirValue(int value){
        cirTopLeftPxValue = cirTopRightPxValue = cirBottomLeftPxValue = cirBottomRightPxValue = value;
    }

    public CircularImageView(@NonNull Context context) {
        super(context);
        init(context,null);
    }

    public CircularImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CircularImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }



    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircularImageView);
        int count = typedArray.getIndexCount();
        for (int i = 0; i < count; i++) {
            int attr = typedArray.getIndex(i);
            if (attr == R.styleable.CircularImageView_cirType) {
                cirType = typedArray.getInt(attr, 1);
            } else if (attr == R.styleable.CircularImageView_cirValue) {
                int value = typedArray.getDimensionPixelSize(attr, 0);
                cirTopLeftPxValue = cirTopRightPxValue = cirBottomLeftPxValue = cirBottomRightPxValue = value;
            }else if (attr == R.styleable.CircularImageView_leftTopValue) {
                cirTopLeftPxValue = typedArray.getDimensionPixelSize(attr, 0);
            } else if (attr == R.styleable.CircularImageView_rightTopValue) {
                cirTopRightPxValue = typedArray.getDimensionPixelSize(attr, 0);
            } else if (attr == R.styleable.CircularImageView_leftBottomValue) {
                cirBottomLeftPxValue = typedArray.getDimensionPixelSize(attr, 0);
            } else if (attr == R.styleable.CircularImageView_rightBottomValue) {
                cirBottomRightPxValue = typedArray.getDimensionPixelSize(attr, 0);
            } else if (attr == R.styleable.CircularImageView_topValue) {
                int value = typedArray.getDimensionPixelSize(attr, 0);
                cirTopLeftPxValue = cirTopRightPxValue = value;
            } else if (attr == R.styleable.CircularImageView_bottomValue) {
                int value = typedArray.getDimensionPixelSize(attr, 0);
                cirBottomLeftPxValue = cirBottomRightPxValue = value;
            } else if (attr == R.styleable.CircularImageView_leftValue) {
                int value = typedArray.getDimensionPixelSize(attr, 0);
                cirTopLeftPxValue = cirBottomLeftPxValue = value;
            } else if (attr == R.styleable.CircularImageView_rightValue) {
                int value = typedArray.getDimensionPixelSize(attr, 0);
                cirTopRightPxValue = cirBottomRightPxValue = value;
            }
        }
        typedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int height = getHeight();
        int width = getWidth();
        Path path = new Path();
        if (cirTopLeftPxValue != 0) {
            RectF rectF = new RectF(0, 0, cirTopLeftPxValue * 2, cirTopLeftPxValue * 2);
            path.addArc(rectF, 180, 90);
            path.lineTo(0,0);
            canvas.clipPath(path, Region.Op.DIFFERENCE);
            path.reset();
        }
        if (cirTopRightPxValue != 0) {
            RectF rectF = new RectF(width - (cirTopRightPxValue * 2), 0, width, cirTopRightPxValue * 2);
            path.addArc(rectF, 0, -90);
            path.lineTo(width, 0);
            canvas.clipPath(path, Region.Op.DIFFERENCE);
            path.reset();
        }
        if (cirBottomLeftPxValue != 0) {
            RectF rectF = new RectF(0, height - (cirBottomLeftPxValue * 2), cirBottomLeftPxValue * 2, height);
            path.addArc(rectF, 90, 90);
            path.lineTo(0, height);
            canvas.clipPath(path, Region.Op.DIFFERENCE);
            path.reset();
        }
        if (cirBottomRightPxValue != 0) {
            RectF rectF = new RectF(width - (cirBottomRightPxValue * 2), height - (cirBottomRightPxValue * 2), width, height);
            path.addArc(rectF, 0, 90);
            path.lineTo(width, height);
            canvas.clipPath(path, Region.Op.DIFFERENCE);
            path.reset();
        }
        super.onDraw(canvas);

    }
}
