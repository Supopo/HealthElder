package com.xaqinren.healthyelders.moduleHome.adapter;

import androidx.databinding.DataBindingUtil;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.bean.SlideBarBean;
import com.xaqinren.healthyelders.databinding.ItemSearchHistoryBinding;
import com.xaqinren.healthyelders.moduleHome.bean.SearchBean;

import java.util.List;


public class HistoryTagAdapter extends BaseQuickAdapter<SlideBarBean.MenuInfoListDTO, BaseViewHolder> implements LoadMoreModule {
    public HistoryTagAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, SlideBarBean.MenuInfoListDTO item) {

        //注意 ItemBinding 改为自己item_layout的名字 ItemXxxBinding
        ItemSearchHistoryBinding binding = DataBindingUtil.bind(helper.itemView);
        binding.setViewModel(item);
        binding.executePendingBindings();
    }

    //局部刷新用的
    @Override
    protected void convert(BaseViewHolder helper, SlideBarBean.MenuInfoListDTO item, List<?> payloads) {
        super.convert(helper, item, payloads);
        if (payloads.size() > 0 && payloads.get(0) instanceof Integer) {
            //不为空，即调用notifyItemChanged(position,payloads)后执行的，可以在这里获取payloads中的数据进行局部刷新
            int type = (Integer) payloads.get(0);// 刷新哪个部分 标志位

        }
    }
}
