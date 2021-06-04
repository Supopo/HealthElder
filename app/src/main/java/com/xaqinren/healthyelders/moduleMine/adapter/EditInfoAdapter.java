package com.xaqinren.healthyelders.moduleMine.adapter;

import androidx.databinding.DataBindingUtil;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ItemEditInfoBinding;
import com.xaqinren.healthyelders.moduleMine.bean.EditMenuBean;

import org.jetbrains.annotations.NotNull;

public class EditInfoAdapter extends BaseQuickAdapter<EditMenuBean, BaseViewHolder> {
    public EditInfoAdapter() {
        super(R.layout.item_edit_info);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, EditMenuBean editMenuBean) {
        ItemEditInfoBinding binding = DataBindingUtil.bind(baseViewHolder.itemView);
        binding.setViewModel(editMenuBean);
    }
}
