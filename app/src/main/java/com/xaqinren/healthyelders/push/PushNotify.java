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
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.global.InfoCache;
import com.xaqinren.healthyelders.moduleMsg.ImManager;

import java.util.Random;

import me.goldze.mvvmhabit.bus.RxBus;

/**
 * 透传消息,展示
 */
public class PushNotify {
    private static NotificationManager manager ;

    public static void showNotify(Context context,PayLoadBean data) {
        if (manager==null)
            manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //创建Notification，传入Context和channelId

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
                .setContentTitle(com.xaqinren.healthyelders.moduleMsg.Constant.getNameByGroup(data.messageGroup))
                .setContentText(PayLoadBean.getInteractiveMessageBody(data))
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.push_small)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.push))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .build();
        manager.notify(r, notification);
//        pushMessage(data);
        RxBus.getDefault().post(data);
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
     * 点击打开
     * @param url
     * @return
     */
    private static Intent createIntent(PayLoadBean url) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("jkzl://app_open");
        if (!InfoCache.getInstance().checkLogin()) {
            buffer.append("/main_activity");
        }else if (url.messageGroup.equals(com.xaqinren.healthyelders.moduleMsg.Constant.INTERACTIVE_MESSAGE)) {
            //互动
            buffer.append("/interactive_activity");
        } else if (url.messageGroup.equals(com.xaqinren.healthyelders.moduleMsg.Constant.SYSTEM)) {
            //系统
            buffer.append("/sys_msg_activity");
        } else if (url.messageGroup.equals(com.xaqinren.healthyelders.moduleMsg.Constant.FANS)) {
            //粉丝
            buffer.append("/fans_msg_activity");
        } else if (url.messageGroup.equals(com.xaqinren.healthyelders.moduleMsg.Constant.LIVE)) {
            //直播
            buffer.append("/live_msg_activity");
        } else if (url.messageGroup.equals(com.xaqinren.healthyelders.moduleMsg.Constant.SERVICE)) {
            //服务
            buffer.append("/service_msg_activity");
        } else if (url.messageGroup.equals(com.xaqinren.healthyelders.moduleMsg.Constant.WALLET)) {
            //钱包
            buffer.append("/wallet_msg_activity");
        } else if (url.messageGroup.equals(com.xaqinren.healthyelders.moduleMsg.Constant.CUSTOMER_SERVICE)) {
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

