package hcm.nnbinh.sendmessage.service;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.telephony.SmsMessage;

import hcm.nnbinh.sendmessage.R;

/**
 * Created by BinhNguyen on 5/8/2017.
 */

public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Object[] smsExtra = (Object[]) intent.getExtras().get("pdus");
        String body = "";

        for (int i = 0; i < smsExtra.length; ++i) {
            SmsMessage sms = SmsMessage.createFromPdu((byte[]) smsExtra[i]);
            body += sms.getMessageBody();
        }

        Notification notification = new Notification.Builder(context)
                .setContentText(body)
                .setContentTitle("New Message")
                .setSmallIcon(R.mipmap.ic_alert)
                .setStyle(new Notification.BigTextStyle().bigText(body))
                .build();
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(1, notification);
    }
}
