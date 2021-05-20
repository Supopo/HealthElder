package com.xaqinren.healthyelders.widget.comment;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ItemCommentListBinding;
import com.xaqinren.healthyelders.moduleHome.bean.CommentListBean;
import com.xaqinren.healthyelders.moduleHome.bean.GirlsBean;

import org.jetbrains.annotations.NotNull;

import java.util.List;

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
        iCommentBean.parentPos = baseViewHolder.getAdapterPosition();//插入评论所在pos
        if (!iCommentBean.hasFavorite) {
            Glide.with(getContext()).load(R.mipmap.icon_pinl_zan_nor).into((ImageView) baseViewHolder.getView(R.id.like_iv));
        } else {
            Glide.with(getContext()).load(R.mipmap.pinl_zan_sel).into((ImageView) baseViewHolder.getView(R.id.like_iv));
        }
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
                CommentListBean replyComment = iCommentBean.shortVideoCommentReplyList.get(position);
                replyComment.parentPos = iCommentBean.parentPos;
                replyComment.itemPos = position;

                switch (view.getId()) {
                    case R.id.rl_content:
                        //回复里面的回复
                        operationItemClickListener.toCommentReply(replyComment);
                        break;
                    case R.id.avatar:
                    case R.id.nickname:
                        operationItemClickListener.toUser(iCommentBean.shortVideoCommentReplyList.get(position));
                        //用户
                        break;
                    case R.id.ll_like:
                        //回复点赞
                        operationItemClickListener.toLikeReply(replyComment);
                        break;
                }
            });
        }
    }

    //局部刷新用的
    @Override
    protected void convert(ViewHolder helper, CommentListBean item, List<?> payloads) {
        super.convert(helper, item, payloads);
        if (payloads.size() > 0 && payloads.get(0) instanceof Integer) {
            //不为空，即调用notifyItemChanged(position,payloads)后执行的，可以在这里获取payloads中的数据进行局部刷新
            int type = (Integer) payloads.get(0);// 刷新哪个部分 标志位
            if (type == 99) {
                //刷新点赞
                helper.setText(R.id.tv_like, String.valueOf(item.favoriteCount));
                if (!item.hasFavorite) {
                    Glide.with(getContext()).load(R.mipmap.icon_pinl_zan_nor).into((ImageView) helper.getView(R.id.like_iv));
                } else {
                    Glide.with(getContext()).load(R.mipmap.pinl_zan_sel).into((ImageView) helper.getView(R.id.like_iv));
                }
            } else if (type == 98) {
                //刷新某条评论中的某条回复
                helper.commentChildAdapter.notifyItemChanged(item.itemPos, 99);
            }
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

        void toLikeReply(CommentListBean iCommentBean);

        void toUser(CommentListBean iCommentBean);
    }

}