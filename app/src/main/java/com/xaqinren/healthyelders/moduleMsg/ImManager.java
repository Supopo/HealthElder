package com.xaqinren.healthyelders.moduleMsg;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMFriendshipManager;
import com.tencent.imsdk.TIMUserProfile;
import com.tencent.imsdk.conversation.Conversation;
import com.tencent.imsdk.v2.V2TIMConversation;
import com.tencent.qcloud.tim.uikit.TUIKit;
import com.tencent.qcloud.tim.uikit.base.IMEventListener;
import com.tencent.qcloud.tim.uikit.base.IUIKitCallBack;
import com.tencent.qcloud.tim.uikit.modules.chat.C2CChatManagerKit;
import com.tencent.qcloud.tim.uikit.modules.chat.base.ChatManagerKit;
import com.tencent.qcloud.tim.uikit.modules.conversation.ConversationManagerKit;
import com.tencent.qcloud.tim.uikit.modules.conversation.base.ConversationInfo;
import com.tencent.qcloud.tim.uikit.modules.message.MessageInfo;
import com.tencent.qcloud.tim.uikit.modules.message.MessageInfoUtil;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.utils.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.goldze.mvvmhabit.utils.SPUtils;
import me.goldze.mvvmhabit.utils.compression.Luban;
import retrofit2.http.PUT;

public class ImManager {
    private static final String TAG = "ImManager";
    private static ImManager imManager = new ImManager();
    private List<ConversationInfo> localCon;
    private ImManager(){}
    public static ImManager getInstance(){
        return imManager;
    }


    public void init() {
        localCon = getLocalConversation();
        if (localCon == null) {
            localCon = new ArrayList<>();
        }
        TUIKit.addIMEventListener(new IMEventListener() {
            @Override
            public void onRefreshConversation(List<V2TIMConversation> conversations) {
                super.onRefreshConversation(conversations);
                LogUtils.e(TAG, "onRefreshConversation -> " + conversations.size());
            }
        });
        ConversationManagerKit.getInstance().setLoadSelfConversation(() -> localCon);
        ConversationManagerKit.getInstance().loadConversation(null);

    }

    /**
     * 更新头像
     * @param mIconUrl
     */
    public void upSelfInfo(String mIconUrl) {
        HashMap<String, Object> hashMap = new HashMap<>();
        // 头像，mIconUrl 就是您上传头像后的 URL，可以参考 Demo 中的随机头像作为示例
        if (!TextUtils.isEmpty(mIconUrl)) {
            hashMap.put(TIMUserProfile.TIM_PROFILE_TYPE_KEY_FACEURL, mIconUrl);
        }
        TIMFriendshipManager.getInstance().modifySelfProfile(hashMap, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                LogUtils.e(TAG, "modifySelfProfile err code = " + i + ", desc = " + s);
            }
            @Override
            public void onSuccess() {
                LogUtils.e(TAG, "modifySelfProfile success");
            }
        });
    }

    private void saveConversationToSp(ConversationInfo conversation) {
        boolean flag = false;
        for (ConversationInfo info : localCon) {
            if (info.getId().equals(conversation.getId())) {
                flag = true;
                info.getLastMessage().setExtra(conversation.getLastMessage().getExtra());
                info.getLastMessage().setMsgTime(conversation.getLastMessage().getMsgTime());
                info.setUnRead(conversation.getUnRead());
                break;
            }
        }
        if (!flag) {
            localCon.add(0, conversation);
        }
       saveSp();
    }

    private void saveSp() {
        SPUtils.getInstance().put(Constant.SP_KEY_CONVERSATION, JSON.toJSONString(localCon));
    }

    private List<ConversationInfo> getLocalConversation() {
        String json = SPUtils.getInstance().getString(Constant.SP_KEY_CONVERSATION, "");
        List<ConversationInfo> temp = JSON.parseArray(json, ConversationInfo.class);
        return temp;
    }

    public void saveConversationToLocal(String id , String title , String extra , String iconUrl) {
        ConversationInfo conversation;

        List<Object> urlIons = new ArrayList<>();
        urlIons.add(iconUrl);
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setMsgType(MessageInfo.MSG_TYPE_TEXT);
        messageInfo.setExtra(extra);
        messageInfo.setMsgTime(System.currentTimeMillis()/1000);
        boolean flag;
        conversation = getConversationById(id);
        if (conversation == null) {
            conversation = new ConversationInfo();
            conversation.setUnRead(1);
            flag = false;
        }else{
            flag = true;
            conversation.setUnRead(conversation.getUnRead() + 1);
        }

        conversation.setType(ConversationInfo.TYPE_COMMON);
        conversation.setId(id);
        conversation.setConversationId(title);
        conversation.setGroup(false);
        conversation.setLastMessageTime(System.currentTimeMillis()/1000);
        conversation.setTitle(title);
        conversation.setTop(false);

        conversation.setIconUrlList(urlIons);
        conversation.setLastMessage(messageInfo);
        if (flag) {
            ConversationManagerKit.getInstance().updateConversation(conversation);
        }else{
            ConversationManagerKit.getInstance().addConversationTop(conversation);
        }
        saveConversationToSp(conversation);
    }

    private ConversationInfo getConversationById(String id) {
        for (int i = 0; i < localCon.size(); i++) {
            if (localCon.get(i).getId().equals(id)) {
                return localCon.remove(i);
            }
        }
        return null;
    }

    public void delConversationLocal(String id) {
        boolean flag = false;
        for (int i = 0; i < localCon.size(); i++) {
            if (localCon.get(i).getId().equals(id)) {
                localCon.remove(i);
                flag = true;
                break;
            }
        }
        if (flag) {
            ConversationManagerKit.getInstance().deleteConversation(id, false);
            saveSp();
        }
    }

    public void clearUnreadById(String id) {
        ConversationInfo info =  getConversationById(id);
        info.setUnRead(0);
        ConversationManagerKit.getInstance().updateConversation(info);
        saveConversationToSp(info);
    }

    public static void testAddConversation() {
        ImManager.getInstance().saveConversationToLocal(Constant.CONVERSATION_SYS_ID,"系统消息","明天下午毁灭","https://img2.baidu.com/it/u=3355464299,584008140&fm=26&fmt=auto&gp=0.jpg");
        ImManager.getInstance().saveConversationToLocal(Constant.CONVERSATION_INT_ID,"互动消息","XXX对您点赞","https://img2.baidu.com/it/u=3355464299,584008140&fm=26&fmt=auto&gp=0.jpg");
        ImManager.getInstance().saveConversationToLocal(Constant.CONVERSATION_FANS_ID,"粉丝消息","XXX关注了您","https://img2.baidu.com/it/u=3355464299,584008140&fm=26&fmt=auto&gp=0.jpg");
        ImManager.getInstance().saveConversationToLocal(Constant.CONVERSATION_LIVE_ID,"直播消息","今天XXX点您要直播了","https://img2.baidu.com/it/u=3355464299,584008140&fm=26&fmt=auto&gp=0.jpg");
    }
}