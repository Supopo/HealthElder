package com.xaqinren.healthyelders.moduleHome.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.xaqinren.healthyelders.apiserver.UserRepository;
import com.xaqinren.healthyelders.moduleHome.bean.GirlsBean;

import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;


public class GirlsViewModel extends BaseViewModel {
    public GirlsViewModel(@NonNull Application application) {
        super(application);
    }

    private UserRepository userRepository = UserRepository.getInstance();
    private MutableLiveData<Integer> girlsPage = new MutableLiveData<>();


    public LiveData<List<GirlsBean>> dataList = Transformations.switchMap(girlsPage, page ->
            userRepository.getGirls(page));


    //加载多页数据写法
    public void getDataList(int page) {
        girlsPage.setValue(page);
    }


    //加载单数据写法
    public LiveData<List<GirlsBean>> getData(){
        return  userRepository.getGirls(1);
    }
}
