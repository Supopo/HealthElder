package com.xaqinren.healthyelders.moduleHome.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.moduleHome.bean.SearchBean;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;


public class SearchAllViewModel extends BaseViewModel {
    public SearchAllViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<List<SearchBean>> searchHistoryList = new MutableLiveData<>();
    public MutableLiveData<List<SearchBean>> searchHotList = new MutableLiveData<>();


}
