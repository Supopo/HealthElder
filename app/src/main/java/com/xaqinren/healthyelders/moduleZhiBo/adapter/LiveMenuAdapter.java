package com.xaqinren.healthyelders.moduleZhiBo.adapter;

import androidx.databinding.DataBindingUtil;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ItemStartLiveMenuBinding;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.moduleHome.bean.MenuBean;
import com.xaqinren.healthyelders.moduleZhiBo.bean.GoodsBean;
import com.xaqinren.healthyelders.utils.GlideUtil;

import java.util.List;

import me.goldze.mvvmhabit.utils.ImageUtils;


public class LiveMenuAdapter extends BaseQuickAdapter<MenuBean, BaseViewHolder> implements LoadMoreModule {
    public LiveMenuAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, MenuBean item) {

        //注意 ItemBinding 改为自己item_layout的名字 ItemXxxBinding
        ItemStartLiveMenuBinding binding = DataBindingUtil.bind(helper.itemView);
        binding.setViewModel(item);
        binding.executePendingBindings();

        GlideUtil.intoImageView(getContext(), item.menuRes, binding.ivMenu);
        if (item.type == 1) {
            binding.tvMenu.setTextColor(getContext().getResources().getColor(R.color.color_252525));
        }

    }

    //局部刷新用的
    @Override
    protected void convert(BaseViewHolder helper, MenuBean item, List<?> payloads) {
        super.convert(helper, item, payloads);
        if (payloads.size() > 0 && payloads.get(0) instanceof Integer) {
            //不为空，即调用notifyItemChanged(position,payloads)后执行的，可以在这里获取payloads中的数据进行局部刷新
            int type = (Integer) payloads.get(0);// 刷新哪个部分 标志位
            if (type == 99) {
                GlideUtil.intoImageView(getContext(), item.menuRes, helper.getView(R.id.iv_menu));
                helper.setText(R.id.tv_menu, item.menuName);
            }
        }
    }

}
