package com.xaqinren.healthyelders.moduleLiteav.adapter;

import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.databinding.ItemMusicSelBinding;
import com.xaqinren.healthyelders.moduleLiteav.bean.MMusicItemBean;
import com.xaqinren.healthyelders.utils.GlideUtil;

import org.jetbrains.annotations.NotNull;

public class MusicSelCollAdapter extends BaseQuickAdapter<MMusicItemBean, BaseViewHolder> implements LoadMoreModule {


    public MusicSelCollAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, MMusicItemBean bean) {
        ItemMusicSelBinding binding = DataBindingUtil.bind(baseViewHolder.itemView);
        binding.setViewModel(bean);
        binding.musicName.setSelected(bean.myMusicStatus != 0);
        if (bean.myMusicStatus == 1) {
            //展示加载动画图标
            binding.progress.setVisibility(View.VISIBLE);
        }else{
            //隐藏加载动画图标
            binding.progress.setVisibility(View.GONE);
        }
        GlideUtil.intoCirImageView(getContext(),bean.coverUrl,binding.itemCover,2);
        binding.border.setVisibility(bean.myMusicStatus == 0 ? View.GONE : View.VISIBLE);
    }
}
