package com.xaqinren.healthyelders.apiserver;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.http.RetrofitClient;
import com.xaqinren.healthyelders.moduleHome.bean.GirlsBean;
import com.xaqinren.healthyelders.moduleHome.viewModel.GirlsViewModel;

import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import me.goldze.mvvmhabit.http.BaseResponse;
import me.goldze.mvvmhabit.utils.RxUtils;

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
}
