package com.xaqinren.healthyelders.moduleHome.adapter;

import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.databinding.DataBindingUtil;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tencent.qcloud.tim.uikit.utils.ScreenUtil;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ItemSearchGoodsBinding;
import com.xaqinren.healthyelders.databinding.ItemSearchVideoBinding;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;
import com.xaqinren.healthyelders.utils.GlideUtil;
import com.xaqinren.healthyelders.utils.UrlUtils;


public class SearchGoodsAdapter extends BaseQuickAdapter<VideoInfo, BaseViewHolder> implements LoadMoreModule {
    public SearchGoodsAdapter(int layoutResId) {
        super(layoutResId);
        addChildClickViewIds(R.id.ll_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, VideoInfo item) {

        //注意 ItemBinding 改为自己item_layout的名字 ItemXxxBinding
        ItemSearchGoodsBinding binding = DataBindingUtil.bind(helper.itemView);
        binding.setViewModel(item);
        binding.executePendingBindings();
        GlideUtil.intoImageView(getContext(), UrlUtils.resetImgUrl(item.coverUrl, 600, 600), binding.ivCover, R.mipmap.bg_video);

        int screenWidth = ScreenUtil.getScreenWidth(getContext());
        //瀑布流图片的宽度
        int itemWidth = (screenWidth - (int) getContext().getResources().getDimension(R.dimen.dp_3)) / 2;

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) binding.ivCover.getLayoutParams();
        params.height = itemWidth;
        binding.ivCover.setLayoutParams(params);
    }
}
