package com.xaqinren.healthyelders.moduleMsg.activity;

import android.Manifest;
import android.graphics.Bitmap;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.ViewUtils;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.L;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.tencent.bugly.proguard.O;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.qcloud.tim.uikit.modules.chat.base.ChatInfo;
import com.tencent.qcloud.tim.uikit.utils.ToastUtil;
import com.tencent.qcloud.ugckit.utils.ScreenUtils;
import com.tencent.weibo.sdk.android.api.adapter.FriendAdapter;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivityAddFriendBinding;
import com.xaqinren.healthyelders.databinding.HeaderAddFriendBinding;
import com.xaqinren.healthyelders.global.AppApplication;
import com.xaqinren.healthyelders.impl.TextWatcherImpl;
import com.xaqinren.healthyelders.moduleHome.bean.ShareBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.LiteAvUserBean;
import com.xaqinren.healthyelders.moduleMine.activity.UserInfoActivity;
import com.xaqinren.healthyelders.moduleMsg.adapter.AddFriendAdapter;
import com.xaqinren.healthyelders.moduleMsg.bean.FriendBean;
import com.xaqinren.healthyelders.moduleMsg.bean.InteractiveBean;
import com.xaqinren.healthyelders.moduleMsg.bean.MessageDetailBean;
import com.xaqinren.healthyelders.moduleMsg.viewModel.AddFriendViewModel;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ListPopMenuBean;
import com.xaqinren.healthyelders.utils.IntentUtils;
import com.xaqinren.healthyelders.utils.LogUtils;
import com.xaqinren.healthyelders.utils.UrlUtils;
import com.xaqinren.healthyelders.widget.ListBottomPopup;

import java.util.ArrayList;
import java.util.List;

import io.dcloud.feature.uniapp.common.UniModule;
import io.dcloud.feature.uniapp.ui.component.UniComponent;
import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.utils.PermissionUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;
import razerdp.basepopup.BasePopupWindow;

public class AddFriendActivity extends BaseActivity<ActivityAddFriendBinding, AddFriendViewModel> {
    private AddFriendAdapter addFriendAdapter;
    private List<FriendBean> friendBeans;
    private List<FriendBean> searchFriendBeans;
    private String TAG = getClass().getSimpleName();
    private int currentScroll;
    private int opIndex;

    private int sPage = 1;
    private int sPageSize = 10;
    private int showType = 1;

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
        searchFriendBeans = new ArrayList<>();
        addFriendAdapter = new AddFriendAdapter(R.layout.item_msg_add_friend);
        addFriendAdapter.setList(friendBeans);
        addFriendAdapter.addHeaderView(View.inflate(this, R.layout.header_empty_148dp, null));
        binding.content.setLayoutManager(new LinearLayoutManager(this));
        binding.content.setAdapter(addFriendAdapter);
        showDialog();
        viewModel.getRecommendFriend();
        initHeader();
        binding.content.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentScroll -= dy;
                LogUtils.e(TAG, "滑动距离 -> " + dy);
                binding.includeHeader.rlHeaderLayout.setTranslationY(currentScroll);
            }
        });
        binding.includeHeader.searchEt.addTextChangedListener(new TextWatcherImpl() {
            @Override
            public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
                if (editable.length() == 0) {
                    //展示默认
                    showType = 1;
                    addFriendAdapter.setList(friendBeans);
                    addFriendAdapter.getLoadMoreModule().loadMoreEnd(false);
                } else {
                    //搜索
                    sPage = 1;
                    showType = 2;
                    viewModel.searchUserList(sPage, sPageSize, editable.toString());
                }
            }
        });
        addFriendAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            opIndex = position;
            FriendBean friendBean = addFriendAdapter.getData().get(position);
            if (view.getId() == R.id.avatar) {
                if (friendBean.getIdentity() == null) {
                    //不是平台用户
                    ToastUtils.showShort("该用户还不是平台用户,快分享给他(她)吧!");
                    return;
                }
                UserInfoActivity.startActivity(this, friendBean.getUserId());
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
        addFriendAdapter.getLoadMoreModule().setEnableLoadMore(true);
        addFriendAdapter.getLoadMoreModule().setAutoLoadMore(true);
        addFriendAdapter.getLoadMoreModule().setOnLoadMoreListener(() -> {
            if (showType == 2) {
                viewModel.searchUserList(sPage, sPageSize, binding.includeHeader.searchEt.getText().toString());
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
                    IntentUtils.sendSMS(AddFriendActivity.this, friendBean.getMobileNumber(), friendBean.getShare().getSmsTemplate());
                } else if (position == 1) {
                    //wx分享
                    ShareBean shareBean = new ShareBean();
                    shareBean.url = friendBean.getShare().getUrl();
                    shareBean.title = friendBean.getShare().getTitle();
                    shareBean.subTitle = friendBean.getShare().getIntroduce();
                    shareWebPage(shareBean);
                }
                listBottomPopup.dismiss();
            }
        });
        listBottomPopup.setOnDismissListener(new BasePopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                ScreenUtils.setWindowAlpha(getContext(), 0.6f, 1.0f, 200);
            }
        });
        listBottomPopup.showPopupWindow();
        ScreenUtils.setWindowAlpha(getContext(), 1.0f, 0.6f, 400);
    }

    /*
     * 分享链接
     */
    private void shareWebPage(ShareBean shareBean) {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = shareBean.url;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = shareBean.title;
        msg.description = shareBean.subTitle;

        Glide.with(getContext()).asBitmap().load(R.mipmap.icon_app).into(new SimpleTarget<Bitmap>(100, 100) {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                msg.setThumbImage(resource);

                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.message = msg;
                req.scene = SendMessageToWX.Req.WXSceneSession;
                AppApplication.mWXapi.sendReq(req);
            }
        });
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
        viewModel.friendLiveData.observe(this, list -> {
            friendBeans.addAll(list);
            addFriendAdapter.setList(friendBeans);
            addFriendAdapter.getLoadMoreModule().loadMoreEnd(false);
        });
        viewModel.flow.observe(this, bool -> {
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
            } else if (friendBean.getIdentity().equals(AddFriendAdapter.FOLLOW)) {
                //关注的人
                friendBean.setIdentity(AddFriendAdapter.STRANGER);
            }
            addFriendAdapter.notifyItemChanged(opIndex + 1);
        });
        viewModel.liteAvUserList.observe(this, liteAvUserBeans -> {
            if (liteAvUserBeans == null) {
                return;
            }

            if (sPage == 1) {
                addFriendAdapter.getData().clear();
                addFriendAdapter.getLoadMoreModule().loadMoreComplete();
            } else {
                if (liteAvUserBeans.size() == 0) {
                    addFriendAdapter.getLoadMoreModule().loadMoreEnd(false);
                }
            }

            if (liteAvUserBeans.size() > 0) {
                sPage++;
            }
            List<FriendBean> list = new ArrayList<>();
            for (LiteAvUserBean bean : liteAvUserBeans) {
                FriendBean friendBean = new FriendBean();
                friendBean.setIdentity(bean.getIdentity());
                friendBean.setId(bean.getId());
                friendBean.setAvatarUrl(bean.getAvatar());
                friendBean.setNickname(bean.getName());
                friendBean.setName(null);
                friendBean.setUserId(bean.getId());
                list.add(friendBean);
            }

            addFriendAdapter.addData(list);
        });
    }


    private void initHeader() {
        binding.includeHeader.searchEt.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                //搜索

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
        if (granted) {
            startActivity(ContactsActivity.class);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 600);
        } else {
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
            } else {
                startActivity(ContactsActivity.class);
            }
        }
    }
}
