package com.xaqinren.healthyelders.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.facebook.imageutils.HeifExifUtil;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

public class ArcView extends View {

    private Paint paint;
    private Paint overPaint;
    //圆弧颜色
    private int color = Color.parseColor("#FF2858");
    //圆弧半径
    private float arcRad ;
    //圆弧宽度
    private float width;
    //圆弧最大值
    private float max = 100;
    private int min = 0;
    private Context context;
    private int startAngle = -90;
    private float currentStart = 0;
    private float currentEnd = 00;

    private boolean isShow = false;
    public ArcView(Context context) {
        super(context);
        initView();
    }

    public ArcView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ArcView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public void setAttr(int color,int arcRad,int width){
        this.color = color;
        this.arcRad = arcRad;
        this.width = width;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setArcRad(float arcRad) {
        this.arcRad = arcRad;
    }

    public void setWidth(float width) {
        this.width = width;
        paint.setStrokeWidth(width);
    }

    public void setProgress(long currentStart,long currentEnd) {
        this.currentStart = currentStart;
        this.currentEnd = currentEnd;
        requestLayout();
    }

    public void setMax(long max) {
        this.max = max;
        LogUtils.e(getClass().getSimpleName(), "max = " + max);
    }

    private void initView() {
        context = getContext();
        arcRad = context.getResources().getDimension(R.dimen.dp_50);
        width = context.getResources().getDimension(R.dimen.dp_3);
        paint = new Paint();
        paint.setDither(true);
        paint.setColor(color);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(width);
        paint.setStyle(Paint.Style.STROKE);
        overPaint = new Paint();
        overPaint.setDither(true);
        overPaint.setColor(color);
        overPaint.setAntiAlias(true);
        overPaint.setStyle(Paint.Style.FILL);
    }

    public void show() {
        isShow = true;
        requestLayout();
    }

    public void hide() {
        isShow = false;
        requestLayout();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isShow)return;
        float offset = (getWidth() - arcRad * 2) / 2;
        float widthHalf = width / 2;
        RectF rect = new RectF(offset + widthHalf, offset + widthHalf, arcRad * 2 + offset - widthHalf, arcRad * 2 + offset - widthHalf);
        float start = currentStart / max * 360;
        float sweep = currentEnd / max * 360;
        LogUtils.e(getClass().getSimpleName(), "start = " + start);
        LogUtils.e(getClass().getSimpleName(), "end = " + sweep);
        canvas.drawArc(rect, start + startAngle, sweep, false, paint);
        //绘制双头圆角
        float centerX = getWidth() / 2;
        float centerY = getWidth() / 2;
        float radian = (float) Math.toRadians(startAngle);
        float x = (float) (centerX + Math.cos(radian) * (arcRad - widthHalf));
        float y = (float) (centerY + Math.sin(radian) * (arcRad - widthHalf));
        canvas.drawCircle(x,y,widthHalf, overPaint);

        float radian1 = (float) Math.toRadians(sweep + startAngle);
        float x1 = (float) (centerX + Math.cos(radian1) * (arcRad - widthHalf));
        float y1 = (float) (centerY + Math.sin(radian1) * (arcRad - widthHalf));


        canvas.drawCircle(x1, y1, widthHalf, overPaint);
    }

}
