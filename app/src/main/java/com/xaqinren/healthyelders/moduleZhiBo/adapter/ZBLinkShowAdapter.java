package com.xaqinren.healthyelders.moduleZhiBo.adapter;

import android.text.TextUtils;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ItemZbLinkShowBinding;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ZBUserListBean;


import java.util.List;

/**
 * 主播页面接受连麦列表
 */
public class ZBLinkShowAdapter extends BaseQuickAdapter<ZBUserListBean, BaseViewHolder> implements LoadMoreModule {
    public ZBLinkShowAdapter(int layoutResId) {
        super(layoutResId);
        addChildClickViewIds(R.id.ll_js);
    }

    @Override
    protected void convert(BaseViewHolder helper, ZBUserListBean item) {
        ItemZbLinkShowBinding binding = DataBindingUtil.bind(helper.itemView);
        binding.setViewModel(item);
        binding.executePendingBindings();


        helper.setText(R.id.tv_name, item.nickname);
        helper.setText(R.id.tv_time, "(" + item.showTime + "s)");
    }

    //局部刷新用的
    @Override
    protected void convert(BaseViewHolder helper, ZBUserListBean item, List<?> payloads) {
        super.convert(helper, item, payloads);
        if (payloads.size() > 0 && payloads.get(0) instanceof Integer) {
            //不为空，即调用notifyItemChanged(position,payloads)后执行的，可以在这里获取payloads中的数据进行局部刷新
            int type = (Integer) payloads.get(0);// 刷新哪个部分 标志位
            if (type == 99) {
                helper.setText(R.id.tv_time, "(" + item.showTime + "s)");
            }
        }
    }
}
