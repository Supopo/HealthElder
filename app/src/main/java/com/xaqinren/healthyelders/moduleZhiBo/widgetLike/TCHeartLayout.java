/*
 * Copyright (C) 2015 tyrantgit
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xaqinren.healthyelders.moduleZhiBo.widgetLike;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;

import com.tencent.qcloud.tim.uikit.utils.ScreenUtil;
import com.tencent.qcloud.ugckit.utils.ScreenUtils;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.utils.LogUtils;

import java.util.Random;

/**
 * Module:   TCHeartLayout
 * <p>
 * Function: 飘心动画界面布局类
 * <p>
 * 通过动画控制每个心形界面的显示
 * TCPathAnimator 控制显示路径
 * TCHeartView 单个心形界面
 */
public class TCHeartLayout extends RelativeLayout {

    private TCAbstractPathAnimator mAnimator;
    private int defStyleAttr = 0;

    private float startY;
    private float startX;
    private int dHeight;
    private int dWidth;
    private int initX;
    private int pointx;
    private int pointy;

    public TCHeartLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        initHeartDrawable();
        init(attrs, defStyleAttr);
    }

    private void initView(Context context) {
        dHeight = (int) getResources().getDimension(R.dimen.dp_32);
        dWidth = (int) getResources().getDimension(R.dimen.dp_32);
        //从View左边开始计算 (32+8) * 2
        startX = getResources().getDimension(R.dimen.dp_90);
        startY = getResources().getDimension(R.dimen.dp_16);
        pointx = dWidth;//随机上浮方向的x坐标
    }

    private static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }


    private void init(AttributeSet attrs, int defStyleAttr) {
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.HeartLayout, defStyleAttr, 0);
        initX = 30;
        if (pointx <= initX && pointx >= 0) {
            pointx -= 50;
        } else if (pointx >= -initX && pointx <= 0) {
            pointx += 50;
        } else
            pointx = initX;

        //float x是从TCHeartLayoutView的宽度左边开始计算的
        //TCHeartLayoutView宽度-位置宽度
        mAnimator = new TCPathAnimator(
                TCAbstractPathAnimator.Config.fromTypeArray(a, startX, startY, pointx, dWidth, dHeight));
        a.recycle();
    }

    public void clearAnimation() {
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).clearAnimation();
        }
        removeAllViews();
    }

    public void resourceLoad() {
        mHearts = new Bitmap[drawableIds.length];
        mHeartsDrawable = new BitmapDrawable[drawableIds.length];
        for (int i = 0; i < drawableIds.length; i++) {
            mHearts[i] = BitmapFactory.decodeResource(getResources(), drawableIds[i]);
            mHeartsDrawable[i] = new BitmapDrawable(getResources(), mHearts[i]);
        }
    }

    private static int[] drawableIds = new int[]{R.mipmap.dianzan_icon01, R.mipmap.dianzan_icon02, R.mipmap.dianzan_icon03, R.mipmap.dianzan_icon04, R.mipmap.dianzan_icon05};
    private Random mRandom = new Random();
    private static Drawable[] sDrawables;
    private Bitmap[] mHearts;
    private BitmapDrawable[] mHeartsDrawable;

    private void initHeartDrawable() {
        int size = drawableIds.length;
        sDrawables = new Drawable[size];
        for (int i = 0; i < size; i++) {
            sDrawables[i] = getResources().getDrawable(drawableIds[i]);
        }
        resourceLoad();
    }

    public void addFavor() {
        TCHeartView heartView = new TCHeartView(getContext());
        heartView.setDrawable(mHeartsDrawable[mRandom.nextInt(drawableIds.length - 1)]);
        mAnimator.start(heartView, this);
    }

}
