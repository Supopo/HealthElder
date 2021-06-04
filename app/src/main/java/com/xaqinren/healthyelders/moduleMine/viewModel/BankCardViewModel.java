package com.xaqinren.healthyelders.moduleMine.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.moduleMine.bean.BankCardBean;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;

public class BankCardViewModel extends BaseViewModel {
    public MutableLiveData<Void> request = new MutableLiveData();
    public MutableLiveData<List<BankCardBean>> bankCardList = new MutableLiveData();
    public BankCardViewModel(@NonNull Application application) {
        super(application);
    }

    public void getBackCard() {
        List<BankCardBean> list = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            list.add(new BankCardBean());
        }
        bankCardList.postValue(list);
    }
}
