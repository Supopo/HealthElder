package com.xaqinren.healthyelders.apiserver;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.alibaba.fastjson.JSON;
import com.igexin.sdk.PushManager;
import com.tencent.bugly.proguard.B;
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
import com.xaqinren.healthyelders.moduleLogin.bean.LoginTokenBean;
import com.xaqinren.healthyelders.moduleLogin.bean.UserInfoBean;
import com.xaqinren.healthyelders.moduleLogin.bean.WeChatUserInfoBean;
import com.xaqinren.healthyelders.moduleMine.bean.BillBean;
import com.xaqinren.healthyelders.moduleMine.bean.BillDetailBean;
import com.xaqinren.healthyelders.moduleMine.bean.BillRecodeBean;
import com.xaqinren.healthyelders.moduleMine.bean.DZVideoInfo;
import com.xaqinren.healthyelders.moduleMine.bean.OrderListBean;
import com.xaqinren.healthyelders.moduleMine.bean.VersionBean;
import com.xaqinren.healthyelders.moduleMine.bean.WalletBean;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ChongZhiListRes;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ZBUserListBean;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.IMLVBLiveRoomListener;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.MLVBLiveRoom;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.roomutil.commondef.LoginInfo;
import com.xaqinren.healthyelders.utils.LogUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
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
import me.goldze.mvvmhabit.utils.Utils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.BufferedSink;

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
                .subscribe(new CustomObserver<MBaseResponse<UserInfoBean>>() {

                    @Override
                    protected void dismissDialog() {

                    }

                    @Override
                    protected void onSuccess(MBaseResponse<UserInfoBean> data) {
                        if (data.getData() != null) {
                            userInfo.postValue(data.getData());

                            InfoCache.getInstance().setLoginUser(data.getData());
                            UserInfoMgr.getInstance().setUserInfo(data.getData());
                            //绑定cid
                            PushManager.getInstance().bindAlias(AppApplication.get(), data.getData().getId());
                            getUserSig(token);
                            if (loginSuccess != null) {
                                loginSuccess.postValue(true);
                            }

                        } else {
                            if (loginSuccess != null) {
                                loginSuccess.postValue(false);
                            }
                        }

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
//                            getUserInfo(null, Constant.API_HEADER + response.getData().access_token);
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

        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        File file = new File(filePath);
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
        if (UserInfoMgr.getInstance().getUserInfo() == null)
            return;
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
        LogUtils.v(Constant.TAG_LIVE, "LiveRoom登录"+loginInfo.userSig);
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
        getMyVideoList(datas, page, pagesize, type, "");
    }

    public void getMyVideoList(MutableLiveData<List<VideoInfo>> datas, int page, int pagesize, String type, String tagerId) {
        //PRIVETE
        userApi.getMyVideoList(UserInfoMgr.getInstance().getHttpToken(), page, pagesize, type, tagerId)
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
        getMyLikeVideoList(datas, page, pagesize, "");
    }

    public void getMyLikeVideoList(MutableLiveData<List<DZVideoInfo>> datas, int page, int pagesize, String tagerId) {
        userApi.getMyLikeVideoList(UserInfoMgr.getInstance().getHttpToken(), page, pagesize, tagerId)
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


    public void bindAlias(MutableLiveData<Boolean> clientIdData, String clientId) {
        userApi.bindAlias(UserInfoMgr.getInstance().getHttpToken(), clientId)
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

    public void searchUser(MutableLiveData<Boolean> dismissDialog, MutableLiveData<List<VideoInfo>> datas, int page, int pagesize, String tags) {
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

    public void delFans(MutableLiveData<Boolean> requestSuccess, MutableLiveData<Boolean> del, String id) {
        userApi.delFans(UserInfoMgr.getInstance().getHttpToken(), id)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribe(new CustomObserver<MBaseResponse<Object>>() {

                    @Override
                    protected void dismissDialog() {
                        requestSuccess.postValue(true);
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<Object> data) {
                        del.postValue(data.isOk());
                    }
                });
    }

    public void chongzhiList(MutableLiveData<ChongZhiListRes> datas) {
        userApi.getChongZhiList(UserInfoMgr.getInstance().getHttpToken())
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribe(new CustomObserver<MBaseResponse<ChongZhiListRes>>() {
                    @Override
                    protected void dismissDialog() {

                    }

                    @Override
                    protected void onSuccess(MBaseResponse<ChongZhiListRes> data) {
                        datas.postValue(data.getData());
                    }
                });

    }

    public void toPay(MutableLiveData<String> payJson, String accountOrderType, String payMethod, String payType, double paymentAmount,String orderNo) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("accountOrderType", accountOrderType);
        hashMap.put("paymentMethod", payMethod);
        hashMap.put("paymentChannel", payType);
        hashMap.put("paymentAmount", paymentAmount);
        hashMap.put("orderNo", orderNo);
        hashMap.put("openid", "");

        String json = JSON.toJSONString(hashMap);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        userApi.toPay(UserInfoMgr.getInstance().getHttpToken(), body)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribe(new CustomObserver<MBaseResponse<String>>() {
                    @Override
                    protected void dismissDialog() {

                    }

                    @Override
                    protected void onSuccess(MBaseResponse<String> data) {
                        payJson.postValue(data.getData());
                    }
                });
    }

    public void getLiveUserInfo(MutableLiveData<UserInfoBean> datas, String userId, String liveRoomRecordId) {
        userApi.getLiveUserInfo(UserInfoMgr.getInstance().getHttpToken(), userId, liveRoomRecordId)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribe(new CustomObserver<MBaseResponse<UserInfoBean>>() {
                    @Override
                    protected void dismissDialog() {

                    }

                    @Override
                    protected void onSuccess(MBaseResponse<UserInfoBean> data) {
                        datas.postValue(data.getData());
                    }
                });

    }

    public void getVersionInfo(MutableLiveData<VersionBean> datas) {
        userApi.checkVersion()
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribe(new CustomObserver<MBaseResponse<VersionBean>>() {
                    @Override
                    protected void dismissDialog() {

                    }

                    @Override
                    protected void onSuccess(MBaseResponse<VersionBean> data) {
                        datas.postValue(data.getData());
                    }
                });

    }

    public void getWalletInfo(MutableLiveData<Boolean> request, MutableLiveData<WalletBean> datas) {
        userApi.getWalletInfo(UserInfoMgr.getInstance().getHttpToken())
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribe(new CustomObserver<MBaseResponse<WalletBean>>() {
                    @Override
                    protected void dismissDialog() {
                        if (request != null) {
                            request.postValue(true);
                        }
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<WalletBean> data) {
                        datas.postValue(data.getData());
                    }
                });

    }

    public void getOrderList(MutableLiveData<Boolean> request , MutableLiveData<List<OrderListBean>>datas,int type,int page) {
        userApi.getOrderList(UserInfoMgr.getInstance().getHttpToken(),type,page)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribe(new CustomObserver<MBaseResponse<BaseListRes<List<OrderListBean>>>>() {
                    @Override
                    protected void dismissDialog() {
                        request.postValue(true);
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<BaseListRes<List<OrderListBean>>> data) {
                        datas.postValue(data.getData().content);
                    }
                });

    }


    public void getBillInfo(MutableLiveData<Boolean> request, MutableLiveData<BillRecodeBean> datas, String key) {

        userApi.getBillInfo(UserInfoMgr.getInstance().getHttpToken(), key)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribe(new CustomObserver<MBaseResponse<BillRecodeBean>>() {
                    @Override
                    protected void dismissDialog() {
                        request.postValue(true);
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<BillRecodeBean> data) {
                        if (data.isOk())
                            datas.postValue(data.getData());
                        else
                            datas.postValue(null);
                    }
                });

    }


    public void getBillInfoDetail(MutableLiveData<Boolean> request, MutableLiveData<BillDetailBean> datas, String key) {
        userApi.getBillInfoDetail(UserInfoMgr.getInstance().getHttpToken(),key)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribe(new CustomObserver<MBaseResponse<BillDetailBean>>() {
                    @Override
                    protected void dismissDialog() {
                        request.postValue(true);
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<BillDetailBean> data) {
                        if (data.isOk())
                            datas.postValue(data.getData());
                        else datas.postValue(null);
                    }
                });

    }


    public void updateUserInfo(MutableLiveData<Boolean> request, MutableLiveData<Boolean> status,
                               String avatarUrl, String nickname, String introduce, String sex, String birthday, String cityAddress) {
        HashMap<String, Object> hashMap = new HashMap<>();
        if (!StringUtils.isEmpty(avatarUrl))
            hashMap.put("avatarUrl", avatarUrl);
        if (!StringUtils.isEmpty(nickname))
            hashMap.put("nickname", nickname);
        if (!StringUtils.isEmpty(introduce))
            hashMap.put("introduce", introduce);
        if (!StringUtils.isEmpty(sex))
            hashMap.put("sex", sex);//FEMALE MALE
        if (!StringUtils.isEmpty(birthday))
            hashMap.put("birthday", birthday);
        if (!StringUtils.isEmpty(cityAddress))
            hashMap.put("cityAddress", cityAddress);

        String json = JSON.toJSONString(hashMap);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        userApi.updateUserInfo(UserInfoMgr.getInstance().getHttpToken(), body)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribe(new CustomObserver<MBaseResponse<Boolean>>() {
                    @Override
                    protected void dismissDialog() {
                        request.postValue(true);
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<Boolean> data) {
                        status.postValue(data.isOk());
                    }
                });
    }

    public void setPassWord(MutableLiveData<Boolean> request, MutableLiveData<Boolean> datas, String key) {
        RequestBody requestBody = new RequestBody() {
            @Override
            public MediaType contentType() {
                return MediaType.parse("application/json");
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                sink.writeString(key, Charset.defaultCharset());
            }
        };
        userApi.setPassWord(UserInfoMgr.getInstance().getHttpToken(), requestBody)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribe(new CustomObserver<MBaseResponse<Object>>() {
                    @Override
                    protected void dismissDialog() {
                        request.postValue(true);
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<Object> data) {
                        datas.postValue(data.isOk());
                    }
                });
    }

    public void getOtherUserInfo(MutableLiveData<UserInfoBean> datas, String userId) {
        userApi.getOtherUserInfo(UserInfoMgr.getInstance().getHttpToken(), userId)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribe(new CustomObserver<MBaseResponse<UserInfoBean>>() {
                    @Override
                    protected void dismissDialog() {

                    }

                    @Override
                    protected void onSuccess(MBaseResponse<UserInfoBean> data) {
                        datas.postValue(data.getData());
                    }
                });

    }

    public void cancelOrder(MutableLiveData<Boolean> request, MutableLiveData<Boolean> datas, String key) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", key);
        String json = JSON.toJSONString(hashMap);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        userApi.cancelOrder(UserInfoMgr.getInstance().getHttpToken(),body)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribe(new CustomObserver<MBaseResponse<Object>>() {
                    @Override
                    protected void dismissDialog() {
//                        request.postValue(true);
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<Object> data) {
                        datas.postValue(data.isOk());
                    }
                });
    }

    public void delOrder(MutableLiveData<Boolean> request, MutableLiveData<Boolean> datas, String key) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", key);
        String json = JSON.toJSONString(hashMap);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        userApi.delOrder(UserInfoMgr.getInstance().getHttpToken(),body)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribe(new CustomObserver<MBaseResponse<Object>>() {
                    @Override
                    protected void dismissDialog() {
//                        request.postValue(true);
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<Object> data) {
                        datas.postValue(data.isOk());
                    }
                });
    }

    public void receiptOrder(MutableLiveData<Boolean> request, MutableLiveData<Boolean> datas, String key) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", key);
        String json = JSON.toJSONString(hashMap);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        userApi.receiptOrder(UserInfoMgr.getInstance().getHttpToken(),body)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribe(new CustomObserver<MBaseResponse<Object>>() {
                    @Override
                    protected void dismissDialog() {
//                        request.postValue(true);
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<Object> data) {
                        datas.postValue(data.isOk());
                    }
                });
    }

    public void getWithdraw(MutableLiveData<Boolean> request, MutableLiveData<Boolean> datas,
                            double appealAmount,
                            String accountBank,
                            String accountName,
                            String accountNo,
                            String cardType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("appealAmount", appealAmount);
        hashMap.put("accountBank", accountBank);
        hashMap.put("accountName", accountName);
        hashMap.put("accountNo", accountNo);
        hashMap.put("cardType", cardType);
        String json = JSON.toJSONString(hashMap);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        userApi.withdrawal(UserInfoMgr.getInstance().getHttpToken(),body)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribe(new CustomObserver<MBaseResponse<Object>>() {
                    @Override
                    protected void dismissDialog() {
                        request.postValue(true);
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<Object> data) {
                        datas.postValue(data.isOk());
                    }
                });
    }

}
