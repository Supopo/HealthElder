package com.xaqinren.healthyelders.apiserver;

import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.http.RetrofitClient;
import com.xaqinren.healthyelders.moduleHome.bean.HomeMenuRes;
import com.xaqinren.healthyelders.moduleMall.bean.MallMenuRes;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.utils.RxUtils;

/**
 * =====================================================
 * 描    述: 商城网络请求仓库
 * =====================================================
 */
public class MallRepository {
    private static MallRepository instance = new MallRepository();

    private MallRepository() {
    }

    public static MallRepository getInstance() {
        if (instance == null) {
            instance = new MallRepository();
        }
        return instance;
    }

    private ApiServer userApi = RetrofitClient.getInstance().create(ApiServer.class);

    public void getMallMenuInfo(MutableLiveData<MallMenuRes> mallMenuRes) {
        userApi.getMallMenu()
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                    }
                })
                .subscribe(new CustomObserver<MBaseResponse<MallMenuRes>>() {
                    @Override
                    protected void dismissDialog() {

                    }

                    @Override
                    protected void onSuccess(MBaseResponse<MallMenuRes> data) {
                        mallMenuRes.postValue(data.getData());
                    }

                });
    }

}
