package com.xaqinren.healthyelders.push;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.xaqinren.healthyelders.MainActivity;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleMsg.ImManager;

import java.util.Random;

/**
 * 透传消息,展示
 */
public class PushNotify {
    private static NotificationManager manager ;

    public static void showNotify(Context context,PayLoadBean data) {
        if (manager==null)
            manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //创建Notification，传入Context和channelId
        pushMessage(data);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 233, createIntent(data), PendingIntent.FLAG_ONE_SHOT);
        Random random = new Random();

        int r = random.nextInt(99999999);
        //判断是否为8.0以上：Build.VERSION_CODES.O为26
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //创建通知渠道ID
            String channelId = "myChannelId";
            //创建通知渠道名称
            String channelName = "健康长老透传";
            //创建通知渠道重要性
            int importance = NotificationManager.IMPORTANCE_MAX;
            createNotificationChannel(context, channelId, channelName, importance);
        }
                Notification notification = new NotificationCompat.Builder(context, "myChannelId")
                .setAutoCancel(true)
                .setContentTitle(PayLoadBean.getNameByGroup(data.messageGroup))
                .setContentText(data.sendUser.nickname+data.content.body)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.push_small)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.push))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .build();
        manager.notify(r, notification);
    }
    //创建通知渠道
    @TargetApi(Build.VERSION_CODES.O)
    private static void createNotificationChannel(Context context, String channelId, String channelName, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        channel.setImportance(NotificationManager.IMPORTANCE_HIGH);
        //channel有很多set方法
        //为NotificationManager设置通知渠道
        manager.createNotificationChannel(channel);
    }

    /**
     * 创建会话
     * @param url
     */
    private static void pushMessage(PayLoadBean url) {

        if (url.messageGroup.equals(PayLoadBean.INTERACTIVE_MESSAGE)) {
            //互动消息
            ImManager.getInstance().saveConversationToLocal(Constant.CONVERSATION_INT_ID, PayLoadBean.getNameByGroup(url.messageGroup), url.sendUser.nickname + url.content.body, "http://oss.hjyiyuanjiankang.com/qnx0/M00/00/0E/rBBcQmCvBeuAV7mXAAANf-bU2kQ139.png?w=75&h=75");
        } else if (url.messageGroup.equals(PayLoadBean.SYSTEM)) {
            //系统消息
            ImManager.getInstance().saveConversationToLocal(Constant.CONVERSATION_SYS_ID, PayLoadBean.getNameByGroup(url.messageGroup), url.sendUser.nickname + url.content.body, "http://oss.hjyiyuanjiankang.com/qnx0/M00/00/0E/rBBcQmCvBeuAO5faAAAGkY_MVxo087.png?w=75&h=75");
        } else if (url.messageGroup.equals(PayLoadBean.FANS)) {
            //粉丝
            ImManager.getInstance().saveConversationToLocal(Constant.CONVERSATION_FANS_ID, PayLoadBean.getNameByGroup(url.messageGroup), url.sendUser.nickname + url.content.body, "http://oss.hjyiyuanjiankang.com/qnx0/M00/00/0E/rBBcQmCvBeyAJdZTAAAPp3MftzI340.png?w=75&h=75");
        } else if (url.messageGroup.equals(PayLoadBean.LIVE)) {
            //直播
            ImManager.getInstance().saveConversationToLocal(Constant.CONVERSATION_LIVE_ID, PayLoadBean.getNameByGroup(url.messageGroup), url.sendUser.nickname + url.content.body, "http://oss.hjyiyuanjiankang.com/qnx0/M00/00/0E/rBBcQmCvBeyAUeEDAAAMYchMDkk202.png?w=75&h=75");
        } else if (url.messageGroup.equals(PayLoadBean.SERVICE)) {
            //服务
            ImManager.getInstance().saveConversationToLocal(Constant.CONVERSATION_SERVICE_ID, PayLoadBean.getNameByGroup(url.messageGroup), url.sendUser.nickname + url.content.body, "http://oss.hjyiyuanjiankang.com/qnx0/M00/00/0E/rBBcQmCvBeuAOWM5AAAOp2f1M9w588.png?w=75&h=75");
        } else if (url.messageGroup.equals(PayLoadBean.WALLET)) {
            //钱包
            ImManager.getInstance().saveConversationToLocal(Constant.CONVERSATION_SERVICE_ID, PayLoadBean.getNameByGroup(url.messageGroup), url.sendUser.nickname + url.content.body, "http://oss.hjyiyuanjiankang.com/qnx0/M00/00/0E/rBBcQmCvBeuAOg2mAAAF7Iyfodc757.png?w=75&h=75");
        } else if (url.messageGroup.equals(PayLoadBean.CUSTOMER_SERVICE)) {
            //客服
            ImManager.getInstance().saveConversationToLocal(Constant.CONVERSATION_SERVICE_ID, PayLoadBean.getNameByGroup(url.messageGroup), url.sendUser.nickname + url.content.body, "http://oss.hjyiyuanjiankang.com/qnx0/M00/00/0E/rBBcQmCvBeuAbOAyAAAHXlHrNdM934.png?w=75&h=75");
        }

    }

    /**
     * 点击打开
     * @param url
     * @return
     */
    private static Intent createIntent(PayLoadBean url) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("jkzl://app_open");
        if (url.messageGroup.equals(PayLoadBean.INTERACTIVE_MESSAGE)) {
            //互动
            buffer.append("/");
        } else if (url.messageGroup.equals(PayLoadBean.SYSTEM)) {
            //系统
            buffer.append("/");
        } else if (url.messageGroup.equals(PayLoadBean.FANS)) {
            //粉丝
            buffer.append("/");
        } else if (url.messageGroup.equals(PayLoadBean.LIVE)) {
            //直播
            buffer.append("/");
        } else if (url.messageGroup.equals(PayLoadBean.SERVICE)) {
            //服务
            buffer.append("/");
        } else if (url.messageGroup.equals(PayLoadBean.WALLET)) {
            //钱包
            buffer.append("/");
        } else if (url.messageGroup.equals(PayLoadBean.CUSTOMER_SERVICE)) {
            //客服
            buffer.append("/");
        } else {
            //首页
            buffer.append("/main_activity");
        }


        Intent action = new Intent(Intent.ACTION_VIEW);
        StringBuilder builder = new StringBuilder();
        builder.append(buffer.toString());
        action.setData(Uri.parse(builder.toString()));
        return action;
    }


}

