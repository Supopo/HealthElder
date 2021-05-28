package com.xaqinren.healthyelders.moduleMsg.adapter.provider;

import androidx.databinding.DataBindingUtil;

import com.chad.library.adapter.base.provider.BaseItemProvider;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ItemProviderServiceBinding;
import com.xaqinren.healthyelders.databinding.ItemProviderSysBinding;
import com.xaqinren.healthyelders.moduleMsg.bean.InteractiveBean;
import com.xaqinren.healthyelders.moduleMsg.bean.MessageDetailBean;

import org.jetbrains.annotations.NotNull;

public class ServiceProvider<T extends MessageDetailBean> extends BaseItemProvider<T> {

    public ServiceProvider() {

    }

    @Override
    public int getItemViewType() {
        return InteractiveBean.TYPE_TOP;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_provider_service;
    }

    @Override
    public void convert(@NotNull BaseViewHolder baseViewHolder, T t) {
        InteractiveBean bean = (InteractiveBean) t;
        ItemProviderServiceBinding binding = DataBindingUtil.bind(baseViewHolder.itemView);
        binding.setViewModel(bean);
    }
}
