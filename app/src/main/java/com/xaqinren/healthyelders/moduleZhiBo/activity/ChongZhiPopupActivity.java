package com.xaqinren.healthyelders.moduleZhiBo.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivityPopChongzhiBinding;
import com.xaqinren.healthyelders.moduleZhiBo.adapter.ChongZhiKeyBordAdapter;
import com.xaqinren.healthyelders.moduleHome.bean.MenuBean;
import com.xaqinren.healthyelders.moduleZhiBo.adapter.ChongZhiNumAdapter;
import com.xaqinren.healthyelders.widget.SpeacesItemDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Lee on 2021/4/2.
 * 充值弹窗页面
 */
public class ChongZhiPopupActivity extends Activity {

    private ActivityPopChongzhiBinding binding;
    private ChongZhiKeyBordAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();

        setContentView(R.layout.activity_pop_chongzhi);
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

    String[] czNums = {"50", "200", "500", "1000"};
    String[] contents = {"1", "2", "3",
            "-", "4", "5", "6", "充值", "7", "8", "9", "0"};

    private void initView() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pop_chongzhi);

        initCZList();
        initKeyBord();
    }

    private void initCZList() {
        ChongZhiNumAdapter numAdapter = new ChongZhiNumAdapter(R.layout.item_chongzhi_list);
        binding.rvNum.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        binding.rvNum.setAdapter(numAdapter);
        List<MenuBean> temp = new ArrayList<>();
        for (int i = 0; i < czNums.length; i++) {
            MenuBean menuBean = new MenuBean(czNums[i]);
            temp.add(menuBean);
        }
        numAdapter.setNewInstance(temp);
    }

    private void initKeyBord() {
        mAdapter = new ChongZhiKeyBordAdapter(R.layout.item_chongzhi_btn);

        //瀑布流
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);
        //防止Item切换
        manager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        binding.rvContent.setLayoutManager(manager);
        binding.rvContent.setAdapter(mAdapter);
        binding.rvContent.addItemDecoration(new SpeacesItemDecoration(this, 8, 4, true));

        List<MenuBean> temp = new ArrayList<>();
        for (int i = 0; i < contents.length; i++) {
            MenuBean menuBean = new MenuBean(contents[i]);
            temp.add(menuBean);
        }
        mAdapter.setNewInstance(temp);
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
