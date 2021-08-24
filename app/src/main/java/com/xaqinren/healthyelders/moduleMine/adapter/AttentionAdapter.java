package com.xaqinren.healthyelders.moduleMine.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.databinding.ItemAttentionBinding;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;
import com.xaqinren.healthyelders.moduleLiteav.bean.LiteAvUserBean;
import com.xaqinren.healthyelders.moduleMsg.adapter.provider.FriendProvider;
import com.xaqinren.healthyelders.utils.GlideUtil;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.dcloud.common.cs.DA;

public class AttentionAdapter extends BaseQuickAdapter<LiteAvUserBean, BaseViewHolder> implements LoadMoreModule {
    boolean showClose;

    public AttentionAdapter(int layoutResId, boolean showClose) {
        super(layoutResId);
        addChildClickViewIds(R.id.avatar, R.id.attention_btn, R.id.close);
        this.showClose = showClose;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, LiteAvUserBean liteAvUserBean) {
        ItemAttentionBinding binding = DataBindingUtil.bind(baseViewHolder.itemView);
        GlideUtil.intoImageView(getContext(), liteAvUserBean.attentionUserInfo.avatarUrl, binding.avatar);
        binding.nickname.setText(liteAvUserBean.attentionUserInfo.nickname);
        binding.close.setVisibility(showClose ? View.VISIBLE : View.GONE);

        if (liteAvUserBean.getAttentionUserId().equals(UserInfoMgr.getInstance().getUserInfo().getId())) {
            binding.attentionBtn.setVisibility(View.GONE);
        }else {
            binding.attentionBtn.setVisibility(View.VISIBLE);
        }

        if (liteAvUserBean.identity == null){
            //陌生人
            binding.attentionBtn.setText("分享");
        } else if (liteAvUserBean.identity.equals(FriendProvider.STRANGER)) {
            //陌生人
            binding.attentionBtn.setText("关注");
            binding.attentionBtn.setSelected(true);
        } else if (liteAvUserBean.identity.equals(FriendProvider.FANS)) {
            //粉丝
            binding.attentionBtn.setText("回关");
            binding.attentionBtn.setSelected(true);
        } else if (liteAvUserBean.identity.equals(FriendProvider.ATTENTION)) {
            //关注的人
            binding.attentionBtn.setText("已关注");
            binding.attentionBtn.setSelected(false);
        } else if (liteAvUserBean.identity.equals(FriendProvider.FRIEND)) {
            //朋友
            binding.attentionBtn.setText("互相关注");
            binding.attentionBtn.setSelected(false);
        } else if (liteAvUserBean.identity.equals(FriendProvider.FOLLOW)) {
            //关注的人
            binding.attentionBtn.setText("已关注");
            binding.attentionBtn.setSelected(false);
        }

    }

    //局部刷新用的
    @Override
    protected void convert(BaseViewHolder helper, LiteAvUserBean liteAvUserBean, List<?> payloads) {
        super.convert(helper, liteAvUserBean, payloads);
        if (payloads.size() > 0 && payloads.get(0) instanceof Integer) {
            //不为空，即调用notifyItemChanged(position,payloads)后执行的，可以在这里获取payloads中的数据进行局部刷新
            int type = (Integer) payloads.get(0);// 刷新哪个部分 标志位
            if (type == 99) {
                TextView attentionBtn = helper.getView(R.id.attention_btn);
                if (liteAvUserBean.identity == null){
                    //陌生人
                    attentionBtn.setText("分享");
                } else if (liteAvUserBean.identity.equals(FriendProvider.STRANGER)) {
                    //陌生人
                    attentionBtn.setText("关注");
                    attentionBtn.setSelected(true);
                } else if (liteAvUserBean.identity.equals(FriendProvider.FANS)) {
                    //粉丝
                    attentionBtn.setText("回关");
                    attentionBtn.setSelected(true);
                } else if (liteAvUserBean.identity.equals(FriendProvider.ATTENTION)) {
                    //关注的人
                    attentionBtn.setText("已关注");
                    attentionBtn.setSelected(false);
                } else if (liteAvUserBean.identity.equals(FriendProvider.FRIEND)) {
                    //朋友
                    attentionBtn.setText("互相关注");
                    attentionBtn.setSelected(false);
                } else if (liteAvUserBean.identity.equals(FriendProvider.FOLLOW)) {
                    //关注的人
                    attentionBtn.setSelected(false);
                }
            }
        }
    }
}
