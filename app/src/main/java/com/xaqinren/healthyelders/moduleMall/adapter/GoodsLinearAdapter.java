package com.xaqinren.healthyelders.moduleMall.adapter;

import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ItemGoodsLinearBinding;
import com.xaqinren.healthyelders.moduleMall.bean.GoodsItemBean;
import com.xaqinren.healthyelders.moduleZhiBo.bean.GoodsBean;
import com.xaqinren.healthyelders.utils.GlideUtil;
import com.xaqinren.healthyelders.utils.UrlUtils;

import org.jetbrains.annotations.NotNull;

public class GoodsLinearAdapter extends BaseQuickAdapter<GoodsBean, BaseViewHolder> implements LoadMoreModule {
    public GoodsLinearAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, GoodsBean goodsItemBean) {
        ItemGoodsLinearBinding binding = DataBindingUtil.bind(baseViewHolder.itemView);
        binding.setViewModel(goodsItemBean);
        GlideUtil.intoImageView(getContext(), UrlUtils.resetImgUrl(goodsItemBean.imageUrl, 400, 400), binding.cover, R.mipmap.icon_video_def);
        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) baseViewHolder.itemView.getLayoutParams();
        if (baseViewHolder.getLayoutPosition() == 0) {
            layoutParams.topMargin = (int) getContext().getResources().getDimension(R.dimen.dp_12);
        }else{
            layoutParams.topMargin = 0;
        }
        baseViewHolder.itemView.setLayoutParams(layoutParams);
    }
}
