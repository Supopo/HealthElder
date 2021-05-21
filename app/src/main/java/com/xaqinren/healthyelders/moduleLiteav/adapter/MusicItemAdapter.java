package com.xaqinren.healthyelders.moduleLiteav.adapter;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.databinding.ItemMusicItemBinding;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.moduleLiteav.bean.MMusicItemBean;

import org.jetbrains.annotations.NotNull;

import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.utils.RxUtils;

public class MusicItemAdapter extends BaseQuickAdapter<MMusicItemBean, BaseViewHolder> {
    private int operationIndex = -1;
    private String currentPlayId;
    public MusicItemAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, MMusicItemBean mMusicItemBean) {
        //item_music_item
        ItemMusicItemBinding binding = DataBindingUtil.bind(baseViewHolder.itemView);
        binding.setViewModel(mMusicItemBean);
        Glide.with(getContext()).load(R.mipmap.ic_aweme_yj).apply(
                new RequestOptions()
                        .transforms(new CenterCrop(), new RoundedCorners((int) getContext().getResources().getDimension(R.dimen.dp_4))
                        )
        ).into(binding.cover);
        baseViewHolder.itemView.setOnClickListener(view -> {
            if (operationIndex == baseViewHolder.getAdapterPosition()) {
                return;
            }
            if (operationIndex != -1) {
                getData().get(operationIndex).setOperation(false);
                notifyItemChanged(operationIndex);
            }
            operationIndex = baseViewHolder.getAdapterPosition();
            getData().get(operationIndex).setOperation(true);
            notifyItemChanged(operationIndex);
            RxBus.getDefault().post(new EventBean(CodeTable.EVENT_MUSIC_OP,mMusicItemBean.getId()));
        });

    }

    public void clear(String id) {
        currentPlayId = id;
        if (operationIndex != -1) {
            if (getData().get(operationIndex).getId().equals(id)) {
                return;
            }
            getData().get(operationIndex).setOperation(false);
            notifyItemChanged(operationIndex);
            operationIndex = -1;
        }
    }

    public int getOperationIndex() {
        return operationIndex;
    }

    public void setOperationIndex(int operationIndex) {
        this.operationIndex = operationIndex;
        if (operationIndex != -1) {
            if (!getData().get(operationIndex).getId().equals(currentPlayId)) {
                this.operationIndex = -1;
                getData().get(operationIndex).setOperation(false);
            }else
                getData().get(operationIndex).setOperation(true);
            notifyItemChanged(operationIndex);
        }
    }
}
