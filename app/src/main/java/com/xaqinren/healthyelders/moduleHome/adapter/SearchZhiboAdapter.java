package com.xaqinren.healthyelders.moduleHome.adapter;

import android.widget.RelativeLayout;

import androidx.databinding.DataBindingUtil;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ItemSearchZbBinding;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;
import com.xaqinren.healthyelders.utils.GlideUtil;
import com.xaqinren.healthyelders.utils.MScreenUtil;
import com.xaqinren.healthyelders.utils.UrlUtils;


public class SearchZhiboAdapter extends BaseQuickAdapter<VideoInfo, BaseViewHolder> implements LoadMoreModule {
    public SearchZhiboAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, VideoInfo item) {

        //注意 ItemBinding 改为自己item_layout的名字 ItemXxxBinding
        ItemSearchZbBinding binding = DataBindingUtil.bind(helper.itemView);
        binding.setViewModel(item);
        binding.executePendingBindings();

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) binding.rivCover.getLayoutParams();

        //瀑布流图片的宽度
        int itemWidth = MScreenUtil.getScreenWidth(getContext()) - (int)getContext().getResources().getDimension(R.dimen.dp_30);
        //图片真是宽高
        try {
            int oldWidth = Integer.parseInt(UrlUtils.getUrlQueryByTag(item.coverUrl, "w"));
            int oldHeight = Integer.parseInt(UrlUtils.getUrlQueryByTag(item.coverUrl, "h"));
            //计算新高度
            params.height = itemWidth * oldHeight / oldWidth;
            binding.rivCover.setLayoutParams(params);

        } catch (Exception e) {
            e.printStackTrace();
        }

        GlideUtil.intoImageView(getContext(), item.coverUrl, binding.rivCover);
    }
}
