package com.xaqinren.healthyelders.moduleMsg.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.moduleMsg.bean.FriendBean;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;

public class AddFriendViewModel extends BaseViewModel {
    public MutableLiveData<List<FriendBean>> friendLiveData = new MutableLiveData<>();

    public AddFriendViewModel(@NonNull Application application) {
        super(application);
    }

    public void getRecommendFriend() {
        List<FriendBean> friendBeans = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            friendBeans.add(new FriendBean());
        }
        friendLiveData.postValue(friendBeans);
    }
}
