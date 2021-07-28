package com.xaqinren.healthyelders.moduleZhiBo.adapter;

import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ItemMallMenu1Binding;
import com.xaqinren.healthyelders.databinding.ItemZbGiftBinding;
import com.xaqinren.healthyelders.moduleHome.bean.MenuBean;
import com.xaqinren.healthyelders.moduleZhiBo.bean.GiftBean;
import com.xaqinren.healthyelders.utils.GlideUtil;
import com.xaqinren.healthyelders.utils.UrlUtils;

import java.util.List;


public class GiftListAdapter extends BaseQuickAdapter<GiftBean, BaseViewHolder> implements LoadMoreModule {
    public GiftListAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, GiftBean item) {

        //注意 ItemBinding 改为自己item_layout的名字 ItemXxxBinding
        ItemZbGiftBinding binding = DataBindingUtil.bind(helper.itemView);
        binding.setViewModel(item);
        binding.executePendingBindings();
        GlideUtil.intoImageView(getContext(), item.giftImage, binding.ivGiftImg, R.mipmap.icon_logo_def);

        if (item.isSelect) {
            binding.tvGiftName.setVisibility(View.GONE);
            binding.tvSend.setVisibility(View.VISIBLE);
            binding.rlGift.setBackgroundResource(R.drawable.bg_gift_select);
        } else {
            binding.tvGiftName.setVisibility(View.VISIBLE);
            binding.tvSend.setVisibility(View.GONE);
            binding.rlGift.setBackgroundResource(0);
        }
    }

    //局部刷新用的
    @Override
    protected void convert(BaseViewHolder helper, GiftBean item, List<?> payloads) {
        super.convert(helper, item, payloads);
        if (payloads.size() > 0 && payloads.get(0) instanceof Integer) {
            //不为空，即调用notifyItemChanged(position,payloads)后执行的，可以在这里获取payloads中的数据进行局部刷新
            int type = (Integer) payloads.get(0);// 刷新哪个部分 标志位
            if (type == 99) {
                if (item.isSelect) {
                    helper.getView(R.id.tv_gift_name).setVisibility(View.GONE);
                    helper.getView(R.id.tv_send).setVisibility(View.VISIBLE);
                    helper.getView(R.id.rl_gift).setBackgroundResource(R.drawable.bg_gift_select);
                } else {
                    helper.getView(R.id.tv_gift_name).setVisibility(View.VISIBLE);
                    helper.getView(R.id.tv_send).setVisibility(View.GONE);
                    helper.getView(R.id.rl_gift).setBackgroundResource(0);
                }
            }
        }
    }
}
