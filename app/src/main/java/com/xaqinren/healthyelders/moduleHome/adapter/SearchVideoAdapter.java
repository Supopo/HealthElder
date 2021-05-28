package com.xaqinren.healthyelders.moduleHome.adapter;

import android.widget.LinearLayout;

import androidx.databinding.DataBindingUtil;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tencent.qcloud.tim.uikit.utils.ScreenUtil;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ItemSearchVideoBinding;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;
import com.xaqinren.healthyelders.utils.UrlUtils;


public class SearchVideoAdapter extends BaseQuickAdapter<VideoInfo, BaseViewHolder> implements LoadMoreModule {
    public SearchVideoAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, VideoInfo item) {

        //注意 ItemBinding 改为自己item_layout的名字 ItemXxxBinding
        ItemSearchVideoBinding binding = DataBindingUtil.bind(helper.itemView);
        binding.setViewModel(item);
        binding.executePendingBindings();


        int screenWidth = ScreenUtil.getScreenWidth(getContext());
        //瀑布流图片的宽度
        int itemWidth = (screenWidth - (int) getContext().getResources().getDimension(R.dimen.dp_3)) / 2;

        //图片真是宽高
        try {
            int oldWidth = Integer.parseInt(UrlUtils.getUrlQueryByTag(item.coverUrl, "w"));
            int oldHeight = Integer.parseInt(UrlUtils.getUrlQueryByTag(item.coverUrl, "h"));
            //计算新高度
            int newHeight = itemWidth * oldHeight / oldWidth;

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) binding.rlItem.getLayoutParams();
            params.height = newHeight;
            binding.rlItem.setLayoutParams(params);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
