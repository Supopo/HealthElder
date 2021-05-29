package com.xaqinren.healthyelders.moduleZhiBo.adapter;

import androidx.databinding.DataBindingUtil;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.databinding.ItemStartLiveMenuBinding;
import com.xaqinren.healthyelders.moduleHome.bean.MenuBean;
import com.xaqinren.healthyelders.utils.GlideUtil;

import me.goldze.mvvmhabit.utils.ImageUtils;


public class LiveMenuAdapter extends BaseQuickAdapter<MenuBean, BaseViewHolder> implements LoadMoreModule {
    public LiveMenuAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, MenuBean item) {

        //注意 ItemBinding 改为自己item_layout的名字 ItemXxxBinding
        ItemStartLiveMenuBinding binding = DataBindingUtil.bind(helper.itemView);
        binding.setViewModel(item);
        binding.executePendingBindings();

        GlideUtil.intoImageView(getContext(),item.menuRes,binding.ivMenu);
    }


}
