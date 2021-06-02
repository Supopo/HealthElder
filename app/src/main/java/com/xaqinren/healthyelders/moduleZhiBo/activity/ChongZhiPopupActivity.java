package com.xaqinren.healthyelders.moduleZhiBo.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
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

    List<String> etTextList = new ArrayList<>();
    String[] czNums = {"50", "200", "500", "1000"};
    String[] contents = {"1", "2", "3",
            "-", "4", "5", "6", "充值", "7", "8", "9", "0"};

    private void initView() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pop_chongzhi);
        binding.etContent.setInputType(InputType.TYPE_NULL);
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
        numAdapter.setOnItemClickListener(((adapter, view, position) -> {
            etTextList.clear();
            etTextList.add(czNums[position]);
            showEditText();
        }));
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

        mAdapter.setOnItemClickListener(((adapter, view, position) -> {
            if (position == 3) {
                //删除
                if (etTextList.size() > 0) {
                    etTextList.remove(etTextList.size() - 1);
                    showEditText();
                }
            } else if (position == 7) {
                //充值
            } else {
                if (etTextList.size() == 1 && etTextList.get(0).equals("0")) {
                    etTextList.clear();
                }
                etTextList.add(contents[position]);
                showEditText();
            }
        }));
    }

    private void showEditText() {
        String et = "";
        for (String s : etTextList) {
            et = et + s;
        }
        binding.etContent.setText(et);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void finish() {
        super.finish();
        //更改关闭页面动画
        overridePendingTransition(R.anim.pop_bottom_2enter, R.anim.pop_bottom_2exit);
    }
}
