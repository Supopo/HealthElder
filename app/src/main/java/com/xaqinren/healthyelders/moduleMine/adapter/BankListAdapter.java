package com.xaqinren.healthyelders.moduleMine.adapter;

import androidx.databinding.DataBindingUtil;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.databinding.ItemBankCardSelBinding;
import com.xaqinren.healthyelders.moduleMine.bean.BankCardBean;
import com.xaqinren.healthyelders.utils.GlideUtil;

import org.jetbrains.annotations.NotNull;

public class BankListAdapter extends BaseQuickAdapter<BankCardBean, BaseViewHolder> {
    public BankListAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, BankCardBean bankCardBean) {
        ItemBankCardSelBinding binding = DataBindingUtil.bind(baseViewHolder.itemView);
        binding.setData(bankCardBean);
    }
}
