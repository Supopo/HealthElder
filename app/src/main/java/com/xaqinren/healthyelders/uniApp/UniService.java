package com.xaqinren.healthyelders.uniApp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.MutableLiveData;

import com.alibaba.fastjson.JSON;
import com.xaqinren.healthyelders.BuildConfig;
import com.xaqinren.healthyelders.apiserver.UniRepository;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.uniApp.bean.SaveBean;
import com.xaqinren.healthyelders.uniApp.bean.UniBean;
import com.xaqinren.healthyelders.uniApp.bean.UniEventBean;
import com.xaqinren.healthyelders.utils.ACache;
import com.xaqinren.healthyelders.utils.LogUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.dcloud.common.DHInterface.ICallBack;
import io.dcloud.feature.sdk.DCUniMPSDK;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.http.DownLoadManager;
import me.goldze.mvvmhabit.http.download.ProgressCallBack;
import me.goldze.mvvmhabit.utils.StringUtils;

public class UniService extends Service implements LifecycleOwner {
    private LifecycleRegistry mLifecycleRegistry = new LifecycleRegistry(this);
    private String fileDir;
    private List<SaveBean> saveBeans;
    private List<UniBean> uniBeans;
    private List<UniBean> needDown;
    private MutableLiveData<List<UniBean>> mutableLiveData = new MutableLiveData<>();
    private final String CACHE_KEY = "uni_cache";
    private ACache aCache;
    private String TAG = UniService.class.getSimpleName();

    public static String KEY = "COMM_KEY";
    public static int KEY_REFRESH = 0X0011;
    public static int KEY_OPEN = 0X0012;

    public static void startService(Context context) {
        Intent intent = new Intent(context, UniService.class);
        context.startService(intent);
    }

    public static void startRefreshData(Context context) {
        Intent intent = new Intent(context, UniService.class);
        intent.putExtra(KEY, KEY_REFRESH);
        context.startService(intent);
    }

