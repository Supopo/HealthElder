package com.xaqinren.healthyelders.moduleMsg.adapter.provider;

import com.chad.library.adapter.base.provider.BaseItemProvider;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.moduleMsg.bean.MessageDetailBean;

import org.jetbrains.annotations.NotNull;


public class LoadMoreProvider extends BaseItemProvider {
    @Override
    public int getItemViewType() {
        return MessageDetailBean.TYPE_LOAD_MORE;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_provider_load_more;
    }

    @Override
    public void convert(@NotNull BaseViewHolder baseViewHolder, Object o) {
        //点击，查看更多
    }
}
