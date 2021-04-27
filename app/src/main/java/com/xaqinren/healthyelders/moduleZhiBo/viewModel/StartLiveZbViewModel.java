package com.xaqinren.healthyelders.moduleZhiBo.viewModel;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.apiserver.ApiServer;
import com.xaqinren.healthyelders.apiserver.CustomObserver;
import com.xaqinren.healthyelders.apiserver.LiveRepository;
import com.xaqinren.healthyelders.apiserver.MBaseResponse;
import com.xaqinren.healthyelders.apiserver.UserRepository;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.http.RetrofitClient;
import com.xaqinren.healthyelders.moduleZhiBo.bean.GoodsBean;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveInitInfo;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.IMLVBLiveRoomListener;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.MLVBLiveRoom;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.roomutil.commondef.LoginInfo;
import com.xaqinren.healthyelders.utils.LogUtils;

import java.io.File;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.http.BaseResponse;
import me.goldze.mvvmhabit.utils.RxUtils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class StartLiveZbViewModel extends BaseViewModel {
    public StartLiveZbViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Boolean> dismissDialog = new MutableLiveData<>();
    public MutableLiveData<Boolean> loginRoomSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> exitSuccess = new MutableLiveData<>();
    public MutableLiveData<LiveInitInfo> liveInfo = new MutableLiveData<>();
    public MutableLiveData<String> fileUrl = new MutableLiveData<>();


    public void checkLiveInfo() {
        LiveRepository.getInstance().checkLiveInfo(dismissDialog, liveInfo);
    }

    public void closeLastLive(String liveRoomId) {
        LiveRepository.getInstance().overLive(dismissDialog, exitSuccess, liveRoomId);
    }

    public void updatePhoto(String filePath) {
        UserRepository.getInstance().updatePhoto(dismissDialog, fileUrl, filePath);
    }

}
