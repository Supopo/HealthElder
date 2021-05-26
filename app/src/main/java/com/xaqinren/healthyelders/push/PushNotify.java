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

import java.util.Random;

/**
 * 透传消息,展示
 */
public class PushNotify {
    private static NotificationManager manager ;
    public static void showNotify(Context context,String data) {
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
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.icon_app);
                Notification notification = new NotificationCompat.Builder(context, "myChannelId")
                .setAutoCancel(true)
                .setContentTitle("这是一个通知标题")
                .setContentText("这是通知内容")
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

    private static Intent createIntent(String url) {
        //协议打开草稿箱
//        String url = "jkzl://app_open/draft_activity?key=value";
        Intent action = new Intent(Intent.ACTION_VIEW);
        StringBuilder builder = new StringBuilder();
        builder.append(url);
        action.setData(Uri.parse(builder.toString()));
        return action;
    }
}

