package com.xaqinren.healthyelders.moduleZhiBo.activity;

import android.app.Activity;
import android.content.Context;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.tencent.qcloud.tim.uikit.component.face.Emoji;
import com.tencent.qcloud.tim.uikit.component.face.FaceFragment;
import com.tencent.qcloud.tim.uikit.component.face.FaceManager;
import com.tencent.qcloud.tim.uikit.utils.ToastUtil;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.utils.LogUtils;

import java.util.Timer;
import java.util.TimerTask;

import me.goldze.mvvmhabit.bus.RxBus;

/**
 * Created by Lee on 2021/4/2.
 * 视频输入框 Dialog
 */
public class VideoEditTextDialogActivity extends AppCompatActivity {

    private String hint;
    private String type;
    private int pos;
    private EditText etView;
    private String TAG = getClass().getSimpleName();
    private RelativeLayout rlView;
    private int openType;
    private boolean hasSend;
    private String commentText;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            hint = extras.getString("hint");
            commentText = extras.getString("commentText");
            type = extras.getString("type");
            pos = extras.getInt("pos");
            openType = extras.getInt("openType", 0);
        }


        setContentView(R.layout.activity_input_video);
        setWindow();
        initView();
    }


    private void setWindow() {
        //窗口对齐屏幕宽度
        Window win = this.getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;//设置对话框置顶显示
        win.setAttributes(lp);
    }

    private void initView() {

        rlView = findViewById(R.id.rl_view);
        etView = findViewById(R.id.et_input_message);
        moreGroups = findViewById(R.id.more_groups);
        if (!TextUtils.isEmpty(hint)) {
            etView.setHint(hint);
        }

        if (!TextUtils.isEmpty(commentText)) {
            etView.setText(commentText);
            //设置光标
            etView.setSelection(commentText.length());
            //加载表情
            FaceManager.handlerEmojiText(etView, commentText, false);
        }

        ImageView ivPublish = findViewById(R.id.iv_send);
        ImageView ivFace = findViewById(R.id.iv_face);

        if (openType == 1) {
            showFaceViewGroup();
        } else {
            //弹出键盘
            showSoftInput(this, etView);
        }

        setEditTextInhibitInputSpace();
        ivPublish.setOnClickListener(lis -> {
            //发送消息通知发送
            if (TextUtils.isEmpty(etView.getText().toString().trim()
            )) {
                ToastUtil.toastShortMessage("请输入内容");
                return;
            }
            RxBus.getDefault().post(new EventBean(CodeTable.VIDEO_SEND_COMMENT, etView.getText().toString().trim(), type, pos));
            hasSend = true;
            finish();
        });
        ivFace.setOnClickListener(lis -> {
            showFaceViewGroup();
        });
        etView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (moreGroups.getVisibility() == View.VISIBLE) {
                        moreGroups.setVisibility(View.GONE);
                    }
                }
            }
        });
        rlView.setOnClickListener(lis -> {
            etView.setFocusable(false);
            etView.setFocusableInTouchMode(false);
            finish();
        });
    }

    //过滤回车 禁止回车
    public void setEditTextInhibitInputSpace() {
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.equals("\n"))
                    return "";
                else
                    return null;
            }
        };
        etView.setFilters(new InputFilter[]{filter});
    }

    @Override
    protected void onStop() {
        super.onStop();
        RxBus.getDefault().post(new EventBean(CodeTable.VIDEO_SEND_COMMENT_OVER, hasSend ? "" : etView.getText().toString().trim(), type, pos));
    }

    private FragmentManager mFragmentManager;
    private FaceFragment mFaceFragment;
    private RelativeLayout moreGroups;

    public void hideSoftInput() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etView.getWindowToken(), 0);
        etView.clearFocus();
        moreGroups.setVisibility(View.GONE);
    }

    private void showFaceViewGroup() {
        if (mFragmentManager == null) {
            mFragmentManager = this.getSupportFragmentManager();
        }
        if (mFaceFragment == null) {
            mFaceFragment = new FaceFragment();
        }
        hideSoftInput();
        moreGroups.setVisibility(View.VISIBLE);
        //        先不要获取焦点 点击键盘再获取焦点 这样方便隐藏表情列表
        //        etView.requestFocus();
        mFaceFragment.setListener(new FaceFragment.OnEmojiClickListener() {
            @Override
            public void onEmojiDelete() {
                int index = etView.getSelectionStart();
                Editable editable = etView.getText();
                boolean isFace = false;
                if (index <= 0) {
                    return;
                }
                if (editable.charAt(index - 1) == ']') {
                    for (int i = index - 2; i >= 0; i--) {
                        if (editable.charAt(i) == '[') {
                            String faceChar = editable.subSequence(i, index).toString();
                            if (FaceManager.isFaceChar(faceChar)) {
                                editable.delete(i, index);
                                isFace = true;
                            }
                            break;
                        }
                    }
                }
                if (!isFace) {
                    editable.delete(index - 1, index);
                }
            }

            @Override
            public void onEmojiClick(Emoji emoji) {
                int index = etView.getSelectionStart();
                Editable editable = etView.getText();
                editable.insert(index, emoji.getFilter());
                FaceManager.handlerEmojiText(etView, editable.toString(), true);
            }

            @Override
            public void onCustomFaceClick(int groupIndex, Emoji emoji) {

            }
        });
        //表情展示
        mFragmentManager.beginTransaction().replace(R.id.more_groups, mFaceFragment).commitAllowingStateLoss();

    }


    /**
     * 自动弹软键盘
     *
     * @param context
     * @param et
     */
    public void showSoftInput(final Context context, final EditText et) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        et.setFocusable(true);
                        et.setFocusableInTouchMode(true);
                        //请求获得焦点
                        et.requestFocus();
                        //调用系统输入法
                        InputMethodManager inputManager = (InputMethodManager) et
                                .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputManager.showSoftInput(et, 0);
                    }
                });
            }
        }, 200);

    }

    /**
     * 自动关闭软键盘
     *
     * @param activity
     */
    public void closeKeybord(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void finish() {
        super.finish();
        //关闭键盘
        closeKeybord(this);
        //更改关闭页面动画
        overridePendingTransition(R.anim.pop_bottom_2enter, R.anim.pop_bottom_2exit);
    }
}
