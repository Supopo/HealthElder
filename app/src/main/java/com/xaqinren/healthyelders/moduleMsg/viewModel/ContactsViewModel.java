package com.xaqinren.healthyelders.moduleMsg.viewModel;

import android.app.Application;
import android.database.Cursor;
import android.provider.ContactsContract;

import androidx.annotation.NonNull;

import com.xaqinren.healthyelders.moduleMsg.bean.ContactsBean;

import me.goldze.mvvmhabit.base.BaseViewModel;

public class ContactsViewModel extends BaseViewModel {
    public ContactsViewModel(@NonNull Application application) {
        super(application);
    }


}
