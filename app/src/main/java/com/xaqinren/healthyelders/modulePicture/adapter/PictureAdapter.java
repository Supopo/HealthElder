package com.xaqinren.healthyelders.modulePicture.adapter;


import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ItemPhptoSelNomrlBinding;
import com.xaqinren.healthyelders.modulePicture.bean.LocalPhotoBean;

import org.jetbrains.annotations.NotNull;

public class PictureAdapter extends BaseMultiItemQuickAdapter<LocalPhotoBean,BaseViewHolder> {
    public PictureAdapter() {
        addItemType(0, R.layout.item_phpto_sel_nomrl);
        addItemType(1, R.layout.item_phpto_sel_add);
        addChildClickViewIds(R.id.item_close);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, LocalPhotoBean localPhotoBean) {
        if (localPhotoBean.getItemType() == 0) {
            ItemPhptoSelNomrlBinding binding = DataBindingUtil.bind(baseViewHolder.itemView);
            Glide.with(binding.itemIv)
                    .load(localPhotoBean.getPath())
                    .apply(new RequestOptions()
                            .transforms(new CenterCrop(), new RoundedCorners((int) getContext().getResources().getDimension(R.dimen.dp_4))
                            ))
                    .into(binding.itemIv);
        }
    }
}
