package com.xaqinren.healthyelders.widget.comment;

import android.graphics.drawable.Icon;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ItemCommentBinding;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.http.PUT;

public class CommentAdapter extends BaseQuickAdapter<ICommentBean , CommentAdapter.ViewHolder> implements LoadMoreModule {
    private OnChildLoadMoreCommentListener loadMoreCommentListener;
    private OnOperationItemClickListener operationItemClickListener;
    public CommentAdapter(int layoutResId , OnChildLoadMoreCommentListener loadMoreCommentListener , OnOperationItemClickListener operationItemClickListener) {
        super(layoutResId);
        this.loadMoreCommentListener = loadMoreCommentListener;
        this.operationItemClickListener = operationItemClickListener;
    }

    @Override
    protected void convert(@NotNull ViewHolder baseViewHolder, ICommentBean iCommentBean) {
        ItemCommentBinding binding = DataBindingUtil.bind(baseViewHolder.itemView);
        binding.setViewModel(iCommentBean);
        Glide.with(getContext()).load(iCommentBean.avatar).into(binding.avatar);
        binding.childList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.childList.setAdapter(baseViewHolder.commentChildAdapter);
        baseViewHolder.commentChildAdapter.setList(iCommentBean.childComment);


        binding.rlContent.setOnClickListener(view -> {
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
            if (iCommentBean.childComment.isEmpty()) {
                //增加一个加载更多底部
                ICommentBean bean = new ICommentBean();
                bean.viewType = 1;
                iCommentBean.childComment.add(bean);
                baseViewHolder.commentChildAdapter.addData(bean);
                baseViewHolder.commentChildAdapter.notifyDataSetChanged();
            }
            baseViewHolder.commentChildAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                    int viewType = iCommentBean.childComment.get(position).viewType;
                    if (viewType == 1) {
                        //点击更多
                        if (iCommentBean.childComment.size() == 0) {
                            //加载更多
                            loadMoreCommentListener.onLoadMore(baseViewHolder.getAdapterPosition(),iCommentBean,baseViewHolder.page,baseViewHolder.pageSize);
                        } else if (iCommentBean.childComment.size() == iCommentBean.commentCount + 1) {
                            //收起
                            iCommentBean.childComment.clear();
                            baseViewHolder.commentChildAdapter.setList(null);
                            loadMoreCommentListener.onPackUp(baseViewHolder.getAdapterPosition(), iCommentBean, baseViewHolder.page, baseViewHolder.pageSize);
                            notifyItemChanged(baseViewHolder.getAdapterPosition());
                        } else {
                            //加载更多
                            loadMoreCommentListener.onLoadMore( baseViewHolder.getAdapterPosition(), iCommentBean, baseViewHolder.page, baseViewHolder.pageSize);
                        }
                    }
                }
            });

            baseViewHolder.commentChildAdapter.setOnItemChildClickListener((adapter, view, position) -> {
                switch (view.getId()) {
                    case R.id.rl_content:
                        //评论
                        operationItemClickListener.toComment(iCommentBean.childComment.get(position));
                        break;
                    case R.id.avatar:
                    case R.id.nickname:
                        operationItemClickListener.toUser(iCommentBean.childComment.get(position));
                        //用户
                        break;
                }
            });
        }
    }

    class ViewHolder extends BaseViewHolder{
        CommentChildAdapter commentChildAdapter;
        int page = 1;
        int pageSize = 3;
        public ViewHolder(@NotNull View view) {
            super(view);
            commentChildAdapter = new CommentChildAdapter(R.layout.item_comment_child);
            commentChildAdapter.setAnimationEnable(false);
        }
    }

    public interface OnChildLoadMoreCommentListener{
        void onLoadMore( int position , ICommentBean iCommentBean , int page , int pageSize);
        void onPackUp( int position , ICommentBean iCommentBean , int page , int pageSize);
    }

    public interface OnOperationItemClickListener{
        void toComment(ICommentBean iCommentBean);
        void toLike(ICommentBean iCommentBean);
        void toUser(ICommentBean iCommentBean);
    }

}
