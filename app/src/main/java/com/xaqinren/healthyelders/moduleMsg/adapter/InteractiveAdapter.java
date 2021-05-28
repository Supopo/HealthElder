package com.xaqinren.healthyelders.moduleMsg.adapter;

import com.chad.library.adapter.base.BaseProviderMultiAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.moduleMsg.adapter.provider.FriendProvider;
import com.xaqinren.healthyelders.moduleMsg.adapter.provider.LoadMoreProvider;
import com.xaqinren.healthyelders.moduleMsg.adapter.provider.TextProvider;
import com.xaqinren.healthyelders.moduleMsg.bean.MessageDetailBean;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class InteractiveAdapter<T extends MessageDetailBean> extends BaseProviderMultiAdapter<T> implements LoadMoreModule {

    public InteractiveAdapter() {
        addItemProvider(new LoadMoreProvider());
        addItemProvider(new TextProvider());
        addItemProvider(new FriendProvider());
        addChildClickViewIds(R.id.avatar,R.id.attention_btn);
    }

    @Override
    protected int getItemType(@NotNull List<? extends T> list, int i) {
        return list.get(i).getItemType();
    }
}
