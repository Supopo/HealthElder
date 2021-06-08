package com.xaqinren.healthyelders.widget.comment;

import android.widget.ImageView;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tencent.qcloud.tim.uikit.component.face.FaceManager;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.FooterCommentExpanBinding;
import com.xaqinren.healthyelders.databinding.ItemCommentListChildBinding;
import com.xaqinren.healthyelders.moduleHome.bean.CommentListBean;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CommentListChildAdapter extends BaseMultiItemQuickAdapter<CommentListBean, BaseViewHolder> {

    private int count;  //总数量

    public void setCount(int count) {
        this.count = count;
    }

    public CommentListChildAdapter(int layoutResId) {
        addItemType(0, R.layout.item_comment_list_child);
        addItemType(1, R.layout.footer_comment_expan);

        addChildClickViewIds(R.id.ll_like);
        addChildClickViewIds(R.id.rl_content);
        addChildClickViewIds(R.id.avatar);
        addChildClickViewIds(R.id.nickname);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, CommentListBean iCommentBean) {
        if (iCommentBean.viewType == 1) {
            FooterCommentExpanBinding binding = DataBindingUtil.bind(baseViewHolder.itemView);
            if (count == getData().size()) {
                //收起
                binding.hintTv.setText("收起");
                binding.ivDown.setBackground(getContext().getResources().getDrawable(R.mipmap.plhuif_shouq));
            } else {
                //加载更多
                binding.hintTv.setText("展开" + (count - getData().size()) + "条回复");
                binding.ivDown.setBackground(getContext().getResources().getDrawable(R.mipmap.plhuif_xiala));
            }
        } else {
            ItemCommentListChildBinding binding = DataBindingUtil.bind(baseViewHolder.itemView);
            binding.setViewModel(iCommentBean);

            //刷新点赞
            baseViewHolder.setText(R.id.tv_like, String.valueOf(iCommentBean.favoriteCount));
            if (!iCommentBean.hasFavorite) {
                Glide.with(getContext()).load(R.mipmap.icon_pinl_zan_nor).into((ImageView) baseViewHolder.getView(R.id.like_iv));
            } else {
                Glide.with(getContext()).load(R.mipmap.pinl_zan_sel).into((ImageView) baseViewHolder.getView(R.id.like_iv));
            }

            //设置处理加载聊天表情文字
            FaceManager.handlerEmojiText(binding.content, iCommentBean.content, false);
        }


    }

    //局部刷新用的
    @Override
    protected void convert(BaseViewHolder helper, CommentListBean item, List<?> payloads) {
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
            }
        }
    }

}
