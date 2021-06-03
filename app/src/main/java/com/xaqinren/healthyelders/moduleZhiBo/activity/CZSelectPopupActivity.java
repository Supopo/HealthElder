package com.xaqinren.healthyelders.moduleZhiBo.activity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.GridLayoutManager;

import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.apiserver.UserRepository;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.databinding.ActivityPopCzSelectBinding;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.moduleHome.bean.MenuBean;
import com.xaqinren.healthyelders.moduleLogin.bean.UserInfoBean;
import com.xaqinren.healthyelders.moduleZhiBo.adapter.ChongZhiSelectAdapter;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ChongZhiListRes;
import com.xaqinren.healthyelders.widget.SpeacesItemDecoration;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.bus.RxBus;

/**
 * Created by Lee on 2021/4/2.
 * 充值选择弹窗页面
 */
public class CZSelectPopupActivity extends BaseActivity<ActivityPopCzSelectBinding, BaseViewModel> {

    private ChongZhiSelectAdapter selectAdapter;
    private Disposable subscribe;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_pop_cz_select;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
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
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;//设置对话框置顶显示
        win.setAttributes(lp);
    }

    private MutableLiveData<ChongZhiListRes> datas = new MutableLiveData<>();

    private int lastPos;
    private int nowPos;
    private double czNum;
    private double yeNum;
    private int jbyeNum;
    private int beiLv;

    private void initView() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        binding.rvContent.setLayoutManager(new GridLayoutManager(this, 3));
        selectAdapter = new ChongZhiSelectAdapter(R.layout.item_chongzhi_select);
        binding.rvContent.setAdapter(selectAdapter);
        binding.rvContent.addItemDecoration(new SpeacesItemDecoration(this, 8, 3));

        selectAdapter.setOnItemClickListener(((adapter, view, position) -> {
            nowPos = position;
            selectAdapter.getData().get(lastPos).isSelect = false;
            selectAdapter.getData().get(nowPos).isSelect = true;
            selectAdapter.notifyItemChanged(lastPos);
            selectAdapter.notifyItemChanged(nowPos);

            czNum = selectAdapter.getData().get(nowPos).rechargeAmount;
            if (selectAdapter.getData().get(nowPos).type != 1) {
                binding.btnCz.setText("立即充值" + selectAdapter.getData().get(nowPos).rechargeAmount + "元");
            } else {
                binding.btnCz.setText("立即充值");
            }
            lastPos = position;
        }));


        getBanlance();

        binding.btnCz.setOnClickListener(lis -> {
            Bundle bundle = new Bundle();
            bundle.putDouble("czNum", czNum);
            bundle.putDouble("jbyeNum", jbyeNum);
            bundle.putDouble("yeNum", yeNum);
            bundle.putInt("beiLv", beiLv);
            if (czNum == 0) {
                startActivity(CZInputPopupActivity.class, bundle);
            } else {
                startActivity(PayActivity.class, bundle);
            }
        });
    }

    private MutableLiveData<UserInfoBean> userBanlance = new MutableLiveData<>();

    public void getBanlance() {
        UserRepository.getInstance().chongzhiList(datas);
        UserRepository.getInstance().getBanlance(userBanlance);
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        datas.observe(this, datas -> {
            if (datas != null) {
                List<MenuBean> menuBeans = new ArrayList<>();
                if (datas.rechargeList.size() > 6) {
                    menuBeans.addAll(datas.rechargeList.subList(0, 6));
                } else {
                    menuBeans.addAll(datas.rechargeList);
                }

                MenuBean menuBean = new MenuBean();
                menuBean.type = 1;
                menuBeans.add(menuBean);

                menuBeans.get(0).isSelect = true;
                czNum = menuBeans.get(0).rechargeAmount;

                for (MenuBean bean : menuBeans) {
                    bean.beiLv = datas.rechargePercent;
                }

                beiLv = datas.rechargePercent;
                selectAdapter.setNewInstance(menuBeans);

            }
        });
        userBanlance.observe(this, datas -> {
            if (datas != null) {
                UserInfoMgr.getInstance().getUserInfo().setWallAccountBalance(datas.getWallAccountBalance());
                UserInfoMgr.getInstance().getUserInfo().setPointAccountBalance(datas.getPointAccountBalance());
                jbyeNum = datas.getPointAccountBalance();
                binding.tvTips.setText("余额：" + jbyeNum + "金币");
            }
        });
        subscribe = RxBus.getDefault().toObservable(EventBean.class).subscribe(event -> {
            if (event != null) {
                if (event.msgId == CodeTable.WX_PAY_CODE && event.msgType == 1) {
                    finish();
                }
            }

        });
    }

    @Override
    public void finish() {
        super.finish();
        //更改关闭页面动画
        overridePendingTransition(R.anim.pop_bottom_2enter, R.anim.pop_bottom_2exit);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subscribe != null) {
            subscribe.dispose();
        }
    }
}
