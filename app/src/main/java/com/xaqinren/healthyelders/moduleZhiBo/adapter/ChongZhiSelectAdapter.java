package com.xaqinren.healthyelders.moduleZhiBo.adapter;

import androidx.databinding.DataBindingUtil;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ItemChongzhiListBinding;
import com.xaqinren.healthyelders.databinding.ItemChongzhiSelectBinding;
import com.xaqinren.healthyelders.moduleHome.bean.MenuBean;


public class ChongZhiSelectAdapter extends BaseQuickAdapter<MenuBean, BaseViewHolder> implements LoadMoreModule {
    public ChongZhiSelectAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, MenuBean item) {

        //注意 ItemBinding 改为自己item_layout的名字 ItemXxxBinding
        ItemChongzhiSelectBinding binding = DataBindingUtil.bind(helper.itemView);
        binding.setViewModel(item);
        binding.executePendingBindings();

        if (item.isSelect) {
            binding.llItem.setBackground(getContext().getResources().getDrawable(R.drawable.bg_btn_white_yellow));
        }else {
            binding.llItem.setBackground(getContext().getResources().getDrawable(R.drawable.bg_btn_gray));
        }
    }

}
