package com.nowfloats.RiaFCM;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.Constants;
import com.thinksity.R;

import java.util.Map;

/**
 * Created by NowFloats on 05-10-2016.
 */

public class RiaFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    public static String deepLinkUrl;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Displaying data in log
        //It is optional
        //super.onMessageReceived(remoteMessage);
        BoostLog.d(TAG, "From: " + remoteMessage.getFrom());
        BoostLog.d(TAG, "Notification Message Body: " + remoteMessage.getData().toString());

        //Calling method to generate notification
        sendNotification(remoteMessage.getData());
        Constants.GCM_Msg = true;
    }

    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void sendNotification(Map<String, String> message) {
        deepLinkUrl = message.get("url");
        Log.v("ggg","notif "+deepLinkUrl);
        String title = message.get("title");
        Intent intent = null;
        if(!Util.isNullOrEmpty(deepLinkUrl)) {
            final PackageManager manager = getPackageManager();
            intent = manager.getLaunchIntentForPackage(getPackageName());
        }
        PendingIntent pendingIntent = null;
        if(intent!=null) {
            pendingIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);
        }

        /*intent.putExtra("payload", payload);
        intent.putExtra("notification", notificationClick);*/

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.app_launcher2)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        com.mixpanel.android.R.drawable.app_launcher))
                .setContentText(message.get("mp_message"))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message.get("mp_message")))
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        if(!Util.isNullOrEmpty(title)){
            notificationBuilder.setContentTitle(title);
        }else {
            notificationBuilder.setContentTitle(getResources().getString(R.string.app_name));
        }
        if(pendingIntent!=null){
            notificationBuilder.setContentIntent(pendingIntent);
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
}
