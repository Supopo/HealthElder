package com.xaqinren.healthyelders.moduleZhiBo.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.moduleZhiBo.bean.GoodsBean;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;

public class CZPayViewModel extends BaseViewModel {

    public MutableLiveData<List<GoodsBean>> dataList = new MutableLiveData<>();

    public CZPayViewModel(@NonNull Application application) {
        super(application);
    }

}
