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
import com.xaqinren.healthyelders.utils.GlideUtil;

import org.jetbrains.annotations.NotNull;

public class CoinDetailAdapter extends BaseQuickAdapter<BillBean,BaseViewHolder> implements LoadMoreModule {

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
        GlideUtil.intoImageView(getContext(),billBean.getOrderTypeIcon(),binding.logo);
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
