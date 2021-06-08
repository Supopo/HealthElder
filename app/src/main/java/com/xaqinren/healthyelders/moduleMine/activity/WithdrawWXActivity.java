package com.xaqinren.healthyelders.moduleMine.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.lifecycle.Observer;

import com.tencent.qcloud.ugckit.utils.ToastUtil;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivityWithdrawBinding;
import com.xaqinren.healthyelders.databinding.ActivityWithdrawDetailBinding;
import com.xaqinren.healthyelders.moduleMine.bean.BillDetailBean;
import com.xaqinren.healthyelders.moduleMine.viewModel.WalletViewModel;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.base.BaseViewModel;

/**
 * 账单详情
 */
public class WithdrawWXActivity extends BaseActivity<ActivityWithdrawDetailBinding, WalletViewModel> {
    BillDetailBean billDetailBean;
    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_withdraw_detail;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        setTitle("提现详情");
        String id = getIntent().getStringExtra("id");
        showDialog();
        viewModel.getBillInfoDetail(id);
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.request.observe(this,bool->{dismissDialog();});
        viewModel.billDetail.observe(this, billDetailBean -> {
            //判断类型
            this.billDetailBean = billDetailBean;
            bindView();
        });
    }

    private void bindView() {

    }

    private void addNormalType(String time1,String time2,String order) {

        createValue("交易单号", /*"1029920202093939030328 "*/order, true);
    }


    private void addWxType(String time1,String time2,String order) {
        createValue("提现时间", /*"2021-04-29 15:07:02"*/time1, false);
        createValue("到账时间", /*"2021-04-29 15:07:02"*/time2, false);
        createValue("交易单号", /*"1029920202093939030328 "*/order, true);
    }

    private void addLiveType(String time1,String time2,String time3,String str1,String str2,String time4,String order) {
        createValue("开播时间", /*"2021-04-29 15:07:02"*/time1, false);
        createValue("下播时间", /*"2021-04-29 15:07:02"*/time2, false);
        createValue("开播时长", /*"1小时30分钟 "*/time3, false);
        createValue("分成比", /*"50% "*/str1, false);
        createValue("本次获得音浪", /*"2899% "*/str2, false);
        createLine();
        createValue("入账时间", /*"2021-04-29 15:07:02"*/time4, false);
        createValue("交易单号", /*"1029920202093939030328 "*/order, true);
    }

    private void addPayType(String title, String payWay,String time1 , String order) {
        createValue("商品订单", null, false);
        createGoodsValue(/*"鲨鱼菲特油醋汁2瓶装600ml日式脱脂水鬼蔬菜沙鲨鱼菲特油醋汁2瓶装600ml日式脱脂水鬼蔬菜沙"*/title);
        createLine();
        createValue("支付方式", /*"微信"*/payWay, false);
        createValue("支付时间", /*"2021-04-29 15:07:02"*/time1, false);
        createValue("交易单号", /*"1029920202093939030328 "*/order, true);
    }

    private void createLine() {
        View view = View.inflate(this, R.layout.line_margin_16, null);
        binding.rlContainer.addView(view);
    }

    private void createValue(String title,String value,boolean isCopy) {
        LinearLayout linearLayout = (LinearLayout) View.inflate(this, R.layout.item_amount_content, null);
        TextView titleV = linearLayout.findViewById(R.id.title);
        TextView valueV = linearLayout.findViewById(R.id.value);
        TextView copyV = linearLayout.findViewById(R.id.copy);
        titleV.setText(title);
        valueV.setText(value);
        if (isCopy) {
            copyV.setVisibility(View.VISIBLE);
        }
        copyV.setOnClickListener(v -> {
            //复制到剪贴板
            toCopy(value);
        });
        binding.rlContainer.addView(linearLayout);
    }

    private void createGoodsValue(String title) {
        LinearLayout linearLayout = (LinearLayout) View.inflate(this, R.layout.item_amount_goods_content, null);
        TextView valueV = linearLayout.findViewById(R.id.value);
        valueV.setText(title);
        binding.rlContainer.addView(linearLayout);
        linearLayout.setOnClickListener(v -> {

        });
    }
    private void toCopy(String content) {
        //获取剪贴板管理器：
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setText(content);
        ToastUtil.toastShortMessage("已复制到剪贴板");
    }
}
