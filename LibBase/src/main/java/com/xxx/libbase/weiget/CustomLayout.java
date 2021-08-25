package com.xxx.libbase.weiget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.xxx.libbase.utils.ScreenUtils;


public class CustomLayout extends RelativeLayout {
    //背景色
    private int bgColor;
    //圆角
    private int cirValue;
    //画笔
    private Paint paint;
    public CustomLayout(Context context) {
        super(context);
        init(context);
    }

    public CustomLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }

    public void setCirValue(int cirValue) {
        this.cirValue = cirValue;
    }

    private void init(Context context) {
        bgColor = Color.WHITE;
        cirValue = (int) ScreenUtils.dp2px(context, 4);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.FILL);
        setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCir(canvas);
    }

    private void drawCir(Canvas canvas) {
        RectF rect = new RectF(0, 0, getWidth(), getHeight());
        paint.setColor(bgColor);
        canvas.drawRoundRect(rect, cirValue, cirValue, paint);
    }

}
