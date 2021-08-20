package com.xaqinren.healthyelders.apiserver;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.alibaba.fastjson.JSON;
import com.xaqinren.healthyelders.bean.BaseListRes;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.http.RetrofitClient;
import com.xaqinren.healthyelders.moduleHome.bean.CommentListBean;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;
import com.xaqinren.healthyelders.moduleLiteav.bean.LiteAvUserBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.MMusicBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.MMusicItemBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.MusicClassBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.PublishAtMyBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.PublishBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.SaveDraftBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.TopicBean;
import com.xaqinren.healthyelders.modulePicture.bean.DiaryInfoBean;
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

/**
 * =====================================================
 * 描    述: 短视频网络请求仓库
 * =====================================================
 */
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

    public void getMyAtList(MutableLiveData<Boolean> dismissDialog, MutableLiveData<List<LiteAvUserBean>> startLiveInfo, int page, int pageSize) {
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


    public void publishVideo(MutableLiveData<Boolean> dismissDialog, MutableLiveData<Boolean> listMutableLiveData, PublishBean bean) {
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

    public void getUserList(MutableLiveData<Boolean> dismissDialog, MutableLiveData<List<LiteAvUserBean>> listMutableLiveData, String targetId, int page, int pageSize, String identity) {
        userApi.getUserFriend(UserInfoMgr.getInstance().getHttpToken(), page, pageSize, identity, targetId)
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

    public void getUserList(MutableLiveData<Boolean> dismissDialog, MutableLiveData<List<LiteAvUserBean>> listMutableLiveData, int page, int pageSize, String identity) {
        userApi.getUserFriend(UserInfoMgr.getInstance().getHttpToken(), page, pageSize, identity, null)
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

    public void getUserList(MutableLiveData<Boolean> dismissDialog, int page, int pageSize, String identity, MutableLiveData<BaseListRes<List<LiteAvUserBean>>> listMutableLiveData) {
        userApi.getUserFriend(UserInfoMgr.getInstance().getHttpToken(), page, pageSize, identity, null)
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
                        listMutableLiveData.postValue(data.getData());
                    }
                });
    }


    public List<SaveDraftBean> getDraftsList(Context context, String fileName) {
        String json = ACache.get(context).getAsString(fileName);
        List<SaveDraftBean> saveDraftBeans;
        if (!StringUtils.isEmpty(json)) {
            saveDraftBeans = JSON.parseArray(json, SaveDraftBean.class);
        } else
            saveDraftBeans = new ArrayList<>();
        if (saveDraftBeans == null)
            saveDraftBeans = new ArrayList<>();
        return saveDraftBeans;
    }

    public SaveDraftBean getDraftsById(Context context, String fileName, long id) {
        List<SaveDraftBean> saveDraftBeans = getDraftsList(context, fileName);
        for (SaveDraftBean bean : saveDraftBeans) {
            if (bean.getId() == id) {
                return bean;
            }
        }
        return null;
    }

    public void saveDraftsById(Context context, String fileName, SaveDraftBean saveDraftBean) {
        List<SaveDraftBean> saveDraftBeans = getDraftsList(context, fileName);
        int index = -1;
        for (int i = 0; i < saveDraftBeans.size(); i++) {
            SaveDraftBean bean = saveDraftBeans.get(i);
            if (bean.getId() == saveDraftBean.getId()) {
                index = i;
                break;
            }
        }
        if (index == -1)
            saveDraftBeans.add(0, saveDraftBean);
        else
            saveDraftBeans.set(index, saveDraftBean);
        ACache.get(context).put(fileName, JSON.toJSONString(saveDraftBeans));
    }

    public void delDraftsById(Context context, String fileName, long id) {
        List<SaveDraftBean> saveDraftBeans = getDraftsList(context, fileName);
        int index = -1;
        for (int i = 0; i < saveDraftBeans.size(); i++) {
            SaveDraftBean bean = saveDraftBeans.get(i);
            if (bean.getId() == id) {
                index = i;
                break;
            }
        }
        if (index == -1)
            return;
        saveDraftBeans.remove(index);
        ACache.get(context).put(fileName, JSON.toJSONString(saveDraftBeans));
    }

    public void getSearchUserList(MutableLiveData<Boolean> requestSuccess, int page, int pageSize, String key, MutableLiveData<BaseListRes<List<LiteAvUserBean>>> searchUserList) {
        userApi.getSearchUserFriend(UserInfoMgr.getInstance().getHttpToken(), page, pageSize, key)
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
                        searchUserList.postValue(data.getData());
                    }
                });
    }

    public void getSearchUserList(MutableLiveData<Boolean> requestSuccess, MutableLiveData<List<LiteAvUserBean>> searchUserList, int page, int pageSize, String key) {
        userApi.getSearchUserFriend(UserInfoMgr.getInstance().getHttpToken(), page, pageSize, key)
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
        LogUtils.e("updatePhoto", "file size  -> " + file.length());
        builder.addFormDataPart("files", file.getName(), RequestBody.create(MediaType.parse("image/jpeg"), file));
        RetrofitClient.getInstance().create(ApiServer.class).uploadMultiFile(

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
     * 上传图片 多图
     *
     * @param dismissDialog
     * @param fileUrl
     * @param filePath
     */
    public void updatePhoto(MutableLiveData<Boolean> dismissDialog, MutableLiveData<List<String>> fileUrl, List<String> filePath) {
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        for (String s : filePath) {
            File file = new File(s);
            builder.addFormDataPart("files", file.getName(), RequestBody.create(MediaType.parse("image/jpeg"), file));
        }
        RetrofitClient.getInstance().create(ApiServer.class).uploadMultiFile(
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
                        if (data.getData() != null)
                            publish.postValue("发布成功");
                        else
                            publish.postValue(data.getMessage());
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
                .subscribe(new CustomObserver<MBaseResponse<List<MMusicBean>>>() {

                    @Override
                    protected void dismissDialog() {
                        dismissDialog.postValue(true);
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<List<MMusicBean>> data) {
                        publish.postValue(data.getData());
                    }
                });
    }

    /**
     * 音乐搜索
     *
     * @param id
     * @param name
     * @param page
     * @param pagesize
     */
    public void getMusicList(String id, String name, int page, int pagesize, MutableLiveData<Boolean> dismissDialog, MutableLiveData<List<MMusicItemBean>> publish) {
        userApi.getMusicList(UserInfoMgr.getInstance().getHttpToken(), page, pagesize, name, id)
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
    public void musicColl(MutableLiveData<Boolean> collList, String objectId, boolean favoriteStatus) {
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

    public void toComment(String id, String content, MutableLiveData<CommentListBean> commentSuccess) {

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", id);
        hashMap.put("content", content);
        String json = JSON.toJSONString(hashMap);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        userApi.toDiaryComment(UserInfoMgr.getInstance().getHttpToken(), body)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                    }
                })
                .subscribe(new CustomObserver<MBaseResponse<CommentListBean>>() {
                    @Override
                    protected void dismissDialog() {

                    }

                    @Override
                    protected void onSuccess(MBaseResponse<CommentListBean> data) {
                        commentSuccess.postValue(data.getData());
                    }

                });
    }

    public void getCommentList(MutableLiveData<List<CommentListBean>> commentList, int page, String id) {

        userApi.getDiaryCommentList(page, 10, id)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                    }
                })
                .subscribe(new CustomObserver<MBaseResponse<BaseListRes<List<CommentListBean>>>>() {
                    @Override
                    protected void dismissDialog() {

                    }

                    @Override
                    protected void onSuccess(MBaseResponse<BaseListRes<List<CommentListBean>>> data) {
                        commentList.postValue(data.getData().content);
                    }

                });
    }

    public void toCommentReply(CommentListBean mCommentListBean, String content, int type, MutableLiveData<CommentListBean> commentSuccess) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("replyType", type == 0 ? "REPLY_COMMENT" : "REPLY_REPLY");
        hashMap.put("id", mCommentListBean.id);
        hashMap.put("content", content);
        String json = JSON.toJSONString(hashMap);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        userApi.toDiaryCommentReply(UserInfoMgr.getInstance().getHttpToken(), body)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                    }
                })
                .subscribe(new CustomObserver<MBaseResponse<CommentListBean>>() {
                    @Override
                    protected void dismissDialog() {

                    }

                    @Override
                    protected void onSuccess(MBaseResponse<CommentListBean> data) {
                        CommentListBean commentListBean = data.getData();
                        commentListBean.parentPos = mCommentListBean.parentPos;
                        commentSuccess.postValue(commentListBean);
                    }

                });
    }

    public void getCommentReplyList(CommentListBean commentListBean, MutableLiveData<CommentListBean> commentList) {
        userApi.getDiaryCommentReplyList(commentListBean.itemPage, commentListBean.itemSize, commentListBean.id)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                    }
                })
                .subscribe(new CustomObserver<MBaseResponse<BaseListRes<List<CommentListBean>>>>() {
                    @Override
                    protected void dismissDialog() {

                    }

                    @Override
                    protected void onSuccess(MBaseResponse<BaseListRes<List<CommentListBean>>> data) {
                        //不要引用传进来的commentListBean 会出错
                        CommentListBean resBean = new CommentListBean();
                        resBean.parentPos = commentListBean.parentPos;
                        resBean.replyList = data.getData().content;
                        commentList.postValue(resBean);
                    }

                });
    }

    public void toLikeComment(String shortVideoId, String commentId, boolean favoriteStatus, boolean notRoot) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("objectId", shortVideoId);
        hashMap.put("id", commentId);
        hashMap.put("favoriteStatus", favoriteStatus);
        hashMap.put("notRoot", notRoot);
        String json = JSON.toJSONString(hashMap);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        userApi.setDiaryCommentLike(UserInfoMgr.getInstance().getHttpToken(), body)
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

    //日记详情
    public void getDiaryInfo(MutableLiveData<Boolean> dismissDialog, MutableLiveData<DiaryInfoBean> diray, String id) {
        userApi.getDiaryInfo(UserInfoMgr.getInstance().getHttpToken(), id)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .doOnSubscribe(disposable -> {
                })
                .subscribe(new CustomObserver<MBaseResponse<DiaryInfoBean>>() {
                    @Override
                    protected void dismissDialog() {
                        dismissDialog.postValue(true);
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<DiaryInfoBean> data) {
                        diray.postValue(data.getData());
                    }

                    @Override
                    public void onNext(MBaseResponse<DiaryInfoBean> response) {
                        super.onNext(response);
                        if (!response.isOk()) {
                            diray.postValue(null);
                        }
                    }
                });
    }

    public void toFollow(MutableLiveData<Boolean> followSuccess, MutableLiveData<Boolean> dismissDialog, String userId) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userId", userId);
        hashMap.put("attentionSource", "USER_DIARY");
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

    public void toFavorite(MutableLiveData<Boolean> followSuccess, MutableLiveData<Boolean> dismissDialog, String id, boolean isF) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("objectId", id);
        hashMap.put("favoriteStatus", isF);
        String json = JSON.toJSONString(hashMap);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        userApi.setDiaryFavorite(UserInfoMgr.getInstance().getHttpToken(), body)
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


    public void videoDetail(MutableLiveData<Boolean> dismissDialog, MutableLiveData<VideoInfo> videoInfo, String id) {
        userApi.getVideoInfo(UserInfoMgr.getInstance().getHttpToken(), id)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                    }
                })
                .subscribe(new CustomObserver<MBaseResponse<VideoInfo>>() {
                    @Override
                    protected void dismissDialog() {
                        if (dismissDialog != null) {
                            dismissDialog.postValue(true);
                        }
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<VideoInfo> data) {
                        videoInfo.postValue(data.getData());
                    }
                });
    }


    public void delDiary(MutableLiveData<Boolean> requestSuccess, MutableLiveData<Boolean> delSuccess, String id) {
        userApi.delDiary(UserInfoMgr.getInstance().getHttpToken(), id)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribe(new CustomObserver<MBaseResponse<Object>>() {
                    @Override
                    protected void dismissDialog() {
                        requestSuccess.postValue(true);
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<Object> data) {
                        delSuccess.postValue(data.isOk());
                    }
                });
    }
}
