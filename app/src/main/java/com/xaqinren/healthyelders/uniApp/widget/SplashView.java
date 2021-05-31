package com.xaqinren.healthyelders.uniApp.widget;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import com.xaqinren.healthyelders.R;

import io.dcloud.feature.sdk.Interface.IDCUniMPAppSplashView;

public class SplashView implements IDCUniMPAppSplashView {
    View view;
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
        }
        view.setAnimation(AnimationUtils.loadAnimation(context,R.anim.activity_bottom_2enter));
        return view;
    }
}
