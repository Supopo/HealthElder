package com.xaqinren.healthyelders.moduleHome;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.http.HttpResponseCache;
import android.os.Build;
import android.os.IBinder;
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
import java.util.List;

public class LoadGiftService extends Service implements LifecycleOwner {
    private LifecycleRegistry mLifecycleRegistry = new LifecycleRegistry(this);
    private MutableLiveData<List<GiftBean>> mutableLiveData = new MutableLiveData<>();
    private MutableLiveData<SlideBarBean> slideBarLiveData = new MutableLiveData<>();
    private String TAG = LoadGiftService.class.getSimpleName();

    public static void startService(Context context) {
        //Android 8.0 不再允许后台service直接通过startService方式去启动，否则就会引起IllegalStateException。
        Intent intent = new Intent(context, LoadGiftService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        //适配8.0service
        NotificationManager notificationManager = (NotificationManager) AppApplication.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel mChannel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel("InitService", getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(mChannel);
            Notification notification = new Notification.Builder(getApplicationContext(), "InitService").build();
            startForeground(1, notification);
        }

        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
        initObservable();
        getGiftList();
        getSlideBar();
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

    //下载svga动画
    private void loadCache(List<GiftBean> gifts) {

        File cacheDir = new File(getApplicationContext().getCacheDir().getAbsolutePath(), "http");
        try {
            HttpResponseCache.install(cacheDir, 1024 * 1024 * 200);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try { // new URL needs try catch.
            SVGAParser svgaParser = new SVGAParser(this);
            svgaParser.setFrameSize(100, 100);
            for (GiftBean gift : gifts) {

                if (!TextUtils.isEmpty(gift.giftUrl)) {
                    svgaParser.decodeFromURL(new URL(gift.giftUrl), new SVGAParser.ParseCompletion() {
                        @Override
                        public void onComplete(@NotNull SVGAVideoEntity videoItem) {

                        }

                        @Override
                        public void onError() {

                        }
                    });
                }
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
