package com.xaqinren.healthyelders.moduleLiteav.adapter;

import androidx.databinding.DataBindingUtil;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.databinding.ItemLiteAvLocationBinding;
import com.xaqinren.healthyelders.moduleLiteav.bean.SelLocationBean;

import org.jetbrains.annotations.NotNull;

public class ChooseLocationAdapter extends BaseQuickAdapter<SelLocationBean, BaseViewHolder> {
    public ChooseLocationAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, SelLocationBean selLocationBean) {
        ItemLiteAvLocationBinding binding = DataBindingUtil.bind(baseViewHolder.itemView);
        binding.setViewModel(selLocationBean);
        binding.executePendingBindings();
    }
}
