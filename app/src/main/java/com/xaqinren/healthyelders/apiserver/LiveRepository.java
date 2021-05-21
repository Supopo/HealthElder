package com.xaqinren.healthyelders.apiserver;

import androidx.lifecycle.MutableLiveData;

import com.alibaba.fastjson.JSON;
import com.tencent.qcloud.tim.uikit.utils.ToastUtil;
import com.xaqinren.healthyelders.bean.BaseListRes;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.http.RetrofitClient;
import com.xaqinren.healthyelders.moduleHome.bean.CommentListBean;
import com.xaqinren.healthyelders.moduleHome.bean.HomeRes;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;
import com.xaqinren.healthyelders.moduleLiteav.bean.VideoCommentBean;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveHeaderInfo;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveInitInfo;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveOverInfo;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ZBSettingBean;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ZBUserListBean;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.roomutil.commondef.AnchorInfo;

import java.util.HashMap;
import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import me.goldze.mvvmhabit.utils.RxUtils;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * =====================================================
 * 描    述: 直播间网络请求仓库
 * =====================================================
 */
public class LiveRepository {
    private static LiveRepository instance = new LiveRepository();

    private LiveRepository() {
    }

    public static LiveRepository getInstance() {
        if (instance == null) {
            instance = new LiveRepository();
        }
        return instance;
    }

    private ApiServer userApi = RetrofitClient.getInstance().create(ApiServer.class);

