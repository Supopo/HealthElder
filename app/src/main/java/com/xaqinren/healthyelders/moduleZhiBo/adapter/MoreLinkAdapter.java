package com.xaqinren.healthyelders.moduleZhiBo.adapter;

import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.tencent.qcloud.tim.uikit.utils.ScreenUtil;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ItemMoreLinkBinding;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ZBUserListBean;
import com.xaqinren.healthyelders.utils.MScreenUtil;

import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * Description: 多人连麦adapter
 */
public class MoreLinkAdapter extends BaseQuickAdapter<ZBUserListBean, BaseViewHolder> implements LoadMoreModule {
    public MoreLinkAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, ZBUserListBean item) {

        ItemMoreLinkBinding binding = DataBindingUtil.bind(helper.itemView);
        binding.setViewModel(item);
        binding.executePendingBindings();

        //动态计算高度

        int screenHeight = MScreenUtil.getScreenHeight(getContext());
        int screenWidth = MScreenUtil.getScreenWidth(getContext());


        //因为UI图按照2330/1080比例做的 比例约2.1
        //比例小于1.9的按( 1920 / 1080)比例约1.77适配
        if (((float) screenHeight / screenWidth) < 1.9) {
            //72/666 = nh/sh
            float wXY = ((float) 72 / 666);
            float n72 = (screenHeight * wXY);//新的高度
            float xy = n72 / 72;//新旧高度比例

            ViewGroup.LayoutParams params = binding.ivYq.getLayoutParams();
            params.width = (int) (20 * xy);
            params.height = (int) (28 * xy);
            binding.ivYq.setLayoutParams(params);

            ViewGroup.LayoutParams params2 = binding.ivJy.getLayoutParams();
            params2.width = (int) (36 * xy);
            params2.height = (int) (13 * xy);
            binding.ivJy.setLayoutParams(params2);

            ViewGroup.LayoutParams params3 = binding.rivAvatar.getLayoutParams();
            params3.height = (int) (36 * xy);
            params3.width = (int) (36 * xy);
            binding.rivAvatar.setLayoutParams(params3);


            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.width = (int) (72 * xy);
            layoutParams.height = layoutParams.width;
            if (helper.getLayoutPosition() != 0) {
                layoutParams.setMargins(0, (int) (3 * xy), 0, 0);
            }
            binding.rlItem.setLayoutParams(layoutParams);


            RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParams2.setMargins(0, (int) (9 * xy), 0, (int) (6 * xy));
            layoutParams2.addRule(RelativeLayout.CENTER_HORIZONTAL);
            binding.rlPhoto.setLayoutParams(layoutParams2);
        } else {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            if (helper.getLayoutPosition() != 0) {
                layoutParams.setMargins(0, (int) (getContext().getResources().getDimension(R.dimen.dp_3)), 0, 0);
            }

//            ViewGroup.LayoutParams params2 = binding.ivBg.getLayoutParams();
//            params2.width = (int) (getContext().getResources().getDimension(R.dimen.dp_93));
//            params2.height = (int) (getContext().getResources().getDimension(R.dimen.dp_93));
//            binding.ivBg.setLayoutParams(params2);
            layoutParams.width = (int) (getContext().getResources().getDimension(R.dimen.dp_93));
            layoutParams.height = (int) (getContext().getResources().getDimension(R.dimen.dp_93));
            binding.rlItem.setLayoutParams(layoutParams);

        }

        if (!TextUtils.isEmpty(item.avatarUrl)) {
            QMUIRadiusImageView riv = helper.getView(R.id.riv_avatar);
            Glide.with(getContext())
                    .load(item.avatarUrl)
                    .into(riv);

            Glide.with(getContext())
                    .load(item.avatarUrl)
                    .apply(RequestOptions.bitmapTransform(new BlurTransformation(5, 15)))
                    .into((ImageView) helper.getView(R.id.iv_bg));
            helper.setTextColor(R.id.tv_name, getContext().getResources().getColor(R.color.white));
        } else {
            Glide.with(getContext())
                    .load(R.mipmap.yp_tx_bg_w)
                    .into((ImageView) helper.getView(R.id.iv_bg));
            helper.setTextColor(R.id.tv_name, getContext().getResources().getColor(R.color.color_FFACA1B0));
        }
    }
}
