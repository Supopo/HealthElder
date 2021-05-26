package com.xaqinren.healthyelders.moduleMall.adapter;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.chad.library.adapter.base.module.BaseLoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ItemMallRvBinding;
import com.xaqinren.healthyelders.moduleHome.bean.MenuBean;
import com.xaqinren.healthyelders.widget.SpeacesItemDecoration;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Lee. on 2021/5/25.
 */
public class MallGoodsPageAdapter extends BaseQuickAdapter<MenuBean, BaseViewHolder> {

    public MallGoodsPageAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder helper, MenuBean menuBean) {
        //注意 ItemBinding 改为自己item_layout的名字 ItemXxxBinding
        ItemMallRvBinding binding = DataBindingUtil.bind(helper.itemView);
        binding.setViewModel(menuBean);
        binding.executePendingBindings();
    }
}
