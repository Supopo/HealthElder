package com.xaqinren.healthyelders.moduleLogin.viewModel;

import android.app.Application;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.alibaba.fastjson.JSON;
import com.xaqinren.healthyelders.MainActivity;
import com.xaqinren.healthyelders.apiserver.ApiServer;
import com.xaqinren.healthyelders.apiserver.CustomObserver;
import com.xaqinren.healthyelders.apiserver.MBaseResponse;
import com.xaqinren.healthyelders.apiserver.UserRepository;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.http.RetrofitClient;
import com.xaqinren.healthyelders.moduleLogin.activity.PhoneLoginActivity;
import com.xaqinren.healthyelders.moduleLogin.bean.LoginTokenBean;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.utils.SPUtils;
import me.goldze.mvvmhabit.utils.StringUtils;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class PhoneLoginViewModel extends BaseViewModel {
    public PhoneLoginViewModel(@NonNull Application application) {
        super(application);
    }

    private UserRepository userRepository = UserRepository.getInstance();
    public MutableLiveData<Boolean> loginSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> smdStatus = new MutableLiveData<>();
    public void loginByPhone(String phone, String code, String openId) {
        userRepository.loginByPhone(loginSuccess, phone, code, openId);
    }
    public void sendSms(String phone) {
        userRepository.sendLoginSms(smdStatus,phone);
    }

}
