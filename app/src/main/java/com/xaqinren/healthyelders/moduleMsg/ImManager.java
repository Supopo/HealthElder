package com.xaqinren.healthyelders.moduleMsg;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMFriendshipManager;
import com.tencent.imsdk.TIMUserProfile;
import com.tencent.imsdk.v2.V2TIMConversation;
import com.tencent.qcloud.tim.uikit.TUIKit;
import com.tencent.qcloud.tim.uikit.base.IMEventListener;
import com.tencent.qcloud.tim.uikit.modules.conversation.ConversationManagerKit;
import com.tencent.qcloud.tim.uikit.modules.conversation.base.ConversationInfo;
import com.tencent.qcloud.tim.uikit.modules.message.MessageInfo;
import com.xaqinren.healthyelders.apiserver.ApiServer;
import com.xaqinren.healthyelders.apiserver.CustomObserver;
import com.xaqinren.healthyelders.apiserver.MBaseResponse;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.http.RetrofitClient;
import com.xaqinren.healthyelders.moduleLogin.bean.UserInfoBean;
import com.xaqinren.healthyelders.moduleMsg.bean.GroupIconBean;
import com.xaqinren.healthyelders.push.PayLoadBean;
import com.xaqinren.healthyelders.utils.ACache;
import com.xaqinren.healthyelders.utils.LogUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.goldze.mvvmhabit.utils.RxUtils;

public class ImManager {
    private static final String TAG = "ImManager";
    private static ImManager imManager = new ImManager();
    private List<ConversationInfo> localCon;
    private String rootDir;
    private String fileName;
    private ACache aCache;

    private ImManager() {
    }

    public static ImManager getInstance() {
        return imManager;
    }

    private int unReadCount;
    private OnUnReadWatch onUnReadWatch;

    public void setOnUnReadWatch(OnUnReadWatch onUnReadWatch) {
        this.onUnReadWatch = onUnReadWatch;
    }

    public void init(String rootDir) {
        getIcon();
        this.rootDir = rootDir;
        getFileName();
        aCache = ACache.get(new File(this.rootDir));
        localCon = getLocalConversation();
        if (localCon == null) {
            localCon = new ArrayList<>();
        }

        TUIKit.addIMEventListener(new IMEventListener() {
            @Override
            public void onRefreshConversation(List<V2TIMConversation> conversations) {
                super.onRefreshConversation(conversations);
                LogUtils.e(TAG, "onRefreshConversation -> " + conversations.size());
                if (Constant.ENABLE_CHAT) {
                    int tempCount = 0;
                    for (V2TIMConversation conversation : conversations) {
                        int type = conversation.getType();
                        if (type == V2TIMConversation.V2TIM_C2C) {
                            tempCount += conversation.getUnreadCount();
                        }
                    }
                    if (onUnReadWatch != null) {
                        onUnReadWatch.onUnReadWatch(unReadCount + tempCount);
                    }
                }
            }
        });
        ConversationManagerKit.getInstance().setLoadSelfConversation(() -> localCon);
        if (onUnReadWatch != null) {
            onUnReadWatch.onUnReadWatch(unReadCount);
        }
    }

    public void getFileName() {
        UserInfoBean bean = UserInfoMgr.getInstance().getUserInfo();
        if (bean != null) {
            fileName = bean.getId();
        }
    }

    //退出登录用的
    public void clear() {
        clearConversationByList();
        unReadCount = 0;
        localCon = new ArrayList<>();
        fileName = null;
        if (onUnReadWatch != null) {
            onUnReadWatch.onUnReadWatch(unReadCount);
        }
    }

