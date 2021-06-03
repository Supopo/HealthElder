package com.xaqinren.healthyelders.moduleZhiBo.activity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.apiserver.UserRepository;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.databinding.ActivityPayBinding;
import com.xaqinren.healthyelders.moduleHome.bean.MenuBean;
import com.xaqinren.healthyelders.moduleLogin.bean.UserInfoBean;
import com.xaqinren.healthyelders.moduleZhiBo.adapter.PayTypeAdapter;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.base.BaseViewModel;

/**
 * Created by Lee. on 2021/6/2.
 * 支付弹窗页面
 */
public class PayActivity extends BaseActivity<ActivityPayBinding, BaseViewModel> {

    private double czNum;
    private double yeNum;
    private PayTypeAdapter payTypeAdapter;


    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_pay;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initParam() {
        super.initParam();
        if (getIntent().getExtras() != null) {
            czNum = getIntent().getExtras().getDouble("czNum", 0);
            yeNum = getIntent().getExtras().getDouble("yeNum", 0);
        }
    }

    @Override
    public void initData() {
        super.initData();
        rlTitle.setVisibility(View.GONE);
        setWindow();
        initView();
        binding.tvCzNum.setText("" + czNum);
        binding.tvTips.setText(czNum * 10 + "个金币");
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

    String[] menuNames = {"微信支付", "我的零钱"};
    int[] menuRes = {R.mipmap.zhifu_wechat, R.mipmap.zhifu_lingq};

    private void initView() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
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
        getBanlance();
    }

    private int selectPos;
    private int lastPos;

    private MutableLiveData<UserInfoBean> userBanlance = new MutableLiveData<>();

    public void getBanlance() {
        UserRepository.getInstance().getBanlance(userBanlance);
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        userBanlance.observe(this, datas -> {
            if (datas != null) {
                UserInfoMgr.getInstance().getUserInfo().setWallAccountBalance(datas.getWallAccountBalance());
                UserInfoMgr.getInstance().getUserInfo().setPointAccountBalance(datas.getPointAccountBalance());
                yeNum = datas.getWallAccountBalance();
                payTypeAdapter.getData().get(1).subMenuName ="(剩余：" + yeNum + ")";
                payTypeAdapter.notifyItemChanged(1,99);
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        //更改关闭页面动画
        overridePendingTransition(R.anim.pop_bottom_2enter, R.anim.pop_bottom_2exit);
    }
}
