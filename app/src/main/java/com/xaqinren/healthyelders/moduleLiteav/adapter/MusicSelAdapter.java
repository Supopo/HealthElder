package com.xaqinren.healthyelders.moduleLiteav.adapter;

import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tencent.qcloud.ugckit.utils.CoverUtil;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ItemMusicSelBinding;
import com.xaqinren.healthyelders.moduleLiteav.bean.MMusicItemBean;
import com.xaqinren.healthyelders.utils.GlideUtil;

import org.jetbrains.annotations.NotNull;

import me.goldze.mvvmhabit.utils.ConvertUtils;

public class MusicSelAdapter extends BaseMultiItemQuickAdapter<MMusicItemBean, BaseViewHolder> {

    public MusicSelAdapter() {
        addItemType(0, R.layout.item_music_sel);
        addItemType(1, R.layout.item_music_sel_more);

        addChildClickViewIds(R.id.cover_rl);

    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, MMusicItemBean mMusicItemBean) {
        if (mMusicItemBean.itemType == 0) {
            ItemMusicSelBinding binding = DataBindingUtil.bind(baseViewHolder.itemView);
            binding.setViewModel(mMusicItemBean);
            binding.musicName.setSelected(mMusicItemBean.myMusicStatus != 0);
            if (mMusicItemBean.myMusicStatus == 1) {
                //展示加载动画图标
                binding.progress.setVisibility(View.VISIBLE);
            }else{
                //隐藏加载动画图标
                binding.progress.setVisibility(View.GONE);
            }
            GlideUtil.intoCirImageView(getContext(),mMusicItemBean.coverUrl,binding.itemCover,2);
            binding.border.setVisibility(mMusicItemBean.myMusicStatus == 0 ? View.GONE : View.VISIBLE);
        }
    }
}
