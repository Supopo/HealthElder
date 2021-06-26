package com.xaqinren.healthyelders.moduleHome.adapter;

import android.util.Log;
import android.widget.LinearLayout;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.SlideBarBean;
import com.xaqinren.healthyelders.databinding.ItemSearchMenuTagBinding;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.utils.GlideUtil;
import com.xaqinren.healthyelders.utils.LogUtils;
import com.xaqinren.healthyelders.utils.MScreenUtil;

import java.util.List;


public class MenuTagAdapter extends BaseQuickAdapter<SlideBarBean.MenuInfoListDTO, BaseViewHolder> implements LoadMoreModule {
    public MenuTagAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, SlideBarBean.MenuInfoListDTO item) {

        //注意 ItemBinding 改为自己item_layout的名字 ItemXxxBinding
        ItemSearchMenuTagBinding binding = DataBindingUtil.bind(helper.itemView);
        binding.setViewModel(item);
        binding.executePendingBindings();


        int newWidth = (MScreenUtil.getScreenWidth(getContext()) - (int) (getContext().getResources().getDimension(R.dimen.dp_43))) / 2;
        int newHeight = newWidth * 90 / 166;
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) binding.ivTag.getLayoutParams();
        params.height = newHeight;
        binding.ivTag.setLayoutParams(params);

        GlideUtil.intoImageView(getContext(), item.getImageUrl(), binding.ivTag);
    }

    //局部刷新用的
    @Override
    protected void convert(BaseViewHolder helper, SlideBarBean.MenuInfoListDTO item, List<?> payloads) {
        super.convert(helper, item, payloads);
        if (payloads.size() > 0 && payloads.get(0) instanceof Integer) {
            //不为空，即调用notifyItemChanged(position,payloads)后执行的，可以在这里获取payloads中的数据进行局部刷新
            int type = (Integer) payloads.get(0);// 刷新哪个部分 标志位

        }
    }
}
