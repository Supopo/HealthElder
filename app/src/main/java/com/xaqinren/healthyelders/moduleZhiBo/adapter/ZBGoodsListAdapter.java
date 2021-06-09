package com.xaqinren.healthyelders.moduleZhiBo.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ItemZbGoodsBinding;
import com.xaqinren.healthyelders.moduleZhiBo.bean.GoodsBean;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ZBGoodsBean;

import java.util.List;


public class ZBGoodsListAdapter extends BaseQuickAdapter<ZBGoodsBean, BaseViewHolder> implements LoadMoreModule {
    public ZBGoodsListAdapter(int layoutResId) {
        super(layoutResId);
        addChildClickViewIds(R.id.tv_show);
    }


    @Override
    protected void convert(BaseViewHolder helper, ZBGoodsBean item) {

        //注意 ItemBinding 改为自己item_layout的名字 ItemXxxBinding
        ItemZbGoodsBinding binding = DataBindingUtil.bind(helper.itemView);
        binding.setViewModel(item);
        binding.executePendingBindings();

        binding.tvPos.setText("" + (helper.getAdapterPosition() + 1));

        if (item.type == 1) {
            if (item.getCanExplain()) {
                binding.tvShow.setText("取消讲解");
                binding.tvShow.setTextColor(getContext().getResources().getColor(R.color.color_252525));
                binding.tvShow.setBackground(getContext().getResources().getDrawable(R.drawable.bg_btn_remove));
                helper.setGone(R.id.rl_show, false);
            } else {
                binding.tvShow.setText("讲解");
                binding.tvShow.setTextColor(getContext().getResources().getColor(R.color.white));
                binding.tvShow.setBackground(getContext().getResources().getDrawable(R.drawable.bg_btn_comfrim_shape));
                helper.setGone(R.id.rl_show, true);
            }
        } else if (item.type == 2) {
            binding.tvShow.setText("购买");
        }

    }

    //局部刷新用的
    @Override
    protected void convert(BaseViewHolder helper, ZBGoodsBean item, List<?> payloads) {
        super.convert(helper, item, payloads);
        if (payloads.size() > 0 && payloads.get(0) instanceof Integer) {
            //不为空，即调用notifyItemChanged(position,payloads)后执行的，可以在这里获取payloads中的数据进行局部刷新
            int type = (Integer) payloads.get(0);// 刷新哪个部分 标志位
            if (type == 99) {
                TextView tvShow = helper.getView(R.id.tv_show);
                if (item.getCanExplain()) {
                    helper.setGone(R.id.rl_show, false);
                    tvShow.setText("取消讲解");
                    tvShow.setTextColor(getContext().getResources().getColor(R.color.color_252525));
                    tvShow.setBackground(getContext().getResources().getDrawable(R.drawable.bg_btn_remove));

                } else {
                    helper.setGone(R.id.rl_show, true);
                    tvShow.setText("讲解");
                    tvShow.setTextColor(getContext().getResources().getColor(R.color.white));
                    tvShow.setBackground(getContext().getResources().getDrawable(R.drawable.bg_btn_comfrim_shape));
                }
            }
        }
    }
}
