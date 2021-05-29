package com.xaqinren.healthyelders.moduleLiteav.adapter;

import androidx.databinding.DataBindingUtil;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.databinding.ItemLiteAvLocationBinding;
import com.xaqinren.healthyelders.moduleLiteav.bean.LocationBean;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

import me.goldze.mvvmhabit.utils.StringUtils;

public class ChooseLocationAdapter extends BaseQuickAdapter<LocationBean, BaseViewHolder> implements LoadMoreModule {
    DecimalFormat decimalFormat=new DecimalFormat(".00");
    public ChooseLocationAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, LocationBean selLocationBean) {
        ItemLiteAvLocationBinding binding = DataBindingUtil.bind(baseViewHolder.itemView);
        binding.setViewModel(selLocationBean);
        binding.executePendingBindings();
        if (StringUtils.isEmpty(selLocationBean.distance)) {
            binding.distanceView.setText(null);
        }else {
            float distance = Float.valueOf(selLocationBean.distance);
            if (distance == 0)
            {
                binding.distanceView.setText(null);
            }
            else if (distance < 1000) {
                binding.distanceView.setText(distance + "m");
            }else {
                String dis = decimalFormat.format(distance / 1000);
                binding.distanceView.setText(dis + "km");
            }
        }
    }
}
