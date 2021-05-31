package com.xaqinren.healthyelders.apiserver;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.alibaba.fastjson.JSON;
import com.igexin.sdk.PushManager;
import com.tencent.qcloud.ugckit.utils.ToastUtil;
import com.xaqinren.healthyelders.bean.BaseListRes;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.global.AppApplication;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.global.InfoCache;
import com.xaqinren.healthyelders.http.RetrofitClient;
import com.xaqinren.healthyelders.moduleHome.bean.GirlsBean;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;
import com.xaqinren.healthyelders.moduleHome.bean.VideoListBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.MMusicItemBean;
import com.xaqinren.healthyelders.moduleLogin.bean.LoginTokenBean;
import com.xaqinren.healthyelders.moduleLogin.bean.UserInfoBean;
import com.xaqinren.healthyelders.moduleLogin.bean.WeChatUserInfoBean;
import com.xaqinren.healthyelders.moduleMine.bean.DZVideoInfo;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ZBUserListBean;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.IMLVBLiveRoomListener;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.MLVBLiveRoom;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.roomutil.commondef.LoginInfo;
import com.xaqinren.healthyelders.utils.LogUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.http.BaseResponse;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.goldze.mvvmhabit.utils.SPUtils;
import me.goldze.mvvmhabit.utils.StringUtils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * =====================================================
 * 描    述: 用户数据网络请求仓库
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
                            girlsList.postValue(response.getData());
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

    public void getUserInfo(MutableLiveData<Boolean> loginSuccess, MutableLiveData<UserInfoBean> userInfo, String token) {
        userApi.getUserInfo(token)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                    }
                })
                .subscribe(new DisposableObserver<MBaseResponse<UserInfoBean>>() {
                    @Override
                    public void onNext(MBaseResponse<UserInfoBean> response) {
                        if (response.isOk()) {
                            if (userInfo != null) {
                                userInfo.postValue(response.getData());
                            }
                            InfoCache.getInstance().setLoginUser(response.getData());
                            UserInfoMgr.getInstance().setUserInfo(response.getData());
                            //绑定cid
                            PushManager.getInstance().bindAlias(AppApplication.get(),response.getData().getId());
                            LogUtils.e("MainActivity", "绑定 cid -> " + response.getData().getId());
                            getUserSig(token);
                            if (loginSuccess != null) {
                                loginSuccess.postValue(true);
                            }
                        }else{
                            if (userInfo != null) {
                                userInfo.postValue(response.getData());
                            }

                            if (loginSuccess != null) {
                                loginSuccess.postValue(false);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
    public void getUserInfo(MutableLiveData<UserInfoBean> userInfo, String token) {
        getUserInfo(null, userInfo, token);
    }

    public void loginByPhone(MutableLiveData<Boolean> loginSuccess, String phone, String code, String openId) {
        HashMap<String, String> map = new HashMap<>();
        map.put("mobileNumber", phone);
        map.put("password", code);
        map.put("clientId", Constant.APP_MID);
        map.put("requestSource", "ANDROID_APP");
        if (!StringUtils.isEmpty(openId)) {
            map.put("openId", openId);
        }
        map.put("recommendCode", "");
        String json = JSON.toJSONString(map);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        userApi.phoneLogin(Constant.HEADER_DEF, Constant.APP_MID, body)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                    }
                })
                .subscribe(new DisposableObserver<MBaseResponse<LoginTokenBean>>() {
                    @Override
                    public void onNext(MBaseResponse<LoginTokenBean> response) {
                        if (response.isOk()) {
                            SPUtils.getInstance().put(Constant.SP_KEY_TOKEN_INFO, JSON.toJSONString(response.getData()));
                            UserInfoMgr.getInstance().setAccessToken(response.getData().access_token);
                            UserInfoMgr.getInstance().setHttpToken(Constant.API_HEADER + response.getData().access_token);
                            getUserInfo(null, Constant.API_HEADER + response.getData().access_token);
                            loginSuccess.postValue(true);
                        } else {
                            ToastUtil.toastShortMessage(response.getMessage());
                            loginSuccess.postValue(false);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        loginSuccess.postValue(false);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void toWxChatRealLogin(MutableLiveData<Integer> loginStatus, WeChatUserInfoBean infoBean) {
        HashMap<String, String> map = new HashMap<>();
        map.put("unionId", infoBean.unionId);
        map.put("openId", infoBean.openId);
        map.put("nickName", infoBean.nickName);
        map.put("sex", infoBean.sex);
        map.put("city", infoBean.city);
        map.put("province", infoBean.province);
        map.put("country", infoBean.country);
        map.put("avatarUrl", infoBean.avatarUrl);
        map.put("sessionKey", infoBean.sessionKey);
        map.put("rCode", infoBean.rcode);

        String json = JSON.toJSONString(map);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        userApi.toWxChatRealLogin(Constant.HEADER_DEF, Constant.APP_MID, body)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                    }
                })
                .subscribe(new DisposableObserver<MBaseResponse<LoginTokenBean>>() {
                    @Override
                    public void onNext(MBaseResponse<LoginTokenBean> response) {
                        if (response.isOk()) {
                            InfoCache.getInstance().setTokenInfo(response.getData());
                            UserInfoMgr.getInstance().setAccessToken(response.getData().access_token);
                            UserInfoMgr.getInstance().setHttpToken(Constant.API_HEADER + response.getData().access_token);
                            getUserInfo(null, Constant.API_HEADER + response.getData().access_token);
                            loginStatus.postValue(1);
                        } else {
                            //需要绑定手机号跳转绑定手机号页面
                            if (response.getCode().equals(CodeTable.NO_PHONE_CODE)) {
                                loginStatus.postValue(2);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void updatePhoto(MutableLiveData<Boolean> dismissDialog, MutableLiveData<String> fileUrl, String filePath) {
        File file = new File(filePath);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("image/jpeg"), file))
                .build();
        RetrofitClient.getInstance().create(ApiServer.class).uploadFile(UserInfoMgr.getInstance().getHttpToken(), requestBody)
                .compose(RxUtils.schedulersTransformer()) //线程调度
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {

                    }
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

    public void toFollow(MutableLiveData<Boolean> followSuccess, MutableLiveData<Boolean> dismissDialog, String userId) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userId", userId);
        hashMap.put("attentionSource", "LIVE_ROOM");
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

    public void toRenZheng(MutableLiveData<Boolean> renZhengSuccess, MutableLiveData<Boolean> dismissDialog, Map hashMap) {
        String json = JSON.toJSONString(hashMap);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        userApi.saveUserIdCardInfo(UserInfoMgr.getInstance().getHttpToken(), body)
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
                        if (renZhengSuccess != null) {
                            renZhengSuccess.postValue(true);
                        }
                    }
                });
    }

    public void getFriendsList(MutableLiveData<List<ZBUserListBean>> datas, int page, int pagesize) {
        userApi.getFriendsList(UserInfoMgr.getInstance().getHttpToken(), page, pagesize)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribe(new CustomObserver<MBaseResponse<BaseListRes<List<ZBUserListBean>>>>() {

                    @Override
                    protected void dismissDialog() {

                    }

                    @Override
                    protected void onSuccess(MBaseResponse<BaseListRes<List<ZBUserListBean>>> data) {
                        datas.postValue(data.getData().content);
                    }
                });
    }

    public void getUserSig(String token) {
        if (UserInfoMgr.getInstance().getUserInfo()==null)return;
        userApi.getUserSig(token)
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                    }
                })
                .subscribe(new CustomObserver<MBaseResponse<String>>() {

                    @Override
                    public void onFail(String code, MBaseResponse<String> data) {
                        super.onFail(code, data);
                    }

                    @Override
                    protected void dismissDialog() {

                    }

                    @Override
                    protected void onSuccess(MBaseResponse<String> data) {
                        InfoCache.getInstance().setUserSig(data.getData());
                        UserInfoMgr.getInstance().setUserSig(data.getData());
                        toLoginRoom();
                    }
                });
    }

    public void toLoginRoom() {
        LoginInfo loginInfo = new LoginInfo();
        loginInfo.userAvatar = UserInfoMgr.getInstance().getUserInfo().getAvatarUrl();
        loginInfo.userName = UserInfoMgr.getInstance().getUserInfo().getNickname();
        loginInfo.sdkAppID = 1400392607;
        loginInfo.userID = UserInfoMgr.getInstance().getUserInfo().getId();
        loginInfo.userSig = UserInfoMgr.getInstance().getUserSig();
         MLVBLiveRoom.sharedInstance(AppApplication.getContext()).login(loginInfo, new IMLVBLiveRoomListener.LoginCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                LogUtils.v(Constant.TAG_LIVE, "LiveRoom登录失败：" + errCode);
                LogUtils.v(Constant.TAG_LIVE, "LiveRoom登录失败：" + errInfo);
            }

            @Override
            public void onSuccess() {
                LogUtils.v(Constant.TAG_LIVE, "LiveRoom登录成功");
                RxBus.getDefault().post(new EventBean(CodeTable.IM_LOGIN_SUCCESS, null));
            }
        });
    }

    public void getMyVideoList(MutableLiveData<List<VideoInfo>> datas, int page, int pagesize, String type) {
        //PRIVETE
        userApi.getMyVideoList(UserInfoMgr.getInstance().getHttpToken(), page, pagesize, type)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribe(new CustomObserver<MBaseResponse<BaseListRes<List<VideoInfo>>>>() {

                    @Override
                    protected void dismissDialog() {

                    }

                    @Override
                    protected void onSuccess(MBaseResponse<BaseListRes<List<VideoInfo>>> data) {
                        datas.postValue(data.getData().content);
                    }
                });
    }

    public void getMyLikeVideoList(MutableLiveData<List<DZVideoInfo>> datas, int page, int pagesize) {
        userApi.getMyLikeVideoList(UserInfoMgr.getInstance().getHttpToken(), page, pagesize)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribe(new CustomObserver<MBaseResponse<BaseListRes<List<DZVideoInfo>>>>() {

                    @Override
                    protected void dismissDialog() {

                    }

                    @Override
                    protected void onSuccess(MBaseResponse<BaseListRes<List<DZVideoInfo>>> data) {
                        datas.postValue(data.getData().content);
                    }
                });
    }

    public void bindAlias( MutableLiveData<Boolean> clientIdData , String clientId) {
        userApi.bindAlias(UserInfoMgr.getInstance().getHttpToken(),clientId)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribe(new CustomObserver<MBaseResponse<Object>>() {

                    @Override
                    protected void dismissDialog() {

                    }

                    @Override
                    protected void onSuccess(MBaseResponse<Object> data) {
                        clientIdData.postValue(data.isOk());
                    }
                });
    }

    public void searchUser(MutableLiveData<Boolean> dismissDialog,MutableLiveData<List<VideoInfo>> datas, int page, int pagesize, String tags ){
        userApi.getSearchUser(page, pagesize, tags)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribe(new CustomObserver<MBaseResponse<BaseListRes<List<VideoInfo>>>>() {

                    @Override
                    protected void dismissDialog() {
                        dismissDialog.postValue(true);
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<BaseListRes<List<VideoInfo>>> data) {
                        datas.postValue(data.getData().content);
                    }
                });
    }

}
