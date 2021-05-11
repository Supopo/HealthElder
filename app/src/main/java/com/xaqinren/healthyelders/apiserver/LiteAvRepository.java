package com.xaqinren.healthyelders.apiserver;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.alibaba.fastjson.JSON;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.http.RetrofitClient;
import com.xaqinren.healthyelders.moduleLiteav.bean.LiteAvUserBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.PublishAtMyBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.SaveDraftBean;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveInitInfo;
import com.xaqinren.healthyelders.utils.ACache;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.goldze.mvvmhabit.utils.StringUtils;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class LiteAvRepository {
    private static LiteAvRepository instance = new LiteAvRepository();

    private LiteAvRepository() {
    }
    public static LiteAvRepository getInstance() {
        if (instance == null) {
            instance = new LiteAvRepository();
        }
        return instance;
    }

    private ApiServer userApi = RetrofitClient.getInstance().create(ApiServer.class);

    public void getMyAtList(MutableLiveData<Boolean> dismissDialog, MutableLiveData<List<LiteAvUserBean>> startLiveInfo,int page ,int pageSize) {
        userApi.getLiteAvPublishMyAtList(UserInfoMgr.getInstance().getHttpToken(), page, pageSize)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                    }
                })
                .subscribe(new CustomObserver<MBaseResponse<PublishAtMyBean>>() {

                    @Override
                    protected void dismissDialog() {
                        dismissDialog.postValue(true);
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<PublishAtMyBean> data) {
                        startLiveInfo.postValue(data.getData().getContent());
                    }
                });
    }

    public List<SaveDraftBean>  getDraftsList(Context context , String fileName) {
        String json = ACache.get(context).getAsString(fileName);
        List<SaveDraftBean> saveDraftBeans;
        if (!StringUtils.isEmpty(json)) {
            saveDraftBeans = JSON.parseArray(json, SaveDraftBean.class);
        } else saveDraftBeans = new ArrayList<>();
        if (saveDraftBeans == null) saveDraftBeans = new ArrayList<>();
        return saveDraftBeans;
    }

    public SaveDraftBean getDraftsById(Context context , String fileName , long id) {
        List<SaveDraftBean> saveDraftBeans =  getDraftsList(context, fileName);
        for (SaveDraftBean bean : saveDraftBeans) {
            if (bean.getId() == id) {
                return bean;
            }
        }
        return null;
    }
    public void saveDraftsById(Context context , String fileName ,  SaveDraftBean saveDraftBean) {
        List<SaveDraftBean> saveDraftBeans =  getDraftsList(context, fileName);
        int index = -1;
        for (int i = 0; i < saveDraftBeans.size(); i++) {
            SaveDraftBean bean =  saveDraftBeans.get(i);
            if (bean.getId() == saveDraftBean.getId()) {
                index = i;
                break;
            }
        }
        if (index == -1)
            saveDraftBeans.add(0 ,saveDraftBean );
        else saveDraftBeans.set(index ,saveDraftBean );
        ACache.get(context).put(fileName , JSON.toJSONString(saveDraftBeans));
    }
}
