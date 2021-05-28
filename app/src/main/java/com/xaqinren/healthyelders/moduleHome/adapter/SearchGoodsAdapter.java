package com.xaqinren.healthyelders.moduleHome.adapter;

import androidx.databinding.DataBindingUtil;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.databinding.ItemSearchGoodsBinding;
import com.xaqinren.healthyelders.databinding.ItemSearchVideoBinding;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;


public class SearchGoodsAdapter extends BaseQuickAdapter<VideoInfo, BaseViewHolder> implements LoadMoreModule {
    public SearchGoodsAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, VideoInfo item) {

        //注意 ItemBinding 改为自己item_layout的名字 ItemXxxBinding
        ItemSearchGoodsBinding binding = DataBindingUtil.bind(helper.itemView);
        binding.setViewModel(item);
        binding.executePendingBindings();
    }
}
