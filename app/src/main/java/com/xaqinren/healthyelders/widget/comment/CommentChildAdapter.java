package com.xaqinren.healthyelders.widget.comment;

import androidx.databinding.DataBindingUtil;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.databinding.ItemCommentChildBinding;

import org.jetbrains.annotations.NotNull;

import me.goldze.mvvmhabit.base.BaseActivity;

public class CommentChildAdapter extends BaseQuickAdapter<ICommentBean , BaseViewHolder> {
    public CommentChildAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, ICommentBean iCommentBean) {
        ItemCommentChildBinding binding = DataBindingUtil.bind(baseViewHolder.itemView);
        binding.setViewModel(iCommentBean);
    }

}
