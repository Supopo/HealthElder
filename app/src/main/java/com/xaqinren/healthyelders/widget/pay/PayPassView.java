package com.xaqinren.healthyelders.widget.pay;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 支付密码控件
 */
public class PayPassView extends FrameLayout {
    private String TAG = PayPassView.class.getSimpleName();
    private int maxChild;
    private int height, width;
    private int lineSize;
    private int borderCir;
    private Paint paint;
    private int itemSize;
    private List<String> passValues;
    private OnKeyChange onKeyChange;
    private int passRes;
    private int passResNormal;
    private int lineColor;


    public void setOnKeyChange(OnKeyChange onKeyChange) {
        this.onKeyChange = onKeyChange;
    }

    public PayPassView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public PayPassView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public PayPassView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PayPassKeyBoard);

        maxChild = 6;
        passValues = new ArrayList<>(maxChild);
        lineSize = (int) getResources().getDimension(R.dimen.dp_1);
        borderCir = (int) getResources().getDimension(R.dimen.dp_4);
        lineColor = getResources().getColor(R.color.color_D2D2D2);
        itemSize = (int) getContext().getResources().getDimension(R.dimen.dp_20);
        setBackgroundColor(getResources().getColor(R.color.white));
        passRes = R.drawable.bg_pass;
        passResNormal = R.drawable.transparent;


        LogUtils.e(TAG,"init");

        paint = new Paint();
        paint.setDither(true);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(lineSize);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(lineColor);

        for (int i = 0; i < maxChild; i++) {
            View textView = createChild(i);
            addView(textView);
        }
        typedArray.recycle();
    }

    private View createChild(int i) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(itemSize, itemSize);
        View textView = new View(getContext());
        textView.setLayoutParams(params);
        textView.setTag(i);
        textView.setBackground(getResources().getDrawable(R.drawable.transparent));
        return textView;
    }

    public void resetChildLayout() {
        int itemWidth = width  / maxChild;
        int itemHeight = height;
        for (int i = 0; i < maxChild; i++) {
            View textView =  getChildAt(i);
            FrameLayout.LayoutParams layoutParams = (LayoutParams) textView.getLayoutParams();
            layoutParams.leftMargin = itemWidth * i + (itemWidth - itemSize) / 2;
            layoutParams.topMargin = (itemHeight - itemSize) / 2;
            textView.setLayoutParams(layoutParams);
        }
    }

    public String getValue() {
        StringBuffer buffer = new StringBuffer();
        for (String s : passValues) {
            buffer.append(s);
        }
        return buffer.toString();
    }

    public void add(String text) {
        if (passValues.size() < maxChild) {
            passValues.add(text);
            int index = passValues.size() - 1;
            View view  = getChildAt(index);
            view.setBackgroundResource(passRes);
        }
        if (passValues.size() == maxChild) {
            if (onKeyChange != null) {
                onKeyChange.onInputComplete(getValue());
            }
        }else{
            if (onKeyChange != null) {
                onKeyChange.onInputVacancy();
            }
        }

    }

    public void remove() {
        if (!passValues.isEmpty()) {
            int index = passValues.size() - 1;
            View view  = getChildAt(index);
            view.setBackgroundResource(passResNormal);
            passValues.remove(passValues.size() - 1);
            if (onKeyChange != null) {
                onKeyChange.onInputVacancy();
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int modeWidth  = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        LogUtils.e(TAG, "modeWidth->" + modeWidth + "\tmodeHeight->" + modeHeight
                + "\twidth->" + width
                + "\theight->" + height);
        resetChildLayout();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        LogUtils.e(TAG,"onDraw");
        //计算单个控件尺寸
        int itemWidth = width / maxChild;
        int itemHeight = height - lineSize * 2;
        //先画出最外框
        Path path = new Path();
        path.rMoveTo(100, 100);
        path.addRoundRect(
                new RectF(0, 0, width, height),borderCir,borderCir, Path.Direction.CW
        );
        canvas.drawPath(path,paint);
        for (int i = 0; i < maxChild; i++) {
            path.reset();
            path.moveTo(itemWidth * (i + 1), 0);
            path.lineTo(itemWidth * (i + 1), height);
            canvas.drawPath(path,paint);
        }
    }

    public interface OnKeyChange{
        void onInputComplete(String value);
        void onInputVacancy();
    }

}
