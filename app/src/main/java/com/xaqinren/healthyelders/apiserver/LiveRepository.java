package com.xaqinren.healthyelders.apiserver;


import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.alibaba.fastjson.JSON;
import com.xaqinren.healthyelders.bean.BaseListRes;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.global.InfoCache;
import com.xaqinren.healthyelders.http.RetrofitClient;
import com.xaqinren.healthyelders.moduleHome.bean.CommentListBean;
import com.xaqinren.healthyelders.moduleHome.bean.HomeMenuRes;
import com.xaqinren.healthyelders.moduleHome.bean.ResBean;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;
import com.xaqinren.healthyelders.moduleLogin.activity.PhoneLoginActivity;
import com.xaqinren.healthyelders.moduleZhiBo.bean.GiftBean;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveHeaderInfo;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveInitInfo;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveOverInfo;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ZBGoodsListRes;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ZBSettingBean;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ZBUserListBean;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.roomutil.commondef.AnchorInfo;

import java.util.HashMap;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.bus.RxBus;
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

    public void startLive(MutableLiveData<Boolean> startError, MutableLiveData<Boolean> dismissDialog, MutableLiveData<LiveInitInfo> startLiveInfo, LiveInitInfo map) {
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

                    @Override
                    public void onFail(String code, MBaseResponse<LiveInitInfo> data) {
                        super.onFail(code, data);
                        startError.postValue(true);
                    }
                });
    }

    public void reStartLive(MutableLiveData<Boolean> startError, MutableLiveData<Boolean> dismissDialog, MutableLiveData<LiveInitInfo> startLiveInfo, String liveRoomRecordId) {
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

                    @Override
                    public void onFail(String code, MBaseResponse<LiveInitInfo> data) {
                        super.onFail(code, data);
                        startError.postValue(true);
                    }
                });
    }

    public void joinLive(MutableLiveData<Boolean> dismissDialog, MutableLiveData<LiveInitInfo> liveInfo, String liveRoomRecordId, String roomPassword) {
        if (!InfoCache.getInstance().checkLogin()) {
            dismissDialog.postValue(true);
            RxBus.getDefault().post(new EventBean(CodeTable.TOKEN_ERR, null));
            return;
        }

        if (!UserInfoMgr.getInstance().getUserInfo().hasMobileNum()) {
            dismissDialog.postValue(true);
            RxBus.getDefault().post(new EventBean(CodeTable.MSG_NO_PHONE, null));
            return;
        }
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("liveRoomId", liveRoomRecordId);
        hashMap.put("roomPassword", roomPassword);
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
                        if (liveInfo != null) {
                            liveInfo.postValue(data.getData());
                        }else {
                            //用于聊天界面中打开直播间页面
                            RxBus.getDefault().post(new EventBean<LiveInitInfo>(CodeTable.SHARE_JOININ_LIVE,data.getData()));
                        }
                    }
                });
    }

    public void joinLive(MutableLiveData<Boolean> dismissDialog, MutableLiveData<LiveInitInfo> liveInfo, String liveRoomRecordId) {
        if (!InfoCache.getInstance().checkLogin()) {
            dismissDialog.postValue(true);
            RxBus.getDefault().post(new EventBean(CodeTable.TOKEN_ERR, null));
            return;
        }

        if (!UserInfoMgr.getInstance().getUserInfo().hasMobileNum()) {
            dismissDialog.postValue(true);
            RxBus.getDefault().post(new EventBean(CodeTable.MSG_NO_PHONE, null));
            return;
        }

        joinLive(dismissDialog, liveInfo, liveRoomRecordId,"");
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
                .subscribe(new CustomObserver<MBaseResponse<Object>>() {
                    @Override
                    protected void dismissDialog() {
                        dismissDialog.postValue(true);
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<Object> data) {
                        exitSuccess.postValue(true);
                    }

                    @Override
                    public void onNext(MBaseResponse<Object> response) {
                        super.onNext(response);
                        if (!response.getCode().equals(CodeTable.SUCCESS_CODE)) {
                            exitSuccess.postValue(false);
                        }
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

    //直播间设置
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
        hashMap.put("pullStreamUrl", anchorInfo.accelerateURL);
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


    public void getHomeVideoList(MutableLiveData<Boolean> dismissDialog, int page, int pageSize, Integer type, MutableLiveData<List<VideoInfo>> videoList, String resourceType, String tags, String key) {
        String uid = "";
        if (UserInfoMgr.getInstance().getUserInfo() != null) {
            if (UserInfoMgr.getInstance().getUserInfo().getId() != null) {
                uid = UserInfoMgr.getInstance().getUserInfo().getId();
            }
        }

        userApi.getHomeVideoList(uid, Constant.HEADER_DEF, Constant.APP_MID, page, 10, type, resourceType, tags, key)
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
                        dismissDialog.postValue(true);
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<BaseListRes<List<VideoInfo>>> data) {
                        videoList.postValue(data.getData().content);
                    }

                    @Override
                    public void onFail(String code, MBaseResponse<BaseListRes<List<VideoInfo>>> data) {
                        super.onFail(code, data);
                    }
                });
    }

    public void getHomeVideoList(MutableLiveData<Boolean> closeRsl, int page, int pageSize, Integer type, MutableLiveData<List<VideoInfo>> videoList) {
        //不传默认返回三种类型的列表
        String resourceType = "";
        if (type == 2) {
            resourceType = "LIVE,VIDEO,USER_DIARY";
        } else if (type == 0 || type == 1) {
            resourceType = "LIVE,VIDEO";
        }
        getHomeVideoList(closeRsl, page, pageSize, type, videoList, resourceType, "");
    }

    public void getHomeVideoList(MutableLiveData<Boolean> dismissDialog, int page, int pageSize, Integer type, MutableLiveData<List<VideoInfo>> videoList, String resourceType, String tags) {
        getHomeVideoList(dismissDialog, page, pageSize, type, videoList, resourceType, tags, "");
    }


    public void getHomeInfo(MutableLiveData<HomeMenuRes> homeRes) {
        userApi.getHomeInfo()
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                    }
                })
                .subscribe(new CustomObserver<MBaseResponse<HomeMenuRes>>() {
                    @Override
                    protected void dismissDialog() {

                    }

                    @Override
                    protected void onSuccess(MBaseResponse<HomeMenuRes> data) {
                        homeRes.postValue(data.getData());
                    }

                });
    }

    public void toLikeVideo(int type, String shortVideoId, boolean favoriteStatus, int position, MutableLiveData<ResBean> dzSuccess, MutableLiveData<Boolean> dismissDialog) {
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
                        dismissDialog.postValue(true);
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<Object> data) {
                        ResBean resBean = new ResBean();
                        resBean.position = position;
                        resBean.type = type;
                        resBean.isSuccess = true;
                        dzSuccess.postValue(resBean);
                    }

                });
    }

    public void toLikeVideo(String shortVideoId, boolean favoriteStatus, int position, MutableLiveData<ResBean> dzSuccess, MutableLiveData<Boolean> dismissDialog) {
        toLikeVideo(0, shortVideoId, favoriteStatus, position, dzSuccess, dismissDialog);
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

    public void toComment(String id, String content, MutableLiveData<CommentListBean> commentSuccess, MutableLiveData<Boolean> dismissDialog) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", id);
        hashMap.put("content", content.trim());
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
                        dismissDialog.postValue(true);
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

    public void toCommentReply(CommentListBean mCommentListBean, String content, int type, MutableLiveData<CommentListBean> commentSuccess, MutableLiveData<Boolean> dismissDialog) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("replyType", type == 0 ? "REPLY_COMMENT" : "REPLY_REPLY");
        hashMap.put("id", mCommentListBean.id);
        hashMap.put("content", content.trim());
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
                        dismissDialog.postValue(true);
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

    public void sendGift(MutableLiveData<Boolean> sendSuccess, String liveRoomRecordId, String targetId, String giftId) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("liveRoomRecordId", liveRoomRecordId);
        hashMap.put("giftId", giftId);
        hashMap.put("count", 1);
        hashMap.put("targetId", targetId);
        String json = JSON.toJSONString(hashMap);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        userApi.sendGift(UserInfoMgr.getInstance().getHttpToken(), body)
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
                        sendSuccess.postValue(true);
                    }

                });
    }

    public void getGiftList(MutableLiveData<List<GiftBean>> giftList) {
        userApi.getGiftList(UserInfoMgr.getInstance().getHttpToken())
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                    }
                })
                .subscribe(new CustomObserver<MBaseResponse<List<GiftBean>>>() {
                    @Override
                    protected void dismissDialog() {

                    }

                    @Override
                    protected void onSuccess(MBaseResponse<List<GiftBean>> data) {
                        giftList.postValue(data.getData());
                    }

                });
    }

    public void liveOverInfoGZ(MutableLiveData<Boolean> dismissDialog, MutableLiveData<LiveOverInfo> liveOverInfo, String liveRoomRecordId) {
        userApi.liveOverInfoGZ(UserInfoMgr.getInstance().getHttpToken(), liveRoomRecordId)
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

    public void getSomeLikeList(MutableLiveData<Boolean> dismissDialog, int page, int pageSize, MutableLiveData<List<VideoInfo>> videoList, String resourceType, String roomId) {
        userApi.getSomeLikeVideoList(UserInfoMgr.getInstance().getHttpToken(), page, pageSize, resourceType, roomId)
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
                        dismissDialog.postValue(true);
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<BaseListRes<List<VideoInfo>>> data) {
                        videoList.postValue(data.getData().content);
                    }

                    @Override
                    public void onFail(String code, MBaseResponse<BaseListRes<List<VideoInfo>>> data) {
                        super.onFail(code, data);
                    }
                });
    }

    public void getBlackList(MutableLiveData<Boolean> dismissDialog, MutableLiveData<BaseListRes<List<ZBUserListBean>>> userList, int page, String liveRoomId) {
        userApi.getBlackList(UserInfoMgr.getInstance().getHttpToken(), page, 10, liveRoomId)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                    }
                })
                .subscribe(new CustomObserver<MBaseResponse<BaseListRes<List<ZBUserListBean>>>>() {
                    @Override
                    protected void dismissDialog() {
                        dismissDialog.postValue(true);
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<BaseListRes<List<ZBUserListBean>>> data) {
                        userList.postValue(data.getData());
                    }

                });
    }

    public void getJinYanList(MutableLiveData<Boolean> dismissDialog, MutableLiveData<BaseListRes<List<ZBUserListBean>>> userList, int page, String liveRoomRecordId) {
        userApi.getJinYanList(UserInfoMgr.getInstance().getHttpToken(), page, 10, liveRoomRecordId)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                    }
                })
                .subscribe(new CustomObserver<MBaseResponse<BaseListRes<List<ZBUserListBean>>>>() {
                    @Override
                    protected void dismissDialog() {
                        dismissDialog.postValue(true);
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<BaseListRes<List<ZBUserListBean>>> data) {
                        userList.postValue(data.getData());
                    }

                });
    }

    public void getZbGoodsLis(MutableLiveData<ZBGoodsListRes> goodsList, String liveRoomId) {
        userApi.getZbGoodsList(UserInfoMgr.getInstance().getHttpToken(), liveRoomId)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                    }
                })
                .subscribe(new CustomObserver<MBaseResponse<ZBGoodsListRes>>() {
                    @Override
                    protected void dismissDialog() {

                    }

                    @Override
                    protected void onSuccess(MBaseResponse<ZBGoodsListRes> data) {
                        if (data.getData().commodityList != null) {
                            goodsList.postValue(data.getData());
                        }
                    }

                });
    }

    public void setZBGoodsShow(MutableLiveData<Boolean> dismissDialog, MutableLiveData<Boolean> setSuccess, String liveRoomId, String commodityId, boolean canExplain) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("liveRoomId", liveRoomId);
        hashMap.put("commodityId", commodityId);
        hashMap.put("canExplain", canExplain);
        String json = JSON.toJSONString(hashMap);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        userApi.setZBGoodsShow(UserInfoMgr.getInstance().getHttpToken(), body)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
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

    public void delVideo(MutableLiveData<Boolean> dismissDialog, MutableLiveData<Boolean> setSuccess, String liveRoomId) {
        userApi.delLiteAvVideo(UserInfoMgr.getInstance().getHttpToken(), liveRoomId)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribe(new CustomObserver<MBaseResponse<Object>>() {
                    @Override
                    protected void dismissDialog() {
                        dismissDialog.postValue(true);
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<Object> data) {
                        setSuccess.postValue(data.isOk());
                    }
                });
    }


    public void setVideoStatus(MutableLiveData<Boolean> request, String id, String status) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", id);
        hashMap.put("creationViewAuth", status);
        String json = JSON.toJSONString(hashMap);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        userApi.setVideoStatus(UserInfoMgr.getInstance().getHttpToken(), body)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribe(new CustomObserver<MBaseResponse<Object>>() {
                    @Override
                    protected void dismissDialog() {
                        if (request != null) {
                            request.postValue(true);
                        }
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<Object> data) {

                    }
                });
    }

    public void feedbackSave(MutableLiveData<Boolean> request, MutableLiveData<Boolean> commitSuccess, String json) {
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        userApi.feedbackSave(UserInfoMgr.getInstance().getHttpToken(), body)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribe(new CustomObserver<MBaseResponse<Object>>() {
                    @Override
                    protected void dismissDialog() {
                        if (request != null) {
                            request.postValue(true);
                        }
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<Object> data) {
                        commitSuccess.postValue(true);
                    }
                });
    }
}
