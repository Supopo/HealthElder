package com.xaqinren.healthyelders.moduleMall.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.apiserver.MallRepository;
import com.xaqinren.healthyelders.moduleHome.bean.MenuBean;
import com.xaqinren.healthyelders.moduleMall.bean.MallMenuRes;
import com.xaqinren.healthyelders.moduleZhiBo.bean.GoodsBean;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;

/**
 * Created by Lee. on 2021/5/25.
 */
public class MallViewModel extends BaseViewModel {
    public MallViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Boolean> dismissDialog = new MutableLiveData<>();
    public MutableLiveData<List<MenuBean>> menu3 = new MutableLiveData<>();
    public MutableLiveData<MallMenuRes> mallMenuRes = new MutableLiveData<>();

    public void getMenuInfo() {
        MallRepository.getInstance().getMallMenuInfo(mallMenuRes);
    }

    public void getMenuType() {
        MallRepository.getInstance().getMallTypeInfo(menu3);
    }

}
