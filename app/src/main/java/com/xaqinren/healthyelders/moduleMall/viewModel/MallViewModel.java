package com.xaqinren.healthyelders.moduleMall.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.moduleHome.bean.MenuBean;
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

    public MutableLiveData<List<GoodsBean>> datas = new MutableLiveData<>();
    public MutableLiveData<List<MenuBean>> menu1 = new MutableLiveData<>();
    public MutableLiveData<List<MenuBean>> menu2 = new MutableLiveData<>();
    public MutableLiveData<List<MenuBean>> menu3 = new MutableLiveData<>();

    public void getMallGoods() {
        List<GoodsBean> goodsBeans = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            GoodsBean goodsBean = new GoodsBean();
            goodsBeans.add(goodsBean);
        }
        datas.setValue(goodsBeans);
    }

    public void getMenu1List() {
        List<MenuBean> menuBeans = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            MenuBean menuBean = new MenuBean();
            menuBean.menuName = "菜单" + i;
            menuBeans.add(menuBean);
        }
        menu1.setValue(menuBeans);
    }

    public void getMenu2List() {
        List<MenuBean> menuBeans = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            MenuBean menuBean = new MenuBean();
            menuBean.menuName = "热门" + i;
            menuBeans.add(menuBean);
        }
        menu2.setValue(menuBeans);
    }

    public void getMenu3List() {
        List<MenuBean> menuBeans = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            MenuBean menuBean = new MenuBean();
            menuBean.menuName = "推荐" + i;
            menuBeans.add(menuBean);
        }
        menu3.setValue(menuBeans);
    }
}
