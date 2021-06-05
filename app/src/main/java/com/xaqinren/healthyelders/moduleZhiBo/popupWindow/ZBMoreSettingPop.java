package com.xaqinren.healthyelders.moduleZhiBo.popupWindow;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveInitInfo;
import com.xaqinren.healthyelders.utils.AnimUtils;

import me.goldze.mvvmhabit.widget.LoadingDialog;
import razerdp.basepopup.BasePopupWindow;

/**
 * Created by Lee. on 2021/4/20.
 * 直播间更多设置-设置
 */
public class ZBMoreSettingPop extends BasePopupWindow {
    private LiveInitInfo mLiveInitInfo;
    private LoadingDialog loadingDialog;
    private LinearLayout llSet;
    private LinearLayout llDes;
    private Context context;
    private ZBBlackUserListPop settingUserListPop;
    private ZBDesSettingPop zbDesSettingPop;

    public ZBMoreSettingPop(Context context, LiveInitInfo mLiveInitInfo) {
        super(context);
        this.mLiveInitInfo = mLiveInitInfo;
        this.context = context;

        //去掉背景
        setBackground(R.color.transparent);
        setShowAnimation(AnimUtils.PopAnimBottom2Enter(context));
        setDismissAnimation(AnimUtils.PopAnimBottom2Exit(context));
        initView();
    }


    private void initView() {
        loadingDialog = new LoadingDialog(getContext());
        llSet = findViewById(R.id.ll_set);
        llDes = findViewById(R.id.ll_des);
        llSet.setOnClickListener(lis -> {
            settingUserListPop = new ZBBlackUserListPop(context, mLiveInitInfo);
            settingUserListPop.showPopupWindow();
        });
        llDes.setOnClickListener(lis -> {
            zbDesSettingPop = new ZBDesSettingPop(context, mLiveInitInfo,1);
            zbDesSettingPop.showPopupWindow();
        });

    }


    public MutableLiveData<Boolean> dismissDialog = new MutableLiveData<>();
    public MutableLiveData<Boolean> setSuccess = new MutableLiveData<>();

    @Override
    public void dismiss() {
        super.dismiss();

    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.pop_zbj_more_setting);
    }
}
