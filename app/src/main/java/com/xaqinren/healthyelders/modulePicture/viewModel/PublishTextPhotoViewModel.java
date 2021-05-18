package com.xaqinren.healthyelders.modulePicture.viewModel;

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
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.IMLVBLiveRoomListener;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.MLVBLiveRoom;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.roomutil.commondef.LoginInfo;
import com.xaqinren.healthyelders.utils.LogUtils;

import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;

public class PublishTextPhotoViewModel extends BaseViewModel {
    public PublishTextPhotoViewModel(@NonNull Application application) {
        super(application);
    }
    public MutableLiveData<Boolean> requestSuccess = new MutableLiveData<>();

    public MutableLiveData<List<LiteAvUserBean>> liteAvUserList = new MutableLiveData<>();
    public MutableLiveData<List<TopicBean>> topicList = new MutableLiveData<>();
    public MutableLiveData<List<TopicBean>> topicSearchList = new MutableLiveData<>();

    public MutableLiveData<List<String>> uploadFile = new MutableLiveData<>();

    public MutableLiveData<String> publishLiveData = new MutableLiveData<>();
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

    /**
     * 草稿箱
     * @param context
     * @param fileName
     * @return
     */
    public List<SaveDraftBean> getDraftsList(Context context , String fileName) {
        return LiteAvRepository.getInstance().getDraftsList(context, fileName);
    }
    public SaveDraftBean getDraftsById(Context context , String fileName , long id) {
        return LiteAvRepository.getInstance().getDraftsById(context, fileName, id);
    }
    public void saveDraftsById(Context context , String fileName , SaveDraftBean saveDraftBean) {
        LiteAvRepository.getInstance().saveDraftsById(context,fileName,saveDraftBean);
    }
    public void delDraftsById(Context context, String fileName, long publishDraftId) {
        LiteAvRepository.getInstance().delDraftsById(context, fileName, publishDraftId);
    }

    /**
     * 向自己服务器上传
     */
    public void UploadUGCVideo(PublishBean bean){

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
    public void uploadFile(List<String> files) {
        LiteAvRepository.getInstance().updatePhoto(requestSuccess , uploadFile , files);
    }

    /**
     * 发布
     */
    public void publish(com.xaqinren.healthyelders.modulePicture.bean.PublishBean publishBean) {
        LiteAvRepository.getInstance().publishTextPhoto(requestSuccess , publishLiveData , publishBean);
    }
}
