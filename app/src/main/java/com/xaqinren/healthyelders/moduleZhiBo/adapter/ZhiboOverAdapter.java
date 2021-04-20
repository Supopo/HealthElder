package com.xaqinren.healthyelders.moduleZhiBo.adapter;

import android.widget.RelativeLayout;

import androidx.databinding.DataBindingUtil;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tencent.qcloud.tim.uikit.utils.ScreenUtil;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ItemZhiboUserBeanBinding;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ZhiboUserBean;

import java.util.List;


/**
 * 直播结束页面Top榜单头像列表
 */
public class ZhiboOverAdapter extends BaseQuickAdapter<ZhiboUserBean, BaseViewHolder> implements LoadMoreModule {
    public ZhiboOverAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, ZhiboUserBean item) {

        //注意 ItemBinding 改为自己item_layout的名字 ItemXxxBinding
        ItemZhiboUserBeanBinding binding = DataBindingUtil.bind(helper.itemView);
        binding.setViewModel(item);
        binding.executePendingBindings();

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        if (helper.getLayoutPosition() != 0) {
            layoutParams.setMargins((int) (ScreenUtil.dp2px(getContext(), 13)), 0, 0, 0);
        }
        binding.rlItem.setLayoutParams(layoutParams);
        binding.tvNum.setText("0" + (helper.getLayoutPosition() + 1));
        binding.tvNum.setTextColor(getContext().getResources().getColor(colors[helper.getLayoutPosition()]));
    }

    private int[] colors = new int[]{R.color.color_FFE6D074, R.color.color_FFB8C6DC, R.color.color_FFB48D73, R.color.color_FF6E6E78, R.color.color_FF6E6E78};

    //局部刷新用的
    @Override
    protected void convert(BaseViewHolder helper, ZhiboUserBean item, List<?> payloads) {
        super.convert(helper, item, payloads);
        if (payloads.size() > 0 && payloads.get(0) instanceof Integer) {
            //不为空，即调用notifyItemChanged(position,payloads)后执行的，可以在这里获取payloads中的数据进行局部刷新
            int type = (Integer) payloads.get(0);// 刷新哪个部分 标志位

        }
    }
}
