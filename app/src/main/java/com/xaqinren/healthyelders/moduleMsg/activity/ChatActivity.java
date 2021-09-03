package com.xaqinren.healthyelders.moduleMsg.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.tencent.imsdk.v2.V2TIMConversation;
import com.tencent.qcloud.tim.uikit.modules.chat.base.ChatInfo;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivityChatBinding;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.base.BaseViewModel;
//聊天页面
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
        chatInfo.setType(V2TIMConversation.V2TIM_C2C);
        binding.chatLayout.initDefault();
        binding.chatLayout.setChatInfo(chatInfo);
        binding.chatLayout.getInputLayout().disableMoreInput(false);
        //隐藏聊天更多里面的摄像
        binding.chatLayout.getInputLayout().disableSendFileAction(true);
        binding.chatLayout.getInputLayout().disableVideoRecordAction(true);
        //设置自己聊天文字颜色
        binding.chatLayout.getMessageLayout().setRightChatContentFontColor(getResources().getColor(R.color.white));
        //设置自己的聊天气泡
        binding.chatLayout.getMessageLayout().setRightBubble(getResources().getDrawable(R.drawable.new_msg_right_bg));
        binding.chatLayout.getMessageLayout().setLeftBubble(getResources().getDrawable(R.drawable.new_msg_left_bg));

    }
}
