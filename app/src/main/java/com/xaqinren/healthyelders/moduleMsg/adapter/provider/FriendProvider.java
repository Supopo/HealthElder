package com.xaqinren.healthyelders.moduleMsg.adapter.provider;

import android.os.Message;

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
    public static String ATTENTION = "ATTENTION";//关注的人
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
        } else if (friendBean.getIdentity().equals(STRANGER)) {
            //陌生人
            binding.favorite.setText("关注");
        } else if (friendBean.getIdentity().equals(FANS)) {
            //粉丝
            binding.favorite.setText("回关");
        } else if (friendBean.getIdentity().equals(ATTENTION)) {
            //关注的人
            binding.favorite.setText("已关注");
        } else if (friendBean.getIdentity().equals(FRIEND)) {
            //朋友
            binding.favorite.setText("互相关注");
        } else if (friendBean.getIdentity().equals(FOLLOW)) {
            //关注的人
            binding.favorite.setText("已关注");
        }
    }
}
