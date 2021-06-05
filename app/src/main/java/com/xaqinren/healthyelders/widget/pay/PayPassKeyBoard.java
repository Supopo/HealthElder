package com.xaqinren.healthyelders.widget.pay;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.utils.LogUtils;

import java.util.List;

import me.goldze.mvvmhabit.utils.ConvertUtils;


/**
 * 输入密码键盘控件
 */
public class PayPassKeyBoard extends FrameLayout implements View.OnClickListener {
    private List<TextView> textViews;
    private int itemBgRes;
    private int itemColorRes;
    private float itemTextSize;
    private int itemMargin;

    private int itemSize = 11;
    private int lineCount = 3;
    private int height, width;
    private PayPassView payPassView;
    private String[] values = {
            "1","2","3","4","5","6","7","8","9","0","del"
    };
    private String TAG = "PayPassKeyBoard";
    private OnKeyChange onKeyChange;

    public void setOnKeyChange(OnKeyChange onKeyChange) {
        this.onKeyChange = onKeyChange;
    }

    public void attachPassView(PayPassView payPassView) {
        if (this.payPassView!=null) return;
        this.payPassView = payPassView;
    }
    public PayPassKeyBoard(Context context) {
        super(context);
        init(context, null, 0);
    }

    public PayPassKeyBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public PayPassKeyBoard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PayPassKeyBoard);
        int backGroundResId = typedArray.getResourceId(R.styleable.PayPassKeyBoard_board_back_ground_res, R.color.color_D5D7DD);
        itemBgRes =  typedArray.getResourceId(R.styleable.PayPassKeyBoard_board_item_bg_res, R.drawable.bg_pass_board_item);
        itemColorRes =  typedArray.getColor(R.styleable.PayPassKeyBoard_board_item_text_color_res, getResources().getColor(R.color.color_252525));
        itemTextSize = typedArray.getDimension(R.styleable.PayPassKeyBoard_board_item_text_size, 25F);
        itemMargin = (int) typedArray.getDimension(R.styleable.PayPassKeyBoard_board_item_margin,
                getResources().getDimension(R.dimen.dp_6));
        //getResources().getDimension(R.dimen.dp_6)
        setBackgroundResource(backGroundResId);
        addItemView();
        typedArray.recycle();
    }

    private void addItemView() {
        for (int i = 0; i < itemSize; i++) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(-2, -2);
            TextView item = new TextView(getContext());
            item.setLayoutParams(params);
            item.setTextSize(itemTextSize);
            item.setBackgroundResource(itemBgRes);
            item.setTextColor(itemColorRes);
            item.setGravity(Gravity.CENTER);
            item.setClickable(true);
            item.setEnabled(true);
            item.setOnClickListener(this);
            item.setTag(values[i]);
            item.setText(values[i]);
            if (i == itemSize - 1) {
                //删除键
//                item.setText(values[i]);
            }
            addView(item);
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
        width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        height = MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();
        LogUtils.e(TAG, "modeWidth->" + modeWidth + "\tmodeHeight->" + modeHeight
                + "\twidth->" + width
                + "\theight->" + height);
        resetChildLayout();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void resetChildLayout() {
        //先布局1-9
        int line = 0;
        int itemWidth = ( width - itemMargin * (lineCount - 1) )/ lineCount;
        int itemHeight = (height - itemMargin * 3) / 4;

        for (int i = 0; i < itemSize; i++) {
            TextView child = (TextView) getChildAt(i);
            FrameLayout.LayoutParams params = (LayoutParams) child.getLayoutParams();
            params.width = itemWidth;
            params.height = itemHeight;
            int liftMargin;
            if (i == 0) {
                liftMargin = 0;
            } else if (i % lineCount == 0) {
                line++;
                liftMargin = 0;
            } else {
                liftMargin = itemMargin * (i % lineCount);
            }
            int a = i % lineCount;
            params.topMargin = itemHeight * line  + line * itemMargin;
            if (child.getTag().toString().equals("0") || child.getTag().toString().equals(
                    values[values.length - 1]
            ) ){
                liftMargin += itemWidth + itemMargin;
            }
            params.leftMargin = itemWidth * (i % lineCount) + liftMargin;
            child.setLayoutParams(params);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

    }

    @Override
    public void onClick(View v) {
        TextView child = (TextView) v;
        String value = child.getTag().toString();
        if (value.equals(values[values.length - 1])) {
            //删除键
            if (payPassView != null) {
                payPassView.remove();
            }
            if (onKeyChange != null) {
                onKeyChange.onKeyBack();
            }
        }else{
            //输入
            if (payPassView != null) {
                payPassView.add(value);
            }
            if (onKeyChange != null) {
                onKeyChange.onKeyDown(value);
            }
        }
    }

    public interface OnKeyChange{
        void onKeyDown(String value);
        void onKeyBack();
    }

}
