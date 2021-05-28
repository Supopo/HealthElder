package com.xaqinren.healthyelders.moduleMsg.adapter.provider;

import android.os.Message;

import com.chad.library.adapter.base.provider.BaseItemProvider;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.moduleMsg.bean.MessageDetailBean;

import org.jetbrains.annotations.NotNull;

public class FriendProvider<T extends MessageDetailBean> extends BaseItemProvider<T> {
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

    }
}
