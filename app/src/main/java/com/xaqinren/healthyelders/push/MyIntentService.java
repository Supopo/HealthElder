package com.xaqinren.healthyelders.push;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;
import com.igexin.sdk.message.BindAliasCmdMessage;
import com.igexin.sdk.message.FeedbackCmdMessage;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTNotificationMessage;
import com.igexin.sdk.message.GTTransmitMessage;
import com.igexin.sdk.message.SetTagCmdMessage;
import com.igexin.sdk.message.UnBindAliasCmdMessage;
import com.xaqinren.healthyelders.BuildConfig;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.utils.LogUtils;

public class MyIntentService extends GTIntentService {
    private String TAG = getClass().getSimpleName();
    /**
     * 为了观察透传数据变化.
     */
    private static int cnt;
    @Override
    public void onReceiveServicePid(Context context, int i) {

    }
    // 接收 cid
    @Override
    public void onReceiveClientId(Context context, String s) {
        LogUtils.e(TAG, "onReceiveClientId ->" + s);
    }

    // 处理透传消息
    @Override
    public void onReceiveMessageData(Context context, GTTransmitMessage msg) {
        LogUtils.e(TAG, "onReceiveMessageData ->" + JSON.toJSONString(msg));
        String appid = msg.getAppid();
        String taskid = msg.getTaskId();
        String messageid = msg.getMessageId();
        byte[] payload = msg.getPayload();
        String pkg = msg.getPkgName();
        String cid = msg.getClientId();

        // 第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
        boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);
        Log.d(TAG, "call sendFeedbackMessage = " + (result ? "success" : "failed"));

        Log.d(TAG, "onReceiveMessageData -> " + "appid = " + appid + "\ntaskid = " + taskid + "\nmessageid = " + messageid + "\npkg = " + pkg
                + "\ncid = " + cid);

        if (payload == null) {
            Log.e(TAG, "receiver payload = null");
        } else {
            String data = new String(payload);
            Log.d(TAG, "receiver payload = " + data);
            PushNotify.showNotify(this, data);
        }
    }
    // cid 离线上线通知
    @Override
    public void onReceiveOnlineState(Context context, boolean b) {
        LogUtils.e(TAG, "onReceiveOnlineState ->" + b);
    }

    // 各种事件处理回执
    @Override
    public void onReceiveCommandResult(Context context, GTCmdMessage cmdMessage) {
        LogUtils.e(TAG, "onReceiveCommandResult ->" + JSON.toJSONString(cmdMessage));
    }
    // 通知到达，只有个推通道下发的通知会回调此方法
    @Override
    public void onNotificationMessageArrived(Context context, GTNotificationMessage gtNotificationMessage) {
        LogUtils.e(TAG, "onNotificationMessageArrived ->" + JSON.toJSONString(gtNotificationMessage));
    }
    // 通知点击，只有个推通道下发的通知会回调此方法
    @Override
    public void onNotificationMessageClicked(Context context, GTNotificationMessage gtNotificationMessage) {
        LogUtils.e(TAG, "onNotificationMessageClicked ->" + JSON.toJSONString(gtNotificationMessage));
    }

    public void sendRxMessage() {

    }
}
