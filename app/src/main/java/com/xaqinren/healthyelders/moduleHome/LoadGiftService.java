package com.xaqinren.healthyelders.moduleHome;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.http.HttpResponseCache;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.MutableLiveData;

import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.apiserver.LiveRepository;
import com.xaqinren.healthyelders.apiserver.UserRepository;
import com.xaqinren.healthyelders.bean.SlideBarBean;
import com.xaqinren.healthyelders.global.AppApplication;
import com.xaqinren.healthyelders.global.GlobalData;
import com.xaqinren.healthyelders.moduleZhiBo.bean.GiftBean;
import com.xaqinren.healthyelders.utils.LogUtils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class LoadGiftService extends Service implements LifecycleOwner {
    private LifecycleRegistry mLifecycleRegistry = new LifecycleRegistry(this);
    private MutableLiveData<List<GiftBean>> mutableLiveData = new MutableLiveData<>();
    private MutableLiveData<SlideBarBean> slideBarLiveData = new MutableLiveData<>();
    private String TAG = LoadGiftService.class.getSimpleName();
    private static Context mContext;
    private List<GiftBean> temp;

    public static void startService(Context context) {
        mContext = context;
        //Android 8.0 不再允许后台service直接通过startService方式去启动，否则就会引起IllegalStateException。
        Intent intent = new Intent(context, LoadGiftService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    public static void stopService(Context context) {
        Intent intent = new Intent(context, LoadGiftService.class);
        context.stopService(intent);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.v("--gifts--", "onCreate");
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
        toStart();
    }

    public void toStart() {
        //适配8.0service
        NotificationManager notificationManager = (NotificationManager) AppApplication.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel mChannel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel("InitService", getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(mChannel);
            Notification notification = new Notification.Builder(getApplicationContext(), "InitService").build();
            //会在通知栏展示
            startForeground(1, notification);
        }

        initObservable();
        getGiftList();
        getSlideBar();
    }


    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.v("--gifts--", "onStart");
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
        Log.v("--gifts--", "onDestroy");
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);
        stopForeground(true);
        super.onDestroy();
    }


    private void initObservable() {
        mutableLiveData.observe(this, list -> {
            if (list != null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        loadCache(list);
                    }
                }).start();
            }
        });
        slideBarLiveData.observe(this, data -> GlobalData.getInstance().saveSlideBar(data));
    }

    private int count;

    //下载svga动画
    private void loadCache(List<GiftBean> gifts) {
        //svga缓存设置
        File cacheDir = new File(getApplicationContext().getCacheDir().getAbsolutePath(), "svga");
        try {
            HttpResponseCache.install(cacheDir, 1024 * 1024 * 200);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try { // new URL needs try catch.
            SVGAParser svgaParser = new SVGAParser(this);
            svgaParser.setFrameSize(100, 100);

            temp = new ArrayList<>();
            //统计需要下载的数量
            for (GiftBean gift : gifts) {
                if (!TextUtils.isEmpty(gift.giftUrl)) {
                    temp.add(gift);
                }
            }

            for (GiftBean gift : temp) {
                svgaParser.decodeFromURL(new URL(gift.giftUrl), new SVGAParser.ParseCompletion() {
                    @Override
                    public void onError() {
                        count++;
                        if (count == temp.size()) {
                            count = 0;
                            stopForeground(true);
                        }
                    }

                    @Override
                    public void onComplete(@NotNull SVGAVideoEntity videoItem) {
                        count++;
                        Log.v("--gifts--", "Count: " + count);
                        if (count == temp.size()) {
                            //下载完成关闭前台通知
                            count = 0;
                            stopForeground(true);
                            AppApplication.get().giftLoadSuccess = true;
                            stopService(mContext);
                        }
                    }
                });
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    //获取礼物列表
    private void getGiftList() {
        count = 0;
        LiveRepository.getInstance().getGiftList(mutableLiveData);
    }

    private void getSlideBar() {
        UserRepository.getInstance().getAppSideBar(slideBarLiveData);
    }


    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return mLifecycleRegistry;
    }

}
