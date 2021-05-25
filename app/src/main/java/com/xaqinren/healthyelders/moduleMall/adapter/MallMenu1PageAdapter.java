package com.xaqinren.healthyelders.moduleMall.adapter;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ItemMallRvBinding;
import com.xaqinren.healthyelders.moduleHome.bean.MenuBean;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Lee. on 2021/5/25.
 */
public class MallMenu1PageAdapter extends BaseQuickAdapter<MenuBean, BaseViewHolder> {

    public MallMenu1PageAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder helper, MenuBean menuBean) {
        //注意 ItemBinding 改为自己item_layout的名字 ItemXxxBinding
        ItemMallRvBinding binding = DataBindingUtil.bind(helper.itemView);
        binding.setViewModel(menuBean);
        binding.executePendingBindings();

        MallMenu1Adapter mallMenu1Adapter = new MallMenu1Adapter(R.layout.item_mall_menu1);
        binding.rvMenu.setLayoutManager(new GridLayoutManager(getContext(), 4));
        binding.rvMenu.setAdapter(mallMenu1Adapter);
        mallMenu1Adapter.setList(menuBean.menuBeans);

    }
}
