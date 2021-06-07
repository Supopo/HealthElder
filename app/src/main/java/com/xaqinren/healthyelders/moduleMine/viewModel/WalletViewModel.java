package com.xaqinren.healthyelders.moduleMine.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.apiserver.UserRepository;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.moduleLogin.bean.UserInfoBean;
import com.xaqinren.healthyelders.moduleMine.bean.BillBean;
import com.xaqinren.healthyelders.moduleMine.bean.BillDetailBean;
import com.xaqinren.healthyelders.moduleMine.bean.BillRecodeBean;
import com.xaqinren.healthyelders.moduleMine.bean.WalletBean;

import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;

public class WalletViewModel extends BaseViewModel {
    public MutableLiveData<WalletBean> wallet = new MutableLiveData<>();
    public MutableLiveData<Boolean> request = new MutableLiveData<>();
    public MutableLiveData<UserInfoBean> userInfo = new MutableLiveData<>();
    public MutableLiveData<BillRecodeBean> billList = new MutableLiveData<>();

    public MutableLiveData<BillDetailBean> billDetail = new MutableLiveData<>();

    public MutableLiveData<Boolean> appeal = new MutableLiveData<>();

    public WalletViewModel(@NonNull Application application) {
        super(application);
    }

    public void getWalletInfo() {
        UserRepository.getInstance().getWalletInfo(request,wallet);
    }

    public void getBillInfo(String key) {
        UserRepository.getInstance().getBillInfo(request, billList, key);
    }

    public void getBillInfoDetail(String key) {
        UserRepository.getInstance().getBillInfoDetail(request, billDetail, key);
    }

    public void refreshUserInfo() {
        UserRepository.getInstance().getUserInfo(request,userInfo, UserInfoMgr.getInstance().getAccessToken());
    }
    public void getWithdraw( double appealAmount,
                             String accountBank,
                             String accountName,
                             String accountNo,
                             String cardType) {
        UserRepository.getInstance().getWithdraw(request,appeal,appealAmount,accountBank,accountName,accountNo,cardType);
    }
}
