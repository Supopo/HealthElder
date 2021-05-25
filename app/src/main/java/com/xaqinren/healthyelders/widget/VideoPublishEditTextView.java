package com.xaqinren.healthyelders.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.text.LineBreaker;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;

import androidx.appcompat.widget.AppCompatEditText;

import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.moduleLiteav.bean.PublishDesBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.PublishFocusItemBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.VideoPublishEditBean;
import com.xaqinren.healthyelders.utils.LogUtils;
import com.xaqinren.healthyelders.widget.editor.EditFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VideoPublishEditTextView extends AppCompatEditText implements TextWatcher {
    private String TAG = "VideoPublishEditTextView";
    private char topicSym = '#';
    private char friendSym = '@';

    public void setTextStyle(int[] textStyle) {
        this.textStyle = textStyle;
    }

    //Topic-At-Normal
    private int[] textStyle = {
            Typeface.NORMAL,
            Typeface.NORMAL,
            Typeface.NORMAL};


    //是否激活#
    private boolean isTopicEnable = true;
    //是否激活@
    private boolean isAtEnable = true;
    //激活上发事件
    private boolean enablePost = true;
    //是否在删除
    private boolean isDel = false;
    //删除时，是否发送事件
    private boolean delEnablePost = true;
    //handel
    private Handler handler = new Handler(Looper.myLooper());


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
    private boolean initView = false;
    private int inputMax = 55;

    private int textStart, textLengthBefore, getTextLengthAfter;

    private OnTextChangeListener onTextChangeListener;

    public void setEnablePost(boolean enablePost) {
        this.enablePost = enablePost;
    }

    public void setDelEnablePost(boolean delEnablePost) {
        this.delEnablePost = delEnablePost;
    }

    public void setColorTopic(int colorTopic) {
        this.colorTopic = colorTopic;
    }

    public void setColorBlock(int colorBlock) {
        this.colorBlock = colorBlock;
    }

    public void setColorNormal(int colorNormal) {
        this.colorNormal = colorNormal;
    }

    public void setInputMax(int inputMax) {
        this.inputMax = inputMax;
    }

    public int getInputMax() {
        return inputMax;
    }

    public void setAtEnable(boolean atEnable) {
        isAtEnable = atEnable;
    }

    public void setTopicEnable(boolean topicEnable) {
        isTopicEnable = topicEnable;
    }

    public void setOnTextChangeListener(OnTextChangeListener onTextChangeListener) {
        this.onTextChangeListener = onTextChangeListener;
    }

    public VideoPublishEditTextView(Context context) {
        super(context);
        initView(null, 0);
    }

    public VideoPublishEditTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(attrs, 0);
    }

    public VideoPublishEditTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs, defStyleAttr);
    }

    private void initView(AttributeSet attrs, int defStyleAttr) {
        addTextChangedListener(this);
        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyEvent.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                    //删除键
                    isDel = true;
                    if (checkDelAt()) {
                        return true;
                    } else {
                        clearAtOption();
                        removeAtBean();
                    }
                }
                return false;
            }
        });

        setEditableFactory(new EditFactory());
        initView = true;

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int start, int lengthBefore, int lengthAfter) {
        if (lengthAfter > 0 ) {
            isDel = false;
        }
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
//        LogUtils.e(TAG, "afterTextChanged" );
        if (editable.length() > 0) {
            if (prepareTopic || isOptionDelAt) {
                return;
            }
//            LogUtils.e(TAG, "执行一次 加载文字 效果" );
            removeTextChangedListener(this);
            clearAtOptionNoColor();
            watchText(editable, textStart, textLengthBefore, getTextLengthAfter);
            if (isTopicEnable) {//激活topic功能
                if (enablePost){//激活发送事件
                    if (!delEnablePost && isDel) {
                        //空处理
                    }else
                        sendBackTopic(editable);
                }
            }
            if (isAtEnable) {
                if (enablePost) {//激活发送#事件
                    if (!delEnablePost && isDel) {
                        //空处理
                    }else
                        sendBackAt(editable);
                }
            }
            addTextChangedListener(this);
//            LogUtils.e(TAG, "完成一次 加载文字 效果" );
        } else {
            if (onTextChangeListener != null) {
                onTextChangeListener.inputNoTopic();
                onTextChangeListener.inputNoAt();
            }
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
        if (!initView) {
            return;
        }
        if (isOptionSelection) {
            isOptionSelection = false;
            return;
        }
        //不让光标移动到@XX中间,如果移动到了需要放到@XX最末尾
        for (VideoPublishEditBean videoAtEditBean : videoAtEditBeans) {
            int start = videoAtEditBean.getStartPoint();
            int end = videoAtEditBean.getEndPoint();
            if (selStart > start && selStart < end) {
                isOptionSelection = true;
                setSelection(end);
            }
        }
    }
    /**
     * 检查删除键
     *
     * @return
     */
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
        if (textLast == -1) {
            clearAtOption();
            return false;//非@区间
        }
        cutStr = currentStr.substring(textLast, selStart);

        //获取合法的@XX区间
        Matcher matcher = patternAt.matcher(cutStr);
        //跟matcher找到的@数量一一对应
        while (matcher.find()) {
            int i = 0;
            int s = matcher.start();
            int e = matcher.end();
            for (VideoPublishEditBean bean : videoAtEditBeans) {
                String cut = cutStr.substring(s, e);
                if (!bean.getContent().trim().equals(cut.trim())) {
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
        }
        return isOptionDelAt;
    }

    private void sendBackAt(Editable editable) {
        char last = editable.charAt(editable.length() - 1);
        if (last == '@') {
            //唤起
            if (onTextChangeListener != null)
                onTextChangeListener.inputAt("@");
            return;
        }
        String text = editable.toString();
        int lastTopic = text.lastIndexOf("@");
        if (lastTopic == -1) {
            //没找到@
            if (onTextChangeListener != null)
                onTextChangeListener.inputNoAt();
            return;
        }
        //光标位置
        int lastPoint = text.length();
        int selStart = getSelectionStart();
        String cut = text.substring(selStart, lastPoint);
        if (cut.lastIndexOf("@") != -1) {
            //说明后面有@符号，则这个属于前面的
            if (onTextChangeListener != null)
                onTextChangeListener.inputNoAt();
            return;
        }

        cut = text.substring(lastTopic, lastPoint);
        Matcher matcher = patternAt.matcher(cut);
        if (matcher.find()) {
            if (matcher.end() != cut.length()) {
                //长度不一致，说明中间有符号阻断了
                if (onTextChangeListener != null)
                    onTextChangeListener.inputNoAt();
                return;
            }
            if (onTextChangeListener != null)
                onTextChangeListener.inputAt(cut);
        }
    }

    /**
     * 向围上层发送输入#事件或移动到#XXX范内
     */
    private void sendBackTopic(Editable editable) {
        char last = editable.charAt(editable.length() - 1);
        if (last == '#') {
            //唤起
            if (onTextChangeListener != null)
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
            if (onTextChangeListener != null)
                onTextChangeListener.inputNoTopic();
            return;
        }
        if (lastTopic == -1) {
            //说明没有topic话题
            if (onTextChangeListener != null)
                onTextChangeListener.inputNoTopic();
            return;
        }
        if (selStart <= lastTopic) {
            //说明已经在#前面了
            if (onTextChangeListener != null)
                onTextChangeListener.inputNoTopic();
            return;
        }
        cutStr = cutStr.substring(lastTopic);
        Matcher matcher = patternTopic.matcher(cutStr);
        if (matcher.find()) {
            //找到符合规则的
            if (matcher.end() != cutStr.length()) {
                //长度不一致，说明中间有符号阻断了
                if (onTextChangeListener != null)
                    onTextChangeListener.inputNoTopic();
                return;
            }
            if (onTextChangeListener != null)
                onTextChangeListener.inputTopic(cutStr);
        }
    }

    /**
     * 提交@XXX
     *
     * @param atStr
     */
    public void setAtStr(String atStr, long id) {
        if (!isAtEnable){
            append(atStr);
            return;
        }
        //提交了一个热点话题，理所应当从当前#开始网后面替换topicStr的文字内容
        for (VideoPublishEditBean bean : videoAtEditBeans) {
            if (bean.getId() == id) {
                //不能@同一个人
                return;
            }
        }
        String currentStr = getText().toString();

        //光标位置
        int selStart = getSelectionStart();
        String cutStr = currentStr.substring(0, selStart);
        int textLast = cutStr.lastIndexOf("@");
        if (currentStr.length() + atStr.length() > inputMax) {
            //超过最大输入限制
            if (onTextChangeListener != null)
                onTextChangeListener.maxInput();
            return;
        }
        if (textLast == -1)
            return;
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
        bean.setId(id);
        bean.setStrLength(getTextLengthAfter);
        if (bean.getEndPoint() > inputMax) {
            bean.setEndPoint(inputMax);
        }
        videoAtEditBeans.add(bean);
        //TODO @功能添加字体颜色未改变
        getText().replace(textLast, currentStr.length(), atStr);
        addBlackKey();
    }

    /**
     * 删除最后一个文字
     */
    public void delLastChar() {
        char last = getText().charAt(getText().length() - 1);
        if (last == '@' || last == '#')
            getText().delete(length() - 1, length());
    }

    /**
     * 添加一个空格键以锁定光标位置
     */
    public void addBlackKey() {
        append(" ");
    }

    /**
     * 提交#XXX
     *
     * @param topicStr
     */
    public void setTopicStr(String topicStr, int id) {
        if (!isTopicEnable){
            append(topicStr);
            return;
        }
        //提交了一个热点话题，理所应当从当前#开始网后面替换topicStr的文字内容
        String currentStr = getText().toString();

        //光标位置
        int selStart = getSelectionStart();
        String cutStr = currentStr.substring(0, selStart);
        int textLast = cutStr.lastIndexOf("#");

        if (currentStr.length() + topicStr.length() > inputMax) {
            //超过最大输入限制
            return;
        }

        if (textLast == -1)
            return;
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
        addBlackKey();
    }

    private void watchText(Editable text, int start, int lengthBefore, int lengthAfter) {
        //        LogUtils.e("VideoPublishEditTextView", "text = " + text.toString() + "\tstart =\t"+start+ "\tlengthBefore =\t"+lengthBefore+ "\tlengthAfter =\t"+lengthAfter);
        if (isTopicEnable)
        checkTopic(text, start, lengthBefore, lengthAfter);
        if (isAtEnable)
        checkFriend(text, start, lengthBefore, lengthAfter);
        if (isTopicEnable || isAtEnable) {
            if (prepareTopic) {
                changeTextColor(videoPublishEditBeans, text, colorTopic, start, lengthBefore, lengthAfter);
            }
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
                if (i > videoAtEditBeans.size() - 1)
                    break;
                int s = matcher.start();
                VideoPublishEditBean videoAtEditBean = videoAtEditBeans.get(i);
                String cutStr = text.toString().substring(s, s + videoAtEditBean.getStrLength());
                if (!videoAtEditBean.getContent().trim().equals(cutStr.trim())) {
                    continue;
                }
                videoAtEditBean.setStartPoint(s);
                videoAtEditBean.setEndPoint(s + videoAtEditBean.getStrLength());
                i++;
            }
            for (VideoPublishEditBean bean : videoAtEditBeans) {
                videoPublishEditBeans.add(bean);
            }
        }
    }

    private void changeTextColor(List<VideoPublishEditBean> videoPublishEditBeans, Editable text, int color, int lengtStart, int lengthBefore, int lengthAfter) {
        if (isDel)return;
        SpannableStringBuilder spanColor = new SpannableStringBuilder(text);
        spanColor.setSpan(new ForegroundColorSpan(colorNormal), 0, text.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        //正常字体
        spanColor.setSpan(new StyleSpan(textStyle[2]), 0, text.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        int length = text.length();
        for (VideoPublishEditBean editBean : videoPublishEditBeans) {
            if (editBean.getStartPoint() >= length || editBean.getEndPoint() > length) {
                //说明已经删除了文字，bean还未删除
                continue;
            }
            if (editBean.isBlock()) {
                //正在操作
                spanColor.setSpan(new BackgroundColorSpan(colorBlock), editBean.getStartPoint(), editBean.getEndPoint(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            } else {
                spanColor.setSpan(new BackgroundColorSpan(0), editBean.getStartPoint(), editBean.getEndPoint(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            }
            //话题和AT加粗
            spanColor.setSpan(new StyleSpan(textStyle[1]), editBean.getStartPoint(), editBean.getEndPoint(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            spanColor.setSpan(new ForegroundColorSpan(color), editBean.getStartPoint(), editBean.getEndPoint(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        //超费时间，卡UI

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

    private void changeTextAtBlockColor(int blockColor, int atColor) {
        VideoPublishEditBean bean = videoAtEditBeans.get(currentDelAtIndex);
        SpannableStringBuilder spanColor = new SpannableStringBuilder(bean.getContent());
        if (isOptionDelAt) {
            //正在操作
            spanColor.setSpan(new ForegroundColorSpan(atColor), 0, bean.getStrLength(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            spanColor.setSpan(new BackgroundColorSpan(blockColor), 0, bean.getStrLength(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        } else {
            //全部换回默认@色
            spanColor.setSpan(new BackgroundColorSpan(0), 0, bean.getStrLength(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            spanColor.setSpan(new ForegroundColorSpan(atColor), 0, bean.getStrLength(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        prepareTopic = false;
        getText().replace(bean.getStartPoint(), bean.getEndPoint(), spanColor);
    }

    /**
     * 获取topic列表
     *
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
            bean.setStartPoint(matcher.start());
            bean.setEndPoint(matcher.end());
            bean.setContent(str);
            bean.setTextType(VideoPublishEditBean.TOPIC_TYPE);
            videoPublishEditBeans.add(bean);
        }
        return videoPublishEditBeans;
    }

    /**
     * 获取@列表
     *
     * @return
     */
    public List<VideoPublishEditBean> getAtList() {
        return videoAtEditBeans;
    }

    /**
     * 获取发布用的文字
     */
    public PublishDesBean getDesStr() {
        String current = getText().toString();
        List<VideoPublishEditBean> beans = getTopicList();
        PublishDesBean publishDesBean = new PublishDesBean();
        publishDesBean.content = current;
        List<PublishFocusItemBean> focusItemBeans = new ArrayList<>();
        for (VideoPublishEditBean editBean : beans) {
            PublishFocusItemBean bean = new PublishFocusItemBean();
            bean.start = editBean.getStartPoint();
            bean.end = editBean.getEndPoint();
            bean.type = editBean.getTextType();
            focusItemBeans.add(bean);
        }
        List<VideoPublishEditBean> beans1 = getAtList();
        for (VideoPublishEditBean editBean : beans1) {
            PublishFocusItemBean bean = new PublishFocusItemBean();
            bean.start = editBean.getStartPoint();
            bean.end = editBean.getEndPoint();
            bean.type = editBean.getTextType();
            bean.uid = editBean.getId();
            focusItemBeans.add(bean);
        }
        publishDesBean.publishFocusItemBeans = focusItemBeans;
        return publishDesBean;
    }

    public void initDesStr(PublishDesBean publishDesBean) {
        String content = publishDesBean.content;
        if (publishDesBean.publishFocusItemBeans != null) {
            for (PublishFocusItemBean bean : publishDesBean.publishFocusItemBeans) {
                int s = bean.start;
                int e = bean.end;
                int t = bean.type;
                if (t == VideoPublishEditBean.AT_TYPE) {
                    VideoPublishEditBean editBean = new VideoPublishEditBean();
                    editBean.setStartPoint(s);
                    editBean.setEndPoint(e);
                    editBean.setStrLength(e - s);
                    editBean.setTextType(t);
                    editBean.setContent(content.substring(s, e));
                    editBean.setId(bean.uid);
                    videoAtEditBeans.add(editBean);
                }
            }
        }
        setText(content);
    }

    public interface OnTextChangeListener {
        void inputTopic(String str);

        void inputNoTopic();

        void inputAt(String str);

        void inputNoAt();

        void maxInput();
    }
}
