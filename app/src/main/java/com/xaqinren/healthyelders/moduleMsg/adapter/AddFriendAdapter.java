package com.xaqinren.healthyelders.moduleMsg.adapter;


import androidx.databinding.DataBindingUtil;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tencent.bugly.proguard.T;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ItemMsgAddFriendBinding;
import com.xaqinren.healthyelders.moduleMsg.bean.FriendBean;
import com.xaqinren.healthyelders.utils.GlideUtil;

import org.jetbrains.annotations.NotNull;

public class AddFriendAdapter extends BaseQuickAdapter<FriendBean,BaseViewHolder> {

    public static String FRIEND = "FRIEND";//朋友
    public static String FANS = "FANS";//粉丝
    public static String ATTENTION = "ATTENTION";//关注的人
    public static String STRANGER = "STRANGER";//陌生人
    public static String FOLLOW = "FOLLOW";//已关注

    public AddFriendAdapter(int layoutResId) {
        super(layoutResId);
        addChildClickViewIds(R.id.avatar, R.id.favorite, R.id.close);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, FriendBean friendBean) {
        ItemMsgAddFriendBinding binding = DataBindingUtil.bind(baseViewHolder.itemView);
        binding.setViewModel(friendBean);
        binding.source.setText("通讯录好友" + friendBean.getName());
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
            binding.favorite.setText("取消关注");
        } else if (friendBean.getIdentity().equals(FRIEND)) {
            //朋友
            binding.favorite.setText("互相关注");
        } else if (friendBean.getIdentity().equals(FOLLOW)) {
            //关注的人
            binding.favorite.setText("取消关注");
        }

    }
}
