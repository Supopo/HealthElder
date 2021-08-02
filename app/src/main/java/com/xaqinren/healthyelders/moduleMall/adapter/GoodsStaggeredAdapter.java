package com.xaqinren.healthyelders.moduleMall.adapter;

import android.widget.RelativeLayout;

import androidx.databinding.DataBindingUtil;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tencent.qcloud.tim.uikit.utils.ScreenUtil;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ItemGoodsStaggeredBinding;
import com.xaqinren.healthyelders.moduleZhiBo.bean.GoodsBean;
import com.xaqinren.healthyelders.utils.GlideUtil;
import com.xaqinren.healthyelders.utils.UrlUtils;

import org.jetbrains.annotations.NotNull;

public class GoodsStaggeredAdapter extends BaseQuickAdapter<GoodsBean, BaseViewHolder> implements LoadMoreModule {
    public GoodsStaggeredAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, GoodsBean goodsItemBean) {
        //注意 ItemBinding 改为自己item_layout的名字 ItemXxxBinding
        ItemGoodsStaggeredBinding binding = DataBindingUtil.bind(baseViewHolder.itemView);
        binding.setViewModel(goodsItemBean);
        binding.executePendingBindings();
        GlideUtil.intoImageView(getContext(), UrlUtils.resetImgUrl(goodsItemBean.imageUrl, 400, 400), binding.cover, R.mipmap.icon_video_def);

        int screenWidth = ScreenUtil.getScreenWidth(getContext());
        //瀑布流图片的宽度 4+3+4
        int itemWidth = (screenWidth - (int) getContext().getResources().getDimension(R.dimen.dp_3)) / 2;

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) binding.cover.getLayoutParams();
        params.height = itemWidth;
        binding.cover.setLayoutParams(params);

    }
}
