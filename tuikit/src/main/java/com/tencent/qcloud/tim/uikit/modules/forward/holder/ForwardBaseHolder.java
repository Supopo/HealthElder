package com.tencent.qcloud.tim.uikit.modules.forward.holder;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.tencent.qcloud.tim.uikit.modules.conversation.base.ConversationInfo;
import com.tencent.qcloud.tim.uikit.modules.conversation.holder.ConversationBaseHolder;
import com.tencent.qcloud.tim.uikit.modules.forward.ForwardSelectListAdapter;

public class ForwardBaseHolder extends ConversationBaseHolder {

    protected View rootView;
    protected ForwardSelectListAdapter mAdapter;

    public ForwardBaseHolder(View itemView) {
        super(itemView);
        rootView = itemView;
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        mAdapter = (ForwardSelectListAdapter) adapter;
    }

    public void layoutViews(ConversationInfo conversationInfo, int position){

    }

}
