package com.xaqinren.healthyelders.uniApp.widget;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.xaqinren.healthyelders.R;

import io.dcloud.feature.sdk.Interface.IDCUniMPAppSplashView;

public class SplashView implements IDCUniMPAppSplashView {
    View view;
    ImageView logo;
    TextView name;
    public SplashView() {

    }

    @Override
    public View getSplashView(Context context, String s) {
        return initView(context, s);
    }

    @Override
    public void onCloseSplash(ViewGroup viewGroup) {
        view.setVisibility(View.GONE);
    }

    private View initView(Context context, String s) {
        if (view == null) {
            view = View.inflate(context, R.layout.uni_splash, null);
            logo = view.findViewById(R.id.logo);
            name = view.findViewById(R.id.name);
        }
        view.setAnimation(AnimationUtils.loadAnimation(context,R.anim.activity_bottom_2enter));
        return view;
    }
}
