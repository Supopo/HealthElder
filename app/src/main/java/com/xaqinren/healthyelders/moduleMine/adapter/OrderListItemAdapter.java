package com.xaqinren.healthyelders.moduleMine.adapter;

import androidx.databinding.DataBindingUtil;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ItemOrderListItemBinding;
import com.xaqinren.healthyelders.moduleMine.bean.OrderListItemBean;
import com.xaqinren.healthyelders.utils.GlideUtil;
import com.xaqinren.healthyelders.utils.UrlUtils;

import org.jetbrains.annotations.NotNull;

public class OrderListItemAdapter extends BaseQuickAdapter<OrderListItemBean, BaseViewHolder> {

    public OrderListItemAdapter() {
        super(R.layout.item_order_list_item);
       /* addChildClickViewIds(
                R.id.goods_item_layout,
                R.id.xgdz,
                R.id.qxdd,
                R.id.ycsh,
                R.id.ckwl,
                R.id.scdd,
                R.id.sqsh,
                R.id.jxfk,
                R.id.qrsh,
                R.id.pj
        );*/
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, OrderListItemBean orderListItemBean) {
        ItemOrderListItemBinding binding = DataBindingUtil.bind(baseViewHolder.itemView);
        binding.setData(orderListItemBean);
        int dp90 = (int) getContext().getResources().getDimension(R.dimen.dp_90);
        GlideUtil.intoImageView(getContext(), UrlUtils.resetImgUrl(orderListItemBean.getItemImage(),dp90,dp90),binding.logo);

    }
}
