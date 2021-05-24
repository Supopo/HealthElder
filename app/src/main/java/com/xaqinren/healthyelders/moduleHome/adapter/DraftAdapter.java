package com.xaqinren.healthyelders.moduleHome.adapter;

import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ItemDraftBinding;
import com.xaqinren.healthyelders.moduleLiteav.bean.SaveDraftBean;
import com.xaqinren.healthyelders.utils.GlideUtil;

import org.jetbrains.annotations.NotNull;

public class DraftAdapter extends BaseQuickAdapter<SaveDraftBean , BaseViewHolder> {
    public DraftAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, SaveDraftBean saveDraftBean) {
        ItemDraftBinding binding = DataBindingUtil.bind(baseViewHolder.itemView);
        if (saveDraftBean.getType() == 0)
        GlideUtil.intoImageView(getContext(),saveDraftBean.getCoverPath(),binding.itemIv);
        else
            GlideUtil.intoImageView(getContext(),saveDraftBean.getFilePaths().get(0),binding.itemIv);
        binding.selIv.setVisibility(saveDraftBean.isEdit() ? View.VISIBLE : View.GONE);
        binding.selIv.setImageResource(saveDraftBean.isSel() ? R.mipmap.rad_py_sel : R.mipmap.rad_py_nor);

    }
}
