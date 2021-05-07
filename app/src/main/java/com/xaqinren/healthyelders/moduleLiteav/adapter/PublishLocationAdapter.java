package com.xaqinren.healthyelders.moduleLiteav.adapter;

import android.graphics.drawable.Drawable;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ItemPublishLocationBinding;
import com.xaqinren.healthyelders.databinding.ItemPublishTopicAdapterBinding;
import com.xaqinren.healthyelders.moduleLiteav.bean.LocationBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.TopicBean;

import org.jetbrains.annotations.NotNull;


public class PublishLocationAdapter extends BaseQuickAdapter<LocationBean, BaseViewHolder> {

    public PublishLocationAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, LocationBean topicBean) {
        ItemPublishLocationBinding binding = DataBindingUtil.bind(baseViewHolder.itemView);
        binding.setViewModel(topicBean);
        binding.executePendingBindings();
        TextView textView = baseViewHolder.getView(R.id.location);
        if (topicBean.isSelLocation) {
            textView.setSelected(true);
        }else{
            textView.setSelected(false);
        }
        if (topicBean.isLookMore) {
            Drawable drawable = getContext().getResources().getDrawable(R.mipmap.ship_dizhi_search);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            textView.setCompoundDrawables(drawable, null, null, null);
        }else{
            textView.setCompoundDrawables(null, null, null, null);
        }
    }
}
