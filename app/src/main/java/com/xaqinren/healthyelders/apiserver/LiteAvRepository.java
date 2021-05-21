package com.xaqinren.healthyelders.apiserver;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.alibaba.fastjson.JSON;
import com.xaqinren.healthyelders.bean.BaseListRes;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.http.RetrofitClient;
import com.xaqinren.healthyelders.moduleLiteav.bean.LiteAvUserBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.MMusicBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.MMusicItemBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.MusicClassBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.PublishAtMyBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.PublishBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.SaveDraftBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.TopicBean;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveInitInfo;
import com.xaqinren.healthyelders.utils.ACache;
import com.xaqinren.healthyelders.utils.LogUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.goldze.mvvmhabit.utils.StringUtils;
import me.goldze.mvvmhabit.utils.compression.Luban;
import okhttp3.FormBody;
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


    public void updatePhoto(MutableLiveData<Boolean> dismissDialog, MutableLiveData<String> fileUrl, String filePath) {
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        File file = new File(filePath);
        builder.addFormDataPart("files", file.getName(),RequestBody.create(MediaType.parse("image/jpeg"), file));
        RetrofitClient.getInstance().create(ApiServer.class).uploadMultiFile(
                Constant.lanUrl + "content/filesUpload",
                UserInfoMgr.getInstance().getHttpToken(), builder.build())
                .compose(RxUtils.schedulersTransformer()) //线程调度
                .doOnSubscribe(disposable -> {

                })
                .subscribe(new CustomObserver<MBaseResponse<List<String>>>() {

                    @Override
                    protected void dismissDialog() {
                        dismissDialog.postValue(true);
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<List<String>> data) {
                        fileUrl.postValue(data.getData().get(0));
                    }
                });
    }

    /**
     * 上传图片
     * @param dismissDialog
     * @param fileUrl
     * @param filePath
     */
    public void updatePhoto(MutableLiveData<Boolean> dismissDialog, MutableLiveData<List<String>> fileUrl, List<String> filePath) {
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        for (String s : filePath) {
            File file = new File(s);
            builder.addFormDataPart("files", file.getName(),RequestBody.create(MediaType.parse("image/jpeg"), file));
        }
        RetrofitClient.getInstance().create(ApiServer.class).uploadMultiFile(
                Constant.lanUrl + "content/filesUpload",
                UserInfoMgr.getInstance().getHttpToken(), builder.build())
                .compose(RxUtils.schedulersTransformer()) //线程调度
                .doOnSubscribe(disposable -> {

                })
                .subscribe(new CustomObserver<MBaseResponse<List<String>>>() {

                    @Override
                    protected void dismissDialog() {
                        dismissDialog.postValue(true);
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<List<String>> data) {
                        fileUrl.postValue(data.getData());
                    }
                });
    }

    /**
     * 发布图文
     */
    public void publishTextPhoto(MutableLiveData<Boolean> dismissDialog, MutableLiveData<String> publish, com.xaqinren.healthyelders.modulePicture.bean.PublishBean publishBean) {
        String json = JSON.toJSONString(publishBean);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        userApi.postPublishTextPhoto(UserInfoMgr.getInstance().getHttpToken(), body)
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
                        publish.postValue("发布成功");
                    }
                });
    }

    /**
     * 音乐分类
     */
    public void getMusicClass(MutableLiveData<Boolean> dismissDialog, MutableLiveData<List<MusicClassBean>> publish) {
        userApi.getMusicClass(UserInfoMgr.getInstance().getHttpToken())
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribe(new CustomObserver<MBaseResponse<BaseListRes<List<MusicClassBean>>>>() {

                    @Override
                    protected void dismissDialog() {
                        dismissDialog.postValue(true);
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<BaseListRes<List<MusicClassBean>>> data) {
                        publish.postValue(data.getData().content);
                    }
                });
    }

    /**
     * 音乐首页
     */
    public void getMusicChannelSheet(MutableLiveData<Boolean> dismissDialog, MutableLiveData<List<MMusicBean>> publish) {
        userApi.getChannelSheet(UserInfoMgr.getInstance().getHttpToken())
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribe(new CustomObserver<MBaseResponse<BaseListRes<List<MMusicBean>>>>() {

                    @Override
                    protected void dismissDialog() {
                        dismissDialog.postValue(true);
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<BaseListRes<List<MMusicBean>>> data) {
                        publish.postValue(data.getData().content);
                    }
                });
    }

    /**
     * 音乐搜索
     * @param id
     * @param name
     * @param page
     * @param pagesize
     */
    public void getMusicList(String id, String name, int page, int pagesize ,MutableLiveData<Boolean> dismissDialog, MutableLiveData<List<MMusicItemBean>> publish) {
        userApi.getMusicList(UserInfoMgr.getInstance().getHttpToken(),page, pagesize, name, id)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribe(new CustomObserver<MBaseResponse<BaseListRes<List<MMusicItemBean>>>>() {

                    @Override
                    protected void dismissDialog() {
                        dismissDialog.postValue(true);
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<BaseListRes<List<MMusicItemBean>>> data) {
                        publish.postValue(data.getData().content);
                    }
                });
    }

    /**
     * 推荐音乐
     */
    public void getMusicReComment(MutableLiveData<List<MMusicItemBean>> commentList) {
        userApi.getMusicRecommend(UserInfoMgr.getInstance().getHttpToken())
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .onErrorResumeNext(new Observable() {
                    @Override
                    protected void subscribeActual(Observer observer) {
                        LogUtils.e("getMusicReComment", "");
                    }
                })
                .subscribe(new CustomObserver<MBaseResponse<List<MMusicItemBean>>>() {

                    @Override
                    protected void dismissDialog() {
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<List<MMusicItemBean>> data) {
                        commentList.postValue(data.getData());
                    }
                });
    }

    /**
     * 获取收藏音乐
     */
    public void getMusicColl(MutableLiveData<List<MMusicItemBean>> collList, int page, int pageSize) {
        userApi.getMusicFavorite(UserInfoMgr.getInstance().getHttpToken(), page, pageSize)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribe(new CustomObserver<MBaseResponse<BaseListRes<List<MMusicItemBean>>>>() {

                    @Override
                    protected void dismissDialog() {
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<BaseListRes<List<MMusicItemBean>>> data) {
                        collList.postValue(data.getData().content);
                    }
                });
    }
    /**
     * 获取收藏音乐
     */
    public void musicColl(MutableLiveData<Boolean> collList, String objectId , boolean favoriteStatus) {
        HashMap hashMap = new HashMap();
        hashMap.put("objectId", objectId);
        hashMap.put("favoriteStatus", favoriteStatus);
        String json = JSON.toJSONString(hashMap);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        userApi.musicFavorite(UserInfoMgr.getInstance().getHttpToken(), body)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribe(new CustomObserver<MBaseResponse<MBaseResponse<Object>>>() {

                    @Override
                    protected void dismissDialog() {
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<MBaseResponse<Object>> data) {
                        collList.postValue(true);
                    }
                });
    }
}
