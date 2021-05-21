package com.xaqinren.healthyelders.moduleZhiBo.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.apiserver.LiveRepository;
import com.xaqinren.healthyelders.apiserver.UserRepository;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveOverInfo;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ZhiboUserBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.goldze.mvvmhabit.base.BaseViewModel;

public class StartRenZheng2ViewModel extends BaseViewModel {

    public MutableLiveData<Boolean> dismissDialog = new MutableLiveData<>();
    public MutableLiveData<Boolean> renZhengSuccess = new MutableLiveData<>();

    public StartRenZheng2ViewModel(@NonNull Application application) {
        super(application);
    }

    public void toRenZheng(Map hashMap) {
        UserRepository.getInstance().toRenZheng(renZhengSuccess, dismissDialog, hashMap);
    }
}
