package com.xaqinren.healthyelders.moduleLiteav.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.moduleLiteav.bean.LiteAvUserBean;
import com.xaqinren.healthyelders.widget.CircleImageView;

import org.jetbrains.annotations.NotNull;

public class ChooseUserAdapter extends BaseMultiItemQuickAdapter<LiteAvUserBean,BaseViewHolder> {
    public ChooseUserAdapter() {
        super();
        addItemType(0,R.layout.item_lite_av_user_class);
        addItemType(1,R.layout.item_lite_av_user);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, LiteAvUserBean liteAvUserBean) {
        if (liteAvUserBean.getItemType() == 0) {

        }else{
            ImageView selIv = baseViewHolder.getView(R.id.sel_iv);
            selIv.setImageResource(liteAvUserBean.isSel ? R.mipmap.rad_py_sel : R.mipmap.rad_py_nor);
        }
    }
}
