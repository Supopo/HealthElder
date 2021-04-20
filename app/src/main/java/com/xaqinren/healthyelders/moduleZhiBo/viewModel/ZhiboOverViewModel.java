package com.xaqinren.healthyelders.moduleZhiBo.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.moduleZhiBo.bean.ZhiboUserBean;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;

public class ZhiboOverViewModel extends BaseViewModel {

    public MutableLiveData<List<ZhiboUserBean>> dataList = new MutableLiveData<>();

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

}
