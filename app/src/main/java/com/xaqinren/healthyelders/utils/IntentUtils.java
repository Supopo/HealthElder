package com.xaqinren.healthyelders.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.SmsManager;

public class IntentUtils {
    /**
     * 打开系统短信
     * @param context
     * @param number
     * @param content
     */
    public static void sendSMS(Context context, String number, String content) {
        Uri uri = Uri.parse("smsto:" + number);
        Intent sendIntent = new Intent(Intent.ACTION_SENDTO, uri);
        sendIntent.putExtra("sms_body", content);
        context.startActivity(sendIntent);
    }

    public static void sendPhone(Context context, String number) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + number);
        intent.setData(data);
        context.startActivity(intent);
    }
}
