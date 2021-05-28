package com.xaqinren.healthyelders.moduleHome.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.apiserver.MallRepository;
import com.xaqinren.healthyelders.moduleHome.bean.SearchBean;

import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;


public class SearchViewModel extends BaseViewModel {
    public SearchViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<List<SearchBean>> searchHistoryList = new MutableLiveData<>();
    public MutableLiveData<List<SearchBean>> searchHotList = new MutableLiveData<>();

    public void getHotList() {
        MallRepository.getInstance().getHotWords(searchHotList);
    }

}