    public static void startService(Context context, String appId, int taskId, String jumpUrl) {
        Intent intent = new Intent(context, UniService.class);
        intent.putExtra(KEY, KEY_OPEN);
        if (StringUtils.isEmpty(appId)) {
            appId = Constant.JKZL_MINI_APP_ID;
        }
        intent.putExtra("id", appId);
        intent.putExtra("tid", taskId);
        intent.putExtra("url", jumpUrl);
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
        if (getExternalCacheDir() != null) { //storage/emulated/0/Android/data/com.xxx.xxx/cache
            fileDir = getExternalCacheDir().getAbsolutePath();
        } else { //防止上面娶不到路径 //storage/emulated/0/Android/data/com.xxx.xxx/files/uni
            fileDir = getExternalFilesDir("uni").getAbsolutePath();
        }

        saveBeans = new ArrayList<>();
        uniBeans = new ArrayList<>();
        needDown = new ArrayList<>();
        aCache = ACache.get(getFilesDir());
        saveBeans = JSON.parseArray(aCache.getAsString(CACHE_KEY), SaveBean.class);
        if (saveBeans == null) {
            saveBeans = new ArrayList<>();
        }
        look = new Object();
        initObservable();
        getUniList();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);
        super.onDestroy();
    }


    private void initObservable() {
        mutableLiveData.observe(this, list -> {
            uniBeans = list;
            saveAllBean();
        });
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            int key = intent.getIntExtra(KEY, 0);
            if (key == KEY_REFRESH) {
                getUniList();
            } else if (key == KEY_OPEN) {
                //appId
                String id = intent.getStringExtra("id");
                int tid = intent.getIntExtra("tid", 0);
                String url = intent.getStringExtra("url");
                for (SaveBean saveBean : saveBeans) {
                    if (saveBean.getAppId().equals(id)) {
                        releaseWgt(saveBean, tid, url);
                        break;
                    }
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    //获取线上小程序列表
    private void getUniList() {
        UniRepository.getInstance().getMyAtList(mutableLiveData);
    }


    //开始下载
    private void down(SaveBean uniBean) {
        //下载
        DownLoadManager.getInstance().load(uniBean.getDownUrl(), new ProgressCallBack(fileDir, uniBean.getAppId() + ".wgt") {
            @Override
            public void onSuccess(Object o) {
                LogUtils.d(TAG, uniBean.getAppId() + " >>>>>>> 下载完成");
                //保存参数到本地
                uniBean.setNeedDown(false);
                uniBean.setDownComplete(true);
                saveCache();
                toXCX(uniBean, 0, null);
            }

            @Override
            public void progress(long progress, long total) {
                LogUtils.d(TAG, uniBean.getAppId() + " >>>>>>> 下载进度 \t " + progress);
            }

            @Override
            public void onError(Throwable e) {
                LogUtils.d(TAG, uniBean.getAppId() + " >>>>>>> 下载失败");
            }
        });
    }

    //自动下载的时候自动释放
    private void toXCX(SaveBean saveBean, int tid, String url) {
        if (!saveBean.isDownComplete()) {
            saveBean.setOpenActivity(true);
            down(saveBean);
            return;
        }
        DCUniMPSDK.getInstance().releaseWgtToRunPathFromePath(saveBean.getAppId(), saveBean.getSavePath(), new ICallBack() {
            @Override
            public Object onCallBack(int code, Object pArgs) {
                if (code == 1) {//释放wgt完成
                    LogUtils.e(TAG, saveBean.getAppId() + " >>>>>>> 释放成功");
                    if (saveBean.isOpenActivity()) {
                        saveBean.setOpenActivity(false);
                        saveCache();
                        if (saveBean.isAutoUpdateApplet()) {
                            RxBus.getDefault().post(new UniEventBean(CodeTable.UNI_RELEASE, saveBean.getAppId(), tid, null, true, url));
                        } else {
                            RxBus.getDefault().post(new UniEventBean(CodeTable.UNI_RELEASE, saveBean.getAppId(), tid, null, false, url));
                        }
                    }
                } else {//释放wgt失败
                    LogUtils.e(TAG, saveBean.getAppId() + " >>>>>>> 释放失败");
                    //RxBus.getDefault().post(new UniEventBean(CodeTable.UNI_RELEASE_FAIL, saveBean.getAppId(), tid));
                }
                return null;
            }
        });
    }

    //上层调用，某些非自动下载的
    private void releaseWgt(SaveBean saveBean, int tid, String url) {
        saveBean.setOpenActivity(true);
        toXCX(saveBean, tid, url);
    }

    private Object look;

    private void saveCache() {
        synchronized (look) {
            aCache.put(CACHE_KEY, JSON.toJSONString(saveBeans));
        }
    }

    /**
     * 检查版本号
     */
    private void checkVersion() {
        boolean debug = BuildConfig.DEBUG;
        for (SaveBean saveBean : saveBeans) {
            if (debug || (saveBean.isNeedDown() && saveBean.isAutoUpdateApplet())) {
                down(saveBean);
            }
        }
    }

    //保存下载的数据
    private void saveAllBean() {
        for (UniBean uniBean : uniBeans) {
            saveBean(uniBean);
        }
        //保存到本地
        saveCache();
        checkVersion();
    }

    //通过uniBean保存一个本地bean
    private void saveBean(UniBean uniBean) {
        boolean flag = false;
        boolean find = false;
        for (SaveBean saveBean : saveBeans) {
            if (saveBean.getId().equals(uniBean.getId())) {
                find = true;
                LogUtils.e(TAG, "version = " + uniBean.getNewAppVersion().getVersionNumber());
                LogUtils.e(TAG, "version = " + saveBean.getCurrentVersion());
                if (uniBean.getNewAppVersion().getVersionNumber() != saveBean.getCurrentVersion()) {
                    saveBean.setId(uniBean.getId());
                    saveBean.setAutoUpdateApplet(uniBean.getAutoUpdateApplet());
                    saveBean.setCurrentVersion(uniBean.getNewAppVersion().getVersionNumber());
                    saveBean.setDownUrl(uniBean.getNewAppVersion().getUpgradeUrl());
                    saveBean.setName(uniBean.getAppName());
                    saveBean.setSavePath(new File(fileDir, uniBean.getAppId() + ".wgt").getAbsolutePath());
                    saveBean.setNeedDown(true);
                    saveBean.setDownComplete(false);
                    saveBean.setAppId(uniBean.getAppId());
                    flag = true;
                }
                break;
            }
        }
        if (!flag && !find) {
            saveBeans.add(saveSaveBean(uniBean));
        }
    }

    private SaveBean saveSaveBean(UniBean uniBean) {
        SaveBean saveBean = new SaveBean();
        saveBean.setId(uniBean.getId());
        saveBean.setAutoUpdateApplet(uniBean.getAutoUpdateApplet());
        saveBean.setCurrentVersion(uniBean.getNewAppVersion().getVersionNumber());
        saveBean.setDownUrl(uniBean.getNewAppVersion().getUpgradeUrl());
        saveBean.setName(uniBean.getAppName());
        saveBean.setAppId(uniBean.getAppId());
        saveBean.setSavePath(new File(fileDir, uniBean.getAppId() + ".wgt").getAbsolutePath());
        saveBean.setNeedDown(true);
        return saveBean;
    }


    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return mLifecycleRegistry;
    }

}
