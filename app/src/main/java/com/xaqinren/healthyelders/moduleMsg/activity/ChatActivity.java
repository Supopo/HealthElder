package com.xaqinren.healthyelders.moduleMsg.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.tencent.qcloud.tim.uikit.modules.chat.base.ChatInfo;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivityChatBinding;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.base.BaseViewModel;

public class ChatActivity extends BaseActivity<ActivityChatBinding, BaseViewModel> {

    public static void startChar(Context context, ChatInfo chatInfo) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("info", chatInfo);
        context.startActivity(intent);
    }

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
        rlTitle.setVisibility(View.GONE);
        ChatInfo chatInfo = (ChatInfo) getIntent().getSerializableExtra("info");
        binding.chatLayout.initDefault();
        binding.chatLayout.setChatInfo(chatInfo);
        binding.chatLayout.getInputLayout().disableMoreInput(false);
    }
}
