package com.xaqinren.healthyelders.moduleZhiBo.popupWindow;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tencent.liteav.demo.beauty.model.BeautyInfo;
import com.tencent.liteav.demo.beauty.model.ItemInfo;
import com.tencent.liteav.demo.beauty.model.TabInfo;
import com.tencent.liteav.demo.beauty.view.BeautyPanel;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.moduleHome.bean.MenuBean;
import com.xaqinren.healthyelders.moduleHome.bean.ShareBean;
import com.xaqinren.healthyelders.moduleZhiBo.adapter.LiveMenuAdapter;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveInitInfo;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.MLVBLiveRoom;
import com.xaqinren.healthyelders.utils.AnimUtils;
import com.xaqinren.healthyelders.widget.BottomDialog;
import com.xaqinren.healthyelders.widget.share.ShareDialog;

import java.util.ArrayList;
import java.util.List;

import razerdp.basepopup.BasePopupWindow;

/**
 * Created by Lee. on 2021/4/20.
 * 直播间更多设置弹窗
 */
public class ZBMoreSettingPop extends BasePopupWindow {


    private RecyclerView rvContent;
    private LiveMenuAdapter menuAdapter;
    private MLVBLiveRoom mLiveRoom;
    private LiveInitInfo mLiveInitInfo;
    private ShareDialog shareDialog;

    public ZBMoreSettingPop(Context context, MLVBLiveRoom mLiveRoom, LiveInitInfo mLiveInitInfo) {
        super(context);
        this.mLiveRoom = mLiveRoom;
        this.mLiveInitInfo = mLiveInitInfo;
        //去掉背景
        setBackground(R.color.transparent);
        setShowAnimation(AnimUtils.PopAnimBottom2Enter(context));
        setDismissAnimation(AnimUtils.PopAnimBottom2Exit(context));
        initView();

    }

    private String[] menuNames = {"翻转", "设置", "分享", "开启连麦", "美颜", "开启评论", "开启刷礼物", "滤镜"
    };
    private int[] menuRes = {
            R.mipmap.icon_zb_fanzhuan, R.mipmap.icon_shezhi_hui, R.mipmap.icon_zb_share,
            R.mipmap.icon_shar_dia, R.mipmap.icon_zb_meiy, R.mipmap.icon_zb_kqpl, R.mipmap.icon_zb_kqlw, R.mipmap.icon_zb_lj
    };

    private void initView() {
        rvContent = findViewById(R.id.rv_menu);
        menuAdapter = new LiveMenuAdapter(R.layout.item_start_live_menu);
        rvContent.setLayoutManager(new GridLayoutManager(getContext(), 4));
        rvContent.setAdapter(menuAdapter);

        List<MenuBean> menus = new ArrayList<>();
        for (int i = 0; i < menuNames.length; i++) {
            menus.add(new MenuBean("" + i, menuNames[i], menuRes[i], 1));
        }
        menuAdapter.setNewInstance(menus);
        menuAdapter.setOnItemClickListener(((adapter, view, position) -> {
            switch (menuAdapter.getData().get(position).menuName) {
                case "翻转":
                    mLiveRoom.switchCamera();
                    break;
                case "设置":
                    break;
                case "分享":
                    if (shareDialog == null) {
                        ShareBean shareBean = new ShareBean();
                        shareBean.coverUrl = mLiveInitInfo.avatarUrl;
                        shareBean.url = mLiveInitInfo.pullStreamUrl;
                        shareBean.title = mLiveInitInfo.liveRoomName;
                        shareBean.subTitle = mLiveInitInfo.liveRoomIntroduce;
                        shareDialog = new ShareDialog(getContext(), shareBean, 2);
                    }
                    shareDialog.show(rvContent);
                    break;
                case "开启连麦":
                    break;
                case "美颜":
                    showMYPop();
                    break;
                case "开启评论":
                    break;
                case "开启刷礼物":
                    break;
                case "滤镜":
                    showLJPop();
                    break;
            }
        }));
    }


    private BottomDialog mLvJingPop;
    private BottomDialog mMeiYanPop;
    private BeautyPanel mMeiYanControl;
    private BeautyPanel mLvJingControl;

    private void showMYPop() {

        if (mMeiYanPop == null) {
            View filterView = View.inflate(getContext(), R.layout.pop_beauty_control, null);
            mMeiYanControl = filterView.findViewById(R.id.beauty_pannel);
            mMeiYanControl.setPosition(0);
            mMeiYanControl.setBeautyManager(mLiveRoom.getBeautyManager());
            mMeiYanControl.setPopTitle("美颜");
            mMeiYanPop = new BottomDialog(getContext(), filterView,
                    null, true);
        }
        mMeiYanPop.show();
        mMeiYanControl.setOnBeautyListener(new BeautyPanel.OnBeautyListener() {
            @Override
            public void onTabChange(TabInfo tabInfo, int position) {
            }

            @Override
            public boolean onClose() {
                return false;
            }

            @Override
            public boolean onClick(TabInfo tabInfo, int tabPosition, ItemInfo itemInfo, int itemPosition) {
                if (itemPosition < 3) {
                    mBeautyStyle = itemPosition;
                }
                return false;
            }

            @Override
            public boolean onLevelChanged(TabInfo tabInfo, int tabPosition, ItemInfo itemInfo, int itemPosition, int beautyLevel) {
                if (itemPosition < 3) {
                    mBeautyStyle = itemPosition;
                    mBeautyLevel = beautyLevel;
                } else if (itemPosition == 3) {
                    mWhitenessLevel = beautyLevel;
                } else {
                    mRuddinessLevel = beautyLevel;
                }
                return false;
            }
        });
    }

    private int mBeautyStyle = 2;//美颜风格，三种美颜风格 默认第三种
    private int mBeautyLevel = 4;//美颜级别，取值范围 0 - 9
    private int mWhitenessLevel = 1;//美白级别，取值范围
    private int mRuddinessLevel;//红润级别，取值范围

    //滤镜设置弹窗
    private void showLJPop() {
        if (mLvJingPop == null) {
            View filterView = View.inflate(getContext(), R.layout.pop_beauty_control, null);
            mLvJingControl = filterView.findViewById(R.id.beauty_pannel);
            mLvJingControl.setPosition(1);
            mLvJingControl.setBeautyManager(mLiveRoom.getBeautyManager());

            mLvJingControl.setPopTitle("滤镜");
            BeautyInfo defaultBeautyInfo = mLvJingControl.getDefaultBeautyInfo();
            mLvJingControl.setBeautyInfo(defaultBeautyInfo);

            mLvJingPop = new BottomDialog(getContext(), filterView,
                    null, true);
        }

        mLvJingPop.show();


        mLvJingControl.setOnBeautyListener(new BeautyPanel.OnBeautyListener() {
            @Override
            public void onTabChange(TabInfo tabInfo, int position) {
            }

            @Override
            public boolean onClose() {
                return false;
            }

            @Override
            public boolean onClick(TabInfo tabInfo, int tabPosition, ItemInfo itemInfo, int itemPosition) {
                return false;
            }

            @Override
            public boolean onLevelChanged(TabInfo tabInfo, int tabPosition, ItemInfo itemInfo, int itemPosition, int beautyLevel) {
                return false;
            }
        });
    }

    @Override
    public void dismiss() {
        super.dismiss();

    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.pop_zbj_more_setting);
    }
}
