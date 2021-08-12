package com.tencent.qcloud.tim.uikit.modules.message;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.tencent.qcloud.tim.uikit.R;
import com.tencent.qcloud.tim.uikit.modules.chat.layout.message.holder.ICustomMessageViewGroup;

//视频分享
public class CustomVideoController {

    private static final String TAG = CustomVideoController.class.getSimpleName();

    public static void onDraw(ICustomMessageViewGroup parent, final CustomMessage data, final Context context) {

        // 把自定义消息view添加到TUIKit内部的父容器里
        View view = LayoutInflater.from(context).inflate(R.layout.custom_message_video, null, false);
        parent.addMessageContentView(view);

        // 自定义消息view的实现，这里仅仅展示文本信息，并且实现超链接跳转
        TextView textView = view.findViewById(R.id.tv_title);
        QMUIRadiusImageView ivCover = view.findViewById(R.id.iv_cover);
        Glide.with(context).load(data.cover).into(ivCover);
        textView.setText((String) data.content);
        view.setClickable(true);
    }
}