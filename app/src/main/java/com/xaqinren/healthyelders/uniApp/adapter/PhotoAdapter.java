package com.xaqinren.healthyelders.uniApp.adapter;

import androidx.databinding.DataBindingUtil;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.databinding.ItemPhotoBinding;
import com.xaqinren.healthyelders.uniApp.bean.FileBean;
import com.xaqinren.healthyelders.utils.GlideUtil;

import org.jetbrains.annotations.NotNull;

public class PhotoAdapter extends BaseQuickAdapter<FileBean, BaseViewHolder> {
    public PhotoAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, FileBean fileBean) {
        ItemPhotoBinding binding = DataBindingUtil.bind(baseViewHolder.itemView);
        GlideUtil.intoImageView(getContext(), fileBean.path, binding.item);
    }
}
