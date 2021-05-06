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
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
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
    private int colorBlock = Color.parseColor("#00004E");
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
    //记录临时@列表一般只有单个
    private List<VideoPublishEditBean> videoAtEditBeans = new ArrayList<>();
    //当前删除用的@XX文字集合
    private int currentDelAtIndex = -1;
    private boolean isOptionDelAt = false;
    private boolean isOptionSelection = false;


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
        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN &&  keyEvent.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                    //删除键
                    if (checkDelAt()){
                        return true;
                    }else{
                        clearAtOption();
                        removeAtBean();
                    }
                }
                return false;
            }
        });

    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int lengthBefore, int lengthAfter) {
        textStart = start;
        textLengthBefore = lengthBefore;
        getTextLengthAfter = lengthAfter;
        if (isOptionDelAt) {
            VideoPublishEditBean bean = videoAtEditBeans.get(currentDelAtIndex);
            if (start < bean.getStartPoint() || start > bean.getEndPoint()) {
                isOptionDelAt = false;
            }
        }

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (editable.length() > 0) {
            if (prepareTopic || isOptionDelAt){
                return;
            }
            removeTextChangedListener(this);
            clearAtOptionNoColor();
            watchText(editable, textStart, textLengthBefore, getTextLengthAfter);
            sendBackTopic(editable);
            sendBackAt(editable);
            addTextChangedListener(this);
        }
    }
    private void clearAtOptionNoColor() {
        if (currentDelAtIndex != -1) {
            VideoPublishEditBean bean = videoAtEditBeans.get(currentDelAtIndex);
            bean.setBlock(false);
        }
        isOptionDelAt = false;
        currentDelAtIndex = -1;
    }
    /**
     * 清理@选中操作
     */
    private void clearAtOption() {
        if (currentDelAtIndex != -1) {
            VideoPublishEditBean bean = videoAtEditBeans.get(currentDelAtIndex);
            bean.setBlock(false);
            changeTextAtBlockColor(colorBlock, colorTopic);
        }
        isOptionDelAt = false;
        currentDelAtIndex = -1;
    }

    private void removeAtBean() {
        if (currentDelAtIndex != -1) {
            videoAtEditBeans.remove(currentDelAtIndex);
        }
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        /*if (prepareTopic)return;
        if (isOptionDelAt) {
            if (currentDelAtIndex == -1) {
                isOptionDelAt = false;
                return;
            }
            if (isOptionSelection){
                isOptionSelection = false;
                return;
            }
             VideoPublishEditBean bean = videoAtEditBeans.get(currentDelAtIndex);
             if (selStart <= bean.getStartPoint() || selStart > bean.getEndPoint() + 2) {
                isOptionSelection = true;
                clearAtOption();
            }
        }*/
    }

    private boolean checkDelAt() {
        if (isOptionDelAt) {
            VideoPublishEditBean bean = videoAtEditBeans.get(currentDelAtIndex);
            //整体删除操作
            removeAtBean();
            isOptionDelAt = false;
            prepareTopic = false;
            currentDelAtIndex = -1;
            getText().delete(bean.getStartPoint(), bean.getStartPoint() + bean.getStrLength());
            setSelection(bean.getStartPoint());
            return true;
        }
        //文字内容
        String currentStr = getText().toString();
        //光标位置
        int selStart = getSelectionStart();
        String cutStr = currentStr.substring(0, selStart);
        int textLast = cutStr.lastIndexOf("@");
        if (textLast==-1){
            clearAtOption();
            return false;//非@区间
        }
        cutStr = currentStr.substring(textLast, currentStr.length());

        //获取合法的@XX区间
        Matcher matcher = patternAt.matcher(cutStr);
        //跟matcher找到的@数量一一对应
        while (matcher.find()) {
            /*if (i > videoAtEditBeans.size() - 1) {
                return false;
            }*/
            int i = 0;
            int s = matcher.start();
            int e = matcher.end();
            for (VideoPublishEditBean bean : videoAtEditBeans) {
                    cutStr = cutStr.substring(s, e);
                    if (!bean.getContent().trim().startsWith(cutStr.trim())) {
                        i++;
                        continue;
                    }
                    if (selStart - textLast > bean.getStrLength()) {
                        i++;
                        continue;
                    }
                    //找到相对应的
                    bean.setIsBlock(true);
                    isOptionDelAt = true;
                    prepareTopic = true;
                    currentDelAtIndex = i;
                    changeTextAtBlockColor(colorBlock, colorTopic);
                    setSelection(bean.getEndPoint());
                    return true;
            }

            /*if (s == textLast) {
                //找到@起始位置,结束位置,检测匹配beanlist的位置,锁住block状态
                cutStr = currentStr.substring(s, e);
                VideoPublishEditBean bean = videoAtEditBeans.get(i);
                if (!bean.getContent().trim().startsWith(cutStr.trim())) {
                    continue;
                }
                if (s < selStart && selStart < e) {
                    //必须在选择范围内
                    clearAtOption();
                    return false;
                }

                bean.setIsBlock(true);
                currentDelAtIndex = i;
                isOptionDelAt = true;
                prepareTopic = true;
                changeTextAtBlockColor(colorBlock, colorTopic);
                setSelection(bean.getEndPoint());
                return true;
            }
            i++;*/
        }
        return isOptionDelAt;
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

        textStart = textLast;
        textLengthBefore = 0;
        getTextLengthAfter = atStr.length();

        VideoPublishEditBean bean = new VideoPublishEditBean();
        bean.setStartPoint(textStart);
        bean.setEndPoint(textStart + atStr.length());
        bean.setTextType(VideoPublishEditBean.AT_TYPE);
        bean.setContent(atStr);
        bean.setStrLength(getTextLengthAfter);
        videoAtEditBeans.add(bean);
        //TODO @功能添加字体颜色未改变
        getText().replace(textLast, currentStr.length(), atStr);
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
            changeTextColor(videoPublishEditBeans, text, colorTopic, start, lengthBefore, lengthAfter);
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
        if (!videoAtEditBeans.isEmpty()) {
            prepareTopic = true;
            Matcher matcher = patternAt.matcher(text);
            //跟matcher找到的@数量一一对应
            int i = 0;
            while (matcher.find()) {
                if (i>videoAtEditBeans.size()-1)break;
                int s = matcher.start();
                int e = matcher.end();
                String cutStr = text.toString().substring(s, e);
                VideoPublishEditBean videoAtEditBean = videoAtEditBeans.get(i);
                if (!videoAtEditBean.getContent().trim().startsWith(cutStr.trim())) {
                    continue;
                }
                videoAtEditBean.setStartPoint(s);
                videoAtEditBean.setEndPoint(e);
                i++;
            }
            for (VideoPublishEditBean bean : videoAtEditBeans) {
                videoPublishEditBeans.add(bean);
            }
        }
    }

    private void changeTextColor(List<VideoPublishEditBean> videoPublishEditBeans,Editable text,int color,int lengtStart, int lengthBefore, int lengthAfter) {
        SpannableStringBuilder spanColor = new SpannableStringBuilder(text);
        spanColor.setSpan(new ForegroundColorSpan(colorNormal), 0, text.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        for (VideoPublishEditBean editBean : videoPublishEditBeans) {
            if (editBean.isBlock()) {
                //正在操作
                spanColor.setSpan(new BackgroundColorSpan(colorBlock), editBean.getStartPoint(), editBean.getEndPoint(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            }else{
                spanColor.setSpan(new BackgroundColorSpan(0), editBean.getStartPoint(), editBean.getEndPoint(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            }
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

    private void changeTextAtBlockColor(int blockColor,int atColor) {
        VideoPublishEditBean bean = videoAtEditBeans.get(currentDelAtIndex);
        SpannableStringBuilder spanColor = new SpannableStringBuilder(bean.getContent());
        if (isOptionDelAt) {
            //正在操作
            spanColor.setSpan(new ForegroundColorSpan(atColor), 0, bean.getStrLength(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            spanColor.setSpan(new BackgroundColorSpan(blockColor), 0, bean.getStrLength(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }else{
            //全部换回默认@色
            spanColor.setSpan(new BackgroundColorSpan(0), 0, bean.getStrLength(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            spanColor.setSpan(new ForegroundColorSpan(atColor), 0, bean.getStrLength(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        prepareTopic = false;
        getText().replace(bean.getStartPoint(), bean.getEndPoint(), spanColor);

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
