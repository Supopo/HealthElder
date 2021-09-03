package com.xaqinren.healthyelders.moduleMsg.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.qcloud.tim.uikit.modules.chat.base.ChatInfo;
import com.tencent.qcloud.tim.uikit.modules.conversation.ConversationManagerKit;
import com.tencent.qcloud.tim.uikit.modules.conversation.base.ConversationInfo;
import com.tencent.qcloud.ugckit.utils.ToastUtil;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.databinding.FragmentMsgBinding;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleLiteav.bean.LiteAvUserBean;
import com.xaqinren.healthyelders.moduleLogin.activity.PhoneLoginActivity;
import com.xaqinren.healthyelders.moduleMsg.ImManager;
import com.xaqinren.healthyelders.moduleMsg.activity.AddFriendActivity;
import com.xaqinren.healthyelders.moduleMsg.activity.ChatActivity;
import com.xaqinren.healthyelders.moduleMsg.activity.EmptyActivity;
import com.xaqinren.healthyelders.moduleMsg.activity.FansMsgActivity;
import com.xaqinren.healthyelders.moduleMsg.activity.FriendsListActivity;
import com.xaqinren.healthyelders.moduleMsg.activity.InteractiveActivity;
import com.xaqinren.healthyelders.moduleMsg.activity.LiveMsgActivity;
import com.xaqinren.healthyelders.moduleMsg.activity.ServiceMsgActivity;
import com.xaqinren.healthyelders.moduleMsg.activity.SysMsgActivity;
import com.xaqinren.healthyelders.moduleMsg.activity.WalletMsgActivity;
import com.xaqinren.healthyelders.moduleMsg.viewModel.MsgViewModel;
import com.xaqinren.healthyelders.moduleZhiBo.activity.LiveGuanzhongActivity;
import com.xaqinren.healthyelders.moduleZhiBo.activity.ZhiboOverGZActivity;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ListPopMenuBean;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveInitInfo;
import com.xaqinren.healthyelders.widget.InputPwdDialog;
import com.xaqinren.healthyelders.widget.ListBottomPopup;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
    private ListBottomPopup listBottomPopup;
    private String liveRoomId;
    private LiveInitInfo liveInfo;

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
        binding.ivFriend.setOnClickListener(v -> {
            startActivity(FriendsListActivity.class);
        });

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
            if (messageInfo == null) {
                return;
            }
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
        //长按删除 消息 置顶
        binding.conversationLayout.getConversationList().setOnItemLongClickListener((view, position, messageInfo) -> {
            //先判断是不是互动和粉丝关注消息
            //            if (messageInfo.getId().equals(Constant.CONVERSATION_INT_ID) ||
            //                    messageInfo.getId().equals(Constant.CONVERSATION_FANS_ID)) {
            //                return;
            //            }
            showListPop(position, messageInfo);
            return;
        });
    }

    private void showListPop(int pos, ConversationInfo messageInfo) {
        List<ListPopMenuBean> menus = new ArrayList<>();
        boolean isTop = ConversationManagerKit.getInstance().isTopConversation(messageInfo.getId());

        menus.add(new ListPopMenuBean(isTop ? "取消置顶" : "置顶消息", 0, 16));
        menus.add(new ListPopMenuBean("删除聊天", 0, 16));
        listBottomPopup = new ListBottomPopup(getActivity(), menus, true);
        listBottomPopup.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                if (position == 0) {
                    ConversationManagerKit.getInstance().setConversationTop(messageInfo, null);
                } else {
                    delMsg(pos, messageInfo);
                }

                listBottomPopup.dismiss();
            }
        });
        listBottomPopup.showPopupWindow();
    }


    private void delMsg(int position, ConversationInfo messageInfo) {
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
            default:
                //删除聊天信息
                ConversationManagerKit.getInstance().deleteConversation(position, messageInfo);
                return;
        }
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
                    } else if (eventBean.msgId == CodeTable.SHARE_LIVE) {
                        liveRoomId = eventBean.content;
                        //判断是否有密码
                        //有密码
                        if (eventBean.msgType == 1) {
                            Bundle bundle = new Bundle();
                            bundle.putString("liveRoomId", liveRoomId);
                            startActivity(EmptyActivity.class, bundle);
                        } else {
                            viewModel.joinLive(liveRoomId, eventBean.type);
                        }
                    } else if (eventBean.msgId == CodeTable.SHARE_JOININ_LIVE) {
                        //不能用LiveData来回调接收，打开的页面会出现在聊天页面下层
                        liveInfo = (LiveInitInfo) eventBean.data;
                        if (liveInfo != null) {

                            if (liveInfo.liveRoomStatus.equals("LIVE_OVER")) {
                                //直播结束 直接跳转结束页面
                                //跳转结算页面
                                Bundle bundle = new Bundle();
                                bundle.putString("liveRoomRecordId", liveInfo.liveRoomRecordId);
                                bundle.putString("liveRoomId", liveRoomId);
                                startActivity(ZhiboOverGZActivity.class, bundle);
                            } else {
                                //进入直播间
                                Bundle bundle = new Bundle();
                                bundle.putSerializable(Constant.LiveInitInfo, liveInfo);
                                startActivity(LiveGuanzhongActivity.class, bundle);
                            }

                        }
                    }
                }
        );
        RxSubscriptions.add(subscribe);
    }

    private boolean isMain() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            return true;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isInitIm = false;
        RxSubscriptions.remove(subscribe);
    }
}
