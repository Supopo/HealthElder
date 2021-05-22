package com.xaqinren.healthyelders.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.xaqinren.healthyelders.R;


/**
 * Created by Boyce
 * on 2020/12/23
 */
public class RingProgressBar extends View {
    private Paint paint;
    private int width;
    private int height;
    private int result;
    private int padding;
    private int ringColor;
    private int ringProgressColor;
    private int textColor;
    private float textSize;
    private float ringWidth;
    private int max;
    private int progress;
    private boolean textIsShow;
    private int style;
    public static final int STROKE = 0;
    public static final int FILL = 1;
    private OnProgressListener mOnProgressListener;
    private int centre;
    private int radius;

    public RingProgressBar(Context context) {
        this(context, (AttributeSet)null);
    }

    public RingProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RingProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.result = 0;
        this.padding = 0;
        this.paint = new Paint();
        this.result = this.dp2px(100);
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.RingProgressBar);
        this.ringColor = mTypedArray.getColor(R.styleable.RingProgressBar_ringColor, -16777216);
        this.ringProgressColor = mTypedArray.getColor(R.styleable.RingProgressBar_ringProgressColor, -1);
        this.textColor = mTypedArray.getColor(R.styleable.RingProgressBar_barTextColor, -16777216);
        this.textSize = mTypedArray.getDimension(R.styleable.RingProgressBar_barTextSize, 16.0F);
        this.ringWidth = mTypedArray.getDimension(R.styleable.RingProgressBar_ringWidth, 5.0F);
        this.max = mTypedArray.getInteger(R.styleable.RingProgressBar_max, 100);
        this.textIsShow = mTypedArray.getBoolean(R.styleable.RingProgressBar_textIsShow, true);
        this.style = mTypedArray.getInt(R.styleable.RingProgressBar_style, 0);
        mTypedArray.recycle();
    }

    @SuppressLint({"DrawAllocation"})
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.centre = this.getWidth() / 2;
        this.radius = (int)((float)this.centre - this.ringWidth / 2.0F);
        this.drawCircle(canvas);
        this.drawTextContent(canvas);
        this.drawProgress(canvas);
    }

    private void drawCircle(Canvas canvas) {
        this.paint.setColor(this.ringColor);
        this.paint.setStyle(Style.STROKE);
        this.paint.setStrokeWidth(this.ringWidth);
        this.paint.setAntiAlias(true);
        canvas.drawCircle((float)this.centre, (float)this.centre, (float)this.radius, this.paint);
    }

    private void drawTextContent(Canvas canvas) {
        this.paint.setStrokeWidth(0.0F);
        this.paint.setColor(this.textColor);
        this.paint.setTextSize(this.textSize);
        this.paint.setStyle(Style.FILL);
        this.paint.setTypeface(Typeface.DEFAULT);
        int percent = (int)((float)this.progress / (float)this.max * 100.0F);
        float textWidth = this.paint.measureText(percent + "%");
        if (this.textIsShow && percent != 0 && this.style == 0) {
            canvas.drawText(percent + "%", (float)this.centre - textWidth / 2.0F, (float)this.centre + this.textSize / 2.0F, this.paint);
        }

    }

    private void drawProgress(Canvas canvas) {
        this.paint.setStrokeWidth(this.ringWidth);
        this.paint.setColor(this.ringProgressColor);
        RectF strokeOval = new RectF((float)(this.centre - this.radius), (float)(this.centre - this.radius), (float)(this.centre + this.radius), (float)(this.centre + this.radius));
        RectF fillOval = new RectF((float)(this.centre - this.radius) + this.ringWidth + (float)this.padding, (float)(this.centre - this.radius) + this.ringWidth + (float)this.padding, (float)(this.centre + this.radius) - this.ringWidth - (float)this.padding, (float)(this.centre + this.radius) - this.ringWidth - (float)this.padding);
        switch(this.style) {
            case 0:
                this.paint.setStyle(Style.STROKE);
                this.paint.setStrokeCap(Cap.ROUND);
                canvas.drawArc(strokeOval, -90.0F, (float)(360 * this.progress / this.max), false, this.paint);
                break;
            case 1:
                this.paint.setStyle(Style.FILL_AND_STROKE);
                this.paint.setStrokeCap(Cap.ROUND);
                if (this.progress != 0) {
                    canvas.drawArc(fillOval, -90.0F, (float)(360 * this.progress / this.max), true, this.paint);
                }
        }

    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode == -2147483648) {
            this.width = this.result;
        } else {
            this.width = widthSize;
        }

        if (heightMode == -2147483648) {
            this.height = this.result;
        } else {
            this.height = heightSize;
        }

        this.setMeasuredDimension(this.width, this.height);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.width = w;
        this.height = h;
        this.padding = this.dp2px(5);
    }

    public synchronized int getMax() {
        return this.max;
    }

    public synchronized void setMax(int max) {
        if (max < 0) {
            throw new IllegalArgumentException("The max progress of 0");
        } else {
            this.max = max;
        }
    }

    public synchronized int getProgress() {
        return this.progress;
    }

    public synchronized void setProgress(int progress) {
        if (progress < 0) {
            throw new IllegalArgumentException("The progress of 0");
        } else {
            if (progress > this.max) {
                progress = this.max;
            }

            if (progress <= this.max) {
                this.progress = progress;
                this.postInvalidate();
            }

            if (progress == this.max && this.mOnProgressListener != null) {
                this.mOnProgressListener.progressToComplete();
            }

        }
    }

    public int getRingColor() {
        return this.ringColor;
    }

    public void setRingColor(int ringColor) {
        this.ringColor = ringColor;
    }

    public int getRingProgressColor() {
        return this.ringProgressColor;
    }

    public void setRingProgressColor(int ringProgressColor) {
        this.ringProgressColor = ringProgressColor;
    }

    public int getTextColor() {
        return this.textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public float getTextSize() {
        return this.textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public float getRingWidth() {
        return this.ringWidth;
    }

    public void setRingWidth(float ringWidth) {
        this.ringWidth = ringWidth;
    }

    public int dp2px(int dp) {
        float density = this.getContext().getResources().getDisplayMetrics().density;
        return (int)((float)dp * density + 0.5F);
    }

    public void setOnProgressListener(OnProgressListener mOnProgressListener) {
        this.mOnProgressListener = mOnProgressListener;
    }

    public interface OnProgressListener {
        void progressToComplete();
    }
}

