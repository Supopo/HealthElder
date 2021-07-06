package com.xaqinren.healthyelders.moduleMall.adapter;

import android.graphics.Paint;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tencent.qcloud.tim.uikit.utils.ScreenUtil;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ItemMallGoodBinding;
import com.xaqinren.healthyelders.databinding.ItemMallHotBinding;
import com.xaqinren.healthyelders.moduleHome.bean.MenuBean;
import com.xaqinren.healthyelders.moduleZhiBo.bean.GoodsBean;
import com.xaqinren.healthyelders.utils.UrlUtils;

import java.util.List;


public class MallGoodsAdapter extends BaseQuickAdapter<GoodsBean, BaseViewHolder> implements LoadMoreModule {
    public MallGoodsAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, GoodsBean item) {

        //注意 ItemBinding 改为自己item_layout的名字 ItemXxxBinding
        ItemMallGoodBinding binding = DataBindingUtil.bind(helper.itemView);
        binding.setViewModel(item);
        binding.executePendingBindings();

        //        binding.tvRmb.getPaint().setAntiAlias(true);//抗锯齿
        //        binding.tvRmb.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG|Paint.ANTI_ALIAS_FLAG);//设置中划线并加清晰
        //
        //        binding.tvMinPrice.getPaint().setAntiAlias(true);//抗锯齿
        //        binding.tvMinPrice.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG|Paint.ANTI_ALIAS_FLAG);//设置中划线并加清晰


        int screenWidth = ScreenUtil.getScreenWidth(getContext());
        //瀑布流图片的宽度 4+3+4
        int itemWidth = (screenWidth - (int) getContext().getResources().getDimension(R.dimen.dp_11)) / 2;

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) binding.ivCover.getLayoutParams();
        params.height = itemWidth;
        binding.ivCover.setLayoutParams(params);

        //判断前两个距离顶部
        if (helper.getLayoutPosition() == 0 || helper.getLayoutPosition() == 1) {
            //动态设置间距
            StaggeredGridLayoutManager.LayoutParams lp = (StaggeredGridLayoutManager.LayoutParams) binding.llItem.getLayoutParams();
            lp.setMargins(0, 20, 0, 0);
            binding.llItem.setLayoutParams(lp);
        }
    }

    //局部刷新用的
    @Override
    protected void convert(BaseViewHolder helper, GoodsBean item, List<?> payloads) {
        super.convert(helper, item, payloads);
        if (payloads.size() > 0 && payloads.get(0) instanceof Integer) {
            //不为空，即调用notifyItemChanged(position,payloads)后执行的，可以在这里获取payloads中的数据进行局部刷新
            int type = (Integer) payloads.get(0);// 刷新哪个部分 标志位
        }
    }
}
