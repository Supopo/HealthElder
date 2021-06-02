package com.xaqinren.healthyelders.moduleMine.adapter;

import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ItemAttentionBinding;
import com.xaqinren.healthyelders.moduleLiteav.bean.LiteAvUserBean;
import com.xaqinren.healthyelders.moduleMsg.adapter.provider.FriendProvider;
import com.xaqinren.healthyelders.utils.GlideUtil;

import org.jetbrains.annotations.NotNull;

import io.dcloud.common.cs.DA;

public class AttentionAdapter extends BaseQuickAdapter<LiteAvUserBean, BaseViewHolder> implements LoadMoreModule {
    boolean showClose;

    public AttentionAdapter(int layoutResId, boolean showClose) {
        super(layoutResId);
        addChildClickViewIds(R.id.avatar, R.id.attention_btn, R.id.close);
        this.showClose = showClose;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, LiteAvUserBean liteAvUserBean) {
        ItemAttentionBinding binding = DataBindingUtil.bind(baseViewHolder.itemView);
        GlideUtil.intoImageView(getContext(), liteAvUserBean.attentionUserInfo.avatarUrl, binding.avatar);
        binding.nickname.setText(liteAvUserBean.attentionUserInfo.nickname);
        binding.close.setVisibility(showClose ? View.VISIBLE : View.GONE);
        if (liteAvUserBean.identity == null){
            //陌生人
            binding.attentionBtn.setText("分享");
        } else if (liteAvUserBean.identity.equals(FriendProvider.STRANGER)) {
            //陌生人
            binding.attentionBtn.setText("关注");
            binding.attentionBtn.setSelected(true);
        } else if (liteAvUserBean.identity.equals(FriendProvider.FANS)) {
            //粉丝
            binding.attentionBtn.setText("回关");
            binding.attentionBtn.setSelected(true);
        } else if (liteAvUserBean.identity.equals(FriendProvider.ATTENTION)) {
            //关注的人
            binding.attentionBtn.setText("已关注");
            binding.attentionBtn.setSelected(false);
        } else if (liteAvUserBean.identity.equals(FriendProvider.FRIEND)) {
            //朋友
            binding.attentionBtn.setText("互相关注");
            binding.attentionBtn.setSelected(false);
        } else if (liteAvUserBean.identity.equals(FriendProvider.FOLLOW)) {
            //关注的人
            binding.attentionBtn.setText("已关注");
            binding.attentionBtn.setSelected(false);
        }
    }
}
