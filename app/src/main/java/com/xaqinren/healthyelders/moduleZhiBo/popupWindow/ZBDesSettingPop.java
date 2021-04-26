package com.xaqinren.healthyelders.moduleZhiBo.popupWindow;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveInitInfo;
import com.xaqinren.healthyelders.utils.AnimUtils;
import com.xaqinren.healthyelders.widget.SwitchButton;

import razerdp.basepopup.BasePopupWindow;

/**
 * Created by Lee. on 2021/4/20.
 * 开启直播间简介设置弹窗
 */
public class ZBDesSettingPop extends BasePopupWindow {
    private LiveInitInfo liveInitInfo;
    private SwitchButton sbKQJS;
    private Button btnSave;
    private EditText etContent;


    public ZBDesSettingPop(Context context, LiveInitInfo liveInfo) {
        super(context);
        //去掉背景
        setBackground(R.color.transparent);
        setShowAnimation(AnimUtils.PopAnimRight2Enter(context));
        setDismissAnimation(AnimUtils.PopAnimRight2Exit(context));
        this.liveInitInfo = liveInfo;
        initView();
    }

    private void initView() {
        findViewById(R.id.iv_back).setOnClickListener(lis -> {
            dismiss();
        });
        sbKQJS = findViewById(R.id.sb_kqjs);
        btnSave = findViewById(R.id.btn_save);
        etContent = findViewById(R.id.et_content);
        etContent.setText(liveInitInfo.liveRoomIntroduce);
        sbKQJS.setChecked(liveInitInfo.getHasIntroduce());
        btnSave.setOnClickListener(lis -> {
            liveInitInfo.liveRoomIntroduce = etContent.getText().toString().trim();
        });
    }

    @Override
    public void dismiss() {
        super.dismiss();
        liveInitInfo.setHasIntroduce(sbKQJS.isChecked());
    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.pop_zbj_start_describe);
    }
}
