package com.xaqinren.healthyelders.moduleMall.adapter;

import android.widget.TextView;

import androidx.databinding.DataBindingUtil;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ItemMallHotBinding;
import com.xaqinren.healthyelders.moduleHome.bean.MenuBean;

import java.util.List;


public class MallHotMenuAdapter extends BaseQuickAdapter<MenuBean, BaseViewHolder> implements LoadMoreModule {
    public MallHotMenuAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, MenuBean item) {

        //注意 ItemBinding 改为自己item_layout的名字 ItemXxxBinding
        ItemMallHotBinding binding = DataBindingUtil.bind(helper.itemView);
        binding.setViewModel(item);
        binding.executePendingBindings();
        TextView tvMenu = helper.getView(R.id.tv_title);
        tvMenu.setTextColor(android.graphics.Color.parseColor(item.fontColor));
        TextView tvSubTitle = helper.getView(R.id.tv_subTitle);
        tvSubTitle.setTextColor(android.graphics.Color.parseColor(item.subFontColor));
        helper.setBackgroundColor(R.id.rl_item, android.graphics.Color.parseColor(item.backgroundColor));


//        // 基本属性设置 暂时隐藏倒计时
//        binding.countdownView.setCountTime(60 * 60 * 2) // 设置倒计时时间戳
//                .setHourTvBackgroundRes(R.drawable.bg_hot_mall_timer)
//                .setHourTvTextColorHex("#FFFFFF")
//                .setHourTvTextSize(11)
//
//                .setHourColonTvSize(16, 0)
//                .setHourColonTvTextColorHex("#F81E4D")
//                .setHourColonTvTextSize(11)
//
//                .setMinuteTvBackgroundRes(R.drawable.bg_hot_mall_timer)
//                .setMinuteTvTextColorHex("#FFFFFF")
//                .setMinuteTvTextSize(11)
//
//                .setMinuteColonTvSize(16, 0)
//                .setMinuteColonTvTextColorHex("#F81E4D")
//                .setMinuteColonTvTextSize(11)
//
//                .setSecondTvBackgroundRes(R.drawable.bg_hot_mall_timer)
//                .setSecondTvTextColorHex("#FFFFFF")
//                .setSecondTvTextSize(11)
//
//
//                // 开启倒计时
//                .startCountDown()
//
//                // 设置倒计时结束监听
//                .setCountDownEndListener(new CountDownView.CountDownEndListener() {
//                    @Override
//                    public void onCountDownEnd() {
//                        Toast.makeText(getContext(), "倒计时结束", Toast.LENGTH_SHORT).show();
//                    }
//                });

    }

    //局部刷新用的
    @Override
    protected void convert(BaseViewHolder helper, MenuBean item, List<?> payloads) {
        super.convert(helper, item, payloads);
        if (payloads.size() > 0 && payloads.get(0) instanceof Integer) {
            //不为空，即调用notifyItemChanged(position,payloads)后执行的，可以在这里获取payloads中的数据进行局部刷新
            int type = (Integer) payloads.get(0);// 刷新哪个部分 标志位
        }
    }
}
