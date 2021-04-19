package com.xaqinren.healthyelders.apiserver;

import com.xaqinren.healthyelders.http.RetrofitClient;
import com.xaqinren.healthyelders.moduleHome.bean.GirlsBean;
import com.xaqinren.healthyelders.moduleHome.viewModel.GirlsViewModel;

import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.http.BaseResponse;
import me.goldze.mvvmhabit.utils.RxUtils;

/**
 * =====================================================
 * 描    述: 网络请求仓库
 * =====================================================
 */
public class UserRepository {
    private static final UserRepository instance = new UserRepository();

    private UserRepository() {
    }

    public static UserRepository getInstance() {
        return instance;
    }

    private ApiServer userApi = RetrofitClient.getInstance().create(ApiServer.class);


    public void getGirls(GirlsViewModel viewModel, int page, int count) {
//                viewModel.showDialog();
//                RetrofitClient.execute(userApi.getGirls(page, count), new CustomObserver<BaseResponse<List<GirlsBean>>>() {
//
//                    @Override
//                    protected void dismissDialog() {
//                        viewModel.dismissDialog();
//                    }
//
//                    @Override
//                    public void onSuccess(BaseResponse<List<GirlsBean>> data) {
//                        viewModel.dataList.postValue(data.getData());
//                    }
//
//                });

        userApi.getGirls(page, count)
                .compose(RxUtils.bindToLifecycle(viewModel.getLifecycleProvider())) // 请求与View周期同步
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        viewModel.showDialog("正在捕获妹子");
                    }
                })
                .subscribe(new CustomObserver<BaseResponse<List<GirlsBean>>>() {

                    @Override
                    protected void dismissDialog() {
                        viewModel.dismissDialog();
                    }

                    @Override
                    public void onSuccess(BaseResponse<List<GirlsBean>> data) {
                        viewModel.dataList.postValue(data.getData());
                    }

                });
    }

}
