package com.xaqinren.healthyelders.moduleMsg.helper;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.tencent.imsdk.v2.V2TIMCustomElem;
import com.tencent.imsdk.v2.V2TIMMessage;
import com.tencent.qcloud.tim.uikit.TUIKit;
import com.tencent.qcloud.tim.uikit.base.IBaseAction;
import com.tencent.qcloud.tim.uikit.base.IBaseInfo;
import com.tencent.qcloud.tim.uikit.base.IBaseViewHolder;
import com.tencent.qcloud.tim.uikit.base.TUIChatControllerListener;
import com.tencent.qcloud.tim.uikit.base.TUIConversationControllerListener;
import com.tencent.qcloud.tim.uikit.modules.chat.layout.message.holder.ICustomMessageViewGroup;
import com.tencent.qcloud.tim.uikit.modules.chat.layout.message.holder.IOnCustomMessageDrawListener;
import com.tencent.qcloud.tim.uikit.modules.chat.layout.message.holder.MessageBaseHolder;
import com.tencent.qcloud.tim.uikit.modules.chat.layout.message.holder.MessageCustomHolder;
import com.tencent.qcloud.tim.uikit.modules.message.MessageInfo;
import com.tencent.qcloud.tim.uikit.modules.message.MessageInfoUtil;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.global.AppApplication;
import com.xaqinren.healthyelders.moduleMsg.bean.MCustomMsgBean;

import java.util.List;

public class CustomChatController implements TUIChatControllerListener {
    private static final String TAG = CustomChatController.class.getSimpleName();

    @Override
    public List<IBaseAction> onRegisterMoreActions() {
        return null;
    }

    @Override
    public IBaseInfo createCommonInfoFromTimMessage(V2TIMMessage timMessage) {
        if (timMessage.getElemType() == V2TIMMessage.V2TIM_ELEM_TYPE_CUSTOM) {
            V2TIMCustomElem customElem = timMessage.getCustomElem();
            if (customElem == null || customElem.getData() == null) {
                return null;
            }
            MCustomMsgBean customMessage = null;
            try {
                customMessage = new Gson().fromJson(new String(customElem.getData()), MCustomMsgBean.class);
            } catch (Exception e) {
                //new一个
            }
            if (customMessage != null && customMessage.msgType != 0) {
                MessageInfo messageInfo = new CustomMessageInfo();
                messageInfo.setMsgType(MessageInfo.MSG_TYPE_CUSTOM);
                MessageInfoUtil.setMessageInfoCommonAttributes(messageInfo, timMessage);
                switch (customMessage.msgType) {
                    case 1:
                        messageInfo.setExtra("[视频分享]");
                        break;
                    case 2:
                        messageInfo.setExtra("[日记分享]");
                        break;
                    default:
                        messageInfo.setExtra("[自定义消息]");
                        break;
                }

                return messageInfo;
            }
        }
        return null;
    }

    @Override
    public IBaseViewHolder createCommonViewHolder(ViewGroup parent, int viewType) {
        //判断是不是自定义类型
        if (viewType != CustomMessageInfo.MSG_TYPE_CUSTOM) {
            return null;
        }
        if (parent == null) {
            return null;
        }
        LayoutInflater inflater = LayoutInflater.from(AppApplication.getContext());
        View contentView = inflater.inflate(R.layout.message_adapter_item_content, parent, false);
        return new CustomViewHolder(contentView);
    }

    @Override
    public boolean bindCommonViewHolder(IBaseViewHolder baseViewHolder, IBaseInfo baseInfo, int position) {
        if (baseViewHolder instanceof ICustomMessageViewGroup && baseInfo instanceof CustomMessageInfo) {
            ICustomMessageViewGroup customHolder = (ICustomMessageViewGroup) baseViewHolder;
            MessageInfo msg = (MessageInfo) baseInfo;
            new CustomMessageDraw().onDraw(customHolder, msg, position);
            return true;
        }
        return false;
    }

    static class CustomMessageInfo extends MessageInfo {
        // 消息类型 ID ，不可重复，包括不能与内置消息类型重复，建议使用大于 100002 的数字
        public static final int MSG_TYPE_SHARE_VIDEO = 100002;
        public static final int MSG_TYPE_SHARE_RIJI = 100003;
    }

    static class CustomViewHolder extends MessageCustomHolder {

        public CustomViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class CustomConversationController implements TUIConversationControllerListener {

        //消息列表显示
        @Override
        public CharSequence getConversationDisplayString(IBaseInfo baseInfo) {
            if (baseInfo instanceof CustomMessageInfo) {
                if (!TextUtils.isEmpty(((CustomMessageInfo) baseInfo).getExtra().toString())) {
                    return ((CustomMessageInfo) baseInfo).getExtra().toString();
                }
                return "[自定义消息]";
            }
            return null;
        }
    }

    public static class CustomMessageDraw implements IOnCustomMessageDrawListener {

        /**
         * 自定义消息渲染时，会调用该方法，本方法实现了自定义消息的创建，以及交互逻辑
         *
         * @param parent 自定义消息显示的父View，需要把创建的自定义消息view添加到parent里
         * @param info   消息的具体信息
         */
        @Override
        public void onDraw(ICustomMessageViewGroup parent, MessageInfo info, int position) {
            // 获取到自定义消息的json数据
            if (info.getTimMessage().getElemType() != V2TIMMessage.V2TIM_ELEM_TYPE_CUSTOM) {
                return;
            }
            V2TIMCustomElem elem = info.getTimMessage().getCustomElem();
            // 自定义的json数据，需要解析成bean实例
            MCustomMsgBean data = null;
            try {
                data = new Gson().fromJson(new String(elem.getData()), MCustomMsgBean.class);
            } catch (Exception e) {
                Log.w(TAG, "invalid json: " + new String(elem.getData()) + " " + e.getMessage());
            }
            if (data == null) {
                Log.e(TAG, "No Custom Data: " + new String(elem.getData()));
            } else if (data.msgType != 0) {
                if (parent instanceof MessageBaseHolder) {
                    CustomTIMUIController.onDraw(parent, data, position, ((MessageBaseHolder) parent).getOnItemClickListener(), info);
                }
            } else {
                Log.w(TAG, "unsupported version: " + data);
            }
        }
    }

}

