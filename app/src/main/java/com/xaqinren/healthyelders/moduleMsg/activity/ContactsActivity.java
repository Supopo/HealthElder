package com.xaqinren.healthyelders.moduleMsg.activity;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;

import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.tencent.bugly.proguard.C;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivityContactsBinding;
import com.xaqinren.healthyelders.moduleMsg.adapter.ContactsAdapter;
import com.xaqinren.healthyelders.moduleMsg.bean.ContactsBean;
import com.xaqinren.healthyelders.moduleMsg.viewModel.ContactsViewModel;
import com.xaqinren.healthyelders.utils.LogUtils;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseActivity;

/**
 * 手机联系人列表
 */
public class ContactsActivity extends BaseActivity <ActivityContactsBinding, ContactsViewModel>{
    private String TAG = "ContactsActivity";
    //存放数据
    List<ContactsBean> contactsList = new ArrayList<>();
    private ContactsAdapter contactsAdapter;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_contacts;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        setTitle("手机通讯录");
        readContacts();
        contactsAdapter = new ContactsAdapter(R.layout.item_contacts);
        contactsAdapter.setList(contactsList);
        binding.content.setLayoutManager(new LinearLayoutManager(this));
        binding.content.setAdapter(contactsAdapter);
    }

    //调用并获取联系人信息
    private void readContacts() {
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String displayName = cursor.getString(cursor.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String number = cursor.getString(cursor.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.NUMBER));
                    ContactsBean bean = new ContactsBean();
                    bean.setName(displayName);
                    bean.setPhone(number);
                    contactsList.add(bean);
                    LogUtils.e(TAG, "displayName -> " + displayName + "\tnumber -> " + number);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
