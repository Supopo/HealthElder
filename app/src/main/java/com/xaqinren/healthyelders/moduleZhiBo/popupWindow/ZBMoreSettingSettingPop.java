package com.xaqinren.healthyelders.moduleZhiBo.popupWindow;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tencent.liteav.demo.beauty.model.BeautyInfo;
import com.tencent.liteav.demo.beauty.model.ItemInfo;
import com.tencent.liteav.demo.beauty.model.TabInfo;
import com.tencent.liteav.demo.beauty.view.BeautyPanel;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.apiserver.LiveRepository;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.moduleHome.bean.MenuBean;
import com.xaqinren.healthyelders.moduleZhiBo.adapter.LiveMenuAdapter;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveInitInfo;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ZBSettingBean;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.LiveConstants;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.MLVBLiveRoom;
import com.xaqinren.healthyelders.utils.AnimUtils;
import com.xaqinren.healthyelders.widget.BottomDialog;
import com.xaqinren.healthyelders.widget.share.ShareDialog;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.widget.LoadingDialog;
import razerdp.basepopup.BasePopupWindow;

/**
 * Created by Lee. on 2021/4/20.
 * 直播间更多设置-设置
 */
public class ZBMoreSettingSettingPop extends BasePopupWindow {
    private LiveInitInfo mLiveInitInfo;
    private LoadingDialog loadingDialog;
    private LinearLayout llSet;
    private LinearLayout llDes;
    private Context context;

    public ZBMoreSettingSettingPop(Context context, LiveInitInfo mLiveInitInfo) {
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
            ZBMoreSettingUserListPop settingUserListPop = new ZBMoreSettingUserListPop(context, mLiveInitInfo);
            settingUserListPop.showPopupWindow();
        });
        llDes.setOnClickListener(lis -> {
            ZBDesSettingPop zbDesSettingPop = new ZBDesSettingPop(context, mLiveInitInfo);
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
        return createPopupById(R.layout.pop_zbj_more_setting_set);
    }
}
