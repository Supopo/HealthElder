package com.xaqinren.healthyelders.moduleZhiBo.popupWindow;

import android.content.Context;
import android.view.View;

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
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleHome.bean.MenuBean;
import com.xaqinren.healthyelders.moduleHome.bean.ShareBean;
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
 * 直播间更多设置弹窗
 */
public class ZBMoreSettingPop extends BasePopupWindow {


    private RecyclerView rvContent;
    private LiveMenuAdapter menuAdapter;
    private MLVBLiveRoom mLiveRoom;
    private LiveInitInfo mLiveInitInfo;
    private ShareDialog shareDialog;
    private LoadingDialog loadingDialog;
    private ZBSettingBean settingBean;

    public ZBMoreSettingPop(Context context, MLVBLiveRoom mLiveRoom, LiveInitInfo mLiveInitInfo) {
        super(context);
        this.mLiveRoom = mLiveRoom;
        this.mLiveInitInfo = mLiveInitInfo;

        //去掉背景
        setBackground(R.color.transparent);
        setShowAnimation(AnimUtils.PopAnimBottom2Enter(context));
        setDismissAnimation(AnimUtils.PopAnimBottom2Exit(context));
        initView();
        initEvent();
    }

    private String[] menuNames = {"翻转", "设置", "分享", "开启连麦", "美颜", "开启评论", "开启刷礼物", "滤镜"
    };
    private int[] menuRes = {
            R.mipmap.icon_zb_fanzhuan, R.mipmap.icon_shezhi_hui, R.mipmap.icon_zb_share,
            R.mipmap.icon_zb_kqlm, R.mipmap.icon_zb_meiy, R.mipmap.icon_zb_kqpl, R.mipmap.icon_zb_kqlw, R.mipmap.icon_zb_lj
    };

    private void initView() {
        loadingDialog = new LoadingDialog(getContext());
        rvContent = findViewById(R.id.rv_menu);
        menuAdapter = new LiveMenuAdapter(R.layout.item_start_live_menu);
        rvContent.setLayoutManager(new GridLayoutManager(getContext(), 4));
        rvContent.setAdapter(menuAdapter);

        List<MenuBean> menus = new ArrayList<>();
        for (int i = 0; i < menuNames.length; i++) {
            menus.add(new MenuBean("" + i, menuNames[i], menuRes[i], 1));
        }

        settingBean = new ZBSettingBean();
        settingBean.liveRoomId = mLiveInitInfo.liveRoomId;
        settingBean.setCanGift(mLiveInitInfo.getCanGift());
        settingBean.setCanComment(mLiveInitInfo.getCanComment());
        settingBean.setCanMic(mLiveInitInfo.getCanMic());
        if (mLiveInitInfo.getCanMic()) {
            menus.get(3).menuName = "开启连麦";
            menus.get(3).menuRes = R.mipmap.icon_zb_kqlm;
        } else {
            menus.get(3).menuName = "禁止连麦";
            menus.get(3).menuRes = R.mipmap.icon_zb_jzlm;
        }

        if (mLiveInitInfo.getCanComment()) {
            menus.get(5).menuName = "开启评论";
            menus.get(5).menuRes = R.mipmap.icon_zb_kqpl;
        } else {
            menus.get(5).menuName = "禁止评论";
            menus.get(5).menuRes = R.mipmap.icon_zb_jzpl;
        }

        if (mLiveInitInfo.getCanGift()) {
            menus.get(6).menuName = "开启刷礼物";
            menus.get(6).menuRes = R.mipmap.icon_zb_kqlw;
        } else {
            menus.get(6).menuName = "禁止刷礼物";
            menus.get(6).menuRes = R.mipmap.icon_zb_jzlw;
        }

        menuAdapter.setNewInstance(menus);
        menuAdapter.setOnItemClickListener(((adapter, view, position) -> {
            selectPos = position;

            switch (position) {
                case 0:
                    mLiveRoom.switchCamera();
                    break;
                case 1:
                    break;
                case 2:
                    if (shareDialog == null) {
                        shareDialog = new ShareDialog(getContext(), mLiveInitInfo.share, 2);
                    }
                    shareDialog.show(rvContent);
                    break;
                case 3:
                    loadingDialog.show();
                    settingBean.setCanMic(!mLiveInitInfo.getCanMic());
                    LiveRepository.getInstance().setZBStatus(dismissDialog, setSuccess, settingBean);
                    break;
                case 4:
                    showMYPop();
                    break;
                case 5:
                    loadingDialog.show();
                    settingBean.setCanComment(!mLiveInitInfo.getCanComment());
                    LiveRepository.getInstance().setZBStatus(dismissDialog, setSuccess, settingBean);
                    break;
                case 6:
                    loadingDialog.show();
                    settingBean.setCanGift(!mLiveInitInfo.getCanGift());
                    LiveRepository.getInstance().setZBStatus(dismissDialog, setSuccess, settingBean);
                    break;
                case 7:
                    showLJPop();
                    break;
            }
        }));
    }

