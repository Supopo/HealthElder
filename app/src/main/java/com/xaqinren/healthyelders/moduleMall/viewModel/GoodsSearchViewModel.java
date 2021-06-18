package com.xaqinren.healthyelders.moduleMall.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.apiserver.MallRepository;
import com.xaqinren.healthyelders.moduleMall.bean.GoodsItemBean;
import com.xaqinren.healthyelders.moduleZhiBo.bean.GoodsBean;

import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;

public class GoodsSearchViewModel extends BaseViewModel {
    public MutableLiveData<List<GoodsBean>> goodsLiveData = new MutableLiveData<>();
    public MutableLiveData<Boolean> dismissDialog = new MutableLiveData<>();
    public GoodsSearchViewModel(@NonNull Application application) {
        super(application);
    }

    public void getGoods(String key,int page ,int pageSize ,String sortBy ,String orderBy ,String category){
        MallRepository.getInstance().getGoodsList(dismissDialog, goodsLiveData, page, pageSize, key, sortBy, orderBy, category);
    }
}
