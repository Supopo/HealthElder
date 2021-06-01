package com.xaqinren.healthyelders.moduleZhiBo.adapter;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ItemGiftRvBinding;
import com.xaqinren.healthyelders.moduleZhiBo.bean.GiftBean;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Lee. on 2021/5/25.
 */
public class GiftListPageAdapter extends BaseQuickAdapter<GiftBean, BaseViewHolder> {

    public GiftListPageAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder helper, GiftBean item) {
        //注意 ItemBinding 改为自己item_layout的名字 ItemXxxBinding
        ItemGiftRvBinding binding = DataBindingUtil.bind(helper.itemView);
        binding.setViewModel(item);
        binding.executePendingBindings();

        GiftListAdapter listAdapter = new GiftListAdapter(R.layout.item_zb_gift);
        binding.rvContent.setLayoutManager(new GridLayoutManager(getContext(), 4));
        binding.rvContent.setAdapter(listAdapter);
        listAdapter.setList(item.giftBeans);

        listAdapter.setOnItemClickListener(((adapter, view, position) -> {
            item.nowPos = position;
            listAdapter.getData().get(position).isSelect = true;
            listAdapter.getData().get(item.lastPos).isSelect = false;
            listAdapter.notifyItemChanged(position, 99);
            listAdapter.notifyItemChanged(item.lastPos, 99);
            item.lastPos = position;
        }));
    }
}
