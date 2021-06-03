package com.xaqinren.healthyelders.moduleZhiBo.adapter;

import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ItemPayTypeBinding;
import com.xaqinren.healthyelders.moduleHome.bean.MenuBean;
import com.xaqinren.healthyelders.utils.GlideUtil;

import java.util.List;


public class PayTypeAdapter extends BaseQuickAdapter<MenuBean, BaseViewHolder> implements LoadMoreModule {
    public PayTypeAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, MenuBean item) {

        //注意 ItemBinding 改为自己item_layout的名字 ItemXxxBinding
        ItemPayTypeBinding binding = DataBindingUtil.bind(helper.itemView);
        binding.setViewModel(item);
        binding.executePendingBindings();

        GlideUtil.intoImageView(getContext(), item.menuRes, binding.ivPay);

        if (item.menuName.equals("我的零钱")) {
            binding.tvDes.setVisibility(View.VISIBLE);
            binding.tvPay.setTextColor(getContext().getResources().getColor(R.color.gray_999));
        } else {
            binding.tvDes.setVisibility(View.INVISIBLE);
            binding.tvPay.setTextColor(getContext().getResources().getColor(R.color.color_252525));
        }

        if (item.isSelect)
            helper.getView(R.id.iv_select).setBackgroundResource(R.mipmap.icon_rad_sel);
        else
            helper.getView(R.id.iv_select).setBackgroundResource(R.mipmap.icon_rad_nor);
    }

    //局部刷新用的
    @Override
    protected void convert(BaseViewHolder helper, MenuBean item, List<?> payloads) {
        super.convert(helper, item, payloads);
        if (payloads.size() > 0 && payloads.get(0) instanceof Integer) {
            //不为空，即调用notifyItemChanged(position,payloads)后执行的，可以在这里获取payloads中的数据进行局部刷新
            int type = (Integer) payloads.get(0);// 刷新哪个部分 标志位
            if (type == 99) {
                helper.setText(R.id.tv_des, item.subMenuName);

                if (item.isSelect)
                    helper.getView(R.id.iv_select).setBackgroundResource(R.mipmap.icon_rad_sel);
                else
                    helper.getView(R.id.iv_select).setBackgroundResource(R.mipmap.icon_rad_nor);
            }
        }
    }
}
