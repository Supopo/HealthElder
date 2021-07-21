package com.xaqinren.healthyelders.moduleZhiBo.adapter;

import android.text.TextUtils;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.databinding.ItemZbListUserBinding;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ZBUserListBean;
import com.xaqinren.healthyelders.utils.GlideUtil;

import java.util.List;

/**
 * 主播页面榜单列表
 */
public class ZBUserListAdapter extends BaseQuickAdapter<ZBUserListBean, BaseViewHolder> implements LoadMoreModule {
    public ZBUserListAdapter(int layoutResId) {
        super(layoutResId);
    }

    private String fansTeamName;

    public ZBUserListAdapter(int layoutResId, String fansTeamName) {
        super(layoutResId);
        this.fansTeamName = fansTeamName;
        addChildClickViewIds(R.id.iv_follow);
        addChildClickViewIds(R.id.iv_avatar);
    }


    @Override
    protected void convert(BaseViewHolder helper, ZBUserListBean item) {
        ItemZbListUserBinding binding = DataBindingUtil.bind(helper.itemView);
        binding.setViewModel(item);
        binding.executePendingBindings();

        QMUIRadiusImageView riv = helper.getView(R.id.iv_avatar);
        Glide.with(getContext())
                .load(item.avatarUrl)
                .placeholder(R.mipmap.login_logo)
                .into(riv);

        helper.setText(R.id.tv_num, String.valueOf(helper.getLayoutPosition() + 1));
        if (helper.getLayoutPosition() == 0) {
            helper.setTextColor(R.id.tv_num, getContext().getResources().getColor(R.color.color_FEDC4D));
            riv.setBorderColor(getContext().getResources().getColor(R.color.color_FEDC4D));
        } else if (helper.getLayoutPosition() == 1) {
            helper.setTextColor(R.id.tv_num, getContext().getResources().getColor(R.color.color_94C1FF));
            riv.setBorderColor(getContext().getResources().getColor(R.color.color_94C1FF));
        } else if (helper.getLayoutPosition() == 2) {
            helper.setTextColor(R.id.tv_num, getContext().getResources().getColor(R.color.color_ECA554));
            riv.setBorderColor(getContext().getResources().getColor(R.color.color_ECA554));
        } else {
            helper.setTextColor(R.id.tv_num, getContext().getResources().getColor(R.color.color_BFBF));
            riv.setBorderColor(getContext().getResources().getColor(R.color.transparent));
        }
        if (item.hasSpeech) {
            helper.setGone(R.id.iv_jy, false);
        } else {
            helper.setGone(R.id.iv_jy, true);
        }

        if (!TextUtils.isEmpty(item.identity)) {
            if ((item.identity.equals("FOLLOW")) || item.identity.equals("FRIEND")) {
                helper.setGone(R.id.iv_follow, true);
            } else {
                helper.setGone(R.id.iv_follow, false);
            }
        }


        if (UserInfoMgr.getInstance().getUserInfo().getId().equals(item.userId)) {
            helper.setGone(R.id.iv_follow, true);
        }

        if (item.getLevelName().equals("0")) {
            helper.setGone(R.id.rl_lv, true);
        } else {
            helper.setGone(R.id.rl_lv, false);
            helper.setText(R.id.tv_leave, item.getLevelName());
            if (TextUtils.isEmpty(item.levelIcon)) {
                GlideUtil.intoImageView(getContext(), R.mipmap.icon_dj_def, binding.ivLeave);
            } else {
                GlideUtil.intoImageView(getContext(), item.levelIcon, binding.ivLeave);
            }
        }


        helper.setText(R.id.tv_fansTeamName, fansTeamName);

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
                    helper.setGone(R.id.iv_jy, false);
                } else {
                    helper.setGone(R.id.iv_jy, true);
                }

                if (!TextUtils.isEmpty(item.identity) && (item.identity.equals("FOLLOW")) || item.identity.equals("FRIEND")) {
                    helper.setGone(R.id.iv_follow, true);
                } else {
                    helper.setGone(R.id.iv_follow, false);
                }

            }
        }
    }
}
