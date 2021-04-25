package com.xaqinren.healthyelders.moduleMine.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.apiserver.UserRepository;
import com.xaqinren.healthyelders.bean.UserInfo;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleMine.bean.MinePageInfo;

import me.goldze.mvvmhabit.base.BaseViewModel;

public class MineViewModel extends BaseViewModel {
    public MutableLiveData<UserInfo> userInfo = new MutableLiveData<>();
    private UserRepository userRepository = UserRepository.getInstance();

    public MineViewModel(@NonNull Application application) {
        super(application);
    }

    public void getUserInfo(String token) {
        //        MinePageInfo minePageInfo = new MinePageInfo();
        //        minePageInfo.nickname = "桃几。";
        //        minePageInfo.headPortrait = "https://img1.baidu.com/it/u=2062164223,3783917881&fm=26&fmt=auto&gp=0.jpg";
        //        minePageInfo.sex = 1;
        //        minePageInfo.birthplace = "西安市";
        //        minePageInfo.hobby = "音乐";
        //        userInfo.setValue(minePageInfo);
        userRepository.getUserInfo(userInfo,Constant.API_HEADER + token);
    }
}
