package com.xaqinren.healthyelders.moduleHome.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.xaqinren.healthyelders.apiserver.UserRepository;
import com.xaqinren.healthyelders.moduleHome.bean.GirlsBean;
import com.xaqinren.healthyelders.moduleHome.bean.SearchBean;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;


public class SearchViewModel extends BaseViewModel {
    public SearchViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<List<SearchBean>> searchHistoryList = new MutableLiveData<>();
    public MutableLiveData<List<SearchBean>> searchHotList = new MutableLiveData<>();

    public void getHistoryList() {
        List<SearchBean> list = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            SearchBean searchBean = new SearchBean();
            if (i == 0) {
                searchBean.content = "标签" + i;
            } else {
                searchBean.content = "标签" + i + list.get(i - 1).content;
            }
            list.add(searchBean);
        }
        searchHistoryList.setValue(list);
    }


    public void getHotList() {
        List<SearchBean> list = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            SearchBean searchBean = new SearchBean();
            searchBean.content = "热门" + i;
            list.add(searchBean);
        }
        searchHotList.setValue(list);
    }

}
