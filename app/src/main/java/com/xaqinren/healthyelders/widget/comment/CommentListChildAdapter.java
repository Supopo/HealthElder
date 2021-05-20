package com.xaqinren.healthyelders.widget.comment;

import androidx.databinding.DataBindingUtil;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.FooterCommentExpanBinding;
import com.xaqinren.healthyelders.databinding.ItemCommentChildBinding;
import com.xaqinren.healthyelders.databinding.ItemCommentListChildBinding;
import com.xaqinren.healthyelders.moduleHome.bean.CommentListBean;

import org.jetbrains.annotations.NotNull;

public class CommentListChildAdapter extends BaseMultiItemQuickAdapter<CommentListBean, BaseViewHolder> {

    private int count;

    public void setCount(int count) {
        this.count = count;
    }

    public CommentListChildAdapter(int layoutResId) {
        addItemType(0, R.layout.item_comment_list_child);
        addItemType(1, R.layout.footer_comment_expan);

        addChildClickViewIds(R.id.rl_content);
        addChildClickViewIds(R.id.avatar);
        addChildClickViewIds(R.id.nickname);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, CommentListBean iCommentBean) {
        if (iCommentBean.viewType == 1) {
            FooterCommentExpanBinding binding = DataBindingUtil.bind(baseViewHolder.itemView);
            if (getData().size() == 1) {
                //加载XX条数据
                binding.hintTv.setText("加载" + (count - 1) + "条回复");
            } else if (getData().size() < count ) {
                //加载更多
                binding.hintTv.setText("加载更多回复");
            } else {
                //收起
                binding.hintTv.setText("收起");
            }
        }else{
            ItemCommentListChildBinding binding = DataBindingUtil.bind(baseViewHolder.itemView);
            binding.setViewModel(iCommentBean);
        }

    }

}
