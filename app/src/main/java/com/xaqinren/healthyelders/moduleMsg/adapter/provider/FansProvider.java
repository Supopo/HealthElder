package com.xaqinren.healthyelders.moduleMsg.adapter.provider;

import androidx.databinding.DataBindingUtil;

import com.chad.library.adapter.base.provider.BaseItemProvider;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ItemProviderFansBinding;
import com.xaqinren.healthyelders.moduleMsg.bean.InteractiveBean;
import com.xaqinren.healthyelders.moduleMsg.bean.MessageDetailBean;
import com.xaqinren.healthyelders.utils.DateUtils;
import com.xaqinren.healthyelders.utils.GlideUtil;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;

public class FansProvider <T extends MessageDetailBean> extends BaseItemProvider<T> {
    SimpleDateFormat simpleDateFormat;
    public FansProvider() {
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    }

    @Override
    public int getItemViewType() {
        return InteractiveBean.TYPE_TOP;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_provider_fans;
    }

    @Override
    public void convert(@NotNull BaseViewHolder baseViewHolder, T t) {
        InteractiveBean bean = (InteractiveBean) t;
        ItemProviderFansBinding binding = DataBindingUtil.bind(baseViewHolder.itemView);
        binding.setViewModel(bean);

        GlideUtil.intoImageView(getContext(), bean.getSendUser().getAvatarUrl(), binding.avatar);
        binding.time.setText(DateUtils.getRelativeTime(bean.getCreatedAt()));
    }
}
