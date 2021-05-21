package com.xaqinren.healthyelders.moduleLiteav.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.apiserver.LiteAvRepository;
import com.xaqinren.healthyelders.moduleLiteav.adapter.MusicClassAdapter;
import com.xaqinren.healthyelders.moduleLiteav.bean.MMusicBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.MusicClassBean;

import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;

public class ChooseMusicViewModel extends BaseViewModel {
    public MutableLiveData<Boolean> requestSuccess = new MutableLiveData<>();
    public MutableLiveData<List<MusicClassBean>> musicClassLiveData = new MutableLiveData<>();
    public MutableLiveData<List<MMusicBean>> musicChannelSheetData = new MutableLiveData<>();

    public ChooseMusicViewModel(@NonNull Application application) {
        super(application);
    }

    public void getMusicClass() {
        LiteAvRepository.getInstance().getMusicClass(requestSuccess, musicClassLiveData);
    }
    public void getMusicChannelSheet() {
        LiteAvRepository.getInstance().getMusicChannelSheet(requestSuccess, musicChannelSheetData);
    }
}
