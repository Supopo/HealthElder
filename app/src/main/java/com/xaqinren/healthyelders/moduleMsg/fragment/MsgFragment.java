package com.xaqinren.healthyelders.moduleMsg.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.qcloud.tim.uikit.modules.chat.base.ChatInfo;
import com.tencent.qcloud.tim.uikit.modules.conversation.ConversationManagerKit;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.databinding.FragmentMsgBinding;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleLogin.activity.PhoneLoginActivity;
import com.xaqinren.healthyelders.moduleMsg.ImManager;
import com.xaqinren.healthyelders.moduleMsg.activity.AddFriendActivity;
import com.xaqinren.healthyelders.moduleMsg.activity.ChatActivity;
import com.xaqinren.healthyelders.moduleMsg.activity.FansMsgActivity;
import com.xaqinren.healthyelders.moduleMsg.activity.InteractiveActivity;
import com.xaqinren.healthyelders.moduleMsg.activity.LiveMsgActivity;
import com.xaqinren.healthyelders.moduleMsg.activity.ServiceMsgActivity;
import com.xaqinren.healthyelders.moduleMsg.activity.SysMsgActivity;
import com.xaqinren.healthyelders.moduleMsg.activity.WalletMsgActivity;
import com.xaqinren.healthyelders.moduleMsg.viewModel.MsgViewModel;

import java.io.File;

import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;

/**
 * Created by Lee. on 2021/5/11.
 * 消息列表
 */
public class MsgFragment extends BaseFragment<FragmentMsgBinding, MsgViewModel> {

    boolean isInitIm;
    private Disposable subscribe;

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
        binding.ivAdd.setOnClickListener(view -> {
            if (!UserInfoMgr.getInstance().getUserInfo().hasMobileNum()) {
                startActivity(PhoneLoginActivity.class);
                return;
            }
            //添加联系人
            startActivity(AddFriendActivity.class);
        });
        if (Constant.DEBUG) {
            binding.ivAdd2.setVisibility(View.VISIBLE);
            binding.ivAdd2.setOnClickListener(view -> {

            });
        }else{
            binding.ivAdd2.setVisibility(View.GONE);
        }

        binding.srlContent.setEnabled(false);
        binding.conversationLayout.getTitleBar().setVisibility(View.GONE);

        int loginStatus = V2TIMManager.getInstance().getLoginStatus();
        if (loginStatus == V2TIMManager.V2TIM_STATUS_LOGINED) {
            if (!isInitIm) {
                initView();
            }
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void initView() {
        ConversationManagerKit.getInstance().setLoadSelfData(false);
        isInitIm = true;
        ImManager.getInstance().init(new File(getContext().getFilesDir(), "msg").getAbsolutePath());
        binding.conversationLayout.initDefault();
        binding.conversationLayout.getConversationList().setOnItemClickListener((view, position, messageInfo) -> {
            String id = messageInfo.getId();
            switch (id) {
                case Constant.CONVERSATION_SYS_ID: {
                    startActivity(SysMsgActivity.class);
                }
                return;
                case Constant.CONVERSATION_INT_ID: {
                    startActivity(InteractiveActivity.class);
                }
                return;
                case Constant.CONVERSATION_FANS_ID: {
                    startActivity(FansMsgActivity.class);
                }
                return;
                case Constant.CONVERSATION_LIVE_ID: {
                    startActivity(LiveMsgActivity.class);
                }
                return;
                case Constant.CONVERSATION_SERVICE_ID: {
                    startActivity(ServiceMsgActivity.class);
                }
                return;
                case Constant.CONVERSATION_WALLET_ID: {
                    startActivity(WalletMsgActivity.class);
                }
                return;
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
        /*binding.conversationLayout.getConversationList().setOnItemLongClickListener((view, position, messageInfo) -> {
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
        });*/
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        subscribe = RxBus.getDefault().toObservable(EventBean.class).subscribe(
                eventBean -> {
                    if (eventBean.msgId == CodeTable.IM_LOGIN_SUCCESS) {
                        //登录成功,重新获取
                        if (!isInitIm)
                            initView();
                    } else if (eventBean.msgId == CodeTable.LOGIN_OUT) {
                        isInitIm = false;
                        initView();
                    }
                }
        );
        RxSubscriptions.add(subscribe);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isInitIm = false;
        RxSubscriptions.remove(subscribe);
    }
}
