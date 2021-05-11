package com.xaqinren.healthyelders.widget.comment;

import android.view.View;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ItemCommentBinding;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.http.PUT;

public class CommentAdapter extends BaseQuickAdapter<ICommentBean , CommentAdapter.ViewHolder> {
    private OnChildLoadMoreCommentListener loadMoreCommentListener;
    public CommentAdapter(int layoutResId , OnChildLoadMoreCommentListener loadMoreCommentListener) {
        super(layoutResId);
        this.loadMoreCommentListener = loadMoreCommentListener;
    }

    @Override
    protected void convert(@NotNull ViewHolder baseViewHolder, ICommentBean iCommentBean) {
        ItemCommentBinding binding = DataBindingUtil.bind(baseViewHolder.itemView);
        binding.setViewModel(iCommentBean);
        Glide.with(getContext()).load(iCommentBean.avatar).into(binding.avatar);
        binding.childList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.childList.setAdapter(baseViewHolder.commentChildAdapter);
        baseViewHolder.commentChildAdapter.setList(iCommentBean.childComment);
        if (iCommentBean.commentCount > 0) {
            View view = null;
            if (!baseViewHolder.commentChildAdapter.hasFooterLayout()){
                view = View.inflate(getContext(), R.layout.footer_comment_expan, null);
                baseViewHolder.commentChildAdapter.addFooterView(view);
            }

            if (view != null) {
                if (iCommentBean.childComment.size() == iCommentBean.commentCount) {
                    TextView hint = view.findViewById(R.id.hint_tv);
                    hint.setText("收起");
                }else if (iCommentBean.childComment.size() > 0 ){
                    TextView hint = view.findViewById(R.id.hint_tv);
                    hint.setText("展示更多评论");
                } else {
                    TextView hint = view.findViewById(R.id.hint_tv);
                    hint.setText("展示"+iCommentBean.commentCount+"条评论");
                }
                view.setOnClickListener(view1 -> {
                    if (iCommentBean.childComment.size() == iCommentBean.commentCount) {
                        //到达最大数量,收起
                        loadMoreCommentListener.onPackUp(baseViewHolder, baseViewHolder.getAdapterPosition(), iCommentBean, baseViewHolder.page, baseViewHolder.pageSize);
                    }else {
                        //加载更多,调用接口
                        loadMoreCommentListener.onLoadMore(baseViewHolder, baseViewHolder.getAdapterPosition(), iCommentBean, baseViewHolder.page, baseViewHolder.pageSize);
                    }
                });
            }
        }
    }

    class ViewHolder extends BaseViewHolder{
        CommentChildAdapter commentChildAdapter;
        int page = 1;
        int pageSize = 3;
        public ViewHolder(@NotNull View view) {
            super(view);
            commentChildAdapter = new CommentChildAdapter(R.layout.item_comment_child);
        }
    }

    public interface OnChildLoadMoreCommentListener{
        void onLoadMore(ViewHolder baseViewHolder , int position , ICommentBean iCommentBean , int page , int pageSize);
        void onPackUp(ViewHolder baseViewHolder , int position , ICommentBean iCommentBean , int page , int pageSize);
    }

}
