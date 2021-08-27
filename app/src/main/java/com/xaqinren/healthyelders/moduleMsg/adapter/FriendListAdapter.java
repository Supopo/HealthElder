package com.xaqinren.healthyelders.moduleMsg.adapter;

import androidx.databinding.DataBindingUtil;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ItemFriendsListBinding;
import com.xaqinren.healthyelders.databinding.ItemMsgBinding;
import com.xaqinren.healthyelders.moduleLiteav.bean.LiteAvUserBean;
import com.xaqinren.healthyelders.moduleMsg.bean.MsgBean;
import com.xaqinren.healthyelders.utils.GlideUtil;

import java.util.List;

public class FriendListAdapter extends BaseQuickAdapter<LiteAvUserBean, BaseViewHolder> implements LoadMoreModule {
    public FriendListAdapter(int layoutResId) {
        super(layoutResId);
        addChildClickViewIds(R.id.tv_send_msg);
    }

    private int type;

    public FriendListAdapter(int layoutResId, int type) {
        super(layoutResId);
        this.type = type;
        addChildClickViewIds(R.id.tv_send_msg);
    }

    @Override
    protected void convert(BaseViewHolder helper, LiteAvUserBean item) {
        ItemFriendsListBinding binding = DataBindingUtil.bind(helper.itemView);
        binding.setViewModel(item);
        binding.executePendingBindings();
        GlideUtil.intoImageView(getContext(), item.getAvatar(), binding.avatar);
        if (type == 1) {
            binding.tvSendMsg.setText("分享");
        }
    }

    //局部刷新用的
    @Override
    protected void convert(BaseViewHolder helper, LiteAvUserBean item, List<?> payloads) {
        super.convert(helper, item, payloads);
        if (payloads.size() > 0 && payloads.get(0) instanceof Integer) {
            //不为空，即调用notifyItemChanged(position,payloads)后执行的，可以在这里获取payloads中的数据进行局部刷新
            int type = (Integer) payloads.get(0);// 刷新哪个部分 标志位
            if (type == 99) {

            }
        }
    }
}
