package com.xaqinren.healthyelders.moduleMsg.activity;

import android.os.Bundle;
import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivityInteractiveBinding;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleMsg.ImManager;
import com.xaqinren.healthyelders.moduleMsg.adapter.AddFriendAdapter;
import com.xaqinren.healthyelders.moduleMsg.adapter.InteractiveAdapter;
import com.xaqinren.healthyelders.moduleMsg.adapter.provider.FansProvider;
import com.xaqinren.healthyelders.moduleMsg.adapter.provider.InteractiveProvider;
import com.xaqinren.healthyelders.moduleMsg.bean.FriendBean;
import com.xaqinren.healthyelders.moduleMsg.bean.InteractiveBean;
import com.xaqinren.healthyelders.moduleMsg.bean.MessageDetailBean;
import com.xaqinren.healthyelders.moduleMsg.viewModel.InteractiveViewModel;
import com.xaqinren.healthyelders.utils.LogUtils;

import me.goldze.mvvmhabit.base.BaseActivity;

/**
 * 粉丝消息
 */
public class FansMsgActivity extends BaseActivity<ActivityInteractiveBinding, InteractiveViewModel> {

    private InteractiveAdapter interactiveAdapter;
    private String TAG = getClass().getSimpleName();

    private boolean isSelShow;

    private int page = 1;
    private int pageSize = 5;

    private String messageGroup = com.xaqinren.healthyelders.moduleMsg.Constant.FANS;

    private String messageType = "";

    private int messageCount = 0;
    private int friendCount = 0;

    private int friendPage = 1;

    private int friendPageSize = 20;
    private boolean enableFriend = true;

    private boolean hasLoadMore = false;
    private FansProvider fansProvider;

    private int opIndex;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_interactive;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        setTitle("粉丝");
        ImManager.getInstance().clearUnreadById(Constant.CONVERSATION_FANS_ID);
        binding.titleLayout.setVisibility(View.GONE);
        interactiveAdapter = new InteractiveAdapter();
        fansProvider = new FansProvider();
        interactiveAdapter.addItemProvider(fansProvider);

        binding.content.setLayoutManager(new LinearLayoutManager(this));
        binding.content.setAdapter(interactiveAdapter);

        interactiveAdapter.setOnItemClickListener((adapter, view, position) -> {
            MessageDetailBean bean = (MessageDetailBean) adapter.getData().get(position);
            LogUtils.e(TAG, "position->" + position + "\t type ->" + bean.getItemType());
            if (bean.getItemType() == MessageDetailBean.TYPE_LOAD_MORE) {
                page++;
                showDialog();
                viewModel.getMessage(page, pageSize, messageGroup, messageType);
            } else if (bean.getItemType() == MessageDetailBean.TYPE_TOP) {

            }
        });

        interactiveAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            //头像 ，关注/取关
            //R.id.avatar,R.id.attention_btn
            MessageDetailBean bean = (MessageDetailBean) adapter.getData().get(position);
            opIndex = position;
                switch (view.getId()) {
                    case R.id.avatar: {
                        InteractiveBean interactiveBean = (InteractiveBean) bean;
                        LogUtils.e(TAG, "点击头像->" + interactiveBean.getSendUser().getUserId());
                    }break;
                    case R.id.attention_btn:
                    {
                        showDialog();
                        InteractiveBean interactiveBean = (InteractiveBean) bean;
                        viewModel.recommendFriend(interactiveBean.getSendUser().getUserId() + "");
                    }
                        break;
                    case R.id.favorite: {
                        //推荐列表,关注
                        showDialog();
                        FriendBean friendBean = (FriendBean) bean;
                        viewModel.recommendFriend(friendBean.getUserId());
                    }break;
                    case R.id.close: {
                        //推荐列表,删除
                        adapter.removeAt(position);
                    }break;
                }
        });

        if (enableFriend) {
            interactiveAdapter.getLoadMoreModule().setEnableLoadMore(true);
            interactiveAdapter.getLoadMoreModule().setAutoLoadMore(false);
            interactiveAdapter.getLoadMoreModule().setOnLoadMoreListener(() -> {
                if (friendCount>0)
                    viewModel.getRecommendFriend();
            });
        }
        showDialog();
        viewModel.getMessage(page, pageSize, messageGroup, "");
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.requestSuccess.observe(this, aBoolean -> {
            dismissDialog();
        });
        viewModel.musicListData.observe(this, interactiveBeans -> {
            if (page == 1) {
                interactiveAdapter.getData().clear();
                messageCount = 0;
                friendCount = 0;
                friendPage = 1;
            }
            interactiveAdapter.addData(messageCount, interactiveBeans);
            messageCount += interactiveBeans.size();
            if (interactiveBeans.size() >= pageSize) {
                if (isSelShow)return;
                isSelShow = true;
                interactiveAdapter.addData(messageCount, new MessageDetailBean() {
                    @Override
                    public int getItemType() {
                        return MessageDetailBean.TYPE_LOAD_MORE;
                    }
                });
            }else{
                //加载完成，隐藏loadmore
                if (isSelShow) {
                    interactiveAdapter.removeAt(messageCount);
                }
            }
            if (!enableFriend)return;
            if (friendCount==0)
                viewModel.getRecommendFriend();
        });
        viewModel.friendListData.observe(this, friendBeans -> {
            if (friendCount == 0 && !friendBeans.isEmpty()) {
                interactiveAdapter.addData(new MessageDetailBean() {
                    @Override
                    public int getItemType() {
                        return MessageDetailBean.TYPE_TEXT;
                    }
                });
            }
            friendCount += friendBeans.size();
            interactiveAdapter.addData(friendBeans);
            if (friendBeans.size() >= friendPageSize) {
                interactiveAdapter.getLoadMoreModule().loadMoreEnd(false);
            }else{
                interactiveAdapter.getLoadMoreModule().loadMoreEnd(false);
            }
        });
        viewModel.flow.observe(this, aBoolean -> {
            dismissDialog();
            MessageDetailBean detailBean = (MessageDetailBean) interactiveAdapter.getData().get(opIndex);
            if (detailBean.getItemType() == MessageDetailBean.TYPE_FRIEND) {
                FriendBean friendBean = (FriendBean) detailBean;
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
            } else if (detailBean.getItemType() == MessageDetailBean.TYPE_TOP) {
                InteractiveBean interactiveBean = (InteractiveBean) detailBean;
                if (interactiveBean.getIdentity().equals(AddFriendAdapter.STRANGER)) {
                    //陌生人
                    interactiveBean.setIdentity(AddFriendAdapter.ATTENTION);
                } else if (interactiveBean.getIdentity().equals(AddFriendAdapter.FANS)) {
                    //粉丝
                    interactiveBean.setIdentity( AddFriendAdapter.FRIEND);
                } else if (interactiveBean.getIdentity().equals(AddFriendAdapter.ATTENTION)) {
                    //关注的人
                    interactiveBean.setIdentity( AddFriendAdapter.STRANGER);
                } else if (interactiveBean.getIdentity().equals(AddFriendAdapter.FRIEND)) {
                    //朋友
                    interactiveBean.setIdentity( AddFriendAdapter.FANS);
                }  else if (interactiveBean.getIdentity().equals(AddFriendAdapter.FOLLOW)) {
                    //关注的人
                    interactiveBean.setIdentity(  AddFriendAdapter.STRANGER);
                }
            }

            interactiveAdapter.notifyItemChanged(opIndex);
        });
    }

}
