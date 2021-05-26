package com.xaqinren.healthyelders.moduleMsg.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.tencent.qcloud.tim.uikit.modules.conversation.ConversationLayout;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.FragmentMsgBinding;
import com.xaqinren.healthyelders.moduleMsg.activity.ChatActivity;
import com.xaqinren.healthyelders.moduleMsg.adapter.MsgListAdapter;
import com.xaqinren.healthyelders.moduleMsg.viewModel.MsgViewModel;

import me.goldze.mvvmhabit.base.BaseFragment;

/**
 * Created by Lee. on 2021/5/11.
 * 消息列表
 */
public class MsgFragment extends BaseFragment<FragmentMsgBinding, MsgViewModel> {

//    private MsgListAdapter msgListAdapter;

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_msg;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
//        msgListAdapter = new MsgListAdapter(R.layout.item_msg);
//        binding.rvMsg.setLayoutManager(new LinearLayoutManager(getActivity()));
//        binding.rvMsg.setAdapter(msgListAdapter);
//        msgListAdapter.setEmptyView(R.layout.item_empty_sqlm);
//        viewModel.getMsgList();
//        binding.srlContent.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                binding.srlContent.setRefreshing(false);
//            }
//        });
        binding.ivAdd.setOnClickListener(view -> {
            startActivity(ChatActivity.class);
        });
        binding.conversationLayout.initDefault();
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
//        viewModel.msgList.observe(this, msgs -> {
//            if (msgs != null) {
//                msgListAdapter.setNewInstance(msgs);
//            }
//        });
    }
}
