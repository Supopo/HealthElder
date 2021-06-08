package com.xaqinren.healthyelders.moduleZhiBo.activity;

import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.tencent.qcloud.ugckit.utils.ToastUtil;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.databinding.ActivityPopCzInputBinding;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.moduleHome.bean.MenuBean;
import com.xaqinren.healthyelders.moduleZhiBo.adapter.ChongZhiKeyBordAdapter;
import com.xaqinren.healthyelders.moduleZhiBo.adapter.ChongZhiNumAdapter;
import com.xaqinren.healthyelders.widget.SpeacesItemDecoration;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.bus.RxBus;

/**
 * Created by Lee on 2021/4/2.
 * 充值弹窗页面
 */
public class CZInputPopupActivity extends BaseActivity<ActivityPopCzInputBinding, BaseViewModel> {

    private ChongZhiKeyBordAdapter mAdapter;
    private Bundle extras;
    private Disposable subscribe;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_pop_cz_input;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initParam() {
        super.initParam();
        extras = getIntent().getExtras();
    }

    @Override
    public void initData() {
        super.initData();
        rlTitle.setVisibility(View.GONE);
        setWindow();
        initView();
    }

    private void setWindow() {
        //窗口对齐屏幕宽度
        Window win = this.getWindow();
//        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;//设置对话框置顶显示
        lp.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        win.setAttributes(lp);
    }

    List<String> etTextList = new ArrayList<>();
    String[] czNums = {"50", "200", "500", "1000"};
    String[] contents = {"1", "2", "3",
            "-", "4", "5", "6", "充值", "7", "8", "9", "0"};

    private void initView() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
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
                if (extras == null) {
                    extras = new Bundle();
                }

                if (Double.parseDouble(binding.etContent.getText().toString()) < 2) {
                    ToastUtil.toastShortMessage("充值金额最少2元");
                    return;
                }

                //充值
                extras.putDouble("czNum", Double.parseDouble(binding.etContent.getText().toString()));
                startActivity(PayActivity.class, extras);
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
    public void initViewObservable() {
        super.initViewObservable();
        subscribe = RxBus.getDefault().toObservable(EventBean.class).subscribe(event -> {
            if (event != null) {
                if (event.msgId == CodeTable.WX_PAY_CODE && event.msgType == 1) {
                    finish();
                }
            }

        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subscribe != null) {
            subscribe.dispose();
        }
    }


    @Override
    public void finish() {
        super.finish();
        //更改关闭页面动画
        overridePendingTransition(R.anim.pop_bottom_2enter, R.anim.pop_bottom_2exit);
    }
}
