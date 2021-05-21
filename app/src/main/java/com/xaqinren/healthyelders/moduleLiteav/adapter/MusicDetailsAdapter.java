package com.xaqinren.healthyelders.moduleLiteav.adapter;

import androidx.databinding.DataBindingUtil;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.databinding.ItemMusicDetailBinding;
import com.xaqinren.healthyelders.moduleLiteav.bean.MMusicItemBean;

import org.jetbrains.annotations.NotNull;

public class MusicDetailsAdapter extends BaseQuickAdapter<MMusicItemBean, BaseViewHolder> {
    public MusicDetailsAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, MMusicItemBean musicBean) {
        ItemMusicDetailBinding binding = DataBindingUtil.bind(baseViewHolder.itemView);
        binding.setViewModel(musicBean);
    }
}
