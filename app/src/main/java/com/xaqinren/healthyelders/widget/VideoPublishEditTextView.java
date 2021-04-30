package com.xaqinren.healthyelders.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatEditText;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.xaqinren.healthyelders.moduleLiteav.bean.VideoPublishEditBean;
import com.xaqinren.healthyelders.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VideoPublishEditTextView extends AppCompatEditText implements TextWatcher {

    private char topicSym = '#';
    private char friendSym = '@';

    //特殊文字颜色
    private int colorTopic = Color.parseColor("#FF004E");
    private int colorNormal = Color.parseColor("#252525");
    //标记是否正在记录topic内容
    private boolean prepareTopic = false;
    //匹配字符
    private static Pattern pattern = Pattern.compile("[^\\u4e00-\\u9fa5a-zA-Z0-9]");
    //匹配 #符号和后一个非符号
    private static Pattern patternTopic = Pattern.compile("#[\\u4e00-\\u9fa5a-zA-Z0-9]+");
    //正常字符
    private static Pattern patternNormal = Pattern.compile("[^#@][\\u4e00-\\u9fa5a-zA-Z0-9]+[#@]");
    //记录bean
    private List<VideoPublishEditBean> videoPublishEditBeans = new ArrayList<>();

    private int textStart,textLengthBefore, getTextLengthAfter;

    public VideoPublishEditTextView(Context context) {
        super(context);
        initView();
    }

    public VideoPublishEditTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public VideoPublishEditTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        addTextChangedListener(this);
    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int lengthBefore, int lengthAfter) {
        textStart = start;
        textLengthBefore = lengthBefore;
        getTextLengthAfter = lengthAfter;
    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (editable.length() > 0) {
            //记录最后一个字符
            /*if (lengthBefore == lengthAfter) {
                //重新生成的文字
                return;
            }*/
            if (prepareTopic){
                return;
            }
            removeTextChangedListener(this);
            watchText(editable, textStart, textLengthBefore, getTextLengthAfter);
            addTextChangedListener(this);
        }
    }


    private void watchText(Editable text, int start, int lengthBefore, int lengthAfter) {
        LogUtils.e("VideoPublishEditTextView", "text = " + text.toString() + "\tstart =\t"+start+ "\tlengthBefore =\t"+lengthBefore+ "\tlengthAfter =\t"+lengthAfter);
        checkTopic(text, start, lengthBefore, lengthAfter);
        checkFriend(text, start, lengthBefore, lengthAfter);
        if (prepareTopic) {
            changeTextColor(text,  colorTopic, start, lengthBefore, lengthAfter);
        }
        prepareTopic = false;
        videoPublishEditBeans.clear();
    }

    private void checkTopic(Editable text, int start, int lengthBefore, int lengthAfter) {
        Matcher matcher = patternTopic.matcher(text);
        while (matcher.find()) {
            int s = matcher.start();
            int e = matcher.end();
            prepareTopic = true;
            VideoPublishEditBean bean = new VideoPublishEditBean();
            bean.setStartPoint(s);
            bean.setEndPoint(e);
            videoPublishEditBeans.add(bean);
        }
    }

    private void checkFriend(Editable text, int start, int lengthBefore, int lengthAfter) {

    }

    /**
     * 提交@符号集
     * @param atStr
     */
    public void setAtStr(String atStr) {

    }

    private void changeTextColor(Editable text,int color,int lengtStart, int lengthBefore, int lengthAfter) {
        SpannableStringBuilder spanColor = new SpannableStringBuilder(text);
        spanColor.setSpan(new ForegroundColorSpan(colorNormal), 0, text.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        for (VideoPublishEditBean editBean : videoPublishEditBeans) {
            spanColor.setSpan(new ForegroundColorSpan(color), editBean.getStartPoint(), editBean.getEndPoint(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }

//        setText(spanColor);
        text.replace(0, text.length(), spanColor);
        if (lengthBefore > 0) {
            //删除文字，需要吧光标放在删除位置
            setSelection(lengtStart);
        } else if (lengtStart != text.length() - 1 + lengthAfter) {
            //在某个位置写文字
            setSelection(lengtStart + lengthAfter);
        } else {
            setSelection(text.length());
        }
    }
}
