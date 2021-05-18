package com.xaqinren.healthyelders.moduleLiteav.adapter;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ItemMusicDetailBinding;
import com.xaqinren.healthyelders.moduleLiteav.bean.MusicBean;

import org.jetbrains.annotations.NotNull;

public class MusicDetailsAdapter extends BaseQuickAdapter<MusicBean , BaseViewHolder> {
    public MusicDetailsAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, MusicBean musicBean) {
        ItemMusicDetailBinding binding = DataBindingUtil.bind(baseViewHolder.itemView);
        binding.setViewModel(musicBean);
        Glide.with(getContext()).load(musicBean.getCoverPath())
                .apply(new RequestOptions()
                        .transforms(new CenterCrop(), new RoundedCorners((int) getContext().getResources().getDimension(R.dimen.dp_4))
                        )).into(binding.itemCover);
    }
}
