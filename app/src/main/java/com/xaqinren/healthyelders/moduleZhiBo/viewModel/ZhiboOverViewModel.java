package com.xaqinren.healthyelders.moduleZhiBo.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.alibaba.fastjson.JSON;
import com.xaqinren.healthyelders.apiserver.LiveRepository;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveOverInfo;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ZhiboUserBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;

public class ZhiboOverViewModel extends BaseViewModel {

    public MutableLiveData<List<ZhiboUserBean>> dataList = new MutableLiveData<>();
    public MutableLiveData<LiveOverInfo> liveOverInfo = new MutableLiveData<>();
    public MutableLiveData<Boolean> dismissDialog = new MutableLiveData<>();
    public MutableLiveData<Boolean> commitSuccess = new MutableLiveData<>();

    public ZhiboOverViewModel(@NonNull Application application) {
        super(application);
    }

    //获取直播结束榜单用户列表
    public void getTopUserList() {
        List<ZhiboUserBean> objects = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            ZhiboUserBean zhiboUserBean = new ZhiboUserBean();
            zhiboUserBean.nickname = "用户" + i;
            objects.add(zhiboUserBean);
        }
        dataList.setValue(objects);
    }

    public void getLiveOverInfo(String liveRoomRecordId) {
        LiveRepository.getInstance().liveOverInfo(dismissDialog, liveOverInfo, liveRoomRecordId);
    }


    public void feedbackSave(String feedbackDesc,String liveRoomId,String liveRoomRecordId){
        HashMap<String, Object> hashMap1 = new HashMap<>();
        HashMap<String, Object> hashMap2 = new HashMap<>();
        hashMap2.put("liveRoomId", liveRoomId);
        hashMap2.put("liveRoomRecordId", liveRoomRecordId);
        hashMap1.put("feedbackType", "LIVE_ANCHOR");
        hashMap1.put("feedbackDesc", feedbackDesc);
        hashMap1.put("feedbackTitle", "直播间反馈");
        hashMap1.put("extJson", hashMap1);

        String json = JSON.toJSONString(hashMap1);

        LiveRepository.getInstance().feedbackSave(dismissDialog,commitSuccess,json);
    }

}
