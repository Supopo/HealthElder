package com.xaqinren.healthyelders.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.xaqinren.healthyelders.R;

public class SearchEditText extends LinearLayout implements TextWatcher {
    private EditText editText;
    private ImageView searchIv,closeIv;
    private OnTextChangeListener onTextChangeListener;
    private int userCount = 0;

    public void setOnTextChangeListener(OnTextChangeListener onTextChangeListener) {
        this.onTextChangeListener = onTextChangeListener;
    }

    public SearchEditText(@NonNull Context context) {
        super(context);
        initView();
    }

    public SearchEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public SearchEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.layout_search_edit, this);
        editText = findViewById(R.id.search_edit);
        searchIv = findViewById(R.id.search_iv);
        closeIv = findViewById(R.id.close_iv);
        editText.addTextChangedListener(this);
        closeIv.setOnClickListener(view -> editText.setText(null));
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH){
                    //搜索
                }
                return false;
            }
        });
        editText.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.KEYCODE_BACK) {
                    if (editText.getText().length() == 0) {
                        //删除选中的用户
                        onTextChangeListener.onDeleteUser();
                    }
                }
                return false;
            }
        });
    }

    public void addUser() {
        editText.setText(null);
        searchIv.setVisibility(View.GONE);
        userCount++;
    }
    public void removeUser() {
        userCount--;
        if (userCount == 0 ) searchIv.setVisibility(View.VISIBLE);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        onTextChangeListener.onTextChange(editable.toString());
        if (editable.length() > 0) {
            closeIv.setVisibility(View.VISIBLE);
        }else{
            closeIv.setVisibility(View.GONE);
        }
    }

    public interface OnTextChangeListener{
        void onTextChange(String text);
        void onDeleteUser();
    }

}
