package com.xaqinren.healthyelders.moduleLiteav.viewModel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.tencent.qcloud.ugckit.utils.LogReport;
import com.tencent.qcloud.ugckit.utils.TCUserMgr;
import com.xaqinren.healthyelders.apiserver.LiteAvRepository;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleLiteav.bean.LiteAvUserBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.PublishBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.SaveDraftBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.TopicBean;
import com.xaqinren.healthyelders.moduleLogin.bean.UserInfoBean;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.IMLVBLiveRoomListener;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.MLVBLiveRoom;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.roomutil.commondef.LoginInfo;
import com.xaqinren.healthyelders.utils.LogUtils;

import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;

public class VideoPublishViewModel extends BaseViewModel {

    public VideoPublishViewModel(@NonNull Application application) {
        super(application);
    }
    public MutableLiveData<Boolean> requestSuccess = new MutableLiveData<>();

    public MutableLiveData<List<LiteAvUserBean>> liteAvUserList = new MutableLiveData<>();
    public MutableLiveData<List<TopicBean>> topicList = new MutableLiveData<>();
    public MutableLiveData<List<TopicBean>> topicSearchList = new MutableLiveData<>();
    public MutableLiveData<Object> publish = new MutableLiveData<>();
    public MutableLiveData<Boolean> loginRoomSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> publishSuccess = new MutableLiveData<>();
    public MutableLiveData<String> fileUpload = new MutableLiveData<>();

    /**
     * 获取用户自身好友，粉丝，
     */
    public void getMyAtList(int page ,int pageSize) {
        LiteAvRepository.getInstance().getMyAtList(requestSuccess, liteAvUserList, page, pageSize);
    }
    /**
     * 搜索用户
     */
    public void searchUserList(int page ,int pageSize,String key ) {
        LiteAvRepository.getInstance().getSearchUserList(requestSuccess, liteAvUserList, page, pageSize,key);
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

    public void delDraftsById(Context context, String fileName, long publishDraftId) {
        LiteAvRepository.getInstance().delDraftsById(context, fileName, publishDraftId);
    }


    public void toLoginRoom(MLVBLiveRoom mLiveRoom) {
        LoginInfo loginInfo = new LoginInfo();
        UserInfoBean userInfo = UserInfoMgr.getInstance().getUserInfo();
        if (userInfo == null) {
            return;
        }
        loginInfo.userAvatar = userInfo.getAvatarUrl();
        loginInfo.userName = userInfo.getNickname();
        loginInfo.sdkAppID = 1400392607;
        loginInfo.userID = userInfo.getId();
        loginInfo.userSig = UserInfoMgr.getInstance().getUserSig();
        loginInfo.userLevel = userInfo.getLevelName();
        loginInfo.userLevelIcon = userInfo.getLevelIcon();

        mLiveRoom.login(false,loginInfo, new IMLVBLiveRoomListener.LoginCallback() {
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
     */
    public void UploadUGCVideo(PublishBean bean){
        LogReport.getInstance().uploadLogs(LogReport.ELK_ACTION_VIDEO_UPLOAD_SERVER, TCUserMgr.SUCCESS_CODE, "UploadUGCVideo Sucess");
        //TODO 发布视频
        LiteAvRepository.getInstance().publishVideo(requestSuccess, publishSuccess, bean);
    }

    /**
     * 热点话题
     */
    public void getHotTopic() {
        LiteAvRepository.getInstance().getHotTopicList(requestSuccess, topicList);
    }

    /**
     * 搜索话题
     * @param key
     */
    public void getSearchTopic(String key) {
        LiteAvRepository.getInstance().getSearchTopicList(requestSuccess, topicSearchList, key);
    }

    /**
     * 发布多图
     * @param files
     */
    public void uploadFile(String files) {
        LiteAvRepository.getInstance().updatePhoto(requestSuccess , fileUpload , files);
    }
}
