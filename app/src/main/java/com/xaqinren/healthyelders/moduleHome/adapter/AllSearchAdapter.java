package com.xaqinren.healthyelders.moduleHome.adapter;

import androidx.databinding.DataBindingUtil;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.FooterCommentExpanBinding;
import com.xaqinren.healthyelders.databinding.ItemCommentChildBinding;
import com.xaqinren.healthyelders.databinding.ItemSearchArticleBinding;
import com.xaqinren.healthyelders.databinding.ItemSearchGoodsBinding;
import com.xaqinren.healthyelders.databinding.ItemSearchUserBinding;
import com.xaqinren.healthyelders.databinding.ItemSearchVideoBinding;
import com.xaqinren.healthyelders.databinding.ItemSearchZbBinding;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;
import com.xaqinren.healthyelders.widget.comment.ICommentBean;

import org.jetbrains.annotations.NotNull;

public class AllSearchAdapter extends BaseMultiItemQuickAdapter<VideoInfo, BaseViewHolder> implements LoadMoreModule {


    public AllSearchAdapter() {
        addItemType(0, R.layout.item_search_video);
        addItemType(1, R.layout.item_search_user);
        addItemType(2, R.layout.item_search_goods);
        addItemType(3, R.layout.item_search_zb);
        addItemType(4, R.layout.item_search_article);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, VideoInfo videoInfo) {
        if (videoInfo.getItemType() == 0) {
            ItemSearchVideoBinding binding = DataBindingUtil.bind(baseViewHolder.itemView);
            binding.setViewModel(videoInfo);
        } else if (videoInfo.getItemType() == 1) {
            ItemSearchUserBinding binding = DataBindingUtil.bind(baseViewHolder.itemView);
            binding.setViewModel(videoInfo);
        } else if (videoInfo.getItemType() == 2) {
            ItemSearchGoodsBinding binding = DataBindingUtil.bind(baseViewHolder.itemView);
            binding.setViewModel(videoInfo);
        } else if (videoInfo.getItemType() == 3) {
            ItemSearchZbBinding binding = DataBindingUtil.bind(baseViewHolder.itemView);
            binding.setViewModel(videoInfo);
        } else if (videoInfo.getItemType() == 4) {
            ItemSearchArticleBinding binding = DataBindingUtil.bind(baseViewHolder.itemView);
            binding.setViewModel(videoInfo);
        }

    }

}