    public void startLive(MutableLiveData<Boolean> dismissDialog, MutableLiveData<LiveInitInfo> startLiveInfo, LiveInitInfo map) {
        String json = JSON.toJSONString(map);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        userApi.toStartLive(UserInfoMgr.getInstance().getHttpToken(), body)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                    }
                })
                .subscribe(new CustomObserver<MBaseResponse<LiveInitInfo>>() {

                    @Override
                    protected void dismissDialog() {
                        dismissDialog.postValue(true);
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<LiveInitInfo> data) {
                        startLiveInfo.postValue(data.getData());
                    }
                });
    }

    public void reStartLive(MutableLiveData<Boolean> dismissDialog, MutableLiveData<LiveInitInfo> startLiveInfo, String liveRoomRecordId) {
        userApi.reStartLive(UserInfoMgr.getInstance().getHttpToken(), liveRoomRecordId)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                    }
                })
                .subscribe(new CustomObserver<MBaseResponse<LiveInitInfo>>() {

                    @Override
                    protected void dismissDialog() {
                        dismissDialog.postValue(true);
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<LiveInitInfo> data) {
                        startLiveInfo.postValue(data.getData());
                    }
                });
    }

    public void joinLive(MutableLiveData<Boolean> dismissDialog, MutableLiveData<LiveInitInfo> liveInfo, String liveRoomRecordId) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("liveRoomId", liveRoomRecordId);
        String json = JSON.toJSONString(hashMap);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        userApi.toJoinLive(UserInfoMgr.getInstance().getHttpToken(), body)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                    }
                })
                .subscribe(new CustomObserver<MBaseResponse<LiveInitInfo>>() {
                    @Override
                    protected void dismissDialog() {
                        dismissDialog.postValue(true);
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<LiveInitInfo> data) {
                        liveInfo.postValue(data.getData());

                    }
                });
    }

    public void checkLiveInfo(MutableLiveData<Boolean> dismissDialog, MutableLiveData<LiveInitInfo> liveInfo) {
        userApi.checkLiveInfo(UserInfoMgr.getInstance().getHttpToken())
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                    }
                })
                .subscribe(new CustomObserver<MBaseResponse<LiveInitInfo>>() {
                    @Override
                    protected void dismissDialog() {
                        dismissDialog.postValue(true);
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<LiveInitInfo> data) {
                        liveInfo.postValue(data.getData());
                    }


                });
    }

    public void overLive(MutableLiveData<Boolean> dismissDialog, MutableLiveData<Boolean> exitSuccess, String liveRoomRecordId, String commentPeopleNum) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("liveRoomRecordId", liveRoomRecordId);
        hashMap.put("commentPeopleNum", commentPeopleNum);
        String json = JSON.toJSONString(hashMap);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        userApi.toOverLive(UserInfoMgr.getInstance().getHttpToken(), body)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                    }
                })
                .subscribe(new CustomObserver<MBaseResponse<LiveInitInfo>>() {
                    @Override
                    protected void dismissDialog() {
                        dismissDialog.postValue(true);
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<LiveInitInfo> data) {
                        ToastUtil.toastShortMessage("结束成功");
                        exitSuccess.postValue(true);
                    }

                });
    }

    public void rushLiveInfo(MutableLiveData<Boolean> dismissDialog, MutableLiveData<LiveHeaderInfo> liveHeaderInfo, String liveRoomRecordId) {
        userApi.refreshLiveRoomInfo(UserInfoMgr.getInstance().getHttpToken(), liveRoomRecordId)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                    }
                })
                .subscribe(new CustomObserver<MBaseResponse<LiveHeaderInfo>>() {
                    @Override
                    protected void dismissDialog() {
                        dismissDialog.postValue(true);
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<LiveHeaderInfo> data) {
                        liveHeaderInfo.postValue(data.getData());
                    }

                });
    }

    public void toZanLive(MutableLiveData<Boolean> dismissDialog, MutableLiveData<Boolean> zanSuccess, String liveRoomRecordId, String count) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("liveRoomRecordId", liveRoomRecordId);
        hashMap.put("count", count);
        String json = JSON.toJSONString(hashMap);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        userApi.toZanLive(UserInfoMgr.getInstance().getHttpToken(), body)
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
                        zanSuccess.postValue(true);
                    }

                });
    }

    public void leaveLive(MutableLiveData<Boolean> dismissDialog, MutableLiveData<Boolean> exitSuccess, String liveRoomRecordId) {
        userApi.leaveLive(UserInfoMgr.getInstance().getHttpToken(), liveRoomRecordId)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                    }
                })
                .subscribe(new CustomObserver<MBaseResponse<LiveInitInfo>>() {
                    @Override
                    protected void dismissDialog() {
                        dismissDialog.postValue(true);
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<LiveInitInfo> data) {
                        exitSuccess.postValue(true);
                    }

                });
    }

    public void liveOverInfo(MutableLiveData<Boolean> dismissDialog, MutableLiveData<LiveOverInfo> liveOverInfo, String liveRoomRecordId) {
        userApi.liveOverInfo(UserInfoMgr.getInstance().getHttpToken(), liveRoomRecordId)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                    }
                })
                .subscribe(new CustomObserver<MBaseResponse<LiveOverInfo>>() {
                    @Override
                    protected void dismissDialog() {
                        dismissDialog.postValue(true);
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<LiveOverInfo> data) {
                        liveOverInfo.postValue(data.getData());
                    }

                });
    }

    public void addFansCount(String liveRoomRecordId) {
        userApi.addFansCount(UserInfoMgr.getInstance().getHttpToken(), liveRoomRecordId)
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

    public void setZBStatus(MutableLiveData<Boolean> dismissDialog, MutableLiveData<Boolean> setSuccess, ZBSettingBean zbSettingBean) {
        String json = JSON.toJSONString(zbSettingBean);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        userApi.setZBStatus(UserInfoMgr.getInstance().getHttpToken(), body)
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
                        setSuccess.postValue(true);
                    }

                });
    }

    public void setVoiceMicMute(MutableLiveData<Boolean> dismissDialog, MutableLiveData<Integer> netSuccess, String liveRoomRecordId, String targetId, boolean voiceMicMute) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("liveRoomRecordId", liveRoomRecordId);
        hashMap.put("targetId", targetId);
        hashMap.put("voiceMicMute", voiceMicMute);
        String json = JSON.toJSONString(hashMap);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        userApi.setVoiceMicMute(UserInfoMgr.getInstance().getHttpToken(), body)
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
                        netSuccess.postValue(1);
                    }

                });
    }

    //设置 加入多人连麦/离开多人连麦
    public void setVoiceMic(MutableLiveData<Boolean> dismissDialog, MutableLiveData<Integer> netSuccess, MutableLiveData<AnchorInfo> micAnchorInfo, String liveRoomRecordId, AnchorInfo anchorInfo) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("liveRoomRecordId", liveRoomRecordId);
        hashMap.put("targetId", anchorInfo.userID);
        hashMap.put("micStatus", anchorInfo.micStatus == 1 ? "CONNECT" : "BREAK");
        hashMap.put("position", anchorInfo.position);
        String json = JSON.toJSONString(hashMap);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        userApi.setVoiceMic(UserInfoMgr.getInstance().getHttpToken(), body)
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
                        if (anchorInfo.micStatus != 1) {
                            //设置断开 成功
                            anchorInfo.netStatus = 1;
                        } else {
                            //加入 成功
                            anchorInfo.netStatus = 2;
                        }
                        micAnchorInfo.postValue(anchorInfo);

                    }

                    @Override
                    public void onFail(String code, MBaseResponse<Object> data) {
                        super.onFail(code, data);
                        if (anchorInfo.micStatus == 1) {
                            //设置加入 失败
                            anchorInfo.netStatus = 3;
                        } else {
                            //断开 失败
                            anchorInfo.netStatus = 4;
                        }
                        micAnchorInfo.postValue(anchorInfo);
                    }
                });
    }

    public void findMicUsers(String liveRoomRecordId, MutableLiveData<List<ZBUserListBean>> moreLinkList) {
        userApi.findMicUsers(UserInfoMgr.getInstance().getHttpToken(), liveRoomRecordId)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                    }
                })
                .subscribe(new CustomObserver<MBaseResponse<List<ZBUserListBean>>>() {
                    @Override
                    protected void dismissDialog() {

                    }

                    @Override
                    protected void onSuccess(MBaseResponse<List<ZBUserListBean>> data) {
                        moreLinkList.postValue(data.getData());
                    }

                });
    }

    public void getLiveStatus(String liveRoomId, MutableLiveData<LiveInitInfo> zbSettingBean) {
        userApi.getLiveStatus(UserInfoMgr.getInstance().getHttpToken(), liveRoomId)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                    }
                })
                .subscribe(new CustomObserver<MBaseResponse<LiveInitInfo>>() {
                    @Override
                    protected void dismissDialog() {

                    }

                    @Override
                    protected void onSuccess(MBaseResponse<LiveInitInfo> data) {
                        zbSettingBean.postValue(data.getData());
                    }

                });
    }

    public void getHomeVideoList(MutableLiveData<Boolean> closeRsl, int page, int pageSize, int type, MutableLiveData<List<VideoInfo>> videoList, String resourceType) {
        String uid = "";
        if (UserInfoMgr.getInstance().getUserInfo() != null) {
            if (UserInfoMgr.getInstance().getUserInfo().getId() != null) {
                uid = UserInfoMgr.getInstance().getUserInfo().getId();
            }
        }

        userApi.getHomeVideoList(uid, Constant.HEADER_DEF, Constant.APP_MID, page, pageSize, type, resourceType)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                    }
                })
                .subscribe(new CustomObserver<MBaseResponse<BaseListRes<List<VideoInfo>>>>() {
                    @Override
                    protected void dismissDialog() {
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<BaseListRes<List<VideoInfo>>> data) {
                        videoList.postValue(data.getData().content);
                    }

                    @Override
                    public void onFail(String code, MBaseResponse<BaseListRes<List<VideoInfo>>> data) {
                        super.onFail(code, data);
                        if (closeRsl != null) {
                            closeRsl.postValue(true);
                        }
                    }
                });
    }

    public void getHomeVideoList(MutableLiveData<Boolean> closeRsl, int page, int pageSize, int type, MutableLiveData<List<VideoInfo>> videoList) {
        //不传默认返回三种类型的列表
        String resourceType = type == 2 ? "LIVE,VIDEO,USER_DIARY" : "";
        getHomeVideoList(closeRsl, page, pageSize, type, videoList, resourceType);
    }

    public void getHomeInfo(MutableLiveData<HomeRes> homeRes) {
        userApi.getHomeInfo()
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                    }
                })
                .subscribe(new CustomObserver<MBaseResponse<HomeRes>>() {
                    @Override
                    protected void dismissDialog() {

                    }

                    @Override
                    protected void onSuccess(MBaseResponse<HomeRes> data) {
                        homeRes.postValue(data.getData());
                    }

                });
    }

    public void toLikeVideo(String shortVideoId, boolean favoriteStatus) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("objectId", shortVideoId);
        hashMap.put("favoriteStatus", favoriteStatus);
        String json = JSON.toJSONString(hashMap);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        userApi.setVideoLike(UserInfoMgr.getInstance().getHttpToken(), body)
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

    public void getLiveFiends(MutableLiveData<List<VideoInfo>> videoList) {
        userApi.getLiveFirends(UserInfoMgr.getInstance().getHttpToken())
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                    }
                })
                .subscribe(new CustomObserver<MBaseResponse<List<VideoInfo>>>() {
                    @Override
                    protected void dismissDialog() {

                    }

                    @Override
                    protected void onSuccess(MBaseResponse<List<VideoInfo>> data) {
                        videoList.postValue(data.getData());
                    }

                });
    }

    public void toComment(String id, String content, MutableLiveData<CommentListBean> commentSuccess) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", id);
        hashMap.put("content", content);
        String json = JSON.toJSONString(hashMap);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        userApi.toComment(UserInfoMgr.getInstance().getHttpToken(), body)
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

        userApi.getCommentList(page, 10, id)
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
        userApi.toCommentReply(UserInfoMgr.getInstance().getHttpToken(), body)
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
        userApi.getCommentReplyList(commentListBean.itemPage, commentListBean.itemSize, commentListBean.id)
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
        userApi.setCommentLike(UserInfoMgr.getInstance().getHttpToken(), body)
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

}
