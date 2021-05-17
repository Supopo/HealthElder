package com.xaqinren.healthyelders.apiserver;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.alibaba.fastjson.JSON;
import com.xaqinren.healthyelders.bean.BaseListRes;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.http.RetrofitClient;
import com.xaqinren.healthyelders.moduleLiteav.bean.LiteAvUserBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.PublishAtMyBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.PublishBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.SaveDraftBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.TopicBean;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveInitInfo;
import com.xaqinren.healthyelders.utils.ACache;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.goldze.mvvmhabit.utils.StringUtils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
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

    public void getHotTopicList(MutableLiveData<Boolean> dismissDialog, MutableLiveData<List<TopicBean>> listMutableLiveData) {
        userApi.getHotTopicList(UserInfoMgr.getInstance().getHttpToken())
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                    }
                })
                .subscribe(new CustomObserver<MBaseResponse<List<TopicBean>>>() {

                    @Override
                    protected void dismissDialog() {
                        dismissDialog.postValue(true);
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<List<TopicBean>> data) {
                        listMutableLiveData.postValue(data.getData());
                    }
                });
    }


    public void publishVideo(MutableLiveData<Boolean> dismissDialog, MutableLiveData<Boolean> listMutableLiveData , PublishBean bean) {
        String json = JSON.toJSONString(bean);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        userApi.postPublishLiteAv(UserInfoMgr.getInstance().getHttpToken(), body)
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
                        listMutableLiveData.postValue(data.isOk());
                    }
                });
    }

    public void getUserList(MutableLiveData<Boolean> dismissDialog, MutableLiveData<List<LiteAvUserBean>> listMutableLiveData, int page, int pageSize, String identity) {
        userApi.getUserFriend(UserInfoMgr.getInstance().getHttpToken(), page, pageSize, identity)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                    }
                })
                .subscribe(new CustomObserver<MBaseResponse<BaseListRes<List<LiteAvUserBean>>>>() {

                    @Override
                    protected void dismissDialog() {
                        dismissDialog.postValue(true);
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<BaseListRes<List<LiteAvUserBean>>> data) {
                        listMutableLiveData.postValue(data.getData().content);
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
    public void delDraftsById(Context context , String fileName ,  long id) {
        List<SaveDraftBean> saveDraftBeans =  getDraftsList(context, fileName);
        int index = -1;
        for (int i = 0; i < saveDraftBeans.size(); i++) {
            SaveDraftBean bean =  saveDraftBeans.get(i);
            if (bean.getId() == id) {
                index = i;
                break;
            }
        }
        if (index==-1)return;
        saveDraftBeans.remove(index);
        ACache.get(context).put(fileName , JSON.toJSONString(saveDraftBeans));
    }

    public void getSearchUserList(MutableLiveData<Boolean> requestSuccess, MutableLiveData<List<LiteAvUserBean>> searchUserList, int page, int pageSize,String key) {
        userApi.getSearchUserFriend(UserInfoMgr.getInstance().getHttpToken(), page, pageSize,key)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                    }
                })
                .subscribe(new CustomObserver<MBaseResponse<BaseListRes<List<LiteAvUserBean>>>>() {

                    @Override
                    protected void dismissDialog() {
                        requestSuccess.postValue(true);
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<BaseListRes<List<LiteAvUserBean>>> data) {
                        searchUserList.postValue(data.getData().content);
                    }
                });
    }

    public void getSearchTopicList(MutableLiveData<Boolean> dismissDialog, MutableLiveData<List<TopicBean>> listMutableLiveData, String key) {
        userApi.getSearchTopicList(UserInfoMgr.getInstance().getHttpToken(), key)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                    }
                })
                .subscribe(new CustomObserver<MBaseResponse<List<TopicBean>>>() {

                    @Override
                    protected void dismissDialog() {
                        dismissDialog.postValue(true);
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<List<TopicBean>> data) {
                        listMutableLiveData.postValue(data.getData());
                    }
                });
    }

    /**
     * 上传图片
     * @param dismissDialog
     * @param fileUrl
     * @param filePath
     */
    public void updatePhoto(MutableLiveData<Boolean> dismissDialog, MutableLiveData<String> fileUrl, List<String> filePath) {

        for (int i = 0; i < filePath.size(); i++) {
            File file = new File(filePath.get(i));
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file"+i, file.getName(), RequestBody.create(MediaType.parse("image/jpeg"), file))
                    .build();
            RetrofitClient.getInstance().create(ApiServer.class).uploadFile(UserInfoMgr.getInstance().getHttpToken(), requestBody)
                    .compose(RxUtils.schedulersTransformer()) //线程调度
                    .doOnSubscribe(disposable -> {

                    })
                    .subscribe(new CustomObserver<MBaseResponse<String>>() {

                        @Override
                        protected void dismissDialog() {
                            dismissDialog.postValue(true);
                        }

                        @Override
                        protected void onSuccess(MBaseResponse<String> data) {
                            fileUrl.postValue(data.getData());
                        }
                    });
        }
    }
}
