package com.xaqinren.healthyelders.moduleLiteav.adapter;

import android.database.DatabaseUtils;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.moduleLiteav.bean.LiteAvUserBean;
import com.xaqinren.healthyelders.widget.CircleImageView;


import org.jetbrains.annotations.NotNull;

public class ChooseUnLookAdapter extends BaseQuickAdapter<LiteAvUserBean,BaseViewHolder> {

    public ChooseUnLookAdapter(int layoutResId) {
        super(layoutResId);

    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, LiteAvUserBean unLookBean) {
        CircleImageView circleImageView = baseViewHolder.getView(R.id.avatar);
        Glide.with(circleImageView).load(R.mipmap.ic_launcher).into(circleImageView);
    }
}
