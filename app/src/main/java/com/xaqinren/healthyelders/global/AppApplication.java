package com.xaqinren.healthyelders.global;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

import com.igexin.sdk.PushManager;
import com.tencent.imsdk.v2.V2TIMSDKConfig;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.qcloud.tim.uikit.TUIKit;
import com.tencent.qcloud.tim.uikit.config.CustomFaceConfig;
import com.tencent.qcloud.tim.uikit.config.GeneralConfig;
import com.tencent.qcloud.tim.uikit.config.TUIKitConfigs;
import com.tencent.qcloud.tim.uikit.utils.ToastUtil;
import com.tencent.qcloud.ugckit.UGCKit;
import com.tencent.qcloud.ugckit.utils.TCUserMgr;
import com.tencent.qcloud.xiaoshipin.config.TCConfigManager;
import com.tencent.rtmp.TXLiveBase;
import com.tencent.rtmp.TXLog;
import com.tencent.ugc.TXUGCBase;
import com.xaqinren.healthyelders.BuildConfig;
import com.xaqinren.healthyelders.MainActivity;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.MLVBLiveRoomImpl;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.TCGlobalConfig;
import com.xaqinren.healthyelders.uniApp.module.JSCommModule;
import com.xaqinren.healthyelders.utils.LogUtils;

import java.util.HashMap;

import io.dcloud.common.util.RuningAcitvityUtil;
import io.dcloud.feature.sdk.DCSDKInitConfig;
import io.dcloud.feature.sdk.DCUniMPSDK;
import io.dcloud.feature.uniapp.UniSDKEngine;
import io.dcloud.feature.uniapp.common.UniException;
import me.goldze.mvvmhabit.base.BaseApplication;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.crash.CaocConfig;
import me.goldze.mvvmhabit.utils.KLog;

import static com.xaqinren.healthyelders.moduleZhiBo.liveRoom.TCGlobalConfig.SDKAPPID;


public class AppApplication extends BaseApplication {
    private String TAG = "AppApplication";
    public static Context mContext;
    public String WX_APP_ID = "wx4083c9a2be58173b";
    public static IWXAPI mWXapi;
    /**
     * 短视频 key , licence
     */
    private String ugcKey = "4018de20566977de6c69d5a22f65c881";
    private String ugcLicenceUrl = "http://license.vod2.myqcloud.com/license/v1/acec2faaf32923c383cbb79df86f42db/TXUgcSDK.licence";
    //    private String ugcLicenceUrl = "RRL6H7YYBLoGamzi1MODRmXiPmaDx99x52iGcVVUOl1/284KGSmZyhFa46OVZB0IBBXfj4lRjmnizionJAXnPeDStZg3eAncIw7dHPeKbJvxu1DS0Ux9uBZ9oHBkjuBjj8nIoBG6ucsdt/pG4GJQhDB/5+31UZz3xEf/fCRtrB28+r9PDK1qAiZKRfd4gfB5Mnoi9BsNiRVGwCF7fs+TpDncSd8UA1pq63lrZLbv5crMMYm3jG3KL9kpvAs0DMkqX0BrnXRqHqLWLa0xRjlaqgsNW3sLjVRY5w/kCSOfSoM=";
    private static AppApplication instance = null;


    public static HashMap<String, Object> activityTask = new HashMap<>();


    public static Context getContext() {
        return mContext;
    }

    public static AppApplication get() {
        return instance;
    }

    /**
     * 是否打开了小程序
     */
    public static boolean isUserMini = false;


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        mContext = getApplicationContext();
        //是否开启打印日志
        KLog.init(BuildConfig.DEBUG);


        //初始化全局异常崩溃
        initCrash();
        //        //内存泄漏检测
        //        if (BuildConfig.DEBUG) {
        //            if (!LeakCanary.isInAnalyzerProcess(this)) {
        //                LeakCanary.install(this);
        //            }
        //        }
        //初始化Uni小程序
        initUni(null);
        //注册微信
        registerWx();
        //短视频
        initLiteAv();
        //初始化im聊天
        initTIM();
        //初始化直播
        initLiveRoom();
        //初始化用户信息持有类
        UserInfoMgr.getInstance().initContext(getApplicationContext());
        //个推
        PushManager.getInstance().initialize(getApplicationContext());
        PushManager.getInstance().setDebugLogger(getApplicationContext(), s -> LogUtils.e("PushManager", s));

        int pid = android.os.Process.myPid();
        LogUtils.e(TAG, "进程 id -> " + pid);
        // 非小程序进程
        if (RuningAcitvityUtil.getAppName(getBaseContext()).contains("io.dcloud.unimp")) {
            LogUtils.e(TAG, "小程序进程");
        }

        //        registerLife();
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    private void initLiveRoom() {
        // 必须：初始化 LiteAVSDK Licence。 用于直播推流鉴权。
        TXLiveBase.getInstance().setLicence(this, TCGlobalConfig.LICENCE_URL, TCGlobalConfig.LICENCE_KEY);
        // 必须：初始化 MLVB 组件
        MLVBLiveRoomImpl.sharedInstance(this);
    }

