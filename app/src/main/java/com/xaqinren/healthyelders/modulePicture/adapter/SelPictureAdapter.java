package com.xaqinren.healthyelders.modulePicture.adapter;

import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tencent.qcloud.ugckit.module.picker.data.TCVideoFileInfo;
import com.tencent.qcloud.ugckit.module.upload.impl.TVCClient;
import com.tencent.qcloud.ugckit.utils.DateTimeUtil;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ItemSelPictureBinding;

import org.jetbrains.annotations.NotNull;

public class SelPictureAdapter extends BaseQuickAdapter<TCVideoFileInfo, BaseViewHolder> {

    public SelPictureAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, TCVideoFileInfo tcVideoFileInfo) {
        ItemSelPictureBinding binding = DataBindingUtil.bind(baseViewHolder.itemView);
        Glide.with(getContext()).asBitmap().centerCrop().load(tcVideoFileInfo.getFilePath()).into(binding.itemIv);

        if (tcVideoFileInfo.isSelected()) {
            binding.itemPosition.setBackground(getContext().getResources().getDrawable(R.mipmap.xiangc_rad_sel));
            binding.itemPosition.setText(""+tcVideoFileInfo.getCurrentPosition());
        }else{
            binding.itemPosition.setBackground(getContext().getResources().getDrawable(R.mipmap.xiangc_rad_nor));
            binding.itemPosition.setText(null);
        }
        if (tcVideoFileInfo.getFileType() == TCVideoFileInfo.FILE_TYPE_PICTURE) {
            binding.tvDuration.setVisibility(View.GONE);
        } else {
            binding.tvDuration.setVisibility(View.VISIBLE);
            binding.tvDuration.setText(DateTimeUtil.duration(tcVideoFileInfo.getDuration()));
        }
    }
}
