package com.xaqinren.healthyelders.widget;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;

import com.qmuiteam.qmui.util.QMUIDisplayHelper;

/**
 * =====================================================
 * 描    述: 自定义展开/收缩TextView
 * =====================================================
 */
public class ExpandableTextView extends AppCompatTextView {
    private boolean needExpand;//是否需要展开
    private boolean isExpand;//展开状态
    private int showMaxEms = 10;//默认展示最大字数
    private int showFontSize = 12;//展开、收起字样字体大小，单位sp
    private int showFontColor = Color.RED;//展开、收起字样颜色
    private String showExpandText = "...\n展开";
    private String showRetractText = "\n收起";
    private String expandText;//展开的文字
    private String retractText;//收起的文字
    private String defaultText;
    private Context context;

    public ExpandableTextView(Context context) {
        super(context);
        this.context = context;
    }

    public ExpandableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public ExpandableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    public void setShowMaxEms(int showMaxEms) {
        this.showMaxEms = showMaxEms;
    }

    public void setShowFontColor(int showFontColor) {
        this.showFontColor = showFontColor;
    }

    public void setShowFontSize(int showFontSize) {
        this.showFontSize = showFontSize;
    }

    public void setDefaultText(String defaultText) {
        this.defaultText = defaultText;
        if (defaultText.length() > showMaxEms) {
            needExpand = true;
            setRetractText();
        } else {
            needExpand = false;
            setText(defaultText);
        }
    }

    //设置展开时候的内容
    private void setExpandText() {
        expandText = defaultText + showRetractText;

        SpannableStringBuilder builder = new SpannableStringBuilder(expandText);
        //ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
        ForegroundColorSpan buleSpan = new ForegroundColorSpan(showFontColor);
        //左闭右开 换行字符也要算一个字符
        builder.setSpan(buleSpan, expandText.length() - 2, expandText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置字体大小（绝对值,单位：像素）
        builder.setSpan(new AbsoluteSizeSpan(QMUIDisplayHelper.sp2px(context, showFontSize)), defaultText.length() + 1, defaultText.length() + 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置局部内容点击事件
        MyClickableSpan clickableSpan = getMyClickableSpan();
        builder.setSpan(clickableSpan, expandText.length() - 2, expandText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //不设置点击会失效
        setMovementMethod(LinkMovementMethod.getInstance());

        setText(builder);
        isExpand = true;
    }

    //设置部分文字点击事件
    private MyClickableSpan getMyClickableSpan() {
        return new MyClickableSpan(false) {
            @Override
            public void onClick(View widget) {
                setExpand();
            }
        };
    }

    //设置收缩时候的内容
    private void setRetractText() {
        retractText = defaultText.substring(0, showMaxEms) + showExpandText;
        //Html标签写法
        //        setText(Html.fromHtml(defaultText.substring(0, maxEms) + "..." + "<br>" + "<font color=" + "#E01919" + ">" + "<small>" + showExpandText + "</small>" + "</font>"));

        SpannableStringBuilder builder = new SpannableStringBuilder(retractText);
        //ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
        ForegroundColorSpan buleSpan = new ForegroundColorSpan(showFontColor);
        //左闭右开 换行字符也要算一个字符 Spanned.SPAN_EXCLUSIVE_EXCLUSIVE 当前设置前后不包括
        builder.setSpan(buleSpan, retractText.length() - 2, retractText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置字体大小（绝对值,单位：像素）
        builder.setSpan(new AbsoluteSizeSpan(QMUIDisplayHelper.sp2px(context, 12)), retractText.length() - 2, retractText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置局部内容点击事件
        MyClickableSpan clickableSpan = getMyClickableSpan();
        builder.setSpan(clickableSpan, retractText.length() - 2, retractText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //不设置点击会失效
        setMovementMethod(LinkMovementMethod.getInstance());

        setText(builder);
        isExpand = false;
    }


    //设置展开与否
    public void setExpand() {
        if (needExpand) {
            if (isExpand) {
                setRetractText();
            } else {
                setExpandText();
            }
        }
    }

    //自定义ClickableSpan去掉下划线
    public class MyClickableSpan extends ClickableSpan {


        private boolean isUnderline;

        public MyClickableSpan(boolean isUnderline) {
            this.isUnderline = isUnderline;
        }


        @Override
        public void onClick(View view) {

        }

        @Override
        public void updateDrawState(TextPaint ds) {
            //设置颜色 ds.setColor
            ds.setUnderlineText(isUnderline); //去掉下划线
        }
    }
}