    /**
     * 更新头像
     *
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
        if (fileName == null) {
            return;
        }

        aCache.put(fileName, JSON.toJSONString(localCon));
        if (onUnReadWatch != null) {
            onUnReadWatch.onUnReadWatch(unReadCount);
        }
    }

    private List<ConversationInfo> getLocalConversation() {
        if (fileName == null) {
            return null;
        }
        unReadCount = 0;
        String json = aCache.getAsString(fileName);
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        List<ConversationInfo> temp = JSON.parseArray(json, ConversationInfo.class);
        if (temp != null) {
            for (ConversationInfo info : temp) {
                unReadCount += info.getUnRead();
                //判断有没有系统消息
                if (info.getId().equals(Constant.CONVERSATION_INT_ID)) {
                    hasIntMsg = true;
                } else if (info.getId().equals(Constant.CONVERSATION_FANS_ID)) {
                    hasFansMsg = true;
                }
            }
        }


        return temp;
    }

    private boolean hasIntMsg;
    private boolean hasFansMsg;

    //初次手动添加互动-关注消息
    public void saveConversationToLocal(boolean showRead, String id, String title, String extra, String iconUrl) {
        ConversationInfo conversation;

        List<Object> urlIons = new ArrayList<>();
        urlIons.add(iconUrl);
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setMsgType(MessageInfo.MSG_TYPE_TEXT);
        messageInfo.setExtra(extra);
        //手动添加时间去掉
        messageInfo.setMsgTime(showRead ? System.currentTimeMillis() / 1000 : 0);
        boolean flag;
        conversation = getConversationById(id);
        if (conversation == null) {
            conversation = new ConversationInfo();
            conversation.setUnRead(showRead ? 1 : 0);
            flag = false;
        } else {
            flag = true;
            if (showRead) {
                conversation.setUnRead(conversation.getUnRead() + 1);
            } else {
                conversation.setUnRead(0);
            }
        }

        conversation.setType(ConversationInfo.TYPE_COMMON);
        conversation.setId(id);
        conversation.setConversationId(title);
        conversation.setGroup(false);
        conversation.setLastMessageTime(System.currentTimeMillis() / 1000);
        conversation.setOrderKey(showRead ? conversation.getLastMessageTime() : 9876543210L);
        conversation.setTitle(title);
        conversation.setTop(false);
        conversation.setIconUrlList(urlIons);
        conversation.setLastMessage(messageInfo);
        if (flag) {
            ConversationManagerKit.getInstance().updateConversation(conversation);
        } else {
            ConversationManagerKit.getInstance().addConversationTop(conversation);
        }
        if (showRead) {
            unReadCount++;
        }
        saveConversationToSp(conversation);
    }

    public void saveConversationToLocal(String id, String title, String extra, String iconUrl) {
        ConversationInfo conversation;

        List<Object> urlIons = new ArrayList<>();
        urlIons.add(iconUrl);
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setMsgType(MessageInfo.MSG_TYPE_TEXT);
        messageInfo.setExtra(extra);
        messageInfo.setMsgTime(System.currentTimeMillis() / 1000);
        boolean flag;
        conversation = getConversationById(id);
        if (conversation == null) {
            conversation = new ConversationInfo();
            conversation.setUnRead(1);
            flag = false;
        } else {
            flag = true;
            conversation.setUnRead(conversation.getUnRead() + 1);
        }

        conversation.setType(ConversationInfo.TYPE_COMMON);
        conversation.setId(id);
        conversation.setConversationId(title);
        conversation.setGroup(false);
        conversation.setLastMessageTime(System.currentTimeMillis() / 1000);
        if (id.equals("22222") || id.equals("33333")) {
            conversation.setOrderKey(9876543210L);
        } else {
            conversation.setOrderKey(conversation.getLastMessageTime());
        }
        conversation.setTitle(title);
        conversation.setTop(false);

        conversation.setIconUrlList(urlIons);
        conversation.setLastMessage(messageInfo);
        if (flag) {
            ConversationManagerKit.getInstance().updateConversation(conversation);
        } else {
            ConversationManagerKit.getInstance().addConversationTop(conversation);
        }
        unReadCount++;
        saveConversationToSp(conversation);
    }

    private ConversationInfo getConversationById(String id) {
        for (int i = 0; i < localCon.size(); i++) {
            if (localCon.get(i).getId().equals(id)) {
                return localCon.get(i);
            }
        }
        return null;
    }

    public void delConversationLocal(String id) {
        boolean flag = false;
        for (int i = 0; i < localCon.size(); i++) {
            if (localCon.get(i).getId().equals(id)) {
                unReadCount -= localCon.get(i).getUnRead();
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

    //在列表上清除
    public void clearConversationByList() {
        if (localCon != null) {
            for (ConversationInfo info : localCon) {
                ConversationManagerKit.getInstance().deleteConversation(info.getId(), false);
            }
        }
    }

    public void clearUnreadById(String id) {
        ConversationInfo info = getConversationById(id);
        if (info == null) {
            return;
        }
        unReadCount -= info.getUnRead();
        info.setUnRead(0);
        ConversationManagerKit.getInstance().updateConversation(info);
        saveConversationToSp(info);
    }

    public int getUnreadCount() {
        return unReadCount;
    }

    public void pushMessage(PayLoadBean url) {
        if (url.messageGroup.equals(com.xaqinren.healthyelders.moduleMsg.Constant.INTERACTIVE_MESSAGE)) {
            //互动消息
            if (url.messageType.equals(com.xaqinren.healthyelders.moduleMsg.Constant.FAVORITE)) {
                ImManager.getInstance().saveConversationToLocal(Constant.CONVERSATION_INT_ID, com.xaqinren.healthyelders.moduleMsg.Constant.getNameByGroup(url.messageGroup), url.sendUser.nickname + url.content.body, groupIconBean.getiNTERACTIVE_MESSAGE());
            } else if (url.messageType.equals(com.xaqinren.healthyelders.moduleMsg.Constant.AT)) {
                ImManager.getInstance().saveConversationToLocal(Constant.CONVERSATION_INT_ID, com.xaqinren.healthyelders.moduleMsg.Constant.getNameByGroup(url.messageGroup), url.sendUser.nickname + url.content.title, groupIconBean.getiNTERACTIVE_MESSAGE());
            } else if (url.messageType.equals(com.xaqinren.healthyelders.moduleMsg.Constant.COMMENT)) {
                ImManager.getInstance().saveConversationToLocal(Constant.CONVERSATION_INT_ID, com.xaqinren.healthyelders.moduleMsg.Constant.getNameByGroup(url.messageGroup), url.sendUser.nickname + url.content.title, groupIconBean.getiNTERACTIVE_MESSAGE());
            } else if (url.messageType.equals(com.xaqinren.healthyelders.moduleMsg.Constant.REPLY)) {
                ImManager.getInstance().saveConversationToLocal(Constant.CONVERSATION_INT_ID, com.xaqinren.healthyelders.moduleMsg.Constant.getNameByGroup(url.messageGroup), url.sendUser.nickname + url.content.title, groupIconBean.getiNTERACTIVE_MESSAGE());
            }
        } else if (url.messageGroup.equals(com.xaqinren.healthyelders.moduleMsg.Constant.SYSTEM)) {
            //系统消息
            ImManager.getInstance().saveConversationToLocal(Constant.CONVERSATION_SYS_ID, com.xaqinren.healthyelders.moduleMsg.Constant.getNameByGroup(url.messageGroup), url.sendUser.nickname + url.content.body, groupIconBean.getsYSTEM());
        } else if (url.messageGroup.equals(com.xaqinren.healthyelders.moduleMsg.Constant.FANS)) {
            //粉丝
            ImManager.getInstance().saveConversationToLocal(Constant.CONVERSATION_FANS_ID, com.xaqinren.healthyelders.moduleMsg.Constant.getNameByGroup(url.messageGroup), url.sendUser.nickname + url.content.body, groupIconBean.getfANS());
        } else if (url.messageGroup.equals(com.xaqinren.healthyelders.moduleMsg.Constant.LIVE)) {
            //直播
            ImManager.getInstance().saveConversationToLocal(Constant.CONVERSATION_LIVE_ID, com.xaqinren.healthyelders.moduleMsg.Constant.getNameByGroup(url.messageGroup), url.sendUser.nickname + url.content.body, groupIconBean.getlIVE());
        } else if (url.messageGroup.equals(com.xaqinren.healthyelders.moduleMsg.Constant.SERVICE)) {
            //服务
            ImManager.getInstance().saveConversationToLocal(Constant.CONVERSATION_SERVICE_ID, com.xaqinren.healthyelders.moduleMsg.Constant.getNameByGroup(url.messageGroup), url.sendUser.nickname + url.content.body, groupIconBean.getsERVICE());
        } else if (url.messageGroup.equals(com.xaqinren.healthyelders.moduleMsg.Constant.WALLET)) {
            //钱包
            ImManager.getInstance().saveConversationToLocal(Constant.CONVERSATION_WALLET_ID, com.xaqinren.healthyelders.moduleMsg.Constant.getNameByGroup(url.messageGroup), url.sendUser.nickname + url.content.body, groupIconBean.getwALLET());
        } else if (url.messageGroup.equals(com.xaqinren.healthyelders.moduleMsg.Constant.CUSTOMER_SERVICE)) {
            //客服
            ImManager.getInstance().saveConversationToLocal(Constant.CONVERSATION_CUSTOMER_SERVICE_ID, com.xaqinren.healthyelders.moduleMsg.Constant.getNameByGroup(url.messageGroup), url.sendUser.nickname + url.content.body, groupIconBean.getcUSTOMER_SERVICE());
        }
    }

    /**
     * 自己发送消息给客服
     */
    public void sendCustomer() {

    }

