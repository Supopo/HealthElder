package com.xaqinren.healthyelders.moduleLiteav.viewModel;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.tencent.qcloud.ugckit.UGCKit;
import com.tencent.qcloud.ugckit.UGCKitConstants;
import com.tencent.qcloud.ugckit.utils.BackgroundTasks;
import com.tencent.qcloud.ugckit.utils.LogReport;
import com.tencent.qcloud.ugckit.utils.NetworkUtil;
import com.tencent.qcloud.ugckit.utils.TCUserMgr;
import com.xaqinren.healthyelders.apiserver.LiteAvRepository;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleLiteav.bean.LiteAvUserBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.SaveDraftBean;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.IMLVBLiveRoomListener;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.MLVBLiveRoom;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.roomutil.commondef.LoginInfo;
import com.xaqinren.healthyelders.utils.LogUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;

public class VideoPublishViewModel extends BaseViewModel {

    public VideoPublishViewModel(@NonNull Application application) {
        super(application);
    }
    public MutableLiveData<Boolean> requestSuccess = new MutableLiveData<>();

    public MutableLiveData<List<LiteAvUserBean>> liteAvUserList = new MutableLiveData<>();
    public MutableLiveData<Boolean> loginRoomSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> publishSuccess = new MutableLiveData<>();

    /**
     * 获取用户自身好友，粉丝，
     */
    public void getMyAtList(int page ,int pageSize) {
        LiteAvRepository.getInstance().getMyAtList(requestSuccess, liteAvUserList, page, pageSize);
    }

    public List<SaveDraftBean> getDraftsList(Context context , String fileName) {
        return LiteAvRepository.getInstance().getDraftsList(context, fileName);
    }
    public SaveDraftBean getDraftsById(Context context , String fileName , long id) {
        return LiteAvRepository.getInstance().getDraftsById(context, fileName, id);
    }
    public void saveDraftsById(Context context , String fileName ,  SaveDraftBean saveDraftBean) {
        LiteAvRepository.getInstance().saveDraftsById(context,fileName,saveDraftBean);
    }


    public void toLoginRoom(MLVBLiveRoom mLiveRoom) {
        LoginInfo loginInfo = new LoginInfo();
        loginInfo.userAvatar = UserInfoMgr.getInstance().getUserInfo().getAvatarUrl();
        loginInfo.userName = UserInfoMgr.getInstance().getUserInfo().getNickname();
        loginInfo.sdkAppID = 1400392607;
        loginInfo.userID = UserInfoMgr.getInstance().getUserInfo().getId();
        //3887
        //loginInfo.userSig = "eJw9Tk0LgjAY-i87h73bcptCh4i6GJQkVtBF2ooXWzodEUT-vaHR8fl*3qTY7CPzarEzJGVCqhnAZCCfpiMpYRGQEfe6rtoWNUlp8PCECZCjgto8PF5xCFCuBOVScZAJxJxT9i-AW9CbjLv8mDXnael7BdXF2*RgEXPn3L3EYh1vdyfWrBZ2Wc9-SY82vKOCAVNhXHy*YwYz4w__";
        //9104
        loginInfo.userSig = "eJw1jksLgkAUhf-LbAu796rzEFq0skhpYZuijTQzNfhAVCqM-nuitTyP73De7Jhknnk1rjUsIi5kALCczIdpWcTIAzbrThd50zjNIhw7viIOYk6cNnXvrJsA9GUohSJAJQmlCrn8D7jbmJeiM5bulKS5tefhuSvTy8pmC73dxIe6ug6tGPbqFId1Aesf2btqfIecgGOgBH6*YVgzug__";
        //        loginInfo.userSig = "eJw1jl0LgjAYhf-Lbg3b3s1tCl0GFoKI4UV0I23GW5YyJY3ovydal*fjOZw3OSS5b8cWnSURSKUFpavZfFpHIgI*JYvuzK1sWzQkYlOHhyCpWhI09tFjhTPAuJagBedKCBUoJgL5H8DLlB-TrbuWYTDkXTo0XjHGtD6tsXbsldEz7eKs8sTOmaTYN5sf2eN9esckUNAAQn**VREzgA__";
        mLiveRoom.login(loginInfo, new IMLVBLiveRoomListener.LoginCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                LogUtils.v(Constant.TAG_LIVE, "LiveRoom登录失败：" + errCode);
                LogUtils.v(Constant.TAG_LIVE, "LiveRoom登录失败：" + errInfo);
            }

            @Override
            public void onSuccess() {
                LogUtils.v(Constant.TAG_LIVE, "LiveRoom登录成功");
                loginRoomSuccess.postValue(true);
            }
        });
    }

    /**
     * 向自己服务器上传
     * @param videoId
     * @param videoURL
     * @param coverURL
     */
    public void UploadUGCVideo(final String videoId, final String videoURL, final String coverURL){
        String title = null; //TODO:传入本地视频文件名称
        if (TextUtils.isEmpty(title)) {
            title = "小视频";
        }
        LogReport.getInstance().uploadLogs(LogReport.ELK_ACTION_VIDEO_UPLOAD_SERVER, TCUserMgr.SUCCESS_CODE, "UploadUGCVideo Sucess");
        publishSuccess.postValue(true);
        //TODO 发布视频
        /*try {
            JSONObject body = new JSONObject().put("file_id", videoId)
                    .put("title", title)
                    .put("frontcover", coverURL)
                    .put("location", "未知")
                    .put("play_url", videoURL);
            TCUserMgr.getInstance().request("/upload_ugc", body, new TCUserMgr.HttpCallback("upload_ugc", new TCUserMgr.Callback() {
                @Override
                public void onSuccess(JSONObject data) {
                    Log.e("TAG", data.toString());

                    *//**
                     * ELK上报：发布视频到服务器
                     *//*
                    LogReport.getInstance().uploadLogs(LogReport.ELK_ACTION_VIDEO_UPLOAD_SERVER, TCUserMgr.SUCCESS_CODE, "UploadUGCVideo Sucess");
                    publishSuccess.postValue(true);
                }

                @Override
                public void onFailure(int code, final String msg) {
                    *//**
                     * ELK上报：发布视频到服务器
                     *//*
                    LogReport.getInstance().uploadLogs(LogReport.ELK_ACTION_VIDEO_UPLOAD_SERVER, code, msg);
                    publishSuccess.postValue(false);
                }
            }));
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
    }
}
