package com.xaqinren.healthyelders.widget.comment;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ItemCommentListBinding;
import com.xaqinren.healthyelders.moduleHome.bean.CommentListBean;

import org.jetbrains.annotations.NotNull;

public class CommentListAdapter extends BaseQuickAdapter<CommentListBean, CommentListAdapter.ViewHolder> implements LoadMoreModule {
    private OnChildLoadMoreCommentListener loadMoreCommentListener;
    private OnOperationItemClickListener operationItemClickListener;

    public CommentListAdapter(int layoutResId, OnChildLoadMoreCommentListener loadMoreCommentListener, OnOperationItemClickListener operationItemClickListener) {
        super(layoutResId);
        this.loadMoreCommentListener = loadMoreCommentListener;
        this.operationItemClickListener = operationItemClickListener;
    }


    @Override
    protected void convert(@NotNull ViewHolder baseViewHolder, CommentListBean iCommentBean) {
        ItemCommentListBinding binding = DataBindingUtil.bind(baseViewHolder.itemView);
        binding.setViewModel(iCommentBean);
        binding.childList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.childList.setAdapter(baseViewHolder.commentChildAdapter);
        baseViewHolder.commentChildAdapter.setList(iCommentBean.shortVideoCommentReplyList);

        binding.rlContent.setOnClickListener(view -> {
            iCommentBean.parentPos = baseViewHolder.getAdapterPosition();
            //发起评论
            operationItemClickListener.toComment(iCommentBean);
        });
        binding.avatar.setOnClickListener(view -> {
            //查看用户
            operationItemClickListener.toUser(iCommentBean);
        });
        binding.nickname.setOnClickListener(view -> {
            //查看用户
            operationItemClickListener.toUser(iCommentBean);
        });
        binding.llLike.setOnClickListener(view -> {
            //点赞/取消点赞
            operationItemClickListener.toLike(iCommentBean);
        });


        if (iCommentBean.commentCount > 0) {
            baseViewHolder.commentChildAdapter.setCount(iCommentBean.commentCount + 1);
            if (iCommentBean.shortVideoCommentReplyList.isEmpty()) {
                //增加一个加载更多底部
                CommentListBean bean = new CommentListBean();
                bean.viewType = 1;
                iCommentBean.shortVideoCommentReplyList.add(bean);
                baseViewHolder.commentChildAdapter.addData(bean);
                baseViewHolder.commentChildAdapter.notifyDataSetChanged();
            }
            baseViewHolder.commentChildAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                    int viewType = iCommentBean.shortVideoCommentReplyList.get(position).viewType;
                    if (viewType == 1) {
                        //点击更多
                        if (iCommentBean.shortVideoCommentReplyList.size() == 0) {
                            //加载更多
                            loadMoreCommentListener.onLoadMore(baseViewHolder.getAdapterPosition(), iCommentBean, baseViewHolder.page, baseViewHolder.pageSize);
                        } else if (iCommentBean.shortVideoCommentReplyList.size() == iCommentBean.commentCount + 1) {
                            //收起
                            iCommentBean.shortVideoCommentReplyList.clear();
                            baseViewHolder.commentChildAdapter.setList(null);
                            loadMoreCommentListener.onPackUp(baseViewHolder.getAdapterPosition(), iCommentBean, baseViewHolder.page, baseViewHolder.pageSize);
                            notifyItemChanged(baseViewHolder.getAdapterPosition());
                        } else {
                            //加载更多
                            loadMoreCommentListener.onLoadMore(baseViewHolder.getAdapterPosition(), iCommentBean, baseViewHolder.page, baseViewHolder.pageSize);
                        }
                    }
                }
            });

            baseViewHolder.commentChildAdapter.setOnItemChildClickListener((adapter, view, position) -> {
                switch (view.getId()) {
                    case R.id.rl_content:
                        //回复里面的回复
                        operationItemClickListener.toCommentReply(iCommentBean.shortVideoCommentReplyList.get(position));
                        break;
                    case R.id.avatar:
                    case R.id.nickname:
                        operationItemClickListener.toUser(iCommentBean.shortVideoCommentReplyList.get(position));
                        //用户
                        break;
                }
            });
        }
    }

    class ViewHolder extends BaseViewHolder {
        CommentListChildAdapter commentChildAdapter;
        int page = 1;
        int pageSize = 3;

        public ViewHolder(@NotNull View view) {
            super(view);
            commentChildAdapter = new CommentListChildAdapter(R.layout.item_comment_list_child);
            commentChildAdapter.setAnimationEnable(false);
        }
    }

    public interface OnChildLoadMoreCommentListener {
        void onLoadMore(int position, CommentListBean iCommentBean, int page, int pageSize);

        void onPackUp(int position, CommentListBean iCommentBean, int page, int pageSize);
    }

    public interface OnOperationItemClickListener {
        void toComment(CommentListBean iCommentBean);

        void toCommentReply(CommentListBean iCommentBean);

        void toLike(CommentListBean iCommentBean);

        void toUser(CommentListBean iCommentBean);
    }

}
