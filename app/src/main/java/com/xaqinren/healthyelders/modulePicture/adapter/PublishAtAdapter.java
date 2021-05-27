package com.xaqinren.healthyelders.modulePicture.adapter;

import androidx.databinding.DataBindingUtil;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.databinding.ItemPuglishAtBinding;
import com.xaqinren.healthyelders.moduleLiteav.bean.LiteAvUserBean;
import com.xaqinren.healthyelders.utils.GlideUtil;

import org.jetbrains.annotations.NotNull;

public class PublishAtAdapter extends BaseQuickAdapter<LiteAvUserBean, BaseViewHolder> implements LoadMoreModule {

    public PublishAtAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, LiteAvUserBean liteAvUserBean) {
        ItemPuglishAtBinding binding = DataBindingUtil.bind(baseViewHolder.itemView);
        binding.setData(liteAvUserBean);
        GlideUtil.intoImageView(getContext(),liteAvUserBean.getAvatar(),binding.avatar);
    }
}
