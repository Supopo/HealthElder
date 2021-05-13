package com.xaqinren.healthyelders.moduleLiteav.adapter;

import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.databinding.ItemPublishTopicViewAdapterBinding;
import com.xaqinren.healthyelders.moduleLiteav.bean.TopicBean;

import org.jetbrains.annotations.NotNull;

public class ChooseTopicAdapter extends BaseQuickAdapter<TopicBean, BaseViewHolder> {
    public ChooseTopicAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, TopicBean topicBean) {
        ItemPublishTopicViewAdapterBinding binding = DataBindingUtil.bind(baseViewHolder.itemView);
        binding.setViewModel(topicBean);
        if (topicBean.getHot() == null) {
            binding.hintIv.setVisibility(View.GONE);
        }
        if (topicBean.getPlayCount() == null) {
            binding.playCount.setVisibility(View.GONE);
        }else{
            binding.playCount.setText(topicBean.getPlayCount() + "");
        }
    }
}