    public void getCustomer() {

    }

    public interface OnUnReadWatch {
        void onUnReadWatch(int count);
    }

    private GroupIconBean groupIconBean;
    private ApiServer userApi = RetrofitClient.getInstance().create(ApiServer.class);

    //获取系统消息图标
    public void getIcon() {
        if (groupIconBean != null) {
            return;
        }
        userApi.getGroupIcon()
                .compose(RxUtils.schedulersTransformer())  // 线程调度
                .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
                .doOnSubscribe(disposable -> {
                })
                .subscribe(new CustomObserver<MBaseResponse<GroupIconBean>>() {

                    @Override
                    protected void dismissDialog() {
                    }

                    @Override
                    protected void onSuccess(MBaseResponse<GroupIconBean> data) {
                        groupIconBean = data.getData();
                        //判断是否已经插入了

                        if (!hasIntMsg) {
                            //插入互动消息 关注粉丝消息
                            ImManager.getInstance().saveConversationToLocal(false,
                                    Constant.CONVERSATION_INT_ID, com.xaqinren.healthyelders.moduleMsg.Constant.getNameByGroup(com.xaqinren.healthyelders.moduleMsg.Constant.INTERACTIVE_MESSAGE), "点击查看全部", groupIconBean.getiNTERACTIVE_MESSAGE());
                        }

                        if (!hasFansMsg) {
                            ImManager.getInstance().saveConversationToLocal(false,
                                    Constant.CONVERSATION_FANS_ID, com.xaqinren.healthyelders.moduleMsg.Constant.getNameByGroup(com.xaqinren.healthyelders.moduleMsg.Constant.FANS), "点击查看全部", groupIconBean.getfANS());

                        }

                    }
                });
    }

}
