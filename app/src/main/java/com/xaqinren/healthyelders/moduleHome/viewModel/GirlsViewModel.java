package com.xaqinren.healthyelders.moduleHome.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.apiserver.UserRepository;
import com.xaqinren.healthyelders.moduleHome.bean.GirlsBean;

import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;


public class GirlsViewModel extends BaseViewModel {
    public GirlsViewModel(@NonNull Application application) {
        super(application);
    }

    private UserRepository userRepository = UserRepository.getInstance();
    public MutableLiveData<List<GirlsBean>> dataList = new MutableLiveData<>();





    public void getDataList(int page) {
        userRepository.getGirls(this, page, 10);
    }

}
