package com.xaqinren.healthyelders.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;

import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.xaqinren.healthyelders.R;

/**
 * =====================================================
 * 描    述: 基于Xfermode实现圆形图片
 * =====================================================
 */
public class CircleImageView extends AppCompatImageView {

    private int strokeWidth;//边框宽度
    private int strokeColor;//边框颜色
    private int radius;//设置了radius说明是圆角矩形
    private RectF rectF;

    public CircleImageView(Context context) {
        this(context, null);

    }

    public CircleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //自定义属性
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView, defStyleAttr, 0);
        strokeWidth = typedArray.getDimensionPixelSize(R.styleable.CircleImageView_border_width, 0);
        strokeColor = typedArray.getColor(R.styleable.CircleImageView_border_corlor, Color.BLACK);
        radius = typedArray.getDimensionPixelSize(R.styleable.CircleImageView_civ_radius, 0);
        typedArray.recycle();
        //该方法千万别放到onDraw()方法里面调用，否则会不停的重绘的，因为该方法调用了invalidate() 方法
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);//禁用硬加速

    }

    //重写onDraw()方法获取BitmapDrawable进行处理
    @Override
    protected void onDraw(Canvas canvas) {
        // 获取当前控件的 drawable
        Drawable drawable = getDrawable();
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            //通过BitmapShader的方式实现
            drawRoundByXfermode(canvas, bitmap);
        } else {
            super.onDraw(canvas);
        }
    }

    private void drawRoundByXfermode(Canvas canvas, Bitmap bitmap) {
        Paint mPaint = new Paint();
        mPaint.setAntiAlias(true);//给画笔设置抗锯齿
        Matrix mMatrix = new Matrix();


        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();

        int viewWidth = getWidth();
        int viewHeight = getHeight();

        //根据图片尺寸和View尺寸进行适当缩放，生成新的图片
        float minScale = Math.min(viewWidth / (float) bitmapWidth, viewHeight / (float) bitmapHeight);
        mMatrix.reset();
        mMatrix.setScale(minScale, minScale);
        //经过矩阵变换后的新的bitmap
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, mMatrix, true);

        //新建图层
        //将原图层的内容保存，使得图形图层是和图片资源图层进行绘制操作
        int layerId = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);

        //在图层上绘制目标图形
        if (radius > 0) {
            //圆角矩形
            rectF = new RectF(0.0f, 0.0f, viewWidth, viewHeight);
            canvas.drawRoundRect(rectF, QMUIDisplayHelper.dpToPx(radius) * 1.0f, QMUIDisplayHelper.dpToPx(radius) * 1.0f, mPaint);
        } else {
            //圆形
            canvas.drawCircle(viewWidth / 2, viewWidth / 2, Math.min(viewHeight / 2, viewHeight / 2), mPaint);
        }


        //设置图层图像合成方式
        //绘制自定义形状图片是通过将自定义图形图层和图片资源图层的内容使用PorterDuffXfermode绘制合并而成
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        mPaint.setStyle(Paint.Style.FILL);
        //绘制图像
        canvas.drawBitmap(newBitmap, (viewWidth - newBitmap.getWidth()) / 2, (viewHeight - newBitmap.getHeight()) / 2, mPaint);
        //在图层绘制结束之后再使用canvas.restoreToCount恢复保存的原图层内容
        canvas.restoreToCount(layerId);

        if (strokeWidth > 0) {
            //设置边框属性
            //设置锯齿
            mPaint.setAntiAlias(true);
            mPaint.setColor(strokeColor);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(QMUIDisplayHelper.dpToPx(strokeWidth));
            //绘制边框

            if (radius > 0) {
                //矩形边框
                canvas.drawRoundRect(rectF, QMUIDisplayHelper.dpToPx(radius) * 1.0f, QMUIDisplayHelper.dpToPx(radius) * 1.0f, mPaint);
            } else {
                //圆形边框
                canvas.drawCircle(viewWidth / 2, viewWidth / 2, Math.min(viewHeight / 2, viewHeight / 2), mPaint);
            }

        }

    }
}
