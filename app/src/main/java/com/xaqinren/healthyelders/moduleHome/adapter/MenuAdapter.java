package com.xaqinren.healthyelders.moduleHome.adapter;

import android.widget.TextView;

import androidx.databinding.DataBindingUtil;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ItemHomeMenu2Binding;
import com.xaqinren.healthyelders.moduleHome.bean.MenuBean;

import java.util.List;


public class MenuAdapter extends BaseQuickAdapter<MenuBean, BaseViewHolder> implements LoadMoreModule {
    public MenuAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, MenuBean item) {

        //注意 ItemBinding 改为自己item_layout的名字 ItemXxxBinding
        ItemHomeMenu2Binding binding = DataBindingUtil.bind(helper.itemView);
        binding.setViewModel(item);
        binding.executePendingBindings();
        TextView tvMenu = helper.getView(R.id.tv_title);
        tvMenu.setTextColor(android.graphics.Color.parseColor(item.fontColor));
        TextView tvDes = helper.getView(R.id.tv_des);
        tvDes.setTextColor(android.graphics.Color.parseColor(item.subFontColor));
        helper.setBackgroundColor(R.id.ll_item, android.graphics.Color.parseColor(item.backgroundColor));
    }

    //局部刷新用的
    @Override
    protected void convert(BaseViewHolder helper, MenuBean item, List<?> payloads) {
        super.convert(helper, item, payloads);
        if (payloads.size() > 0 && payloads.get(0) instanceof Integer) {
            //不为空，即调用notifyItemChanged(position,payloads)后执行的，可以在这里获取payloads中的数据进行局部刷新
            int type = (Integer) payloads.get(0);// 刷新哪个部分 标志位

        }
    }
}
