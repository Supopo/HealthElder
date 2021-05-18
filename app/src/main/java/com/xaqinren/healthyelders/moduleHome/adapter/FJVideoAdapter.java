package com.xaqinren.healthyelders.moduleHome.adapter;

import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.databinding.DataBindingUtil;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tencent.qcloud.tim.uikit.utils.ScreenUtil;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ItemFjVideoBinding;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;

import java.util.List;


public class FJVideoAdapter extends BaseQuickAdapter<VideoInfo, BaseViewHolder> implements LoadMoreModule {
    public FJVideoAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, VideoInfo item) {

        //注意 ItemBinding 改为自己item_layout的名字 ItemXxxBinding
        ItemFjVideoBinding binding = DataBindingUtil.bind(helper.itemView);
        binding.setViewModel(item);
        binding.executePendingBindings();

        //模拟展示
        if (helper.getAdapterPosition() == 0 || helper.getAdapterPosition() == 6 || helper.getAdapterPosition() == 3) {
            helper.setGone(R.id.rl_bottom, false);
        } else {
            helper.setGone(R.id.rl_bottom, true);
        }

        if (item.isArticle()) {
            int screenWidth = ScreenUtil.getScreenWidth(getContext());
            //瀑布流图片的宽度
            int itemWidth = (screenWidth - (int) getContext().getResources().getDimension(R.dimen.dp_3)) / 2;

            //图片真是宽高
            int oldWidth = 1280;
            int oldHeight = 1980;

            //计算新高度
            int newHeight = itemWidth * oldHeight / oldWidth;

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) binding.rlItem.getLayoutParams();
            params.height = newHeight;
            binding.rlItem.setLayoutParams(params);
        }

    }

    //局部刷新用的
    @Override
    protected void convert(BaseViewHolder helper, VideoInfo item, List<?> payloads) {
        super.convert(helper, item, payloads);
        if (payloads.size() > 0 && payloads.get(0) instanceof Integer) {
            //不为空，即调用notifyItemChanged(position,payloads)后执行的，可以在这里获取payloads中的数据进行局部刷新
            int type = (Integer) payloads.get(0);// 刷新哪个部分 标志位

        }
    }
}
