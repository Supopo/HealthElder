package com.xaqinren.healthyelders.moduleZhiBo.popupWindow;

import android.content.Context;
import android.view.View;

import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.utils.AnimUtils;

import razerdp.basepopup.BasePopupWindow;

/**
 * Created by Lee. on 2021/4/20.
 * 直播间双人聊设置弹窗
 */
public class ZB2LinkSettingPop extends BasePopupWindow {


    public ZB2LinkSettingPop(Context context) {
        super(context);
        setShowAnimation(AnimUtils.PopAnimBottom2Enter(context));
        setDismissAnimation(AnimUtils.PopAnimBottom2Exit(context));
        initView();
    }

    private void initView() {

    }


    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.pop_zbj_2link_setting);
    }
}