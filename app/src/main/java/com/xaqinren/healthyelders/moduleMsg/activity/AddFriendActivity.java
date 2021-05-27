package com.xaqinren.healthyelders.moduleMsg.activity;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.tencent.qcloud.tim.uikit.utils.ToastUtil;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivityAddFriendBinding;
import com.xaqinren.healthyelders.databinding.HeaderAddFriendBinding;
import com.xaqinren.healthyelders.moduleMsg.adapter.AddFriendAdapter;
import com.xaqinren.healthyelders.moduleMsg.bean.FriendBean;
import com.xaqinren.healthyelders.moduleMsg.viewModel.AddFriendViewModel;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.utils.PermissionUtils;

public class AddFriendActivity extends BaseActivity<ActivityAddFriendBinding, AddFriendViewModel> {
    private AddFriendAdapter addFriendAdapter;
    private List<FriendBean> friendBeans;
    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_add_friend;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        setTitle("添加朋友");
        friendBeans = new ArrayList<>();
        addFriendAdapter = new AddFriendAdapter(R.layout.item_msg_add_friend);
        addFriendAdapter.setList(friendBeans);
        binding.content.setLayoutManager(new LinearLayoutManager(this));
        binding.content.setAdapter(addFriendAdapter);
        //添加头布局
        addHeader();
        viewModel.getRecommendFriend();
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.friendLiveData.observe(this,list->{
            friendBeans.addAll(list);
            addFriendAdapter.setList(friendBeans);
        });
    }

    HeaderAddFriendBinding friendBinding;
    private void addHeader() {
        View view = View.inflate(this, R.layout.header_add_friend, null);
        addFriendAdapter.addHeaderView(view);
        friendBinding = DataBindingUtil.bind(view);
        friendBinding.searchEt.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH) {

                return true;
            }
            return false;
        });
        friendBinding.contactsLayout.setOnClickListener(view1 -> {
            checkPermission();
        });
        friendBinding.scanIv.setOnClickListener(view1 -> {
            //扫码
        });

    }

    private void checkPermission() {
        boolean granted = PermissionUtils.checkPermissionAllGranted(this, new String[]{Manifest.permission.READ_CONTACTS});
        if (granted){
            startActivity(ContactsActivity.class);
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},600);
        }else{
            startActivity(ContactsActivity.class);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 600) {
            boolean granted = PermissionUtils.checkPermissionAllGranted(this, new String[]{Manifest.permission.READ_CONTACTS});
            if (!granted) {
                ToastUtil.toastLongMessage("获取联系人权限失败");
            }else{
                startActivity(ContactsActivity.class);
            }
        }
    }
}
