package com.xaqinren.healthyelders.moduleMsg.activity;

import android.os.Bundle;

import com.tencent.qcloud.tim.uikit.modules.chat.base.ChatInfo;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivityChatBinding;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.base.BaseViewModel;

public class ChatActivity extends BaseActivity<ActivityChatBinding, BaseViewModel> {
    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_chat;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        binding.chatLayout.initDefault();
        ChatInfo chatInfo = new ChatInfo();
        chatInfo.setChatName("的范德萨发");
        chatInfo.setTopChat(true);
        chatInfo.setId("1396644406169178112");
        binding.chatLayout.setChatInfo(chatInfo);
    }
}
