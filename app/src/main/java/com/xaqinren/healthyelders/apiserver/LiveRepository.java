package com.xaqinren.healthyelders.apiserver;

import androidx.lifecycle.MutableLiveData;

import com.alibaba.fastjson.JSON;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.http.RetrofitClient;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveInitInfo;

import java.util.HashMap;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import me.goldze.mvvmhabit.utils.RxUtils;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * =====================================================
 * 描    述: 直播间网络请求仓库
 * =====================================================
 */
public class LiveRepository {
    private static LiveRepository instance = new LiveRepository();

    private LiveRepository() {
    }

    public static LiveRepository getInstance() {
        if (instance == null) {
            instance = new LiveRepository();
        }
        return instance;
    }

    private ApiServer userApi = RetrofitClient.getInstance().create(ApiServer.class);

    public void startLive(MutableLiveData<Boolean> startSuccess, HashMap<String, Object> map) {
        String json = JSON.toJSONString(map);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        userApi.toStartLive(UserInfoMgr.getInstance().getHttpToken(), body)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                    }
                })
                .subscribe(new DisposableObserver<MBaseResponse<LiveInitInfo>>() {
                    @Override
                    public void onNext(MBaseResponse<LiveInitInfo> response) {
                        if (response.isOk()) {
                            startSuccess.postValue(true);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void joinLive(MutableLiveData<LiveInitInfo> liveInfo, String liveRoomId) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("liveRoomId", liveRoomId);
        String json = JSON.toJSONString(hashMap);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        userApi.toJoinLive(UserInfoMgr.getInstance().getHttpToken(), body)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                    }
                })
                .subscribe(new DisposableObserver<MBaseResponse<LiveInitInfo>>() {
                    @Override
                    public void onNext(MBaseResponse<LiveInitInfo> response) {
                        if (response.isOk()) {
                            liveInfo.postValue(response.getData());
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
