package com.xaqinren.healthyelders.moduleLiteav.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.apiserver.LiteAvRepository;
import com.xaqinren.healthyelders.moduleLiteav.bean.MMusicBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.MMusicItemBean;

import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;

public class MusicClassViewModel extends BaseViewModel {
    public MutableLiveData<Boolean> requestSuccess = new MutableLiveData<>();
    public MutableLiveData<List<MMusicItemBean>> musicListData = new MutableLiveData<>();

    public MusicClassViewModel(@NonNull Application application) {
        super(application);
    }


    public void getMusicList(String id, String name, int page, int pagesize) {
        LiteAvRepository.getInstance().getMusicList(id, name, page, pagesize, requestSuccess, musicListData);
    }


}
