package com.xaqinren.healthyelders.moduleLiteav.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.apiserver.LiteAvRepository;
import com.xaqinren.healthyelders.moduleLiteav.bean.LiteAvUserBean;

import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;

public class ChooseUnLookViewModel extends BaseViewModel {

    public MutableLiveData<Boolean> requestSuccess = new MutableLiveData<>();

    public MutableLiveData<List<LiteAvUserBean>> userList = new MutableLiveData<>();
    public MutableLiveData<List<LiteAvUserBean>> searchUserList = new MutableLiveData<>();

    public ChooseUnLookViewModel(@NonNull Application application) {
        super(application);
    }


    public void getUserList(int page, int pageSize, String identity) {
        LiteAvRepository.getInstance().getUserList(requestSuccess, userList, page, pageSize, identity);
    }

    public void searchUserList(int page, int pageSize, String identity) {
        LiteAvRepository.getInstance().getSearchUserList(requestSuccess, searchUserList, page, pageSize, identity);
    }

}
