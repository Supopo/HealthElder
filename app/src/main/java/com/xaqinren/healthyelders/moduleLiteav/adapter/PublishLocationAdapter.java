package com.xaqinren.healthyelders.moduleLiteav.adapter;

import androidx.databinding.DataBindingUtil;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.databinding.ItemPublishLocationBinding;
import com.xaqinren.healthyelders.databinding.ItemPublishTopicAdapterBinding;
import com.xaqinren.healthyelders.moduleLiteav.bean.LocationBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.TopicBean;

import org.jetbrains.annotations.NotNull;


public class PublishLocationAdapter extends BaseQuickAdapter<LocationBean, BaseViewHolder> {

    public PublishLocationAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, LocationBean topicBean) {
        ItemPublishLocationBinding binding = DataBindingUtil.bind(baseViewHolder.itemView);
        binding.setViewModel(topicBean);
        binding.executePendingBindings();
    }
}
