package com.xaqinren.healthyelders;

import android.database.DatabaseUtils;
import android.graphics.Color;

import androidx.databinding.DataBindingUtil;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.bean.SlideBarBean;
import com.xaqinren.healthyelders.databinding.ItemSlideBarBinding;
import com.xaqinren.healthyelders.utils.GlideUtil;

import org.jetbrains.annotations.NotNull;

public class SlideBarAdapter extends BaseQuickAdapter<SlideBarBean.MenuInfoListDTO, BaseViewHolder> {
    public SlideBarAdapter() {
        super(R.layout.item_slide_bar);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, SlideBarBean.MenuInfoListDTO menuInfoListDTO) {
        ItemSlideBarBinding binding = DataBindingUtil.bind(baseViewHolder.itemView);
        binding.setData(menuInfoListDTO);
        binding.title.setTextColor(Color.parseColor(menuInfoListDTO.getFontColor()));
        baseViewHolder.itemView.setBackgroundColor(Color.parseColor(menuInfoListDTO.getBackgroundColor()));
        GlideUtil.intoImageView(getContext(),menuInfoListDTO.getIcon(),binding.icon);
    }
}
