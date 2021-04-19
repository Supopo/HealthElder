package com.xaqinren.healthyelders.moduleHome.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.moduleHome.bean.NewsBean;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;


public class NewsViewModel extends BaseViewModel {
    public MutableLiveData<List<NewsBean>> dataList = new MutableLiveData<>();

    //封装一个界面发生改变的观察者 其实duck不必 内部也是使用LiveData 还不如直接使用
    public UIChangeObservable uc = new UIChangeObservable();

    public class UIChangeObservable {
        //状态监听 比如刷新结束啊 等等
        public SingleLiveEvent someEvent = new SingleLiveEvent<>();
    }


    public NewsViewModel(@NonNull Application application) {
        super(application);
    }

    public void getDataList() {
        List<NewsBean> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add(new NewsBean("新闻标题" + i));
        }
        dataList.postValue(list);
        uc.someEvent.call();
    }

}
