package com.xaqinren.healthyelders.moduleZhiBo.activity;

import android.app.Activity;
import android.content.Intent;
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
import com.tencent.qcloud.tim.uikit.utils.ScreenUtil;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.apiserver.UserRepository;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.bean.PayRes;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.databinding.ActivityPayBinding;
import com.xaqinren.healthyelders.global.AppApplication;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleHome.bean.MenuBean;
import com.xaqinren.healthyelders.moduleLogin.bean.UserInfoBean;
import com.xaqinren.healthyelders.moduleMine.bean.WalletBean;
import com.xaqinren.healthyelders.moduleZhiBo.adapter.PayTypeAdapter;
import com.xaqinren.healthyelders.utils.MScreenUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.utils.SPUtils;
import me.goldze.mvvmhabit.utils.StringUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;

import static android.view.WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

/**
 * Created by Lee. on 2021/6/2.
 * 支付弹窗页面
 */
public class PayActivity extends BaseActivity<ActivityPayBinding, BaseViewModel> {

    private double czNum;
    private double yeNum;
    private int beiLv;
    private PayTypeAdapter payTypeAdapter;
    private Disposable subscribe;
    private String orderNo;
    private String orderType;
    private String payWay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int theme = getIntent().getIntExtra("theme", R.style.EditDialogStyleEx);
        setTheme(theme);
        super.onCreate(savedInstanceState);
    }

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
            czNum = getIntent().getExtras().getDouble("czNum", 0);//充值，付款
            yeNum = getIntent().getExtras().getInt("yeNum", 0);//余额，我的零钱
            beiLv = getIntent().getExtras().getInt("beiLv", 0);//倍率
            orderNo = getIntent().getExtras().getString("orderNo", "");//订单编号
            orderType = getIntent().getExtras().getString("orderType", "");//订单类型
            payWay = getIntent().getExtras().getString("payWay", "app");//订单类型
        }
    }

    @Override
    public void initData() {
        super.initData();
        rlTitle.setVisibility(View.GONE);
        setWindow();
        initView();
        binding.tvCzNum.setText("" + czNum);
        if(StringUtils.isEmpty(orderNo)){
            binding.tvTips.setText("金币 " + beiLv * czNum);
        }
        SPUtils.getInstance().put(Constant.PAY_WAY,payWay);
    }

    private void setWindow() {
        //窗口对齐屏幕宽度
        int theme = getIntent().getIntExtra("theme", R.style.EditDialogStyleEx);
        Window win = this.getWindow();
//        if (AppApplication.get().isHasNavBar()) {
//            win.getDecorView().setPadding(0, 0, 0, MScreenUtil.getBottomStatusHeight(this));
//        }else
//            win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;//设置对话框置顶显示

        //lp.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        if (theme != R.style.EditDialogStyleEx) {
            lp.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        }else{
            lp.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        }

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
            if (StringUtils.isEmpty(orderType)) {
                ToastUtils.showShort("订单异常，请重新下单");
                return;
            }
            if (selectPos == 0) {
                payType = "WE_CHAT_PAY";
            } else if (selectPos == 1) {
                payType = "WALLET_PAY";
            }
            //COMMODITY_CONSUMPTION 购物消费  POINT_RECHARGE 积分充值
            UserRepository.getInstance().toPay(wxPayJson, orderType, "APP_PAY", payType, czNum, orderNo);
        });
    }

    private int selectPos;
    private int lastPos;

    private MutableLiveData<WalletBean> userBanlance = new MutableLiveData<>();
    private MutableLiveData<String> wxPayJson = new MutableLiveData<>();

    public void getBanlance() {
        UserRepository.getInstance().getWalletInfo(null, userBanlance);
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        subscribe = RxBus.getDefault().toObservable(EventBean.class).subscribe(event -> {
            if (event != null) {
                if (event.msgId == CodeTable.WX_PAY_CODE) {
//                    binding.loadingView.cancelAnimation();
//                    binding.loadingView.setVisibility(View.GONE);
                    if (event.msgType == 1) {
                        Intent intent = new Intent();
                        intent.putExtra("status", true);
                        setResult(Activity.RESULT_OK,intent);
                        finish();
                    } else {
                        //支付失败
                        if (orderNo != null) {
                            Intent intent = new Intent();
                            intent.putExtra("status", false);
                            intent.putExtra("msg", event.content);
                            setResult(Activity.RESULT_OK,intent);
                            finish();
                        }
                    }
                }
            }

        });

        userBanlance.observe(this, datas -> {
            if (datas != null) {
                yeNum = datas.getWalletAccount().getAccountBalance();
                UserInfoMgr.getInstance().getUserInfo().setAccountInfo(datas);
                payTypeAdapter.getData().get(1).subMenuName = "(剩余：" + yeNum + ")";
                payTypeAdapter.notifyItemChanged(1, 99);
            }
        });
        wxPayJson.observe(this, data -> {
            if (data != null) {
                PayRes payRes = JSON.parseObject(data, PayRes.class);
                toWxPay(payRes);
            }
        });
    }

    public void toWxPay(PayRes payRes) {
        if (!AppApplication.mWXapi.isWXAppInstalled()) {
            Toast.makeText(AppApplication.mContext, "您还未安装微信客户端", Toast.LENGTH_SHORT).show();
            return;
        }

//        binding.loadingView.setVisibility(View.VISIBLE);
//        binding.loadingView.playAnimation();

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subscribe != null) {
            subscribe.dispose();
        }
    }
}
