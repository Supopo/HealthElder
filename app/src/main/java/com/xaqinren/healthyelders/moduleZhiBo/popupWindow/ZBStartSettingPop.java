package com.xaqinren.healthyelders.moduleZhiBo.popupWindow;

import android.content.Context;
import android.view.View;

import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveInitInfo;
import com.xaqinren.healthyelders.utils.AnimUtils;
import com.xaqinren.healthyelders.widget.SwitchButton;

import razerdp.basepopup.BasePopupWindow;

/**
 * Created by Lee. on 2021/4/20.
 * 开启直播间设置弹窗
 */
public class ZBStartSettingPop extends BasePopupWindow {
    private LiveInitInfo liveInitInfo;
    private SwitchButton sbMenu1;
    private SwitchButton sbMenu2;
    private SwitchButton sbMenu3;

    public ZBStartSettingPop(Context context, LiveInitInfo liveInfo) {
        super(context);
        setShowAnimation(AnimUtils.PopAnimBottom2Enter(context));
        setDismissAnimation(AnimUtils.PopAnimBottom2Exit(context));
        this.liveInitInfo = liveInfo;
        initView();
    }

    private void initView() {
        sbMenu1 = findViewById(R.id.sb_menu1);
        sbMenu2 = findViewById(R.id.sb_menu2);
        sbMenu3 = findViewById(R.id.sb_menu3);
        sbMenu1.setChecked(liveInitInfo.getHasNotice());
        sbMenu2.setChecked(liveInitInfo.getCanRecordVideo());
        sbMenu3.setChecked(liveInitInfo.getCanGift());
        findViewById(R.id.ll_menu).setOnClickListener(lis -> {
            ZBDesSettingPop desSettingPop = new ZBDesSettingPop(getContext(), liveInitInfo);
            desSettingPop.showPopupWindow();
        });
    }

    @Override
    public void dismiss() {
        super.dismiss();
        liveInitInfo.setHasNotice(sbMenu1.isChecked());
        liveInitInfo.setCanRecordVideo(sbMenu2.isChecked());
        liveInitInfo.setCanGift(sbMenu3.isChecked());
    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.pop_zbj_start_setting);
    }
}
