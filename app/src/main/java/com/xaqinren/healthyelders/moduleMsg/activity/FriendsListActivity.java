package com.xaqinren.healthyelders.moduleMsg.activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.module.BaseLoadMoreModule;
import com.tencent.imsdk.v2.V2TIMConversation;
import com.tencent.qcloud.tim.uikit.modules.chat.base.ChatInfo;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.databinding.ActivityFriendsListBinding;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.moduleMsg.adapter.FriendListAdapter;
import com.xaqinren.healthyelders.moduleMsg.viewModel.FriendsListViewModel;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.bus.RxBus;

/**
 * Created by Lee. on 2021/8/20.
 * 朋友列表
 */
public class FriendsListActivity extends BaseActivity<ActivityFriendsListBinding, FriendsListViewModel> {

    private FriendListAdapter mAdapter;
    private BaseLoadMoreModule loadMoreModule;
    private int pageNum = 1;
    private Bundle extras;
    private int type;


    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_friends_list;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initParam() {
        super.initParam();
        extras = getIntent().getExtras();
        if (extras != null) {
            type = extras.getInt("type");
        }
    }

    @Override
    public void initData() {
        super.initData();
        tvTitle.setText("朋友列表");
        mAdapter = new FriendListAdapter(R.layout.item_friends_list, type);
        binding.rvContent.setLayoutManager(new LinearLayoutManager(this));
        binding.rvContent.setAdapter(mAdapter);
        loadMoreModule = mAdapter.getLoadMoreModule();
        loadMoreModule.setEnableLoadMore(true);
        loadMoreModule.setAutoLoadMore(true);
        loadMoreModule.setOnLoadMoreListener(() -> {
            pageNum++;
            viewModel.getFriendsList(pageNum);
        });
        showDialog();
        viewModel.getFriendsList(pageNum);
        binding.srlContent.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadMoreModule.loadMoreComplete();
                pageNum = 1;
                viewModel.getFriendsList(pageNum);
            }
        });
        mAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                if (view.getId() == R.id.tv_send_msg) {
                    if (type == 0) {
                        //打开聊天页面
                        ChatInfo chatInfo = new ChatInfo();
                        chatInfo.setChatName(mAdapter.getData().get(position).getName());
                        chatInfo.setTopChat(false);
                        chatInfo.setId(mAdapter.getData().get(position).getAttentionUserId());
                        ChatActivity.startChar(getContext(), chatInfo);
                    } else if (type == 1) {//分享
                        RxBus.getDefault().post(new EventBean(CodeTable.SHARE_USER, mAdapter.getData().get(position).getAttentionUserId()));
                    }


                }
            }
        });
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.friendsList.observe(this, list -> {
            if (list != null) {
                if (binding.srlContent.isRefreshing()) {
                    binding.srlContent.setRefreshing(false);
                }
                if (list.size() > 0) {
                    //加载更多加载完成
                    loadMoreModule.loadMoreComplete();
                }
                if (pageNum == 1) {
                    mAdapter.setNewInstance(list);
                } else {
                    mAdapter.addData(list);
                    if (list.size() == 0) {
                        loadMoreModule.loadMoreEnd(true);
                        pageNum--;
                    }
                }
            }
        });
        viewModel.dismissDialog.observe(this, dis -> {
            if (dis != null) {
                if (dis) {
                    dismissDialog();
                }
            }
        });
    }
}
