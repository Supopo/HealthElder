package com.xaqinren.healthyelders.widget.share;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.databinding.ItemShareUserBinding;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ZBUserListBean;

import org.jetbrains.annotations.NotNull;

public class ShareFriendAdapter extends BaseQuickAdapter<ZBUserListBean, BaseViewHolder> {
    public ShareFriendAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, ZBUserListBean item) {
        ItemShareUserBinding binding = DataBindingUtil.bind(baseViewHolder.itemView);
        binding.setViewModel(item);
        binding.executePendingBindings();
    }
}
