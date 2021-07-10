package com.xaqinren.healthyelders.utils;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.xaqinren.healthyelders.R;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class GlideUtil {
    public static void intoCirImageView(Context context, Object res, ImageView view, int cir) {
        Glide.with(context).load(res).apply(
                new RequestOptions()
                        .transforms(new CenterCrop(), new RoundedCorners(cir)
                        )
        ).into(view);
    }

    public static void intoImageView(Context context, Object res, ImageView view) {
        Glide.with(context).load(res).diskCacheStrategy(DiskCacheStrategy.ALL).thumbnail(0.5f).into(view);
    }

    public static void intoImageView(Context context, Object res, ImageView view, int placeholder) {
        RequestOptions options = new RequestOptions()
                .placeholder(placeholder)//图片加载出来前，显示的图片
                .fallback(placeholder) //url为空的时候,显示的图片
                .error(placeholder);//图片加载失败后，显示的图片
        Glide.with(context).load(res).diskCacheStrategy(DiskCacheStrategy.ALL)
                .thumbnail(0.5f)
                .apply(options).into(view);
    }


    public static void intoPhotoImageView(Context context, Object res, ImageView view) {
        RequestOptions options = new RequestOptions()
                .placeholder(R.mipmap.icon_photo_def)//图片加载出来前，显示的图片
                .fallback(R.mipmap.icon_photo_def) //url为空的时候,显示的图片
                .error(R.mipmap.icon_photo_def);//图片加载失败后，显示的图片
        Glide.with(context).load(res).diskCacheStrategy(DiskCacheStrategy.ALL)
                .thumbnail(0.5f)
                .apply(options).into(view);
    }

    public static void intoGaoSiImageView(Context context, Object res, ImageView view) {
        Glide.with(context).load(res).diskCacheStrategy(DiskCacheStrategy.ALL)
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(15, 15)))
                .into(view);
    }

    public static void intoGaoSiImageView(Context context, Object res, ImageView view, int radius) {
        Glide.with(context).load(res).diskCacheStrategy(DiskCacheStrategy.ALL)
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(radius, 15)))
                .into(view);
    }
}