    public int selectPos;

    public void initEvent() {
        dismissDialog.observe((LifecycleOwner) getContext(), dismiss -> {
            if (dismiss != null) {
                loadingDialog.dismiss();
            }
        });

        setSuccess.observe((LifecycleOwner) getContext(), setSuccess -> {
            if (setSuccess != null && setSuccess) {
                if (selectPos == 3) {
                    //更改 是否开启连麦
                    mLiveInitInfo.setCanMic(!mLiveInitInfo.getCanMic());
                    if (mLiveInitInfo.getCanMic()) {
                        menuAdapter.getData().get(3).menuName = "开启连麦";
                        menuAdapter.getData().get(3).menuRes = R.mipmap.icon_zb_kqlm;
                    } else {
                        menuAdapter.getData().get(3).menuName = "禁止连麦";
                        menuAdapter.getData().get(3).menuRes = R.mipmap.icon_zb_jzlm;
                    }
                    menuAdapter.notifyItemChanged(3);
                    //通知主播页面发送全局自定义消息
                    RxBus.getDefault().post(new EventBean(LiveConstants.ZBJ_MORE_SETTING, mLiveInitInfo.getCanMic() ? 1 : 2));
                } else if (selectPos == 5) {
                    //更改 是否开启评论
                    mLiveInitInfo.setCanComment(!mLiveInitInfo.getCanComment());
                    if (mLiveInitInfo.getCanComment()) {
                        menuAdapter.getData().get(5).menuName = "开启评论";
                        menuAdapter.getData().get(5).menuRes = R.mipmap.icon_zb_kqpl;
                    } else {
                        menuAdapter.getData().get(5).menuName = "禁止评论";
                        menuAdapter.getData().get(5).menuRes = R.mipmap.icon_zb_jzpl;
                    }
                    menuAdapter.notifyItemChanged(5);
                    //通知主播页面发送全局自定义消息
                    RxBus.getDefault().post(new EventBean(LiveConstants.ZBJ_MORE_SETTING, mLiveInitInfo.getCanComment() ? 3 : 4));
                } else if (selectPos == 6) {
                    //更改 是否开启礼物
                    mLiveInitInfo.setCanGift(!mLiveInitInfo.getCanGift());
                    if (mLiveInitInfo.getCanGift()) {
                        menuAdapter.getData().get(6).menuName = "开启刷礼物";
                        menuAdapter.getData().get(6).menuRes = R.mipmap.icon_zb_kqlw;
                    } else {
                        menuAdapter.getData().get(6).menuName = "禁止刷礼物";
                        menuAdapter.getData().get(6).menuRes = R.mipmap.icon_zb_jzlw;
                    }
                    menuAdapter.notifyItemChanged(6);
                    //通知主播页面发送全局自定义消息
                    RxBus.getDefault().post(new EventBean(LiveConstants.ZBJ_MORE_SETTING, mLiveInitInfo.getCanGift() ? 5 : 6));
                }
            }
        });
    }

    public MutableLiveData<Boolean> dismissDialog = new MutableLiveData<>();
    public MutableLiveData<Boolean> setSuccess = new MutableLiveData<>();


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
