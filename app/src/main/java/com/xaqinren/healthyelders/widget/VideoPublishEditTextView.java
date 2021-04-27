package com.xaqinren.healthyelders.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;

import com.xaqinren.healthyelders.utils.LogUtils;

public class VideoPublishEditTextView extends AppCompatEditText implements TextWatcher {

    private char topicSym = '#';
    private char friendSym = '@';



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
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        watchText(charSequence, i, i1, i2);
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    private void watchText(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        LogUtils.e("VideoPublishEditTextView", "text = " + text.toString() + "\tstart =\t"+start+ "\tlengthBefore =\t"+lengthBefore+ "\tlengthAfter =\t"+lengthAfter);
        checkTopic();
        checkFriend();
    }

    private void checkTopic() {

    }

    private void checkFriend() {

    }

}
