package com.xaqinren.healthyelders.moduleMine.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.tencent.qcloud.tim.uikit.utils.ToastUtil;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.FramentAttentionBinding;
import com.xaqinren.healthyelders.moduleLiteav.bean.LiteAvUserBean;
import com.xaqinren.healthyelders.moduleMine.activity.UserInfoActivity;
import com.xaqinren.healthyelders.moduleMine.adapter.AttentionAdapter;
import com.xaqinren.healthyelders.moduleMine.viewModel.AttentionViewModel;
import com.xaqinren.healthyelders.moduleMsg.adapter.AddFriendAdapter;
import com.xaqinren.healthyelders.moduleMsg.bean.FriendBean;
import com.xaqinren.healthyelders.moduleMsg.bean.InteractiveBean;
import com.xaqinren.healthyelders.moduleMsg.bean.MessageDetailBean;
import com.xaqinren.healthyelders.utils.GlideUtil;
import com.xaqinren.healthyelders.utils.LogUtils;

import java.util.List;

import me.goldze.mvvmhabit.base.BaseFragment;

/**
 *
 * 关注/粉丝
 */
public class AttentionFragment extends BaseFragment<FramentAttentionBinding, AttentionViewModel> {
    private int type;
    private AttentionAdapter adapter;
    private int currentScroll;
    private int opIndex;
    private String currentRequestType;

    private int currentPage = 1;
    private int pageSize = 10;
    private String uid;

    private LiteAvUserBean tempUserBean = null;
    private int tempUserIndex = -1;

    public AttentionFragment(int i, String uid) {
        super();
        this.type = i;
        if (type == 0) {
            currentRequestType = "FOLLOW";
        } else if (type == 1) {
            currentRequestType = "FANS";
        }
        this.uid = uid;
    }

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.frament_attention;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        adapter = new AttentionAdapter(R.layout.item_attention, type == 1 ? true : false);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);
        adapter.setEmptyView(R.layout.list_empty);
//        adapter.addHeaderView(View.inflate(getContext(), R.layout.header_empty_56dp, null));
        initHeader();
        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentScroll -= dy;
                binding.rlHead.setTranslationY(currentScroll);
            }
        });
        adapter.setOnItemChildClickListener((adapter, view, position) -> {
        });
        adapter.setOnItemChildClickListener((adapter, view, position) -> {
            LiteAvUserBean userBean = (LiteAvUserBean) adapter.getData().get(position);
            opIndex = position;
            switch (view.getId()) {
                case R.id.avatar: {
                    tempUserBean = userBean;
                    tempUserIndex = position;
                    Bundle bundle = new Bundle();
                    bundle.putString("userId", userBean.getId());
                    startActivity(UserInfoActivity.class,bundle);
                }break;
                case R.id.attention_btn:
                {
                    showDialog();
                    viewModel.recommendFriend(userBean.getId());
                }
                break;
                case R.id.close:{
                    showDelPop(userBean);
                }
                    break;
            }
        });
        showDialog();
        viewModel.getUserList(currentPage, pageSize, currentRequestType, uid);
        adapter.getLoadMoreModule().setEnableLoadMore(true);
        adapter.getLoadMoreModule().setAutoLoadMore(true);
        adapter.getLoadMoreModule().setOnLoadMoreListener(() -> {
            showDialog();
            viewModel.getUserList(currentPage, pageSize, currentRequestType, uid);
        });
        binding.swipeContent.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = 1;
                showDialog();
                viewModel.getUserList(currentPage, pageSize, currentRequestType, uid);
            }
        });
    }

    private void initHeader() {
        binding.searchEt.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                return true;
            }
            return false;
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (tempUserBean != null && tempUserIndex != -1) {
            //重新请求单个用户信息
            if (tempUserBean.attentionUserInfo != null) {
                viewModel.refreshUserList(tempUserBean.attentionUserInfo.nickname);
            }
        }
    }



    @Override
    public void initViewObservable() {
        super.initViewObservable();
        dismissDialog();
        viewModel.refreshUserList.observe(this, liteAvUserBeans -> {
            if (!liteAvUserBeans.isEmpty()) {
                if (tempUserBean != null && tempUserIndex != -1) {
                    //重新请求单个用户信息
                    if (tempUserBean.attentionUserInfo != null) {
                        String id = tempUserBean.getAttentionUserId();
                        for (LiteAvUserBean liteAvUserBean : liteAvUserBeans) {
                            if (id.equals(liteAvUserBean.getId())) {
                                tempUserBean.setIdentity(liteAvUserBean.identity);
                                adapter.notifyItemChanged(tempUserIndex);
                                tempUserIndex = -1;
                                break;
                            }
                        }
                    }
                }
            }
        });
        viewModel.requestSuccess.observe(this,a -> dismissDialog());
        viewModel.userList.observe(this, liteAvUserBeans -> {
            if (currentPage==1) adapter.getData().clear();
            adapter.addData(liteAvUserBeans);
            if (liteAvUserBeans.isEmpty()) {
                adapter.getLoadMoreModule().loadMoreEnd(false);
            }else{
                adapter.getLoadMoreModule().loadMoreComplete();
            }
            currentPage++;
            binding.swipeContent.setRefreshing(false);
        });
        viewModel.flow.observe(this, aBoolean -> {
                dismissDialog();
                LiteAvUserBean friendBean = (LiteAvUserBean) adapter.getData().get(opIndex);
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
            adapter.notifyItemChanged(opIndex,99);
        });
        viewModel.del.observe(this, aBoolean -> {
            if (aBoolean) {
                dialog.dismiss();
                adapter.getData().remove(opIndex);
                adapter.notifyItemChanged(opIndex + 1);
            }else{
                ToastUtil.toastShortMessage("解除失败,请重试");
            }
        });
    }

    private Dialog dialog;
    private void showDelPop(LiteAvUserBean userBean) {
        if (dialog == null) {
            dialog = new Dialog(getContext(), R.style.CustomerDialog);
            //填充对话框的布局
            View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_del_fans, null);
            //点击外部不可dismiss
            dialog.setCancelable(false);
            //将布局设置给Dialog
            dialog.setContentView(view);
            //获取当前Activity所在的窗体
            Window dialogWindow = dialog.getWindow();
            //设置Dialog从窗体底部弹出
            dialogWindow.setGravity(Gravity.CENTER);
            //设置弹出动画
            //        dialogWindow.setWindowAnimations(R.style.DialogBottomAnimation);
            //获得窗体的属性
            WindowManager.LayoutParams params = dialogWindow.getAttributes();
            params.width = WindowManager.LayoutParams.WRAP_CONTENT;//设置宽高模式，
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;//设置宽高模式，
            dialogWindow.setAttributes(params);
        }

        ImageView avatar = dialog.findViewById(R.id.avatar);
        TextView cancel = dialog.findViewById(R.id.cancel);
        TextView confirm = dialog.findViewById(R.id.confirm);
        GlideUtil.intoImageView(getContext(),userBean.attentionUserInfo.avatarUrl,avatar);
        cancel.setOnClickListener(view -> dialog.dismiss());
        confirm.setOnClickListener(view -> {
            //TODO 删除粉丝
            showDialog();
            viewModel.delFans(userBean.getId());
        });
        dialog.show();
    }
}
