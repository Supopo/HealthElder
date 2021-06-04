package com.xaqinren.healthyelders.moduleMine.adapter;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;

import androidx.databinding.DataBindingUtil;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.qmuiteam.qmui.span.QMUITextSizeSpan;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ItemBankCardBinding;
import com.xaqinren.healthyelders.moduleMine.bean.BankCardBean;

import org.jetbrains.annotations.NotNull;

public class BankCardAdapter extends BaseQuickAdapter<BankCardBean, BaseViewHolder> {
    public BankCardAdapter() {
        super(R.layout.item_bank_card);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, BankCardBean bankCardBean) {
        ItemBankCardBinding binding = DataBindingUtil.bind(baseViewHolder.itemView);

    }
}
