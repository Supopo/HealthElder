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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.tencent.qcloud.tim.uikit.utils.ToastUtil;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.databinding.FramentAttentionBinding;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.moduleLiteav.bean.LiteAvUserBean;
import com.xaqinren.healthyelders.moduleMine.activity.UserInfoActivity;
import com.xaqinren.healthyelders.moduleMine.adapter.AttentionAdapter;
import com.xaqinren.healthyelders.moduleMine.viewModel.AttentionViewModel;
import com.xaqinren.healthyelders.moduleMsg.adapter.AddFriendAdapter;
import com.xaqinren.healthyelders.moduleMsg.adapter.provider.FriendProvider;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ListPopMenuBean;
import com.xaqinren.healthyelders.utils.GlideUtil;
import com.xaqinren.healthyelders.widget.ListBottomPopup;
import com.xaqinren.healthyelders.widget.YesOrNoDialog;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.utils.StringUtils;
import me.goldze.mvvmhabit.utils.Utils;

/**
 * ??????/??????
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
    private Disposable disposable;
    private ListBottomPopup listBottomPopup;

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
        String mUid = UserInfoMgr.getInstance().getUserInfo().getId();
        boolean showClose = type == 1 ? true : false;
        boolean isMe = false;
        if (StringUtils.isEmpty(uid) || uid.equals(mUid)) {
            isMe = true;
        }

        adapter = new AttentionAdapter(R.layout.item_attention, showClose && isMe);
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
                    startActivity(UserInfoActivity.class, bundle);
                }
                break;
                case R.id.attention_btn: {
                    //??????????????????
                    if (userBean.getIdentity().equals(AddFriendAdapter.FOLLOW)
                            || userBean.getIdentity().equals(AddFriendAdapter.FRIEND)
                            || userBean.getIdentity().equals(AddFriendAdapter.ATTENTION)
                    ) {
                        //??????????????????
                        showListPop(userBean);

                        return;
                    }

                    showDialog();
                    viewModel.recommendFriend(userBean.getId());
                }
                break;
                case R.id.close: {
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

    private void showListPop(LiteAvUserBean userBean) {
        List<ListPopMenuBean> menus = new ArrayList<>();
        menus.add(new ListPopMenuBean("?????????????????????", getResources().getColor(R.color.gray_999), 14));
        menus.add(new ListPopMenuBean("????????????", getResources().getColor(R.color.color_DC3530), 16));
        if (listBottomPopup == null) {
            listBottomPopup = new ListBottomPopup(getActivity(), menus, true);
        }
        listBottomPopup.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                if (position == 1) {
                    showDialog();
                    viewModel.recommendFriend(userBean.getId());
                }
                listBottomPopup.dismiss();
            }
        });
        listBottomPopup.showPopupWindow();
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
        //        if (tempUserBean != null && tempUserIndex != -1) {
        //            //??????????????????????????????
        //            if (tempUserBean.attentionUserInfo != null) {
        //                viewModel.refreshUserList(tempUserBean.attentionUserInfo.nickname);
        //            }
        //        }
    }


    @Override
    public void initViewObservable() {
        super.initViewObservable();
        dismissDialog();
        viewModel.refreshUserList.observe(this, liteAvUserBeans -> {
            if (!liteAvUserBeans.isEmpty()) {
                if (tempUserBean != null && tempUserIndex != -1) {
                    //??????????????????????????????
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
        viewModel.requestSuccess.observe(this, a -> dismissDialog());
        viewModel.userList.observe(this, liteAvUserBeans -> {
            if (liteAvUserBeans == null) {
                return;
            }
            int temp = -1;
            for (int i = 0; i < liteAvUserBeans.size(); i++) {
                if (liteAvUserBeans.get(i).getAttentionUserId().equals(UserInfoMgr.getInstance().getUserInfo().getId())) {
                    temp = i;
                }
            }
            if (temp != -1) {
                liteAvUserBeans.remove(temp);
            }


            if (currentPage == 1)
                adapter.getData().clear();
            adapter.addData(liteAvUserBeans);
            if (liteAvUserBeans.isEmpty()) {
                adapter.getLoadMoreModule().loadMoreEnd(false);
            } else {
                adapter.getLoadMoreModule().loadMoreComplete();
            }
            currentPage++;
            binding.swipeContent.setRefreshing(false);
        });
        viewModel.flow.observe(this, aBoolean -> {
            dismissDialog();
            LiteAvUserBean friendBean = (LiteAvUserBean) adapter.getData().get(opIndex);
            if (friendBean.getIdentity().equals(AddFriendAdapter.STRANGER)) {
                //?????????
                friendBean.setIdentity(AddFriendAdapter.ATTENTION);
            } else if (friendBean.getIdentity().equals(AddFriendAdapter.FANS)) {
                //??????
                friendBean.setIdentity(AddFriendAdapter.FRIEND);
            } else if (friendBean.getIdentity().equals(AddFriendAdapter.ATTENTION)) {
                //????????????
                friendBean.setIdentity(AddFriendAdapter.STRANGER);
            } else if (friendBean.getIdentity().equals(AddFriendAdapter.FRIEND)) {
                //??????
                friendBean.setIdentity(AddFriendAdapter.FANS);
            } else if (friendBean.getIdentity().equals(AddFriendAdapter.FOLLOW)) {
                //????????????
                friendBean.setIdentity(AddFriendAdapter.STRANGER);
            }
            adapter.notifyItemChanged(opIndex, 99);
        });
        viewModel.del.observe(this, aBoolean -> {
            if (aBoolean) {
                dialog.dismiss();
                adapter.remove(opIndex);
            } else {
                ToastUtil.toastShortMessage("????????????,?????????");
            }
        });
        disposable = RxBus.getDefault().toObservable(EventBean.class).subscribe(eventBean -> {
            if (eventBean != null) {
                if (eventBean.msgId == CodeTable.FOLLOW_USER) {
                    if (eventBean.content.equals(adapter.getData().get(opIndex).getId())) {
                        //??????
                        if (eventBean.msgType == 1) {
                            //?????????????????? ???????????????
                            if (adapter.getData().get(opIndex).identity.equals(FriendProvider.STRANGER)) {
                                adapter.getData().get(opIndex).identity = FriendProvider.ATTENTION;
                            } else if (adapter.getData().get(opIndex).identity.equals(FriendProvider.FANS)) {
                                adapter.getData().get(opIndex).identity = FriendProvider.FRIEND;
                            }
                        } else if (eventBean.msgType == 0) {
                            if (adapter.getData().get(opIndex).identity.equals(FriendProvider.FOLLOW) || adapter.getData().get(opIndex).identity.equals(FriendProvider.ATTENTION)) {
                                adapter.getData().get(opIndex).identity = FriendProvider.STRANGER;
                            } else if (adapter.getData().get(opIndex).identity.equals(FriendProvider.FRIEND)) {
                                adapter.getData().get(opIndex).identity = FriendProvider.FANS;
                            }
                        }
                        adapter.notifyItemChanged(opIndex, 99);
                    }
                }
            }
        });

    }

    private Dialog dialog;

    private void showDelPop(LiteAvUserBean userBean) {
        if (dialog == null) {
            dialog = new Dialog(getContext(), R.style.CustomerDialog);
            //????????????????????????
            View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_del_fans, null);
            //??????????????????dismiss
            dialog.setCancelable(false);
            //??????????????????Dialog
            dialog.setContentView(view);
            //????????????Activity???????????????
            Window dialogWindow = dialog.getWindow();
            //??????Dialog?????????????????????
            dialogWindow.setGravity(Gravity.CENTER);
            //??????????????????
            //        dialogWindow.setWindowAnimations(R.style.DialogBottomAnimation);
            //?????????????????????
            WindowManager.LayoutParams params = dialogWindow.getAttributes();
            params.width = WindowManager.LayoutParams.WRAP_CONTENT;//?????????????????????
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;//?????????????????????
            dialogWindow.setAttributes(params);
        }

        ImageView avatar = dialog.findViewById(R.id.avatar);
        TextView cancel = dialog.findViewById(R.id.cancel);
        TextView confirm = dialog.findViewById(R.id.confirm);
        GlideUtil.intoImageView(getContext(), userBean.attentionUserInfo.avatarUrl, avatar);
        cancel.setOnClickListener(view -> dialog.dismiss());
        confirm.setOnClickListener(view -> {
            //TODO ????????????
            showDialog();
            viewModel.delFans(userBean.getId());
        });
        dialog.show();
    }
}
