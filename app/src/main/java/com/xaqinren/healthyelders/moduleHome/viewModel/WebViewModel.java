package com.xaqinren.healthyelders.moduleHome.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;

import me.goldze.mvvmhabit.base.BaseViewModel;

public class WebViewModel extends BaseViewModel {


    public WebViewModel(@NonNull Application application) {
        super(application);
    }

    public void getDataList(int pageNum) {

        //        dataList.postValue(list);
        //        if (list.size() == 0) {
        //            //加载结束，没有更多数据了
        //            loadStatus.postValue(0);
        //        } else {
        //            //当前页数据加载完成
        //            loadStatus.postValue(1);
        //        }

    }

}
