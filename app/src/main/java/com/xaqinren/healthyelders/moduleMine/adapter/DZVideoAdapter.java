package com.xaqinren.healthyelders.moduleMine.adapter;

import androidx.databinding.DataBindingUtil;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ItemMineDzVideoBinding;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;
import com.xaqinren.healthyelders.moduleMine.bean.DZVideoInfo;
import com.xaqinren.healthyelders.utils.GlideUtil;
import com.xaqinren.healthyelders.utils.UrlUtils;

import java.util.List;


public class DZVideoAdapter extends BaseQuickAdapter<DZVideoInfo, BaseViewHolder> implements LoadMoreModule {
    public DZVideoAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, DZVideoInfo item) {

        //注意 ItemBinding 改为自己item_layout的名字 ItemXxxBinding
        ItemMineDzVideoBinding binding = DataBindingUtil.bind(helper.itemView);
        binding.setViewModel(item);
        binding.executePendingBindings();
        if (item.homeComprehensiveHall != null) {
            GlideUtil.intoImageView(getContext(), UrlUtils.resetImgUrl(item.homeComprehensiveHall.coverUrl, 400, 400), binding
                    .ivVideo, R.mipmap.icon_video_def);
            binding.tvNum.setText("" + item.homeComprehensiveHall.getFavoriteCount());
        }

    }

    //局部刷新用的
    @Override
    protected void convert(BaseViewHolder helper, DZVideoInfo item, List<?> payloads) {
        super.convert(helper, item, payloads);
        if (payloads.size() > 0 && payloads.get(0) instanceof Integer) {
            //不为空，即调用notifyItemChanged(position,payloads)后执行的，可以在这里获取payloads中的数据进行局部刷新
            int type = (Integer) payloads.get(0);// 刷新哪个部分 标志位
            if (type == 99) {
                helper.setText(R.id.tv_num, item.homeComprehensiveHall.favoriteCount);
            }
        }
    }
}
