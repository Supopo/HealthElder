package com.xaqinren.healthyelders.moduleMsg.activity;

import android.Manifest;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.ViewUtils;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.L;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.tencent.qcloud.tim.uikit.utils.ToastUtil;
import com.tencent.weibo.sdk.android.api.adapter.FriendAdapter;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivityAddFriendBinding;
import com.xaqinren.healthyelders.databinding.HeaderAddFriendBinding;
import com.xaqinren.healthyelders.moduleMsg.adapter.AddFriendAdapter;
import com.xaqinren.healthyelders.moduleMsg.bean.FriendBean;
import com.xaqinren.healthyelders.moduleMsg.viewModel.AddFriendViewModel;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ListPopMenuBean;
import com.xaqinren.healthyelders.utils.IntentUtils;
import com.xaqinren.healthyelders.utils.LogUtils;
import com.xaqinren.healthyelders.widget.ListBottomPopup;
import com.xaqinren.healthyelders.widget.share.ShareDialog;

import java.util.ArrayList;
import java.util.List;

import io.dcloud.feature.uniapp.common.UniModule;
import io.dcloud.feature.uniapp.ui.component.UniComponent;
import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.utils.PermissionUtils;

public class AddFriendActivity extends BaseActivity<ActivityAddFriendBinding, AddFriendViewModel> {
    private AddFriendAdapter addFriendAdapter;
    private List<FriendBean> friendBeans;
    private String TAG = getClass().getSimpleName();
    private int currentScroll;
    private int opIndex;

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
        addFriendAdapter.addHeaderView(View.inflate(this, R.layout.header_empty_148dp, null));
        binding.content.setLayoutManager(new LinearLayoutManager(this));
        binding.content.setAdapter(addFriendAdapter);
        viewModel.getRecommendFriend();
        initHeader();
        binding.content.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentScroll -= dy;
                LogUtils.e(TAG, "滑动距离 -> "+dy);
                binding.includeHeader.rlHeaderLayout.setTranslationY(currentScroll);
            }
        });
        addFriendAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            opIndex = position;
            FriendBean friendBean = addFriendAdapter.getData().get(position);
            if (view.getId() == R.id.avatar) {
                //用户详情
            } else if (view.getId() == R.id.favorite) {
                //关注，回关
                if (friendBean.getIdentity() == null) {
                    //分享
                    showShareType(friendBean);
                    return;
                }
                showDialog();
                viewModel.recommendFriend(friendBean.getId());
            } else if (view.getId() == R.id.close) {
                adapter.removeAt(position);
            }
        });
    }

    private void showShareType(FriendBean friendBean) {
        List<ListPopMenuBean> menus = new ArrayList<>();
        menus.add(new ListPopMenuBean("短信"));
        menus.add(new ListPopMenuBean("微信分享"));
        ListBottomPopup listBottomPopup = new ListBottomPopup(this, menus);
        listBottomPopup.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                if (position == 0) {
                    //短信
                    IntentUtils.sendSMS(AddFriendActivity.this, friendBean.getMobileNumber(), "123456");
                } else if (position == 1) {
                    //wx分享
                    ShareDialog shareDialog = new ShareDialog(AddFriendActivity.this);
                }
            }
        });
        listBottomPopup.showPopupWindow();
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.request.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                dismissDialog();
            }
        });
        viewModel.friendLiveData.observe(this,list->{
            friendBeans.addAll(list);
            addFriendAdapter.setList(friendBeans);
        });
        viewModel.flow.observe(this,bool->{
            FriendBean friendBean = addFriendAdapter.getData().get(opIndex);
            if (friendBean.getIdentity().equals(AddFriendAdapter.STRANGER)) {
                //陌生人
                friendBean.setIdentity(AddFriendAdapter.ATTENTION);
            } else if (friendBean.getIdentity().equals(AddFriendAdapter.FANS)) {
                //粉丝
                friendBean.setIdentity(AddFriendAdapter.FRIEND);
            } else if (friendBean.getIdentity().equals(AddFriendAdapter.ATTENTION)) {
                //关注的人
                friendBean.setIdentity(AddFriendAdapter.STRANGER);
            } else if (friendBean.getIdentity().equals(AddFriendAdapter.FRIEND)) {
                //朋友
                friendBean.setIdentity(AddFriendAdapter.FANS);
            }  else if (friendBean.getIdentity().equals(AddFriendAdapter.FOLLOW)) {
                //关注的人
                friendBean.setIdentity(AddFriendAdapter.STRANGER);
            }
            addFriendAdapter.notifyItemChanged(opIndex + 1);
        });
    }


    private void initHeader() {
        binding.includeHeader.searchEt.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                return true;
            }
            return false;
        });
        binding.includeHeader.contactsLayout.setOnClickListener(view1 -> {
            checkPermission();
        });
        binding.includeHeader.scanIv.setOnClickListener(view1 -> {
            //TODO 扫码
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
