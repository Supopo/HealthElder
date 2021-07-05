package com.xaqinren.healthyelders.moduleLiteav.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.apiserver.LiteAvRepository;
import com.xaqinren.healthyelders.bean.BaseListRes;
import com.xaqinren.healthyelders.moduleLiteav.bean.LiteAvUserBean;

import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;

public class ChooseUnLookViewModel extends BaseViewModel {

    public MutableLiveData<Boolean> requestSuccess = new MutableLiveData<>();

    public MutableLiveData<BaseListRes<List<LiteAvUserBean>>> userList = new MutableLiveData<>();
    public MutableLiveData<BaseListRes<List<LiteAvUserBean>>> filterList = new MutableLiveData<>();
    public MutableLiveData<BaseListRes<List<LiteAvUserBean>>> searchUserList = new MutableLiveData<>();

    public ChooseUnLookViewModel(@NonNull Application application) {
        super(application);
    }


    public void getUserList(int page, int pageSize, String identity) {
        LiteAvRepository.getInstance().getUserList(requestSuccess, page, pageSize, identity,userList);
    }

    public void filterUserList(int page, int pageSize, String identity) {
        LiteAvRepository.getInstance().getUserList(requestSuccess, page, pageSize, identity,filterList);
    }

    public void searchUserList(int page, int pageSize, String identity) {
        LiteAvRepository.getInstance().getSearchUserList(requestSuccess, page, pageSize, identity, searchUserList);
    }

}
