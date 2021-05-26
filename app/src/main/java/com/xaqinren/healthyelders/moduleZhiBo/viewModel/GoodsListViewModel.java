package com.xaqinren.healthyelders.moduleZhiBo.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.moduleZhiBo.bean.GoodsBean;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;

public class GoodsListViewModel extends BaseViewModel {

    public MutableLiveData<List<GoodsBean>> dataList = new MutableLiveData<>();

    public GoodsListViewModel(@NonNull Application application) {
        super(application);
    }

    public void getDataList() {
        List<GoodsBean> objects = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            GoodsBean goodsBean = new GoodsBean();
            goodsBean.pos = i + 1;
            goodsBean.name = "商品" + i;
            objects.add(goodsBean);
        }
        dataList.setValue(objects);
    }

}
