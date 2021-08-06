package com.xaqinren.healthyelders.moduleZhiBo.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.tencent.qcloud.tim.uikit.component.face.Emoji;
import com.tencent.qcloud.tim.uikit.component.face.FaceFragment;
import com.tencent.qcloud.tim.uikit.component.face.FaceManager;
import com.tencent.qcloud.tim.uikit.utils.TUIKitLog;
import com.tencent.qcloud.tim.uikit.utils.ToastUtil;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.LiveConstants;
import com.xaqinren.healthyelders.utils.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import me.goldze.mvvmhabit.bus.RxBus;

/**
 * Created by Lee on 2021/4/2.
 * 直播间输入框 Dialog
 */
public class ZBEditTextDialogActivity extends Activity {

    private EditText etView;
    private LinearLayout llInput;
    private ImageView ivSend;
    private ImageView ivFace;
    private RelativeLayout rlView;
    private FragmentManager mFragmentManager;
    private FaceFragment mFaceFragment;
    private RelativeLayout moreGroups;
    private int type;//1-直播间 设置屏蔽词弹窗


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getIntent().getIntExtra("type", 0);

        setContentView(R.layout.activity_input_text);
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

    public void hideSoftInput() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etView.getWindowToken(), 0);
        etView.clearFocus();
        moreGroups.setVisibility(View.GONE);
    }

    private void showFaceViewGroup() {
        if (mFragmentManager == null) {
            mFragmentManager = this.getFragmentManager();
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
        mFragmentManager.beginTransaction().replace(R.id.more_groups, mFaceFragment).commitAllowingStateLoss();

    }

    private List<Integer> highs = new ArrayList<>();

    private void initView() {
        rlView = findViewById(R.id.rl_view);
        ivSend = findViewById(R.id.iv_send);
        ivFace = findViewById(R.id.iv_face);
        etView = findViewById(R.id.et_input_message);
        llInput = findViewById(R.id.ll_input);
        moreGroups = findViewById(R.id.more_groups);


        if (getIntent().getExtras() != null) {
            String content = getIntent().getExtras().getString("content");
            if (!TextUtils.isEmpty(content)) {
                etView.setText(content);
                etView.setSelection(content.length());//将光标移至文字末尾
                //加载表情
                FaceManager.handlerEmojiText(etView, content, false);
            }
        }

        if (type == 1) {
            ivFace.setVisibility(View.GONE);
            etView.setHint("请输入需要屏蔽的词语");
        }

        //弹出键盘
        showSoftInput(this, etView);

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
        etView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case KeyEvent.KEYCODE_ENDCALL:
                    case KeyEvent.KEYCODE_ENTER:
                        if (etView.getText().length() > 0) {
                            finish();
                        } else {
                            Toast.makeText(ZBEditTextDialogActivity.this, "input can not be empty!", Toast.LENGTH_LONG).show();
                        }
                        return true;
                    case KeyEvent.KEYCODE_BACK:
                        finish();
                        return false;
                    default:
                        return false;
                }
            }
        });


        //计算键盘弹起高度1
        rlView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                Rect r = new Rect();
                //获取当前界面可视部分
                getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
                //获取可见屏幕的高度
                int screenHeight = getWindow().getDecorView().getRootView().getHeight();

                //用map存为了防止重复记录
                highss.put(screenHeight, 1);

                //此处就是用来获取键盘的高度的， 在键盘没有弹出的时候 此高度为0 键盘弹出的时候为一个正数
                int heightDifference = screenHeight - r.bottom;
                //目前不适用了

                if (highss.keySet().size() > 1) {

                    for (Integer integer : highss.keySet()) {
                        highs.add(integer);
                    }
                    heightDifference = highs.get(0) - highs.get(1);
                    //键盘弹出去后通知页面调整消息列表位置
                    if (type == 0) {
                        RxBus.getDefault().post(new EventBean(LiveConstants.SHOW_ET, Math.abs(heightDifference) + llInput.getHeight()));
                    }
                    highs.clear();
                    highss.clear();
                }

            }
        });

        llInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ivSend.setOnClickListener(lis -> {

            if (TextUtils.isEmpty(etView.getText().toString())) {
                ToastUtil.toastShortMessage("请输入内容");
            } else {
                if (type == 0) {
                    RxBus.getDefault().post(new EventBean(LiveConstants.SEND_MSG, etView.getText().toString(), System.currentTimeMillis()));
                    hasSend = true;
                } else if (type == 1) {
                    RxBus.getDefault().post(new EventBean(LiveConstants.SEND_WORD, etView.getText().toString()));
                }
                finish();
            }

        });
        rlView.setOnClickListener(lis -> {
            etView.setFocusable(false);
            etView.setFocusableInTouchMode(false);
            finish();
        });
        ivFace.setOnClickListener(lis -> {
            showFaceViewGroup();
        });
    }

    private Map<Integer, Integer> highss = new HashMap<>();

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

    private boolean hasSend;


    @Override
    public void finish() {
        super.finish();
        if (type == 0) {
            //通知前一页消息列表位置还原
            RxBus.getDefault().post(new EventBean(LiveConstants.DISMISS_ET, 0, hasSend ? "" : etView.getText().toString()));
        }
        //关闭键盘
        closeKeybord(this);
        //更改关闭页面动画
        overridePendingTransition(R.anim.pop_bottom_2enter, R.anim.pop_bottom_2exit);
    }
}
