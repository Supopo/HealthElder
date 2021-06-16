package com.xaqinren.healthyelders.moduleLiteav.adapter;

import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.databinding.ItemMusicItemBinding;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.moduleLiteav.bean.MMusicItemBean;
import com.xaqinren.healthyelders.utils.GlideUtil;

import org.jetbrains.annotations.NotNull;

import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.utils.RxUtils;

public class MusicItemAdapter extends BaseQuickAdapter<MMusicItemBean, BaseViewHolder> implements LoadMoreModule {

    public MusicItemAdapter(int layoutResId) {
        super(layoutResId);
        addChildClickViewIds(R.id.shoucang,R.id.use_btn);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, MMusicItemBean mMusicItemBean) {
        //item_music_item
        ItemMusicItemBinding binding = DataBindingUtil.bind(baseViewHolder.itemView);
        binding.setViewModel(mMusicItemBean);
        GlideUtil.intoCirImageView(getContext(),mMusicItemBean.coverUrl,binding.cover,4);
        if (mMusicItemBean.myMusicStatus == 0){
            //隐藏加载动画图标
            binding.progress.setVisibility(View.GONE);
            binding.playBtn.setImageResource(R.mipmap.icon_ship_yiny_bof);
        }
        else if (mMusicItemBean.myMusicStatus == 1) {
            //展示加载动画图标
            binding.progress.setVisibility(View.VISIBLE);
            binding.playBtn.setImageResource(R.mipmap.icon_ship_yiny_bof);
        }else{
            //隐藏加载动画图标
            binding.progress.setVisibility(View.GONE);
            binding.playBtn.setImageResource(R.mipmap.icon_ship_yiny_zt);
        }

        binding.shoucang.setImageResource(mMusicItemBean.hasFavorite ? R.mipmap.icon_music_coll : R.mipmap.icon_music_coll_nor);

        if (mMusicItemBean.isUse) {
            binding.useBtn.setText("取消");
        }else{
            binding.useBtn.setText("使用");
        }
        if (mMusicItemBean.isOperation()) {
            binding.useBtn.setVisibility(View.VISIBLE);
        }else{
            binding.useBtn.setVisibility(View.GONE);
        }
        binding.jianji.setVisibility(View.GONE);
    }


}
