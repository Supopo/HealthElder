package com.xaqinren.healthyelders.moduleMsg.adapter.provider;

import androidx.databinding.DataBindingUtil;

import com.chad.library.adapter.base.provider.BaseItemProvider;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ItemProviderFriendBinding;
import com.xaqinren.healthyelders.moduleMsg.bean.FriendBean;
import com.xaqinren.healthyelders.moduleMsg.bean.MessageDetailBean;
import com.xaqinren.healthyelders.utils.GlideUtil;

import org.jetbrains.annotations.NotNull;

public class FriendProvider<T extends MessageDetailBean> extends BaseItemProvider<T> {

    public static String FRIEND = "FRIEND";//朋友
    public static String FANS = "FANS";//粉丝
    public static String ATTENTION = "ATTENTION";//关注的人 //自定义的 为了展示 出已关注
    public static String STRANGER = "STRANGER";//陌生人
    public static String FOLLOW = "FOLLOW";//已关注


    @Override
    public int getItemViewType() {
        return MessageDetailBean.TYPE_FRIEND;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_provider_friend;
    }

    @Override
    public void convert(@NotNull BaseViewHolder baseViewHolder, T t) {
        FriendBean friendBean = (FriendBean) t;
        ItemProviderFriendBinding binding = DataBindingUtil.bind(baseViewHolder.itemView);
        binding.setViewModel(friendBean);

        GlideUtil.intoImageView(getContext(),friendBean.getAvatarUrl(),binding.avatar,R.mipmap.default_avatar);
        if (friendBean.getIdentity() == null){
            //陌生人
            binding.favorite.setText("分享");
            binding.favorite.setSelected(true);
        } else if (friendBean.getIdentity().equals(STRANGER)) {
            //陌生人
            binding.favorite.setText("关注");
            binding.favorite.setSelected(true);
        } else if (friendBean.getIdentity().equals(FANS)) {
            //粉丝
            binding.favorite.setText("回关");
            binding.favorite.setSelected(true);
        } else if (friendBean.getIdentity().equals(ATTENTION)) {
            //关注的人
            binding.favorite.setText("已关注");
            binding.favorite.setSelected(false);
        } else if (friendBean.getIdentity().equals(FRIEND)) {
            //朋友
            binding.favorite.setText("互相关注");
            binding.favorite.setSelected(false);
        } else if (friendBean.getIdentity().equals(FOLLOW)) {
            //关注的人
            binding.favorite.setText("已关注");
            binding.favorite.setSelected(false);
        }
    }
}
