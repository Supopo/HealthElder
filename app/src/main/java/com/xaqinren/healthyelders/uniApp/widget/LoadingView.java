package com.xaqinren.healthyelders.uniApp.widget;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.xaqinren.healthyelders.R;

import io.dcloud.feature.sdk.Interface.IDCUniMPAppSplashView;

public class LoadingView implements IDCUniMPAppSplashView {
    View view;
    LottieAnimationView animationView;
    public LoadingView() {

    }

    @Override
    public View getSplashView(Context context, String s) {
        return initView(context, s);
    }

    @Override
    public void onCloseSplash(ViewGroup viewGroup) {
        animationView.cancelAnimation();
        view.setVisibility(View.GONE);
    }

    private View initView(Context context, String s) {
        if (view == null) {
            view = View.inflate(context, R.layout.uni_loding, null);
            animationView = view.findViewById(R.id.loadingView);
        }
        view.setAnimation(AnimationUtils.loadAnimation(context,R.anim.activity_right_2enter));
        animationView.playAnimation();
        return view;
    }
}
