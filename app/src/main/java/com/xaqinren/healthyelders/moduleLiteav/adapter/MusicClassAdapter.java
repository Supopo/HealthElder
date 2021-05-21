package com.xaqinren.healthyelders.moduleLiteav.adapter;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.databinding.ItemMusicClassBinding;
import com.xaqinren.healthyelders.moduleLiteav.bean.MusicClassBean;

import org.jetbrains.annotations.NotNull;

public class MusicClassAdapter extends BaseQuickAdapter<MusicClassBean, BaseViewHolder> {
    public MusicClassAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, MusicClassBean musicClassBean) {
        ItemMusicClassBinding binding = DataBindingUtil.bind(baseViewHolder.itemView);
        binding.setViewModel(musicClassBean);
        Glide.with(getContext()).load(musicClassBean.jumpUrl).into(binding.icon);
    }
}