    public void initUni(OnUniIntCallBack callBack) {

        DCSDKInitConfig config = new DCSDKInitConfig.Builder()
                .setCapsule(true)
                .setMenuDefFontSize("16px")
                .setMenuDefFontColor("#ff00ff")
                .setMenuDefFontWeight("normal")
                .setEnableBackground(true)
                .build();

        try {
            UniSDKEngine.registerModule("qnx-extPlugin", JSCommModule.class);
        } catch (UniException e) {
            e.printStackTrace();
        }
        DCUniMPSDK.getInstance().initialize(this, config, isSuccess -> {
            if (callBack != null)
                callBack.initSuccess();
        });
        DCUniMPSDK.getInstance().setUniMPOnCloseCallBack(s -> {
            LogUtils.e(TAG, "小程序关闭\t" + s);
        });
        DCUniMPSDK.getInstance().setDefMenuButtonClickCallBack(new DCUniMPSDK.IMenuButtonClickCallBack() {

            @Override
            public void onClick(String s, String s1) {
                LogUtils.e(TAG, "小程序点击 \t" + s + " \t s1 -> " + s1);
            }
        });
        LogUtils.e(TAG, "小程序初始化完成\t");
    }

    private void initCrash() {
        CaocConfig.Builder.create()
                .backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT) //背景模式,开启沉浸式
                .enabled(true) //是否启动全局异常捕获
                .showErrorDetails(true) //是否显示错误详细信息
                .showRestartButton(true) //是否显示重启按钮
                .trackActivities(true) //是否跟踪Activity
                .minTimeBetweenCrashesMs(500) //崩溃的间隔时间(毫秒)
                .errorDrawable(R.mipmap.no_goods_data) //错误图标
                .restartActivity(MainActivity.class) //重新启动后的activity
                // .errorActivity(YourCustomErrorActivity.class) //崩溃后的错误activity
                // .eventListener(new YourCustomEventListener()) //崩溃后的错误监听
                .apply();
    }

    private void registerWx() {
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        mWXapi = WXAPIFactory.createWXAPI(this, WX_APP_ID, true);

        // 将应用的appId注册到微信
        mWXapi.registerApp(WX_APP_ID);

        //建议动态监听微信启动广播进行注册到微信
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // 将该app注册到微信
                mWXapi.registerApp(WX_APP_ID);
            }
        }, new IntentFilter(ConstantsAPI.ACTION_REFRESH_WXAPP));

    }

    private void initLiteAv() {
        TCConfigManager.init(this);
        TCUserMgr.getInstance().initContext(getApplicationContext());
        TXLog.w("AppApplication", "app init sdk");
        // 短视频licence设置
        TXUGCBase.getInstance().setLicence(this, ugcLicenceUrl, ugcKey);
        UGCKit.init(this);
    }

    private void initTIM() {
        // 配置 Config，请按需配置
        TUIKitConfigs configs = TUIKit.getConfigs();
        configs.setSdkConfig(new V2TIMSDKConfig());
        configs.setCustomFaceConfig(new CustomFaceConfig());
        configs.setGeneralConfig(new GeneralConfig());

        TUIKit.init(this, SDKAPPID, configs);
    }

    public boolean isShowTopMenu() {
        return showTopMenu;
    }

    public void setShowTopMenu(boolean showTopMenu) {
        this.showTopMenu = showTopMenu;
    }

    public boolean isHasNavBar() {
        return hasNavBar;
    }

    public void setHasNavBar(boolean hasNavBar) {
        this.hasNavBar = hasNavBar;
    }

    private boolean hasNavBar;//是否开启了虚拟按键


    //本次关注列表
    public HashMap<String, Boolean> followList = new HashMap<>();

    //底部菜单位置
    public int bottomMenu;

    //顶部菜单是否展示
    private boolean showTopMenu = true;

    //用于标记首页推荐滑动播放位置
    private int tjPlayPosition = -1;
    //用于标记首页关注滑动播放位置
    private int gzPlayPosition = -1;
    //用户标记首页推荐-关注位置
    private int layoutPos = 0;
    //小视频列表标记位置
    private int playPosition = 0;
    //视频标记
    private String tag = "home-tj";

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }


    public int getPlayPosition() {
        return playPosition;
    }

    public void setPlayPosition(int playPosition) {
        this.playPosition = playPosition;
    }


    public int getLayoutPos() {
        return layoutPos;
    }

    public void setLayoutPos(int layoutPos) {
        this.layoutPos = layoutPos;
    }


    public int getTjPlayPosition() {
        return tjPlayPosition;
    }

    public void setTjPlayPosition(int tjPlayPosition) {
        this.tjPlayPosition = tjPlayPosition;
    }

    public int getGzPlayPosition() {
        return gzPlayPosition;
    }

    public void setGzPlayPosition(int gzPlayPosition) {
        this.gzPlayPosition = gzPlayPosition;
    }

    public double getmLat() {
        return mLat;
    }

    public void setmLat(double mLat) {
        this.mLat = mLat;
    }

    public double getmLon() {
        return mLon;
    }

    public void setmLon(double mLon) {
        this.mLon = mLon;
    }

    private double mLat;
    private double mLon;


    public static boolean isToLogin() {
        //判断有无登录
        if (TextUtils.isEmpty(UserInfoMgr.getInstance().getAccessToken())) {
            RxBus.getDefault().post(new EventBean(CodeTable.TOKEN_ERR, null));
            return true;
        }
        return false;
    }

    public interface OnUniIntCallBack {
        void initSuccess();
    }
}
