package com.xaqinren.healthyelders.moduleZhiBo.adapter;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ItemUserSettingBinding;
import com.xaqinren.healthyelders.databinding.ItemZbListUserBinding;
import com.xaqinren.healthyelders.moduleHome.bean.MenuBean;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ZBUserListBean;

import java.util.List;

/**
 * 黑名单列表
 */
public class ZBBlackListAdapter extends BaseQuickAdapter<ZBUserListBean, BaseViewHolder> implements LoadMoreModule {
    public ZBBlackListAdapter(int layoutResId) {
        super(layoutResId);
        addChildClickViewIds(R.id.tv_remove);
    }

    @Override
    protected void convert(BaseViewHolder helper, ZBUserListBean item) {
        ItemUserSettingBinding binding = DataBindingUtil.bind(helper.itemView);
        binding.setViewModel(item);
        binding.executePendingBindings();

        if (item.showType == 0) {
            binding.tvRemove.setText("移除");
        } else {
            binding.tvRemove.setText("取消");
        }
    }


    //局部刷新用的
    @Override
    protected void convert(BaseViewHolder helper, ZBUserListBean item, List<?> payloads) {
        super.convert(helper, item, payloads);
        if (payloads.size() > 0 && payloads.get(0) instanceof Integer) {
            //不为空，即调用notifyItemChanged(position,payloads)后执行的，可以在这里获取payloads中的数据进行局部刷新
            int type = (Integer) payloads.get(0);// 刷新哪个部分 标志位
            if (type == 99) {
                if (item.hasSpeech) {
                    helper.setText(R.id.tv_remove, "取消");
                }else {
                    helper.setText(R.id.tv_remove, "禁言");
                }
            }
        }
    }
}
