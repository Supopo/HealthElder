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
import com.xaqinren.healthyelders.moduleLiteav.activity.VideoEditerActivity;
import com.xaqinren.healthyelders.moduleLiteav.bean.VideoPublishEditBean;
import com.xaqinren.healthyelders.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VideoPublishEditTextView extends AppCompatEditText implements TextWatcher {
    private String TAG = "VideoPublishEditTextView";
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
    //@XXX
    private static Pattern patternAt = Pattern.compile("[@][\\u4e00-\\u9fa5a-zA-Z0-9]+");
    //记录bean
    private List<VideoPublishEditBean> videoPublishEditBeans = new ArrayList<>();

    private int textStart,textLengthBefore, getTextLengthAfter;

    private OnTextChangeListener onTextChangeListener;

    public void setOnTextChangeListener(OnTextChangeListener onTextChangeListener) {
        this.onTextChangeListener = onTextChangeListener;
    }

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
            if (prepareTopic){
                return;
            }
            removeTextChangedListener(this);
            watchText(editable, textStart, textLengthBefore, getTextLengthAfter);
            sendBackTopic(editable);
            sendBackAt(editable);
            addTextChangedListener(this);
        }
    }

    private void sendBackAt(Editable editable) {
        char last = editable.charAt(editable.length() - 1);
        if (last == '@') {
            //唤起
            onTextChangeListener.inputAt("@");
            return;
        }
        String text = editable.toString();
        int lastTopic = text.lastIndexOf("@");
        if (lastTopic == -1) {
            //没找到@
            onTextChangeListener.inputNoAt();
            return;
        }
        //光标位置
        int lastPoint = text.length();
        int selStart = getSelectionStart();
        String cut = text.substring(selStart, lastPoint);
        if (cut.lastIndexOf("@") != -1) {
            //说明后面有@符号，则这个属于前面的
            onTextChangeListener.inputNoAt();
            return;
        }

        cut = text.substring(lastTopic, lastPoint);
        Matcher matcher = patternAt.matcher(cut);
        if (matcher.find()) {
            if (matcher.end() != cut.length()) {
                //长度不一致，说明中间有符号阻断了
                onTextChangeListener.inputNoAt();
                return;
            }
            onTextChangeListener.inputAt(cut);
        }
    }
    /**
     * 向上层发送输入#事件或移动到#XXX范围内
     */
    private void sendBackTopic(Editable editable) {
        char last = editable.charAt(editable.length() - 1);
        if (last == '#') {
            //唤起
            onTextChangeListener.inputTopic("#");
            return;
        }
        //光标位置
        int selStart = getSelectionStart();
        //非#则判断是否未是在#XXX范围内
        String cutStr = editable.toString().substring(0, selStart);
        int lastTopic = cutStr.lastIndexOf("#");
        int textLast = editable.toString().lastIndexOf("#");
        if (lastTopic != textLast) {
            //只处理最后一个topic话题
            onTextChangeListener.inputNoTopic();
            return;
        }
        if (lastTopic == -1) {
            //说明没有topic话题
            onTextChangeListener.inputNoTopic();
            return;
        }
        if (selStart <= lastTopic) {
            //说明已经在#前面了
            onTextChangeListener.inputNoTopic();
            return;
        }
        cutStr = cutStr.substring(lastTopic);
        Matcher matcher = patternTopic.matcher(cutStr);
        if (matcher.find()) {
            //找到符合规则的
            if (matcher.end() != cutStr.length()) {
                //长度不一致，说明中间有符号阻断了
                onTextChangeListener.inputNoTopic();
                return;
            }
            onTextChangeListener.inputTopic(cutStr);
        }
    }

    /**
     * 提交@XXX
     * @param atStr
     */
    public void setAtStr(String atStr) {
        //提交了一个热点话题，理所应当从当前#开始网后面替换topicStr的文字内容
        String currentStr = getText().toString();

        //光标位置
        int selStart = getSelectionStart();
        String cutStr = currentStr.substring(0, selStart);
        int textLast = cutStr.lastIndexOf("@");
        if (textLast==-1)return;
        if (selStart < textLast) {
            //光标在#左边
            return;
        }

        int currentLast = currentStr.lastIndexOf("@");
        if (textLast != currentLast) {
            //不是最后一个topic,不给粘贴
            return;
        }

        cutStr = cutStr.substring(textLast);
        Matcher matcher = patternAt.matcher(cutStr);
        if (matcher.find()) {
            //找到符合规则的
            if (matcher.end() != cutStr.length()) {
                //长度不一致，说明中间有符号阻断了
                return;
            }
        }

        textStart = textLast + atStr.length() - 1;
        textLengthBefore = 0;
        getTextLengthAfter = atStr.length();

        VideoPublishEditBean bean = new VideoPublishEditBean();
        bean.setStartPoint(textStart);
        bean.setEndPoint(textStart + atStr.length());
        bean.setTextType(VideoPublishEditBean.AT_TYPE);
        bean.setContent(atStr);
        videoPublishEditBeans.add(bean);
        //可粘贴,在textLast位置,z粘贴topicStr.length - 1

        getText().replace(textLast, selStart, atStr);
    }

    /**
     * 提交#XXX
     * @param topicStr
     */
    public void setTopicStr(String topicStr) {
        //提交了一个热点话题，理所应当从当前#开始网后面替换topicStr的文字内容
        String currentStr = getText().toString();

        //光标位置
        int selStart = getSelectionStart();
        String cutStr = currentStr.substring(0, selStart);
        int textLast = cutStr.lastIndexOf("#");
        if (textLast==-1)return;
        if (selStart < textLast) {
            //光标在#左边
            return;
        }

        int currentLast = currentStr.lastIndexOf("#");
        if (textLast != currentLast) {
            //不是最后一个topic,不给粘贴
            return;
        }

        cutStr = cutStr.substring(textLast);
        Matcher matcher = patternTopic.matcher(cutStr);
        if (matcher.find()) {
            //找到符合规则的
            if (matcher.end() != cutStr.length()) {
                //长度不一致，说明中间有符号阻断了
                return;
            }
        }

        textStart = textLast + topicStr.length() - 1;
        textLengthBefore = 0;
        getTextLengthAfter = topicStr.length();
        //可粘贴,在textLast位置,z粘贴topicStr.length - 1
        getText().replace(textLast, selStart, topicStr);
    }


    private void watchText(Editable text, int start, int lengthBefore, int lengthAfter) {
//        LogUtils.e("VideoPublishEditTextView", "text = " + text.toString() + "\tstart =\t"+start+ "\tlengthBefore =\t"+lengthBefore+ "\tlengthAfter =\t"+lengthAfter);
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

    private void changeTextColor(Editable text,int color,int lengtStart, int lengthBefore, int lengthAfter) {
        SpannableStringBuilder spanColor = new SpannableStringBuilder(text);
        spanColor.setSpan(new ForegroundColorSpan(colorNormal), 0, text.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        for (VideoPublishEditBean editBean : videoPublishEditBeans) {
            spanColor.setSpan(new ForegroundColorSpan(color), editBean.getStartPoint(), editBean.getEndPoint(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }

        text.replace(0, text.length(), spanColor);
        if (lengthBefore > 0 && lengthAfter == 0) {
            //删除文字，需要吧光标放在删除位置
            setSelection(lengtStart);
        } else if (lengtStart != text.length() - 1 + lengthAfter) {
            //在某个位置写文字
            setSelection(lengtStart + lengthAfter);
        } else {
            setSelection(text.length());
        }
    }

    /**
     * 获取topic列表
     * @return
     */
    public List<VideoPublishEditBean> getTopicList() {
        String text = getText().toString();
        Matcher matcher = patternTopic.matcher(text);
        List<VideoPublishEditBean> videoPublishEditBeans = new ArrayList<>();
        while (matcher.find()) {
            VideoPublishEditBean bean = new VideoPublishEditBean();
            String str = text.substring(matcher.start(), matcher.end());
            LogUtils.e(TAG, "热点话题 -> " + str);
            bean.setContent(str);
            bean.setTextType(VideoPublishEditBean.TOPIC_TYPE);
            videoPublishEditBeans.add(bean);
        }
        return videoPublishEditBeans;
    }

    /**
     * 获取@列表
     * @return
     */
    public List<VideoPublishEditBean> getAtList() {
        String text = getText().toString();
        Matcher matcher = patternAt.matcher(text);
        List<VideoPublishEditBean> videoPublishEditBeans = new ArrayList<>();
        while (matcher.find()) {
            VideoPublishEditBean bean = new VideoPublishEditBean();
            String str = text.substring(matcher.start(), matcher.end());
            LogUtils.e(TAG, "@用户 -> " + str);
            bean.setContent(str);
            bean.setTextType(VideoPublishEditBean.AT_TYPE);
            videoPublishEditBeans.add(bean);
        }
        return videoPublishEditBeans;
    }

    public interface OnTextChangeListener{
        void inputTopic(String str);
        void inputNoTopic();
        void inputAt(String str);
        void inputNoAt();
    }
}
