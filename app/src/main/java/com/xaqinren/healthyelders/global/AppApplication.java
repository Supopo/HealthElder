package com.xaqinren.healthyelders.global;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.squareup.leakcanary.LeakCanary;

import com.tencent.imsdk.v2.V2TIMSDKConfig;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.qcloud.tim.uikit.TUIKit;
import com.tencent.qcloud.tim.uikit.config.CustomFaceConfig;
import com.tencent.qcloud.tim.uikit.config.GeneralConfig;
import com.tencent.qcloud.tim.uikit.config.TUIKitConfigs;
import com.tencent.qcloud.ugckit.UGCKit;
import com.tencent.qcloud.ugckit.utils.TCUserMgr;
import com.tencent.qcloud.xiaoshipin.config.TCConfigManager;
import com.tencent.rtmp.TXLiveBase;
import com.tencent.rtmp.TXLog;
import com.tencent.ugc.TXUGCBase;
import com.xaqinren.healthyelders.BuildConfig;
import com.xaqinren.healthyelders.MainActivity;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.MLVBLiveRoomImpl;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.TCGlobalConfig;

import java.util.ArrayList;
import java.util.List;

import io.dcloud.feature.sdk.DCSDKInitConfig;
import io.dcloud.feature.sdk.DCUniMPSDK;
import io.dcloud.feature.sdk.MenuActionSheetItem;
import me.goldze.mvvmhabit.base.BaseApplication;
import me.goldze.mvvmhabit.crash.CaocConfig;
import me.goldze.mvvmhabit.utils.KLog;

import static com.xaqinren.healthyelders.moduleZhiBo.liveRoom.TCGlobalConfig.SDKAPPID;


public class AppApplication extends BaseApplication {
    public static Context mContext;
    public String WX_APP_ID = "wx4083c9a2be58173b";
    public static IWXAPI mWXapi;
    /**
     * 短视频 key , licence
     */
    private String ugcKey = "4018de20566977de6c69d5a22f65c881";
    private String ugcLicenceUrl = "http://license.vod2.myqcloud.com/license/v1/acec2faaf32923c383cbb79df86f42db/TXUgcSDK.licence";
//    private String ugcLicenceUrl = "RRL6H7YYBLoGamzi1MODRmXiPmaDx99x52iGcVVUOl1/284KGSmZyhFa46OVZB0IBBXfj4lRjmnizionJAXnPeDStZg3eAncIw7dHPeKbJvxu1DS0Ux9uBZ9oHBkjuBjj8nIoBG6ucsdt/pG4GJQhDB/5+31UZz3xEf/fCRtrB28+r9PDK1qAiZKRfd4gfB5Mnoi9BsNiRVGwCF7fs+TpDncSd8UA1pq63lrZLbv5crMMYm3jG3KL9kpvAs0DMkqX0BrnXRqHqLWLa0xRjlaqgsNW3sLjVRY5w/kCSOfSoM=";


    public static Context getContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        //是否开启打印日志
        KLog.init(BuildConfig.DEBUG);
        //初始化全局异常崩溃
        initCrash();
        //内存泄漏检测
        if (BuildConfig.DEBUG) {
            if (!LeakCanary.isInAnalyzerProcess(this)) {
                LeakCanary.install(this);
            }
        }
        //初始化Uni小程序
        initUni();
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
    }

    private void initLiveRoom() {
        // 必须：初始化 LiteAVSDK Licence。 用于直播推流鉴权。
        TXLiveBase.getInstance().setLicence(this, TCGlobalConfig.LICENCE_URL, TCGlobalConfig.LICENCE_KEY);
        // 必须：初始化 MLVB 组件
        MLVBLiveRoomImpl.sharedInstance(this);
    }

    private void initUni() {
        MenuActionSheetItem item = new MenuActionSheetItem("关于", "gy");
        List<MenuActionSheetItem> sheetItems = new ArrayList<>();
        sheetItems.add(item);
        DCSDKInitConfig config = new DCSDKInitConfig.Builder()
                .setCapsule(true)
                .setMenuDefFontSize("16px")
                .setMenuDefFontColor("#ff00ff")
                .setMenuDefFontWeight("normal")
                .setMenuActionSheetItems(sheetItems)
                .build();
        DCUniMPSDK.getInstance().initialize(this, config, new DCUniMPSDK.IDCUNIMPPreInitCallback() {
            @Override
            public void onInitFinished(boolean isSuccess) {
            }
        });
    }

    private void initCrash() {
        CaocConfig.Builder.create()
                .backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT) //背景模式,开启沉浸式
                .enabled(true) //是否启动全局异常捕获
                .showErrorDetails(true) //是否显示错误详细信息
                .showRestartButton(true) //是否显示重启按钮
                .trackActivities(true) //是否跟踪Activity
                .minTimeBetweenCrashesMs(2000) //崩溃的间隔时间(毫秒)
                .errorDrawable(R.mipmap.ic_launcher) //错误图标
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
}
