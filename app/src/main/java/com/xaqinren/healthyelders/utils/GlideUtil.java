package com.xaqinren.healthyelders.utils;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.xaqinren.healthyelders.R;

public class GlideUtil {
    public static void intoCirImageView(Context context , Object res , ImageView view , int cir) {
        Glide.with(context).load(res).apply(
                new RequestOptions()
                        .transforms(new CenterCrop(), new RoundedCorners(cir)
                        )
        ).into(view);
    }
}
