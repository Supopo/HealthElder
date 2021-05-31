package com.xaqinren.healthyelders.moduleHome.adapter;

import android.widget.LinearLayout;
import android.widget.RelativeLayout;

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
import com.xaqinren.healthyelders.utils.UrlUtils;

import org.jetbrains.annotations.NotNull;

public class AllSearchAdapter extends BaseMultiItemQuickAdapter<VideoInfo, BaseViewHolder> implements LoadMoreModule {


    public AllSearchAdapter() {
        addItemType(0, R.layout.item_all_search_article);
        addItemType(1, R.layout.item_search_user);
        addItemType(2, R.layout.item_all_search_goods);
        addItemType(3, R.layout.item_search_zb);
        addItemType(4, R.layout.item_all_search_article);
        addChildClickViewIds(R.id.iv_zan);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, VideoInfo videoInfo) {

        int screenWidth = ScreenUtil.getScreenWidth(getContext());
        if (videoInfo.getItemType() == 0 || videoInfo.getItemType() == 4) {
            ItemAllSearchArticleBinding binding = DataBindingUtil.bind(baseViewHolder.itemView);
            binding.setViewModel(videoInfo);

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

            //计算View宽度
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) binding.ivCover.getLayoutParams();
            int itemWidth = (screenWidth - (int) getContext().getResources().getDimension(R.dimen.dp_103));

            params.height = itemWidth;
            binding.ivCover.setLayoutParams(params);

        } else if (videoInfo.getItemType() == 3) {
            ItemSearchZbBinding binding = DataBindingUtil.bind(baseViewHolder.itemView);
            binding.setViewModel(videoInfo);

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

}
