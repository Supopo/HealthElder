package com.xaqinren.healthyelders.moduleZhiBo.adapter;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ItemChongzhiBtnBinding;
import com.xaqinren.healthyelders.moduleHome.bean.MenuBean;
import com.xaqinren.healthyelders.utils.MScreenUtil;


public class ChongZhiKeyBordAdapter extends BaseQuickAdapter<MenuBean, BaseViewHolder> implements LoadMoreModule {
    public ChongZhiKeyBordAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, MenuBean item) {

        //注意 ItemBinding 改为自己item_layout的名字 ItemXxxBinding
        ItemChongzhiBtnBinding binding = DataBindingUtil.bind(helper.itemView);
        binding.setViewModel(item);
        binding.executePendingBindings();

        //设定了imageView的高度之后 加载时候就不会闪烁了
        StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) binding.llItem.getLayoutParams();

        if (item.menuName.equals("充值")) {
            params.height = (int) getContext().getResources().getDimension(R.dimen.dp_150);
            binding.llItem.setLayoutParams(params);
            binding.llItem.setBackground(getContext().getResources().getDrawable(R.drawable.bg_btn_comfrim_shape));
            binding.tvMenu.setTextColor(getContext().getResources().getColor(R.color.white));
        } else if (item.menuName.equals("0")) {
            int ScreenWidth = MScreenUtil.getScreenWidth(getContext());
            params.width = (3 * (ScreenWidth - (int) getContext().getResources().getDimension(R.dimen.dp_56)) / 4) + ((int) getContext().getResources().getDimension(R.dimen.dp_16));
            binding.llItem.setLayoutParams(params);
        }


    }

}
