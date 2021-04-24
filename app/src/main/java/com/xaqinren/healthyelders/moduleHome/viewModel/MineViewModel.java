package com.xaqinren.healthyelders.moduleHome.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.moduleHome.bean.MinePageInfo;

import me.goldze.mvvmhabit.base.BaseViewModel;

public class MineViewModel extends BaseViewModel {
    public MutableLiveData<MinePageInfo> userInfo = new MutableLiveData<>();

    public MineViewModel(@NonNull Application application) {
        super(application);
    }

    public void getUserInfo(){
        MinePageInfo minePageInfo = new MinePageInfo();
        minePageInfo.nickname = "桃几。";
        minePageInfo.headPortrait = "https://img1.baidu.com/it/u=2062164223,3783917881&fm=26&fmt=auto&gp=0.jpg";
        minePageInfo.sex = 1;
        minePageInfo.birthplace = "西安市";
        minePageInfo.hobby = "音乐";
        userInfo.setValue(minePageInfo);
    }
}
