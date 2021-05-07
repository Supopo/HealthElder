package com.xaqinren.healthyelders.apiserver;

import androidx.lifecycle.MutableLiveData;

import com.alibaba.fastjson.JSON;
import com.tencent.qcloud.tim.uikit.utils.ToastUtil;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.http.RetrofitClient;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveHeaderInfo;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveInitInfo;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveOverInfo;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ZBSettingBean;

import java.util.HashMap;

import io.reactivex.annotations.NonNull;
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

    public void startLive(MutableLiveData<Boolean> dismissDialog, MutableLiveData<LiveInitInfo> startLiveInfo, LiveInitInfo map) {
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
                .subscribe(new CustomObserver<MBaseResponse<LiveInitInfo>>() {

                    @Override
                    protected void dismissDialog() {
                        dismissDialog.postValue(true);
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<LiveInitInfo> data) {
                        startLiveInfo.postValue(data.getData());
                    }
                });
    }

    public void reStartLive(MutableLiveData<Boolean> dismissDialog, MutableLiveData<LiveInitInfo> startLiveInfo, String liveRoomRecordId) {
        userApi.reStartLive(UserInfoMgr.getInstance().getHttpToken(), liveRoomRecordId)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                    }
                })
                .subscribe(new CustomObserver<MBaseResponse<LiveInitInfo>>() {

                    @Override
                    protected void dismissDialog() {
                        dismissDialog.postValue(true);
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<LiveInitInfo> data) {
                        startLiveInfo.postValue(data.getData());
                    }
                });
    }

    public void joinLive(MutableLiveData<Boolean> dismissDialog, MutableLiveData<LiveInitInfo> liveInfo, String liveRoomRecordId) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("liveRoomId", liveRoomRecordId);
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
                .subscribe(new CustomObserver<MBaseResponse<LiveInitInfo>>() {
                    @Override
                    protected void dismissDialog() {
                        dismissDialog.postValue(true);
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<LiveInitInfo> data) {
                        liveInfo.postValue(data.getData());

                    }
                });
    }

    public void checkLiveInfo(MutableLiveData<Boolean> dismissDialog, MutableLiveData<LiveInitInfo> liveInfo) {
        userApi.checkLiveInfo(UserInfoMgr.getInstance().getHttpToken())
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                    }
                })
                .subscribe(new CustomObserver<MBaseResponse<LiveInitInfo>>() {
                    @Override
                    protected void dismissDialog() {
                        dismissDialog.postValue(true);
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<LiveInitInfo> data) {
                        liveInfo.postValue(data.getData());
                    }


                });
    }

    public void overLive(MutableLiveData<Boolean> dismissDialog, MutableLiveData<Boolean> exitSuccess, String liveRoomRecordId, String commentPeopleNum) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("liveRoomRecordId", liveRoomRecordId);
        hashMap.put("commentPeopleNum", commentPeopleNum);
        String json = JSON.toJSONString(hashMap);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        userApi.toOverLive(UserInfoMgr.getInstance().getHttpToken(), body)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                    }
                })
                .subscribe(new CustomObserver<MBaseResponse<LiveInitInfo>>() {
                    @Override
                    protected void dismissDialog() {
                        dismissDialog.postValue(true);
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<LiveInitInfo> data) {
                        ToastUtil.toastShortMessage("结束成功");
                        exitSuccess.postValue(true);
                    }

                });
    }

    public void rushLiveInfo(MutableLiveData<Boolean> dismissDialog, MutableLiveData<LiveHeaderInfo> liveHeaderInfo, String liveRoomRecordId) {
        userApi.refreshLiveRoomInfo(UserInfoMgr.getInstance().getHttpToken(), liveRoomRecordId)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                    }
                })
                .subscribe(new CustomObserver<MBaseResponse<LiveHeaderInfo>>() {
                    @Override
                    protected void dismissDialog() {
                        dismissDialog.postValue(true);
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<LiveHeaderInfo> data) {
                        liveHeaderInfo.postValue(data.getData());
                    }

                });
    }

    public void toZanLive(MutableLiveData<Boolean> dismissDialog, MutableLiveData<Boolean> zanSuccess, String liveRoomRecordId, String count) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("liveRoomRecordId", liveRoomRecordId);
        hashMap.put("count", count);
        String json = JSON.toJSONString(hashMap);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        userApi.toZanLive(UserInfoMgr.getInstance().getHttpToken(), body)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                    }
                })
                .subscribe(new CustomObserver<MBaseResponse<Object>>() {
                    @Override
                    protected void dismissDialog() {
                        dismissDialog.postValue(true);
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<Object> data) {
                        zanSuccess.postValue(true);
                    }

                });
    }

    public void leaveLive(MutableLiveData<Boolean> dismissDialog, MutableLiveData<Boolean> exitSuccess, String liveRoomRecordId) {
        userApi.leaveLive(UserInfoMgr.getInstance().getHttpToken(), liveRoomRecordId)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                    }
                })
                .subscribe(new CustomObserver<MBaseResponse<LiveInitInfo>>() {
                    @Override
                    protected void dismissDialog() {
                        dismissDialog.postValue(true);
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<LiveInitInfo> data) {
                        exitSuccess.postValue(true);
                    }

                });
    }

    public void liveOverInfo(MutableLiveData<Boolean> dismissDialog, MutableLiveData<LiveOverInfo> liveOverInfo, String liveRoomRecordId) {
        userApi.liveOverInfo(UserInfoMgr.getInstance().getHttpToken(), liveRoomRecordId)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                    }
                })
                .subscribe(new CustomObserver<MBaseResponse<LiveOverInfo>>() {
                    @Override
                    protected void dismissDialog() {
                        dismissDialog.postValue(true);
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<LiveOverInfo> data) {
                        liveOverInfo.postValue(data.getData());
                    }

                });
    }

    public void addFansCount(String liveRoomRecordId) {
        userApi.addFansCount(UserInfoMgr.getInstance().getHttpToken(), liveRoomRecordId)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                    }
                })
                .subscribe(new CustomObserver<MBaseResponse<Object>>() {
                    @Override
                    protected void dismissDialog() {
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<Object> data) {
                    }

                });
    }

    public void setZBStatus(MutableLiveData<Boolean> dismissDialog, MutableLiveData<Boolean> setSuccess, ZBSettingBean zbSettingBean) {
        String json = JSON.toJSONString(zbSettingBean);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        userApi.setZBStatus(UserInfoMgr.getInstance().getHttpToken(), body)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                    }
                })
                .subscribe(new CustomObserver<MBaseResponse<Object>>() {
                    @Override
                    protected void dismissDialog() {
                        dismissDialog.postValue(true);
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<Object> data) {
                        setSuccess.postValue(true);
                    }

                });
    }


}
