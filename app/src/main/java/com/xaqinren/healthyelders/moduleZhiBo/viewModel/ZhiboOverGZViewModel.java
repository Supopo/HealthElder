package com.xaqinren.healthyelders.moduleZhiBo.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.apiserver.LiveRepository;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveOverInfo;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ZhiboUserBean;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;

public class ZhiboOverGZViewModel extends BaseViewModel {

    public MutableLiveData<Boolean> dismissDialog = new MutableLiveData<>();

    public ZhiboOverGZViewModel(@NonNull Application application) {
        super(application);
    }


}
