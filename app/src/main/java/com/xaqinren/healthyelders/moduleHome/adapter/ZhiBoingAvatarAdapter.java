package com.xaqinren.healthyelders.moduleHome.adapter;

import android.graphics.drawable.AnimationDrawable;
import android.view.animation.Animation;
import android.widget.ImageView;

import androidx.databinding.DataBindingUtil;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ItemZbingAvatarBinding;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ZBUserListBean;
import com.xaqinren.healthyelders.utils.AnimUtils;

import java.util.List;


public class ZhiBoingAvatarAdapter extends BaseQuickAdapter<VideoInfo, BaseViewHolder> implements LoadMoreModule {


    public ZhiBoingAvatarAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, VideoInfo item) {

        //注意 ItemBinding 改为自己item_layout的名字 ItemXxxBinding
        ItemZbingAvatarBinding binding = DataBindingUtil.bind(helper.itemView);
        binding.setViewModel(item);
        binding.executePendingBindings();
        ImageView ivBg = helper.getView(R.id.avatarBg);
        AnimationDrawable bgAnim = (AnimationDrawable) ivBg.getBackground();
        bgAnim.start();
        //头像缩小动画
        Animation avatarAnim = AnimUtils.getAnimation(getContext(), R.anim.avatar_start_zb);
        //直播头像动画
        binding.rlAvatar.clearAnimation();
        binding.rlAvatar.startAnimation(avatarAnim);
    }


    //局部刷新用的
    @Override
    protected void convert(BaseViewHolder helper, VideoInfo item, List<?> payloads) {
        super.convert(helper, item, payloads);
        if (payloads.size() > 0 && payloads.get(0) instanceof Integer) {
            //不为空，即调用notifyItemChanged(position,payloads)后执行的，可以在这里获取payloads中的数据进行局部刷新
            int type = (Integer) payloads.get(0);// 刷新哪个部分 标志位

        }
    }
}
