package com.xaqinren.healthyelders.moduleZhiBo.adapter;

import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.SlideBarBean;
import com.xaqinren.healthyelders.databinding.ItemSearchHistoryBinding;
import com.xaqinren.healthyelders.databinding.ItemSensitiveWordsBinding;

import java.util.List;


public class SensitiveWordsAdapter extends BaseQuickAdapter<String, BaseViewHolder> implements LoadMoreModule {
    public SensitiveWordsAdapter(int layoutResId) {
        super(layoutResId);
        addChildClickViewIds(R.id.rl_del);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {

        //注意 ItemBinding 改为自己item_layout的名字 ItemXxxBinding
        ItemSensitiveWordsBinding binding = DataBindingUtil.bind(helper.itemView);
        binding.tvWord.setText(item);
        if (helper.getLayoutPosition() == 0) {
            binding.llPbc.setVisibility(View.GONE);
            binding.ivAdd.setVisibility(View.VISIBLE);
        } else {
            binding.llPbc.setVisibility(View.VISIBLE);
            binding.ivAdd.setVisibility(View.GONE);

        }
    }

}
