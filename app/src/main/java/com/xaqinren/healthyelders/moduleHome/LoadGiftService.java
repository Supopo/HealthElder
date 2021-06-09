package com.xaqinren.healthyelders.moduleHome;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.http.HttpResponseCache;
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
import com.xaqinren.healthyelders.apiserver.LiveRepository;
import com.xaqinren.healthyelders.moduleZhiBo.bean.GiftBean;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class LoadGiftService extends Service implements LifecycleOwner {
    private LifecycleRegistry mLifecycleRegistry = new LifecycleRegistry(this);
    private MutableLiveData<List<GiftBean>> mutableLiveData = new MutableLiveData<>();
    private String TAG = LoadGiftService.class.getSimpleName();

    public static void startService(Context context) {
        Intent intent = new Intent(context, LoadGiftService.class);
        context.startService(intent);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
        initObservable();
        getGiftList();
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


    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return mLifecycleRegistry;
    }

}
