package com.xaqinren.healthyelders.moduleHome.adapter;

import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tencent.qcloud.tim.uikit.utils.ScreenUtil;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ItemAllSearchArticleBinding;
import com.xaqinren.healthyelders.databinding.ItemAllSearchGoodsBinding;
import com.xaqinren.healthyelders.databinding.ItemSearchUserBinding;
import com.xaqinren.healthyelders.databinding.ItemSearchZbBinding;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;
import com.xaqinren.healthyelders.utils.GlideUtil;
import com.xaqinren.healthyelders.utils.UrlUtils;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AllSearchAdapter extends BaseMultiItemQuickAdapter<VideoInfo, BaseViewHolder> implements LoadMoreModule {


    public AllSearchAdapter() {
        addItemType(0, R.layout.item_all_search_article);
        addItemType(1, R.layout.item_search_user);
        addItemType(2, R.layout.item_all_search_goods);
        addItemType(3, R.layout.item_search_zb);
        addItemType(4, R.layout.item_all_search_article);
        addChildClickViewIds(R.id.iv_zan);
        addChildClickViewIds(R.id.iv_avatar);
        addChildClickViewIds(R.id.iv_comment);
        addChildClickViewIds(R.id.iv_share);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, VideoInfo videoInfo) {

        int screenWidth = ScreenUtil.getScreenWidth(getContext());
        if (videoInfo.getItemType() == 0 || videoInfo.getItemType() == 4) {
            ItemAllSearchArticleBinding binding = DataBindingUtil.bind(baseViewHolder.itemView);
            binding.setViewModel(videoInfo);
            GlideUtil.intoImageView(getContext(), UrlUtils.resetImgUrl(videoInfo.coverUrl, 600, 600), binding.rivCover, R.mipmap.bg_video);

            if (videoInfo.hasFavorite) {
                GlideUtil.intoImageView(getContext(), R.mipmap.icon_zan_gray_1, binding.ivZan);
            } else {
                GlideUtil.intoImageView(getContext(), R.mipmap.icon_zan_gray, binding.ivZan);
            }

            //计算View宽度
            int itemWidthHT = (screenWidth - (int) getContext().getResources().getDimension(R.dimen.dp_32));
            int itemWidthST = (screenWidth - (int) getContext().getResources().getDimension(R.dimen.dp_103));
            //判断是横图还是竖图
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) binding.rivCover.getLayoutParams();
            int itemWidth = (screenWidth - (int) getContext().getResources().getDimension(R.dimen.dp_103));
            int newHeight = itemWidth * 350 / 272;


            //图片真实宽高
            try {
                int oldWidth = Integer.parseInt(UrlUtils.getUrlQueryByTag(videoInfo.coverUrl, "w"));
                int oldHeight = Integer.parseInt(UrlUtils.getUrlQueryByTag(videoInfo.coverUrl, "h"));

                if (oldHeight > oldWidth) {
                    //竖图 计算新高度
                    newHeight = itemWidthST * oldHeight / oldWidth;
                    params.width = itemWidthST;
                } else {
                    newHeight = itemWidthHT * oldHeight / oldWidth;
                    params.width = itemWidthHT;
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
            params.height = newHeight;
            binding.rivCover.setLayoutParams(params);

        } else if (videoInfo.getItemType() == 1) {
            ItemSearchUserBinding binding = DataBindingUtil.bind(baseViewHolder.itemView);
            binding.setViewModel(videoInfo);
        } else if (videoInfo.getItemType() == 2) {
            ItemAllSearchGoodsBinding binding = DataBindingUtil.bind(baseViewHolder.itemView);
            binding.setViewModel(videoInfo);
            GlideUtil.intoImageView(getContext(), UrlUtils.resetImgUrl(videoInfo.coverUrl, 900, 900), binding.ivCover, R.mipmap.bg_video);

            //计算View宽度
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) binding.ivCover.getLayoutParams();
            int itemWidth = (screenWidth - (int) getContext().getResources().getDimension(R.dimen.dp_103));

            params.height = itemWidth;
            binding.ivCover.setLayoutParams(params);

        } else if (videoInfo.getItemType() == 3) {
            ItemSearchZbBinding binding = DataBindingUtil.bind(baseViewHolder.itemView);
            binding.setViewModel(videoInfo);
            GlideUtil.intoImageView(getContext(), UrlUtils.resetImgUrl(videoInfo.coverUrl, 1000, 1000), binding.rivCover, R.mipmap.bg_video);
            //            GlideUtil.intoImageView(getContext(), videoInfo.coverUrl, binding.rivCover, R.drawable.bg_edit_zb);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) binding.rivCover.getLayoutParams();
            int itemWidth = (screenWidth - (int) getContext().getResources().getDimension(R.dimen.dp_103));
            int newHeight = itemWidth * 350 / 272;

            //图片真是宽高
            try {
                int oldWidth = Integer.parseInt(UrlUtils.getUrlQueryByTag(videoInfo.coverUrl, "w"));
                int oldHeight = Integer.parseInt(UrlUtils.getUrlQueryByTag(videoInfo.coverUrl, "h"));
                //计算新高度
                newHeight = itemWidth * oldHeight / oldWidth;

            } catch (Exception e) {
                e.printStackTrace();
            }
            params.width = itemWidth;
            params.height = newHeight;
            binding.rivCover.setLayoutParams(params);

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
                    TextView tvFollow = helper.getView(R.id.tv_zanNum);
                    tvFollow.setText(item.getFavoriteCountEx());

                    if (item.hasFavorite) {
                        GlideUtil.intoImageView(getContext(), R.mipmap.icon_zan_gray_1, helper.getView(R.id.iv_zan));
                    } else {
                        GlideUtil.intoImageView(getContext(), R.mipmap.icon_zan_gray, helper.getView(R.id.iv_zan));
                    }
                }
            }
        }
    }
}
