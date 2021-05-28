package com.xaqinren.healthyelders.moduleMsg.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.tencent.qcloud.tim.uikit.modules.chat.base.ChatInfo;
import com.tencent.qcloud.tim.uikit.modules.conversation.ConversationLayout;
import com.tencent.qcloud.tim.uikit.modules.conversation.ConversationListLayout;
import com.tencent.qcloud.tim.uikit.modules.conversation.ConversationManagerKit;
import com.tencent.qcloud.tim.uikit.modules.conversation.base.ConversationInfo;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.FragmentMsgBinding;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleLiteav.activity.LiveAVActivity;
import com.xaqinren.healthyelders.moduleMsg.ImManager;
import com.xaqinren.healthyelders.moduleMsg.activity.AddFriendActivity;
import com.xaqinren.healthyelders.moduleMsg.activity.ChatActivity;
import com.xaqinren.healthyelders.moduleMsg.activity.ContactsActivity;
import com.xaqinren.healthyelders.moduleMsg.activity.FansMsgActivity;
import com.xaqinren.healthyelders.moduleMsg.activity.InteractiveActivity;
import com.xaqinren.healthyelders.moduleMsg.activity.LiveMsgActivity;
import com.xaqinren.healthyelders.moduleMsg.activity.ServiceMsgActivity;
import com.xaqinren.healthyelders.moduleMsg.activity.SysMsgActivity;
import com.xaqinren.healthyelders.moduleMsg.activity.WalletMsgActivity;
import com.xaqinren.healthyelders.moduleMsg.adapter.MsgListAdapter;
import com.xaqinren.healthyelders.moduleMsg.viewModel.MsgViewModel;
import com.xaqinren.healthyelders.push.PayLoadBean;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;
import me.goldze.mvvmhabit.http.download.DownLoadStateBean;

/**
 * Created by Lee. on 2021/5/11.
 * 消息列表
 */
public class MsgFragment extends BaseFragment<FragmentMsgBinding, MsgViewModel> {

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
        ConversationManagerKit.getInstance().setLoadSelfData(false);
        binding.ivAdd.setOnClickListener(view -> {
            //添加联系人
            startActivity(AddFriendActivity.class);
        });
        binding.ivAdd2.setOnClickListener(view -> {
            ImManager.testAddConversation();
        });
        binding.conversationLayout.initDefault();
        binding.conversationLayout.getTitleBar().setVisibility(View.GONE);
        binding.conversationLayout.getConversationList().setOnItemClickListener((view, position, messageInfo) -> {
                String id = messageInfo.getId();
                switch (id) {
                    case Constant.CONVERSATION_SYS_ID: {
                        startActivity(SysMsgActivity.class);
                    }return;
                    case Constant.CONVERSATION_INT_ID: {
                        startActivity(InteractiveActivity.class);
                    }return;
                    case Constant.CONVERSATION_FANS_ID: {
                        startActivity(FansMsgActivity.class);
                    }return;
                    case Constant.CONVERSATION_LIVE_ID:{
                        startActivity(LiveMsgActivity.class);
                    }return;
                    case Constant.CONVERSATION_SERVICE_ID: {
                        startActivity(ServiceMsgActivity.class);
                    }return;
                    case Constant.CONVERSATION_WALLET_ID: {
                        startActivity(WalletMsgActivity.class);
                    }return;
                    case Constant.CONVERSATION_CUSTOMER_SERVICE_ID:
                        ImManager.getInstance().clearUnreadById(Constant.CONVERSATION_CUSTOMER_SERVICE_ID);
                        return;
                }
            //条目点击
            ChatInfo chatInfo = new ChatInfo();
            chatInfo.setChatName(messageInfo.getTitle());
            chatInfo.setTopChat(messageInfo.isTop());
            chatInfo.setId(messageInfo.getId());
            ChatActivity.startChar(getContext(), chatInfo);
        });
        binding.conversationLayout.getConversationList().setOnItemLongClickListener((view, position, messageInfo) -> {
            //条目长按,置顶，删除，
            String id = messageInfo.getId();
            switch (id) {
                case Constant.CONVERSATION_SYS_ID:
                    ImManager.getInstance().delConversationLocal(Constant.CONVERSATION_SYS_ID);
                    return;
                case Constant.CONVERSATION_INT_ID:
                    ImManager.getInstance().delConversationLocal(Constant.CONVERSATION_INT_ID);
                    return;
                case Constant.CONVERSATION_FANS_ID:
                    ImManager.getInstance().delConversationLocal(Constant.CONVERSATION_FANS_ID);
                    return;
                case Constant.CONVERSATION_LIVE_ID:
                    ImManager.getInstance().delConversationLocal(Constant.CONVERSATION_LIVE_ID);
                    return;
                case Constant.CONVERSATION_SERVICE_ID:
                    ImManager.getInstance().delConversationLocal(Constant.CONVERSATION_SERVICE_ID);
                    return;
                case Constant.CONVERSATION_WALLET_ID:
                    ImManager.getInstance().delConversationLocal(Constant.CONVERSATION_WALLET_ID);
                    return;
                case Constant.CONVERSATION_CUSTOMER_SERVICE_ID:
                    ImManager.getInstance().delConversationLocal(Constant.CONVERSATION_CUSTOMER_SERVICE_ID);
                    return;
            }
        });
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
