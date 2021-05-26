package com.xaqinren.healthyelders.moduleMine.adapter;

import android.widget.RelativeLayout;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tencent.qcloud.tim.uikit.utils.ScreenUtil;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.databinding.ItemFjVideoBinding;
import com.xaqinren.healthyelders.databinding.ItemMineZpVideoBinding;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;
import com.xaqinren.healthyelders.utils.GlideUtil;

import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;


public class ZPVideoAdapter extends BaseQuickAdapter<VideoInfo, BaseViewHolder> implements LoadMoreModule {
    public ZPVideoAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, VideoInfo item) {

        //注意 ItemBinding 改为自己item_layout的名字 ItemXxxBinding
        ItemMineZpVideoBinding binding = DataBindingUtil.bind(helper.itemView);
        binding.setViewModel(item);
        binding.executePendingBindings();

        if (item.isDraft) {
            GlideUtil.intoGSImageView(getContext(), item.coverUrl, binding
                    .ivVideo, 3);
        } else {
            GlideUtil.intoImageView(getContext(), item.coverUrl, binding
                    .ivVideo);
        }

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
