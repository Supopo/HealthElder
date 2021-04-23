package com.xaqinren.healthyelders.moduleZhiBo.popupWindow;

import android.content.Context;
import android.view.View;

import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.utils.AnimUtils;

import razerdp.basepopup.BasePopupWindow;

/**
 * Created by Lee. on 2021/4/20.
 * 开启直播间设置弹窗
 */
public class ZBStartSettingPop extends BasePopupWindow {


    public ZBStartSettingPop(Context context) {
        super(context);
        setShowAnimation(AnimUtils.PopAnimBottom2Enter(context));
        setDismissAnimation(AnimUtils.PopAnimBottom2Exit(context));
        initView();
    }

    private void initView() {
        findViewById(R.id.ll_menu).setOnClickListener(lis -> {
            ZBDesSettingPop desSettingPop = new ZBDesSettingPop(getContext());
            desSettingPop.showPopupWindow();
        });
    }


    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.pop_zbj_start_setting);
    }
}
