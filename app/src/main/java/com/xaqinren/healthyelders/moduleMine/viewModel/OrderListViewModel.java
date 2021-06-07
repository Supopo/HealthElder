package com.xaqinren.healthyelders.moduleMine.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.apiserver.UserRepository;
import com.xaqinren.healthyelders.moduleLogin.bean.UserInfoBean;
import com.xaqinren.healthyelders.moduleMine.bean.OrderListBean;

import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;

public class OrderListViewModel extends BaseViewModel {
    public MutableLiveData<Boolean> requestSuccess = new MutableLiveData<>();
    public MutableLiveData<List<OrderListBean>> orderList = new MutableLiveData<>();

    public MutableLiveData<Boolean> orderCancelStatus = new MutableLiveData<>();
    public MutableLiveData<Boolean> orderDelStatus = new MutableLiveData<>();
    public MutableLiveData<Boolean> orderReceiptStatus = new MutableLiveData<>();

    public MutableLiveData<UserInfoBean> userBanlance = new MutableLiveData<>();

    public OrderListViewModel(@NonNull Application application) {
        super(application);
    }

    public void getOrderList(int type , int page , int pageCount) {
        UserRepository.getInstance().getOrderList(requestSuccess, orderList, type, page);
    }

    public void cancelOrder(String orderId) {
        UserRepository.getInstance().cancelOrder(requestSuccess,orderCancelStatus,orderId);
    }
    public void delOrder(String orderId) {
        UserRepository.getInstance().delOrder(requestSuccess,orderDelStatus,orderId);
    }
    public void receiptOrder(String orderId) {
        UserRepository.getInstance().receiptOrder(requestSuccess,orderReceiptStatus,orderId);
    }
    public void getBalance() {
        UserRepository.getInstance().getBanlance(userBanlance);
    }
}
