package com.xaqinren.healthyelders.moduleMine.adapter;


import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.Bindable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.BindingConversion;
import androidx.databinding.BindingMethod;
import androidx.databinding.BindingMethods;
import androidx.databinding.DataBindingUtil;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ItemCoinDetailBinding;
import com.xaqinren.healthyelders.moduleMine.bean.BillBean;

import org.jetbrains.annotations.NotNull;

public class CoinDetailAdapter extends BaseQuickAdapter<BillBean,BaseViewHolder> implements LoadMoreModule {
    //USER_WALLET_RECHARGE(AccountRecordTypeEnum.ADD, "余额充值"),
    //USER_SECURITY_DEPOSIT(AccountRecordTypeEnum.ADD, "缴纳保证金"),
    //
    //COMMODITY_CONSUMPTION(AccountRecordTypeEnum.SUB,"购物消费"),
    //COMMODITY_CONSUMPTION_REFUND(AccountRecordTypeEnum.ADD,"消费退款"),
    //
    //TRANSPORT_ORDER_PAY( AccountRecordTypeEnum.SUB,"运输订单支付"),
    //TRANSPORT_ORDER_RETURN( AccountRecordTypeEnum.ADD,"运输订单退款"),
    //
    //RECHARGE_ORDER(AccountRecordTypeEnum.ADD,"微信充值"),
    //RECHARGE_DEPOSIT_ORDER(AccountRecordTypeEnum.ADD,"缴纳保证金"),
    //UNFROZEN_FUNDS_ADD(AccountRecordTypeEnum.ADD,"资金解冻增加"),
    //UNFROZEN_FUNDS_SUB(AccountRecordTypeEnum.SUB,"资金解冻扣减"),
    //
    //WITHDRAWAL_APPEAL(AccountRecordTypeEnum.SUB,"提现申请"),
    //WITHDRAWAL_APPEAL_BACK(AccountRecordTypeEnum.ADD,"提现退还"),
    //
    //CONSUMPTION_REBATE_ADD(AccountRecordTypeEnum.ADD,"消费佣金"),
    //CONSUMPTION_REBATE_SUB(AccountRecordTypeEnum.SUB,"消费佣金"),
    //
    //STORE_COMMISSION_ADD(AccountRecordTypeEnum.ADD,"店铺佣金"),
    //STORE_COMMISSION_SUB(AccountRecordTypeEnum.SUB,"店铺佣金"),
    //
    //MANAGE_ADD_BALANCE(AccountRecordTypeEnum.ADD,"人工增加余额"),
    //MANAGE_SUB_BALANCE(AccountRecordTypeEnum.SUB,"人工扣减余额"),
    //MANAGE_ADD_OUTSTANDING_BALANCE(AccountRecordTypeEnum.ADD,"人工增加未结算金额"),
    //MANAGE_SUB_OUTSTANDING_BALANCE(AccountRecordTypeEnum.SUB,"人工扣减未结算金额"),
    //
    //EXCHANGE_POINTS_SUB(AccountRecordTypeEnum.SUB,"余额兑换积分扣减"),
    //EXCHANGE_POINTS_ADD(AccountRecordTypeEnum.ADD,"余额兑换积分增加"),
    //POINT_RECHARGE(AccountRecordTypeEnum.ADD, "积分充值"),
    //SIGN_IN_SEND_POINT(AccountRecordTypeEnum.ADD,"签到送积分"),
    //PAY_ORDER_SEND_POINT(AccountRecordTypeEnum.ADD,"消费送积分"),
    //ORDER_BACK_SUB_POINT(AccountRecordTypeEnum.SUB,"退款扣积分"),
    //GIVE_GIFT_SUB_POINT(AccountRecordTypeEnum.SUB,"送礼物扣积分")
    public String GIVE_GIFT_SUB_POINT = "GIVE_GIFT_SUB_POINT";
    public String POINT_RECHARGE = "POINT_RECHARGE";
    public CoinDetailAdapter() {
        super(R.layout.item_coin_detail);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, BillBean billBean) {
        ItemCoinDetailBinding binding = DataBindingUtil.bind(baseViewHolder.itemView);
        binding.setData(billBean);
        String orderType = billBean.getOrderType();
        if (orderType.equals(GIVE_GIFT_SUB_POINT)) {
            binding.setIcon(getContext().getResources().getDrawable(R.mipmap.zhangd_xiaof));
        } else if (orderType.equals(POINT_RECHARGE)) {
            binding.setIcon(getContext().getResources().getDrawable(R.mipmap.zhangd_jkb));
        }
    }

    public boolean isGroupHeader(int p) {
        if (p == 0) {
            return true;
        }
        if (p >= getData().size()) {
            return false;
        }
        return !getData().get(p).getHeader_group_name().equals(getData().get(p - 1).getHeader_group_name());
    }

    public String getGroupName(int p) {
        return getData().get(p).getHeader_group_name();
    }

    public String getTotalName(int p) {
        return getData().get(p).getHeader_group_income();
    }


}
