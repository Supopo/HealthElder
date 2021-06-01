package com.xaqinren.healthyelders.apiserver;

import androidx.lifecycle.MutableLiveData;

import com.alibaba.fastjson.JSON;
import com.xaqinren.healthyelders.bean.BaseListRes;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.http.RetrofitClient;
import com.xaqinren.healthyelders.moduleLiteav.bean.PublishAtMyBean;
import com.xaqinren.healthyelders.moduleMsg.bean.ContactsBean;
import com.xaqinren.healthyelders.moduleMsg.bean.FriendBean;
import com.xaqinren.healthyelders.moduleMsg.bean.InteractiveBean;

import java.util.HashMap;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.utils.RxUtils;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * =====================================================
 * 描    述: 消息通知网络请求仓库
 * =====================================================
 */
public class MsgRepository {

    private static MsgRepository instance = new MsgRepository();

    private MsgRepository() {
    }
    public static MsgRepository getInstance() {
        if (instance == null) {
            instance = new MsgRepository();
        }
        return instance;
    }

    private ApiServer userApi = RetrofitClient.getInstance().create(ApiServer.class);

    public void getMessage(MutableLiveData<Boolean> dismissDialog, MutableLiveData<List<InteractiveBean>> liveData, int page, int pageSize, String messageGroup, String messageType) {
        userApi.getMessageData(UserInfoMgr.getInstance().getHttpToken(), page, pageSize, messageGroup, messageType)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .doOnSubscribe(disposable -> {
                })
                .subscribe(new CustomObserver<MBaseResponse<BaseListRes<List<InteractiveBean>>>>() {

                    @Override
                    protected void dismissDialog() {
                        dismissDialog.postValue(true);
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<BaseListRes<List<InteractiveBean>>> data) {
                        liveData.postValue(data.getData().content);
                    }
                });
    }

    public void postUserContact(MutableLiveData<Boolean> dismissDialog, MutableLiveData<Boolean> liveData, List<ContactsBean> contactsBeans) {
        String json = JSON.toJSONString(contactsBeans);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        userApi.postUserContact(UserInfoMgr.getInstance().getHttpToken(), body)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .doOnSubscribe(disposable -> {
                })
                .subscribe(new CustomObserver<MBaseResponse<Object>>() {

                    @Override
                    protected void dismissDialog() {
                        dismissDialog.postValue(true);
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<Object> data) {
                        liveData.postValue(data.isOk() ? true : false);
                    }
                });

    }
    public void getUserContactF(MutableLiveData<Boolean> dismissDialog, MutableLiveData<List<FriendBean>> liveData) {
        userApi.getRecommendContactF(UserInfoMgr.getInstance().getHttpToken())
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .doOnSubscribe(disposable -> {
                })
                .subscribe(new CustomObserver<MBaseResponse<List<FriendBean>>>() {

                    @Override
                    protected void dismissDialog() {
                        dismissDialog.postValue(true);
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<List<FriendBean>> data) {
                        liveData.postValue(data.getData());
                    }
                });
    }


    public void toFollow( MutableLiveData<Boolean> dismissDialog, MutableLiveData<Boolean> followSuccess,String userId) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userId", userId);
        hashMap.put("attentionSource", "PERSONAL_CENTER");
        String json = JSON.toJSONString(hashMap);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        userApi.setUserFollow(UserInfoMgr.getInstance().getHttpToken(), body)
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
                        if (dismissDialog != null) {
                            dismissDialog.postValue(true);
                        }
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<Object> data) {
                        if (followSuccess != null) {
                            followSuccess.postValue(true);
                        }
                    }
                });
    }

    public void getRecommendF( MutableLiveData<Boolean> dismissDialog, MutableLiveData<List<FriendBean>> liveData) {
        userApi.getRecommendF(UserInfoMgr.getInstance().getHttpToken())
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .doOnSubscribe(disposable -> {
                })
                .subscribe(new CustomObserver<MBaseResponse<List<FriendBean>>>() {

                    @Override
                    protected void dismissDialog() {
                        dismissDialog.postValue(true);
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<List<FriendBean>> data) {
                        liveData.postValue(data.getData());
                    }
                });
    }
}
