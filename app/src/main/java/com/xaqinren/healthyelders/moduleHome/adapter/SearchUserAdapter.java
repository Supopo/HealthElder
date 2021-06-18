package com.xaqinren.healthyelders.moduleHome.adapter;

import android.widget.TextView;

import androidx.databinding.DataBindingUtil;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ItemSearchUserBinding;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;

import java.util.List;


public class SearchUserAdapter extends BaseQuickAdapter<VideoInfo, BaseViewHolder> implements LoadMoreModule {
    public SearchUserAdapter(int layoutResId) {
        super(layoutResId);
        addChildClickViewIds(R.id.tv_follow,R.id.rl_avatar);
    }

    @Override
    protected void convert(BaseViewHolder helper, VideoInfo item) {

        //注意 ItemBinding 改为自己item_layout的名字 ItemXxxBinding
        ItemSearchUserBinding binding = DataBindingUtil.bind(helper.itemView);
        binding.setViewModel(item);
        binding.executePendingBindings();

        if (!item.hasAttention) {
            binding.tvFollow.setText("关注");
            binding.tvFollow.setTextColor(getContext().getResources().getColor(R.color.white));
            binding.tvFollow.setBackground(getContext().getResources().getDrawable(R.drawable.bg_btn_comfrim_shape_15));
        } else {
            binding.tvFollow.setText("已关注");
            binding.tvFollow.setTextColor(getContext().getResources().getColor(R.color.color_252525));
            binding.tvFollow.setBackground(getContext().getResources().getDrawable(R.drawable.btn_follow));
        }
    }

    //局部刷新用的
    @Override
    protected void convert(BaseViewHolder helper, VideoInfo item, List<?> payloads) {
        super.convert(helper, item, payloads);
        if (payloads.size() > 0 && payloads.get(0) instanceof Integer) {
            //不为空，即调用notifyItemChanged(position,payloads)后执行的，可以在这里获取payloads中的数据进行局部刷新
            int type = (Integer) payloads.get(0);// 刷新哪个部分 标志位
            if (type == 99) {
                TextView tvFollow = (TextView) helper.getView(R.id.tv_follow);
                if (!item.hasAttention) {
                    tvFollow.setText("关注");
                    tvFollow.setTextColor(getContext().getResources().getColor(R.color.white));
                    tvFollow.setBackground(getContext().getResources().getDrawable(R.drawable.bg_btn_comfrim_shape_15));
                } else {
                    tvFollow.setText("已关注");
                    tvFollow.setTextColor(getContext().getResources().getColor(R.color.color_252525));
                    tvFollow.setBackground(getContext().getResources().getDrawable(R.drawable.btn_follow));
                }
            }
        }
    }
}
