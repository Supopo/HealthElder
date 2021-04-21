package com.xaqinren.healthyelders.moduleZhiBo.adapter;

import androidx.databinding.DataBindingUtil;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ItemGoodsBeanBinding;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.moduleZhiBo.bean.GoodsBean;

import java.util.List;


public class GoodsListAdapter extends BaseQuickAdapter<GoodsBean, BaseViewHolder> implements LoadMoreModule {
    public GoodsListAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, GoodsBean item) {

        //注意 ItemBinding 改为自己item_layout的名字 ItemXxxBinding
        ItemGoodsBeanBinding binding = DataBindingUtil.bind(helper.itemView);
        binding.setViewModel(item);
        binding.executePendingBindings();

        if (item.isSelect)
            helper.getView(R.id.iv_select).setBackgroundResource(R.mipmap.icon_rad_sel);
        else
            helper.getView(R.id.iv_select).setBackgroundResource(R.mipmap.icon_rad_nor);
    }

    //局部刷新用的
    @Override
    protected void convert(BaseViewHolder helper, GoodsBean item, List<?> payloads) {
        super.convert(helper, item, payloads);
        if (payloads.size() > 0 && payloads.get(0) instanceof Integer) {
            //不为空，即调用notifyItemChanged(position,payloads)后执行的，可以在这里获取payloads中的数据进行局部刷新
            int type = (Integer) payloads.get(0);// 刷新哪个部分 标志位
            if (type == CodeTable.RESH_VIEW_CODE) {
                if (item.isSelect)
                    helper.getView(R.id.iv_select).setBackgroundResource(R.mipmap.icon_rad_sel);
                else
                    helper.getView(R.id.iv_select).setBackgroundResource(R.mipmap.icon_rad_nor);
            }
        }
    }
}
