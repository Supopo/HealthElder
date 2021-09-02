package com.xaqinren.healthyelders.moduleMsg.helper;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.google.gson.Gson;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.tencent.qcloud.tim.uikit.modules.chat.layout.message.MessageLayout;
import com.tencent.qcloud.tim.uikit.modules.chat.layout.message.holder.ICustomMessageViewGroup;
import com.tencent.qcloud.tim.uikit.modules.message.MessageInfo;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.global.AppApplication;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.moduleHome.activity.VideoListActivity;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;
import com.xaqinren.healthyelders.moduleHome.bean.VideoListBean;
import com.xaqinren.healthyelders.moduleMsg.bean.MCustomMsgBean;
import com.xaqinren.healthyelders.modulePicture.activity.TextPhotoDetailActivity;
import com.xaqinren.healthyelders.utils.GlideUtil;

import java.util.ArrayList;

import me.goldze.mvvmhabit.bus.RxBus;

public class CustomMsgController {

    private static final String TAG = CustomMsgController.class.getSimpleName();

    public static void onDraw(ICustomMessageViewGroup parent, final MCustomMsgBean data, final int position, final MessageLayout.OnItemLongClickListener onItemLongClickListener, final MessageInfo info) {

        // 把自定义消息view添加到TUIKit内部的父容器里
        View view = LayoutInflater.from(AppApplication.getContext()).inflate(R.layout.item_chat_msg_custom, null, false);
        parent.addMessageContentView(view);

        // 自定义消息view的实现，这里仅仅展示文本信息，并且实现超链接跳转
        LinearLayout llShowZb = view.findViewById(R.id.ll_showZhibo);
        ImageView ivVideo = view.findViewById(R.id.iv_video);
        TextView tvTitle = view.findViewById(R.id.tv_title);
        TextView tvName = view.findViewById(R.id.tv_name);
        QMUIRadiusImageView ivCover = view.findViewById(R.id.iv_cover);
        QMUIRadiusImageView ivAvatar = view.findViewById(R.id.iv_photo);
        if (data.msgType == 1) {
            ivVideo.setVisibility(View.VISIBLE);
            llShowZb.setVisibility(View.GONE);
        } else if (data.msgType == 2) {
            ivVideo.setVisibility(View.GONE);
            llShowZb.setVisibility(View.GONE);
        } else if (data.msgType == 3) {
            llShowZb.setVisibility(View.VISIBLE);
            ivVideo.setVisibility(View.GONE);
        }
        tvTitle.setText(data.content);
        tvName.setText(data.userName);
        GlideUtil.intoImageView(AppApplication.getContext(), data.cover, ivCover);
        GlideUtil.intoImageView(AppApplication.getContext(), data.userAvatar, ivAvatar);

        view.setClickable(true);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                switch (data.msgType) {
                    case 1:
                        VideoInfo videoInfo = new Gson().fromJson(data.resource, VideoInfo.class);
                        intent = new Intent(AppApplication.getContext(), VideoListActivity.class);
                        Bundle bundle = new Bundle();
                        VideoListBean listBean = new VideoListBean();
                        listBean.videoInfos = new ArrayList<>();
                        listBean.videoInfos.add(videoInfo);
                        bundle.putSerializable("key", listBean);
                        bundle.putBoolean("isSingle", true);
                        intent.putExtras(bundle);
                        intent.setAction("android.intent.action.VIEW");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        AppApplication.getContext().startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(AppApplication.getContext(), TextPhotoDetailActivity.class);
                        intent.putExtra(com.xaqinren.healthyelders.moduleLiteav.Constant.VIDEO_ID, data.resourceId);
                        intent.setAction("android.intent.action.VIEW");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        AppApplication.getContext().startActivity(intent);
                        break;
                    case 3:
                        //发送消息进入直播间
                        RxBus.getDefault().post(new EventBean(CodeTable.SHARE_LIVE, data.resourceId, data.roomPwd, 0));
                        break;
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
