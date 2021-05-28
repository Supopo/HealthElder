package com.xaqinren.healthyelders.apiserver;

import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.bean.BaseListRes;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.http.RetrofitClient;
import com.xaqinren.healthyelders.moduleLiteav.bean.PublishAtMyBean;
import com.xaqinren.healthyelders.moduleMsg.bean.InteractiveBean;

import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.utils.RxUtils;

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
}
