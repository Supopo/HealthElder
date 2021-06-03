package com.xaqinren.healthyelders.moduleZhiBo.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.apiserver.UserRepository;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.bean.PayRes;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.databinding.ActivityPayBinding;
import com.xaqinren.healthyelders.global.AppApplication;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.moduleHome.bean.MenuBean;
import com.xaqinren.healthyelders.moduleLogin.bean.UserInfoBean;
import com.xaqinren.healthyelders.moduleZhiBo.adapter.PayTypeAdapter;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.bus.RxBus;

/**
 * Created by Lee. on 2021/6/2.
 * 支付弹窗页面
 */
public class PayActivity extends BaseActivity<ActivityPayBinding, BaseViewModel> {

    private double czNum;
    private double yeNum;
    private int beiLv;
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
            beiLv = getIntent().getExtras().getInt("beiLv", 0);
        }
    }

    @Override
    public void initData() {
        super.initData();
        rlTitle.setVisibility(View.GONE);
        setWindow();
        initView();
        binding.tvCzNum.setText("" + czNum);
        binding.tvTips.setText("金币 " + beiLv * czNum);
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


        binding.rlPay.setOnClickListener(lis -> {
            String payType = "";

            if (selectPos == 0) {
                payType = "WE_CHAT_PAY";
            } else if (selectPos == 1) {
                payType = "WALLET_PAY";
            }

            UserRepository.getInstance().toPay(wxPayJson, "POINT_RECHARGE", "APP_PAY", payType, czNum);
        });
    }

    private int selectPos;
    private int lastPos;

    private MutableLiveData<UserInfoBean> userBanlance = new MutableLiveData<>();
    private MutableLiveData<String> wxPayJson = new MutableLiveData<>();

    public void getBanlance() {
        UserRepository.getInstance().getBanlance(userBanlance);
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        RxBus.getDefault().toObservable(EventBean.class).subscribe(event -> {
            if (event != null) {
                if (event.msgId == CodeTable.WX_PAY_CODE && event.msgType == 1) {
                            finish();
                }
            }

        });

        userBanlance.observe(this, datas -> {
            if (datas != null) {
                UserInfoMgr.getInstance().getUserInfo().setWallAccountBalance(datas.getWallAccountBalance());
                UserInfoMgr.getInstance().getUserInfo().setPointAccountBalance(datas.getPointAccountBalance());
                yeNum = datas.getWallAccountBalance();
                payTypeAdapter.getData().get(1).subMenuName = "(剩余：" + yeNum + ")";
                payTypeAdapter.notifyItemChanged(1, 99);
            }
        });
        wxPayJson.observe(this, data -> {
            if (data != null) {
                PayRes payRes = JSON.parseObject(data, PayRes.class);
                toWxPay(payRes);
                Log.e("----------", payRes.toString());
            }
        });
    }

    public void toWxPay(PayRes payRes) {
        if (!AppApplication.mWXapi.isWXAppInstalled()) {
            Toast.makeText(AppApplication.mContext, "您还未安装微信客户端", Toast.LENGTH_SHORT).show();
            return;
        }
        PayReq payReq = new PayReq();
        payReq.appId = payRes.appId;
        payReq.partnerId = payRes.partnerId;
        payReq.prepayId = payRes.prepayId;
        payReq.packageValue = payRes.packageValue;
        payReq.nonceStr = payRes.nonceStr;
        payReq.timeStamp = payRes.timeStamp;
        payReq.sign = payRes.sign;
        AppApplication.mWXapi.sendReq(payReq);
    }

    @Override
    public void finish() {
        super.finish();
        //更改关闭页面动画
        overridePendingTransition(R.anim.pop_bottom_2enter, R.anim.pop_bottom_2exit);
    }

}
