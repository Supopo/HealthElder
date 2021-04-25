package com.xaqinren.healthyelders.apiserver;

import android.content.Intent;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.alibaba.fastjson.JSON;
import com.xaqinren.healthyelders.MainActivity;
import com.xaqinren.healthyelders.bean.UserInfo;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.http.RetrofitClient;
import com.xaqinren.healthyelders.moduleHome.bean.GirlsBean;
import com.xaqinren.healthyelders.moduleHome.viewModel.GirlsViewModel;
import com.xaqinren.healthyelders.moduleLogin.activity.PhoneLoginActivity;
import com.xaqinren.healthyelders.moduleLogin.bean.LoginTokenBean;

import java.util.HashMap;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import me.goldze.mvvmhabit.http.BaseResponse;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.goldze.mvvmhabit.utils.SPUtils;
import me.goldze.mvvmhabit.utils.StringUtils;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * =====================================================
 * 描    述: 网络请求仓库
 * =====================================================
 */
public class UserRepository {
    private static UserRepository instance = new UserRepository();

    private UserRepository() {
    }

    public static UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

    private ApiServer userApi = RetrofitClient.getInstance().create(ApiServer.class);


    public MutableLiveData<List<GirlsBean>> getGirls(int page) {
        MutableLiveData<List<GirlsBean>> girlsList = new MutableLiveData<>();
        Log.e("--", "1");
        userApi.getGirls(page, 10)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                    }
                })
                .subscribe(new DisposableObserver<BaseResponse<List<GirlsBean>>>() {
                    @Override
                    public void onNext(BaseResponse<List<GirlsBean>> response) {
                        if (response.getData() != null) {
                            girlsList.setValue(response.getData());
                            Log.e("--", "2");
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

        Log.e("--", "3");
        return girlsList;
    }

    public MutableLiveData<UserInfo> getUserInfo(String token) {
        MutableLiveData<UserInfo> userInfo = new MutableLiveData<>();
        userApi.getUserInfo(token)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                    }
                })
                .subscribe(new DisposableObserver<MBaseResponse<UserInfo>>() {
                    @Override
                    public void onNext(MBaseResponse<UserInfo> response) {
                        if (response.isOk()) {
                            userInfo.setValue(response.getData());
                            UserInfoMgr.getInstance().setUserInfo(response.getData());
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        return userInfo;
    }

    public MutableLiveData<Boolean> loginByPhone(String phone, String code, String openId) {
        MutableLiveData<Boolean> loginSuccess = new MutableLiveData<>();

        HashMap<String, String> map = new HashMap<>();
        map.put("mobileNumber", phone);
        map.put("password", code);
        map.put("clientId", Constant.mid);
        map.put("requestSource", "ANDROID_APP");
        if (!StringUtils.isEmpty(openId)) {
            map.put("openId", openId);
        }
        map.put("recommendCode", "");
        String json = JSON.toJSONString(map);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        RetrofitClient.getInstance().create(ApiServer.class)
                .phoneLogin(Constant.auth, Constant.mid, body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                    }
                })
                .subscribe(new CustomObserver<MBaseResponse<LoginTokenBean>>() {
                    @Override
                    protected void dismissDialog() {
                    }

                    @Override
                    public void onSuccess(MBaseResponse<LoginTokenBean> data) {
                        if (data.isOk()) {
                            SPUtils.getInstance().put(Constant.SP_KEY_TOKEN_INFO, JSON.toJSONString(data.getData()));
                            loginSuccess.setValue(true);
                        }
                    }
                });
        return loginSuccess;
    }


}
