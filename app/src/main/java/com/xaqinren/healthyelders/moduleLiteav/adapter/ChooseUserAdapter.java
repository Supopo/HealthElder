package com.xaqinren.healthyelders.moduleLiteav.adapter;

import android.view.View;
import android.widget.ImageView;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ItemLiteAvUserBinding;
import com.xaqinren.healthyelders.moduleLiteav.bean.LiteAvUserBean;
import com.xaqinren.healthyelders.widget.CircleImageView;

import org.jetbrains.annotations.NotNull;

public class ChooseUserAdapter extends BaseMultiItemQuickAdapter<LiteAvUserBean,BaseViewHolder> implements LoadMoreModule {
    public ChooseUserAdapter() {
        super();
        addItemType(1,R.layout.item_lite_av_user_class);
        addItemType(0,R.layout.item_lite_av_user);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, LiteAvUserBean liteAvUserBean) {

        if (liteAvUserBean.getItemType() == 1) {

        }else{
            ItemLiteAvUserBinding binding = DataBindingUtil.bind(baseViewHolder.itemView);
            binding.setViewModel(liteAvUserBean);
            ImageView selIv = binding.selIv;
            selIv.setImageResource(liteAvUserBean.isSel ? R.mipmap.rad_py_sel : R.mipmap.rad_py_nor);
            Glide.with(getContext()).load(liteAvUserBean.avatarUrl).into(binding.avatar);
        }
    }
}
