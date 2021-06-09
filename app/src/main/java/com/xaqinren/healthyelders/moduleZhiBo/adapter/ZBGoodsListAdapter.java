package com.xaqinren.healthyelders.moduleZhiBo.adapter;

import android.widget.TextView;

import androidx.databinding.DataBindingUtil;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ItemZbGoodsBinding;
import com.xaqinren.healthyelders.moduleZhiBo.bean.GoodsBean;

import java.util.List;


public class ZBGoodsListAdapter extends BaseQuickAdapter<GoodsBean, BaseViewHolder> implements LoadMoreModule {
    public ZBGoodsListAdapter(int layoutResId) {
        super(layoutResId);
        addChildClickViewIds(R.id.tv_show);
    }


    @Override
    protected void convert(BaseViewHolder helper, GoodsBean item) {

        //注意 ItemBinding 改为自己item_layout的名字 ItemXxxBinding
        ItemZbGoodsBinding binding = DataBindingUtil.bind(helper.itemView);
        binding.setViewModel(item);
        binding.executePendingBindings();

        if (item.type == 1) {
            if (item.canExplain) {
                binding.tvShow.setText("取消讲解");
            } else {
                binding.tvShow.setText("讲解");
            }
        }else if(item.type == 2){
            binding.tvShow.setText("购买");
        }

    }

    //局部刷新用的
    @Override
    protected void convert(BaseViewHolder helper, GoodsBean item, List<?> payloads) {
        super.convert(helper, item, payloads);
        if (payloads.size() > 0 && payloads.get(0) instanceof Integer) {
            //不为空，即调用notifyItemChanged(position,payloads)后执行的，可以在这里获取payloads中的数据进行局部刷新
            int type = (Integer) payloads.get(0);// 刷新哪个部分 标志位
            if (type == 99) {
                TextView tvShow = helper.getView(R.id.tv_show);
                if (item.canExplain) {
                    helper.setGone(R.id.rl_show, true);
                    tvShow.setText("取消讲解");
                } else {
                    helper.setGone(R.id.rl_show, false);
                    tvShow.setText("讲解");
                }
            }
        }
    }
}
