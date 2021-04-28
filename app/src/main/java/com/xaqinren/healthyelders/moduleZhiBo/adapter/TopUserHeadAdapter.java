package com.xaqinren.healthyelders.moduleZhiBo.adapter;

import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ItemTopUserHeadBinding;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ZBUserListBean;


import java.util.List;

/**
 * Description: TopUserHeadAdapter
 * 直播间榜单头像展示
 */
public class TopUserHeadAdapter extends BaseQuickAdapter<ZBUserListBean, BaseViewHolder> implements LoadMoreModule {


    public TopUserHeadAdapter(int layoutResId, @Nullable List<ZBUserListBean> data) {
        super(layoutResId, data);
    }

    public TopUserHeadAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, ZBUserListBean item) {
        ItemTopUserHeadBinding binding = DataBindingUtil.bind(helper.itemView);
        binding.setViewModel(item);
        binding.executePendingBindings();
    }
}
