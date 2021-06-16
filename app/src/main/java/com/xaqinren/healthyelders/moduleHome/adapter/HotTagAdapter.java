package com.xaqinren.healthyelders.moduleHome.adapter;

import android.content.res.Resources;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.databinding.DataBindingUtil;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.SlideBarBean;
import com.xaqinren.healthyelders.databinding.ItemSearchHistoryBinding;
import com.xaqinren.healthyelders.databinding.ItemSearchHotBinding;
import com.xaqinren.healthyelders.moduleHome.bean.SearchBean;
import com.xaqinren.healthyelders.utils.GlideUtil;
import com.xaqinren.healthyelders.utils.UrlUtils;

import java.util.List;


public class HotTagAdapter extends BaseQuickAdapter<SlideBarBean.MenuInfoListDTO, BaseViewHolder> implements LoadMoreModule {
    public HotTagAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, SlideBarBean.MenuInfoListDTO item) {

        //注意 ItemBinding 改为自己item_layout的名字 ItemXxxBinding
        ItemSearchHotBinding binding = DataBindingUtil.bind(helper.itemView);
        binding.setViewModel(item);
        binding.executePendingBindings();

        if (helper.getAdapterPosition() % 2 == 0) {
            binding.line.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(item.getIcon())) {
            GlideUtil.intoImageView(getContext(), item.getIcon(), binding.ivTag);

            try {
                int width = Integer.parseInt(UrlUtils.getUrlQueryByTag(item.getIcon(), "w"));
                int height = Integer.parseInt(UrlUtils.getUrlQueryByTag(item.getIcon(), "h"));
                //计算新高度
                int newW = (int) ((getContext().getResources().getDimension(R.dimen.dp_14)) * width / height);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) binding.ivTag.getLayoutParams();
                params.width = newW;
                binding.ivTag.setLayoutParams(params);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
