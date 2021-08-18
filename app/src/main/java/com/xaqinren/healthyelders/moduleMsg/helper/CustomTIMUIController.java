package com.xaqinren.healthyelders.moduleMsg.helper;

import android.view.LayoutInflater;
import android.view.View;

import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.tencent.qcloud.tim.uikit.modules.chat.layout.message.MessageLayout;
import com.tencent.qcloud.tim.uikit.modules.chat.layout.message.holder.ICustomMessageViewGroup;
import com.tencent.qcloud.tim.uikit.modules.message.MessageInfo;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.global.AppApplication;
import com.xaqinren.healthyelders.moduleMsg.bean.MCustomMsgBean;
import com.xaqinren.healthyelders.utils.GlideUtil;

public class CustomTIMUIController {

    private static final String TAG = CustomTIMUIController.class.getSimpleName();

    public static void onDraw(ICustomMessageViewGroup parent, final MCustomMsgBean data, final int position, final MessageLayout.OnItemLongClickListener onItemLongClickListener, final MessageInfo info) {

        // 把自定义消息view添加到TUIKit内部的父容器里
        View view = LayoutInflater.from(AppApplication.getContext()).inflate(R.layout.item_chat_msg_video, null, false);
        parent.addMessageContentView(view);

        // 自定义消息view的实现，这里仅仅展示文本信息，并且实现超链接跳转
        QMUIRadiusImageView ivCover = view.findViewById(R.id.iv_cover);
        GlideUtil.intoImageView(AppApplication.getContext(), data.cover, ivCover);

        view.setClickable(true);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (data == null) {
                    return;
                }
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onItemLongClickListener != null) {
                    onItemLongClickListener.onMessageLongClick(v, position, info);
                }
                return false;
            }
        });
    }
}
