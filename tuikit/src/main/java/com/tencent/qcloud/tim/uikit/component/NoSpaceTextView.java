package com.tencent.qcloud.tim.uikit.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Layout;
import android.text.TextPaint;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tencent.qcloud.tim.uikit.R;


public class NoSpaceTextView extends androidx.appcompat.widget.AppCompatTextView {
    int left = 0;
    int right = 0;
    int top = 0;
    int bottom = 0;
    boolean needNoSpace = false;
    float fontWidth;
    /**
     * 控制measure()方法 刷新测量
     */
    private boolean refreshMeasure = false;
    public NoSpaceTextView(@NonNull Context context) {
        super(context);
        init(context,null);
    }

    public NoSpaceTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public NoSpaceTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        left = getPaddingLeft();
        right = getPaddingRight();
        top = getPaddingTop();
        bottom = getPaddingBottom();
        if (attrs == null) {
            return;
        }
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NoSpaceTextView);
        int count = typedArray.getIndexCount();
        for (int i = 0; i < count; i++) {
            int attr = typedArray.getIndex(i);
            if (attr == R.styleable.NoSpaceTextView_needNoSpace) {
                boolean need = typedArray.getBoolean(attr, false);
                needNoSpace = need;
            } else if (attr == R.styleable.NoSpaceTextView_textWeight) {
                float value = typedArray.getFloat(attr, 0);
                fontWidth = value;
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (needNoSpace)
            removeSpace(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (fontWidth > 0) {
            TextPaint paint = getPaint();
            //设置画笔的描边宽度值
//            paint.setStrokeWidth(fontWidth);
            paint.setFakeBoldText(true);
//            paint.setStyle(Paint.Style.FILL_AND_STROKE);
        }
        super.onDraw(canvas);
    }

    /**
     * 这里处理文本的上下留白问题
     */
    private void removeSpace(int widthspc, int heightspc) {

        int       paddingTop = 0;
        int       paddingBottom = 0;
        String[]  linesText  = getLinesText();
        TextPaint paint      = getPaint();
        Rect rect       = new Rect();
//        String    text       = linesText[0];
        String    text       = "\uD83D\uDE00";
        paint.getTextBounds(text, 0, text.length(), rect);

        Paint.FontMetricsInt fontMetricsInt = new Paint.FontMetricsInt();
        paint.getFontMetricsInt(fontMetricsInt);

        paddingTop = (fontMetricsInt.top - rect.top);
        paddingBottom = (fontMetricsInt.bottom - rect.bottom);

        // 设置TextView向上的padding (小于0, 即把TextView文本内容向上移动)
        setPadding(getLeftPaddingOffset() + left
                , paddingTop + getTopPaddingOffset() + top
                , getRightPaddingOffset() + right
                , getBottomPaddingOffset() + bottom
//                ,paddingBottom + getBottomPaddingOffset()
        );

        String endText = linesText[linesText.length - 1];
        paint.getTextBounds(endText, 0, endText.length(), rect);

        // 再减去最后一行文本的底部空白，得到的就是TextView内容上线贴边的的高度，到达消除文本上下留白的问题
        setMeasuredDimension(getMeasuredWidth()
                , getMeasuredHeight() - (fontMetricsInt.bottom - rect.bottom));

        if (refreshMeasure) {
            refreshMeasure = false;
            measure(widthspc, heightspc);
        }
    }

    /**
     * 获取每一行的文本内容
     */
    private String[] getLinesText() {

        int start = 0;
        int end   = 0;
        String hint = (String) getHint();
        String[] texts = new String[getLineCount()];
        String text = getText().toString();
        if (hint != null && hint.length() != 0 && text.length() == 0) {
            texts = new String[1];
        }
        if (text.length() == 0 && hint != null) {
            text = hint;
        }
        Layout layout = getLayout();
        for (int i = 0; i < getLineCount(); i++) {
            end = layout.getLineEnd(i);
            if (end == 0 && hint != null && hint.length() > 0) {
                end = hint.length();
            }
            String line = text.substring(start, end); //指定行的内容
            start = end;

            texts[i] = line;
        }


        return texts;
    }
}
