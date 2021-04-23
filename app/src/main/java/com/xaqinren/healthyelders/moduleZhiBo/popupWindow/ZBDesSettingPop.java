package com.xaqinren.healthyelders.moduleZhiBo.popupWindow;

import android.content.Context;
import android.view.View;

import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.utils.AnimUtils;

import razerdp.basepopup.BasePopupWindow;

/**
 * Created by Lee. on 2021/4/20.
 * 开启直播间简介设置弹窗
 */
public class ZBDesSettingPop extends BasePopupWindow {


    public ZBDesSettingPop(Context context) {
        super(context);
        //去掉背景
        setBackground(R.color.transparent);
        setShowAnimation(AnimUtils.PopAnimRight2Enter(context));
        setDismissAnimation(AnimUtils.PopAnimRight2Exit(context));
        initView();
    }

    private void initView() {
        findViewById(R.id.iv_back).setOnClickListener(lis -> {
            dismiss();
        });
    }


    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.pop_zbj_start_describe);
    }
}
