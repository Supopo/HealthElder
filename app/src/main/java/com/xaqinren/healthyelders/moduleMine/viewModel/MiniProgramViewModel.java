package com.xaqinren.healthyelders.moduleMine.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.moduleMine.bean.MiniProgramBean;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;

public class MiniProgramViewModel extends BaseViewModel {
    public MutableLiveData<Boolean> requestSuccess = new MutableLiveData<>();
    public MutableLiveData<List<MiniProgramBean>> miniProgram = new MutableLiveData<>();
    public MutableLiveData<List<MiniProgramBean>> nativeProgram = new MutableLiveData<>();


    public MiniProgramViewModel(@NonNull Application application) {
        super(application);
    }

    public void getNative() {
        List<MiniProgramBean> programBeans = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            programBeans.add(new MiniProgramBean());
        }
        miniProgram.postValue(programBeans);
    }
    public void getRemote() {
        List<MiniProgramBean> programBeans = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            programBeans.add(new MiniProgramBean());
        }
        nativeProgram.postValue(programBeans);
    }
}
