package com.nowfloats.RiaFCM;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.freshdesk.hotline.Hotline;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.MixPanelController;
import com.thinksity.R;

import java.util.Map;

/**
 * Created by NowFloats on 05-10-2016.
 */

public class RiaFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    public static String deepLinkUrl;
    private SharedPreferences pref;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        pref = getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);

        if (Hotline.isHotlineNotification(remoteMessage)) {
            Hotline.getInstance(this).handleFcmMessage(remoteMessage);
        } else {
            //Calling method to generate notification
            sendNotification(remoteMessage.getData());
            /*Log.v("notif",remoteMessage.getData().get("title"));
            Log.v("notif",remoteMessage.getSentTime()+"  "+String.valueOf(System.currentTimeMillis()));*/
            Constants.GCM_Msg = true;
            //Handle notifications with data payload for your app
        }
        Log.v("Message", "received bubble");
    }

    private static final String SAM_BUBBLE_MSG = "I have Got some data";
    private static final String SAM_BUBBLE_MSG_KEY = "100";

    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void sendNotification(Map<String, String> message) {

        Log.d("Message", message.toString());

        if (message == null || message.size() == 0) {

        } else {
            if ((message.containsKey("mp_message") && message.get("mp_message").equalsIgnoreCase(SAM_BUBBLE_MSG))
                    || (message.containsKey("mp_message_key") && message.get("mp_message_key").equalsIgnoreCase(SAM_BUBBLE_MSG_KEY))) {
                MixPanelController.track(MixPanelController.SAM_BUBBLE_NOTIFICATION, null);
                pref.edit().putBoolean(Key_Preferences.HAS_SUGGESTIONS, true).apply();
            } else {
                deepLinkUrl = message.get("url");
                //Log.v("ggg","notif "+deepLinkUrl);
                if (deepLinkUrl != null && !deepLinkUrl.contains(Constants.PACKAGE_NAME)) {
                    return;
                }
                String title = message.get("title");
                Intent intent = null;
                if (!Util.isNullOrEmpty(deepLinkUrl)) {
                    final PackageManager manager = getPackageManager();
                    intent = manager.getLaunchIntentForPackage(getPackageName());
                    intent.putExtra("from", "notification");
                    intent.putExtra("url", deepLinkUrl);
                    if(deepLinkUrl.contains(getString(R.string.facebook_chat)))
                    {
                        intent.putExtra("user_id",message.get("user_id"));
                    }

                }
                PendingIntent pendingIntent = null;
                if (intent != null) {
                    pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                }

        /*intent.putExtra("payload", payload);
        intent.putExtra("notification", notificationClick);*/

                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.app_launcher2)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.app_launcher))
                        .setContentText(message.get("mp_message"))
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setColor(ContextCompat.getColor(this, R.color.primaryColor))
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message.get("mp_message")))
                        .setPriority(NotificationCompat.PRIORITY_HIGH);
                if (!Util.isNullOrEmpty(title)) {
                    notificationBuilder.setContentTitle(title);
                } else {
                    notificationBuilder.setContentTitle(getResources().getString(R.string.app_name));
                }
                if (pendingIntent != null) {
                    notificationBuilder.setContentIntent(pendingIntent);
                }

                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                notificationManager.notify(0, notificationBuilder.build());
            }
        }


    }
}
