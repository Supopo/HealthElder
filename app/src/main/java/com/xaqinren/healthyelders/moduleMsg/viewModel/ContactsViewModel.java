package com.xaqinren.healthyelders.moduleMsg.viewModel;

import android.app.Application;
import android.database.Cursor;
import android.provider.ContactsContract;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.alibaba.fastjson.JSON;
import com.tencent.bugly.proguard.B;
import com.xaqinren.healthyelders.apiserver.MsgRepository;
import com.xaqinren.healthyelders.global.AppApplication;
import com.xaqinren.healthyelders.moduleMsg.bean.ContactsBean;
import com.xaqinren.healthyelders.moduleMsg.bean.FriendBean;
import com.xaqinren.healthyelders.utils.ACache;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;

public class ContactsViewModel extends BaseViewModel {
    public MutableLiveData<Boolean> request = new MutableLiveData<>();
    public MutableLiveData<Boolean> liveData = new MutableLiveData<>();
    public String contactKey = "contact";
    public ContactsViewModel(@NonNull Application application) {
        super(application);
    }

    public void postContact(List<ContactsBean> data) {
        MsgRepository.getInstance().postUserContact(request, liveData, data);
    }

    public void saveNativeContact(List<ContactsBean> data) {
        ACache aCache = ACache.get(AppApplication.get());
        aCache.put(contactKey,JSON.toJSONString(data));
    }

    public List<ContactsBean> getNativeContact() {
        ACache aCache = ACache.get(AppApplication.get());
        String contact = aCache.getAsString(contactKey);
        List<ContactsBean> data = JSON.parseArray(contact, ContactsBean.class);
        return data == null ? new ArrayList<>() : data;
    }



}
