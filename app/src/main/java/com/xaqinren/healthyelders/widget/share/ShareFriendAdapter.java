package com.xaqinren.healthyelders.widget.share;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.databinding.ItemShareUserBinding;

import org.jetbrains.annotations.NotNull;

public class ShareFriendAdapter extends BaseQuickAdapter< IShareUser, BaseViewHolder> {
    public ShareFriendAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, IShareUser iShareUser) {
        ItemShareUserBinding binding = DataBindingUtil.bind(baseViewHolder.itemView);
//        Glide.with(getContext()).load(iShareUser.getAvatar()).into(binding.avatar);
//        binding.nickname.setText(iShareUser.getName());
    }
}
