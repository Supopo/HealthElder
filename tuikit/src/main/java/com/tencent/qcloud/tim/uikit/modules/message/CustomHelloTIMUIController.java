package com.tencent.qcloud.tim.uikit.modules.message;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tencent.qcloud.tim.uikit.R;
import com.tencent.qcloud.tim.uikit.modules.chat.layout.message.holder.ICustomMessageViewGroup;
import com.tencent.qcloud.tim.uikit.utils.ToastUtil;

public class CustomHelloTIMUIController {

    private static final String TAG = CustomHelloTIMUIController.class.getSimpleName();

    public static void onDraw(ICustomMessageViewGroup parent, final ShareTopicBean data, final Context context) {

        // 把自定义消息view添加到TUIKit内部的父容器里
        View view = LayoutInflater.from(context).inflate(R.layout.custom_message_view, null, false);
        parent.addMessageContentView(view);

        // 自定义消息view的实现，这里仅仅展示文本信息，并且实现超链接跳转
        TextView textView = view.findViewById(R.id.tvShareTitle);
        ImageView ivShareImg = view.findViewById(R.id.ivShareImg);
        Glide.with(context).load(data.getBackgroundImageName()).into(ivShareImg);
        final String text = "不支持的自定义消息";
        textView.setText(data.getTitleName());
        view.setClickable(true);
    }
}