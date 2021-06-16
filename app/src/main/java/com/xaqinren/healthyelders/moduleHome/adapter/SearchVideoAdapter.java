package com.xaqinren.healthyelders.moduleHome.adapter;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tencent.qcloud.tim.uikit.utils.ScreenUtil;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ItemSearchVideoBinding;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;
import com.xaqinren.healthyelders.utils.GlideUtil;
import com.xaqinren.healthyelders.utils.MScreenUtil;
import com.xaqinren.healthyelders.utils.UrlUtils;

import java.util.List;


public class SearchVideoAdapter extends BaseQuickAdapter<VideoInfo, BaseViewHolder> implements LoadMoreModule {
    public SearchVideoAdapter(int layoutResId) {
        super(layoutResId);
        addChildClickViewIds(R.id.ll_like);
    }

    @Override
    protected void convert(BaseViewHolder helper, VideoInfo item) {

        //注意 ItemBinding 改为自己item_layout的名字 ItemXxxBinding
        ItemSearchVideoBinding binding = DataBindingUtil.bind(helper.itemView);
        binding.setViewModel(item);
        binding.executePendingBindings();


        if (item.hasFavorite) {
            GlideUtil.intoImageView(getContext(), R.mipmap.icon_zan_gary, binding.ivZan);
        }else {
            GlideUtil.intoImageView(getContext(), R.mipmap.icon_zan_gary_0, binding.ivZan);
        }

        int screenWidth = MScreenUtil.getScreenWidth(getContext());

        //瀑布流图片的宽度
        int itemWidth = (screenWidth - (int) getContext().getResources().getDimension(R.dimen.dp_3)) / 2;

        binding.ivCover.setScaleType(ImageView.ScaleType.CENTER_CROP);

        //图片真是宽高
        try {
            int picWidth = Integer.parseInt(UrlUtils.getUrlQueryByTag(item.coverUrl, "w"));
            int picHeight = Integer.parseInt(UrlUtils.getUrlQueryByTag(item.coverUrl, "h"));
            //计算新高度
            int newHeight = itemWidth * 280 / 186;

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) binding.rlItem.getLayoutParams();
            params.height = newHeight;
            binding.rlItem.setLayoutParams(params);

            if (picWidth > picHeight) {
                binding.ivCover.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

    }

    //局部刷新用的
    @Override
    protected void convert(BaseViewHolder helper, VideoInfo item, List<?> payloads) {
        super.convert(helper, item, payloads);
        if (payloads.size() > 0 && payloads.get(0) instanceof Integer) {
            //不为空，即调用notifyItemChanged(position,payloads)后执行的，可以在这里获取payloads中的数据进行局部刷新
            int type = (Integer) payloads.get(0);// 刷新哪个部分 标志位
            if (type == 99) {
                if (item.getItemType() == 0 || item.getItemType() == 4) {
                    TextView tvFollow = helper.getView(R.id.tv_zan);
                    tvFollow.setText(item.getFavoriteCountEx());

                    if (item.hasFavorite) {
                        GlideUtil.intoImageView(getContext(), R.mipmap.icon_zan_gary, helper.getView(R.id.iv_zan));
                    }else {
                        GlideUtil.intoImageView(getContext(), R.mipmap.icon_zan_gary_0, helper.getView(R.id.iv_zan));
                    }
                }
            }
        }
    }
}
