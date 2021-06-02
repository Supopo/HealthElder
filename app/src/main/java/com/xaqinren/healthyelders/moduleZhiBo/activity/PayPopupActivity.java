package com.xaqinren.healthyelders.moduleZhiBo.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.databinding.ActivityPopPayBinding;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.moduleHome.bean.MenuBean;
import com.xaqinren.healthyelders.moduleZhiBo.adapter.PayTypeAdapter;
import com.xaqinren.healthyelders.widget.VideoPublishEditTextView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import me.goldze.mvvmhabit.bus.RxBus;

/**
 * Created by Lee on 2021/4/2.
 * 支付弹窗页面
 */
public class PayPopupActivity extends Activity {

    private ActivityPopPayBinding binding;
    private PayTypeAdapter payTypeAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();

        setContentView(R.layout.activity_pop_pay);
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

    String[] menuDes = {"", "(剩余：0.00)"};
    String[] menuNames = {"微信支付", "我的零钱"};
    int[] menuRes = {R.mipmap.zhifu_wechat, R.mipmap.zhifu_lingq};

    private void initView() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pop_pay);
        binding.ivClose.setOnClickListener(lis -> {
            finish();
        });
        payTypeAdapter = new PayTypeAdapter(R.layout.item_pay_type);
        binding.rvPayType.setLayoutManager(new LinearLayoutManager(this));
        binding.rvPayType.setAdapter(payTypeAdapter);

        List<MenuBean> menuBeanList = new ArrayList<>();
        for (int i = 0; i < menuNames.length; i++) {
            MenuBean menuBean = new MenuBean();
            if (i == 0) {
                menuBean.isSelect = true;
            }
            menuBean.id = String.valueOf(i);
            menuBean.menuName = menuNames[i];
            menuBean.menuRes = menuRes[i];
            menuBean.subMenuName = menuDes[i];
            menuBeanList.add(menuBean);
        }
        payTypeAdapter.setNewInstance(menuBeanList);

        payTypeAdapter.setOnItemClickListener(((adapter, view, position) -> {
            selectPos = position;
            payTypeAdapter.getData().get(lastPos).isSelect = false;
            payTypeAdapter.getData().get(selectPos).isSelect = true;
            payTypeAdapter.notifyItemChanged(lastPos, 99);
            payTypeAdapter.notifyItemChanged(selectPos, 99);
            lastPos = position;
        }));
    }

    private int selectPos;
    private int lastPos;

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
