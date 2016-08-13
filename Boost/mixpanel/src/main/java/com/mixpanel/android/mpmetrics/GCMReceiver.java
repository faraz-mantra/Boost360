package com.mixpanel.android.mpmetrics;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.mixpanel.android.R;
import com.mixpanel.android.mpmetrics.MixpanelAPI.InstanceProcessor;

/**
 * BroadcastReciever for handling Google Cloud Messaging intents.
 *
 * <p>You can use GCMReciever to report Google Cloud Messaging registration identifiers
 * to Mixpanel, and to display incoming notifications from Mixpanel to
 * the device status bar. Together with {@link MixpanelAPI.People#initPushHandling(String) }
 * this is the simplest way to get up and running with notifications from Mixpanel.
 *
 * <p>To enable GCMReciever in your application, add a clause like the following
 * in your AndroidManifest.xml. (Be sure to replace "YOUR APPLICATION PACKAGE NAME"
 * in the snippet with the actual package name of your app.)
 *
 *<pre>
 *{@code
 *
 * <receiver android:name="com.mixpanel.android.mpmetrics.GCMReceiver"
 * android:permission="com.google.android.c2dm.permission.SEND" >
 * <intent-filter>
 * <action android:name="com.google.android.c2dm.intent.RECEIVE" />
 * <action android:name="com.google.android.c2dm.intent.REGISTRATION" />
 * <category android:name="YOUR APPLICATION PACKAGE NAME" />
 * </intent-filter>
 * </receiver>
 *
 *}
 *</pre>
 *
 * <p>In addition, GCMReciever will also need the following permissions configured
 * in your AndroidManifest.xml file:
 *
 * <pre>
 * {@code
 *
 * <!-- Be sure to change YOUR_PACKAGE_NAME to the real name of your application package -->
 * <permission android:name="YOUR_PACKAGE_NAME.permission.C2D_MESSAGE" android:protectionLevel="signature" />
 * <uses-permission android:name="YOUR_PACKAGE_NAME.permission.C2D_MESSAGE" />
 *
 * <uses-permission android:name="android.permission.INTERNET" />
 * <uses-permission android:name="android.permission.GET_ACCOUNTS" />
 * <uses-permission android:name="com.google.android.c2dm.permission.RECEIVE" />
 * <uses-permission android:name="android.permission.WAKE_LOCK" />
 *
 * }
 * </pre>
 *
 * <p>Once the GCMReciever is configured, the only thing you have to do to
 * get set up Mixpanel messages is call {@link MixpanelAPI.People#identify(String) }
 * with a distinct id for your user, and call {@link MixpanelAPI.People#initPushHandling(String) }
 * with the your Google API project identifier.
 * <pre>
 * {@code
 *
 * MixpanelAPI.People people = mMixpanelAPI.getPeople();
 * people.identify("A USER DISTINCT ID");
 * people.initPushHandling("123456789123");
 *
 * }
 * </pre>
 *
 * <p>If you would prefer to handle either sending a registration id to Mixpanel yourself
 * but allow GCMReciever to handle displaying Mixpanel messages, remove the
 * REGISTRATION intent from the GCMReciever {@code <reciever> } tag, and call
 * {@link MixpanelAPI.People#setPushRegistrationId(String)}
 * in your own REGISTRATION handler.
 *
 * @see MixpanelAPI#getPeople()
 * @see MixpanelAPI.People#initPushHandling(String)
 * @see <a href="https://mixpanel.com/docs/people-analytics/android-push">Getting Started with Android Push Notifications</a>
 */
public class GCMReceiver extends BroadcastReceiver {
    String LOGTAG = "MPGCMReceiver";
    public static String deeplinkUrl;
    public static Bitmap notificationIconBitmap = null;
    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        try{
            if(notificationIconBitmap==null)
                notificationIconBitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.app_launcher);
        }catch(Exception e){e.printStackTrace(); notificationIconBitmap.recycle(); System.gc();}

        if ("com.google.android.c2dm.intent.REGISTRATION".equals(action)) {
            handleRegistrationIntent(intent);
        } else if ("com.google.android.c2dm.intent.RECEIVE".equals(action)) {
            String payload = null;
            String notificationClick = "notification clicked";
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object keys = bundle.get("url");
                if(keys!=null){
                    payload = keys.toString();
                    deeplinkUrl = keys.toString();
                }
            }
            handleNotificationIntent(context, intent, payload, notificationClick);
        }
    }

    private void handleRegistrationIntent(Intent intent) {
        final String registration = intent.getStringExtra("registration_id");
        if (intent.getStringExtra("error") != null) {
            Log.e(LOGTAG, "Error when registering for GCM: " + intent.getStringExtra("error"));
        } else if (registration != null) {
            if (MPConfig.DEBUG) Log.d(LOGTAG, "registering GCM ID: " + registration);
            MixpanelAPI.allInstances(new InstanceProcessor() {
                @Override
                public void process(MixpanelAPI api) {
                    api.getPeople().setPushRegistrationId(registration);
                }
            });
        } else if (intent.getStringExtra("unregistered") != null) {
            if (MPConfig.DEBUG) Log.d(LOGTAG, "unregistering from GCM");
            MixpanelAPI.allInstances(new InstanceProcessor() {
                @Override
                public void process(MixpanelAPI api) {
                    api.getPeople().clearPushRegistrationId();
                }
            });
        }
    }

    private void handleNotificationIntent(Context context, Intent intent, String payload, String notificationClick) {
        String message = intent.getExtras().getString("mp_message");
        if(message == null){
            message = intent.getExtras().getString("message");
        }

        if (message == null) return;
        if (MPConfig.DEBUG) Log.d(LOGTAG, "MP GCM notification received: " + message);

        // Old code --can be removed
        final PackageManager manager = context.getPackageManager();
        final Intent appIntent = manager.getLaunchIntentForPackage(context.getPackageName());
        appIntent.putExtra("payload", payload);
        appIntent.putExtra("notification", notificationClick);

        CharSequence notificationTitle = "";
        int notificationIcon = R.drawable.app_launcher;
//        int notificationIcon = android.R.drawable.sym_def_app_icon;
//        try {
//            final ApplicationInfo appInfo = manager.getApplicationInfo(context.getPackageName(), 0);
//            notificationTitle = manager.getApplicationLabel(appInfo);
//            notificationIcon = appInfo.icon;
//        } catch (final NameNotFoundException e) {
            //In this case, use a blank title and default icon
//        }

        final PendingIntent contentIntent = PendingIntent.getActivity(
                context.getApplicationContext(),
                0,
                appIntent, // add this pass null to intent
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        if (Build.VERSION.SDK_INT < 11) {
            showNotificationSDKLessThan11(context, contentIntent, notificationIcon, notificationTitle, message);
        } else {
            showNotificationSDK11OrHigher(context, contentIntent, notificationIcon, notificationTitle, message, payload);
        }
    }

//    @Override
//    protected void onMessage(Context context, Intent intent)
//    {
//        Konotor instance = Konotor.getInstance(context);
//        if(instance.isKonotorMessage(intent))
//        {
//            instance.handleGcmOnMessage(intent);
//            return;
//        }
//
//        // Your application code goes here
//    }
    @SuppressWarnings("deprecation")
    @TargetApi(8)
    private void showNotificationSDKLessThan11(Context context, PendingIntent intent, int notificationIcon, CharSequence title, CharSequence message) {
        if(title.equals("")){
            title = "NowFloats Boost";
        }
        final NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        final Notification n = new Notification(notificationIcon, message, System.currentTimeMillis());
        n.flags |= Notification.FLAG_AUTO_CANCEL;
        n.setLatestEventInfo(context, title, message, intent);
        nm.notify(0, n);
    }

    private void showNotificationSDK11OrHigher(Context context, PendingIntent intent, int notificationIcon,
                                               CharSequence title, CharSequence message, String payload) {
//        final NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
//        final Notification n = new Notification.Builder(context).
//                    setSmallIcon(notificationIcon).
//                    setTicker(message).
//                    setWhen(System.currentTimeMillis()).
//                    setContentTitle(title).
//                    setContentText(message).
//                    setContentIntent(intent).
//                    build();
//        n.flags |= Notification.FLAG_AUTO_CANCEL;
        if(title.equals("")){
            title = "NowFloats Boost";
        }
        Log.i("Notification triggered","...!!");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setAutoCancel(true);
        builder.setContentTitle(title);
        builder.setContentText(message);
        builder.setColor(Color.parseColor("#ffb900"));
        try {
            builder.setSmallIcon(R.drawable.app_launcher2);
        }catch(Exception e){e.printStackTrace();}
        if(notificationIconBitmap!=null)
            builder.setLargeIcon(notificationIconBitmap);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
        if(payload!=null){
            if(payload.contains("update")){
                builder.addAction(com.mixpanel.android.R.drawable.notification_update, " Update Now", intent);
            }
            else if(payload.contains("featuredImage")){
                builder.addAction(com.mixpanel.android.R.drawable.notification_buy, " Buy", intent);
            }
            else if(payload.contains("store")){
                builder.addAction(com.mixpanel.android.R.drawable.notification_buy, " Purchase", intent);
            }
            else if(payload.contains("enquiries")){
                builder.addAction(com.mixpanel.android.R.drawable.notification_buy, " Purchase", intent);
            }
            else if(payload.contains("hours")){
                builder.addAction(com.mixpanel.android.R.drawable.notification_buy, " Purchase", intent);
            }
            else if(payload.contains("gallery")){
                builder.addAction(com.mixpanel.android.R.drawable.notification_buy, " Purchase", intent);
            }
            else if(payload.contains("contact")){
                builder.addAction(com.mixpanel.android.R.drawable.notification_buy, " Purchase", intent);
            }
            else if(payload.contains("searchqueries")){
                builder.addAction(com.mixpanel.android.R.drawable.notification_buy, " Purchase", intent);
            }
            else if(payload.contains("nfstoreDomainTTBCombo")){
                builder.addAction(com.mixpanel.android.R.drawable.notification_buy, " Purchase", intent);
            }
            else if(payload.contains("nfstoreNoAds")){
                builder.addAction(com.mixpanel.android.R.drawable.notification_buy, " Purchase", intent);
            }
            else if(payload.contains("social")){
                builder.addAction(com.mixpanel.android.R.drawable.notification_share, " Share", intent);
            }
            else if(payload.contains("featuredImage")){
                builder.addAction(com.mixpanel.android.R.drawable.notification_pic, " Update Now", intent);
            }
            else if(payload.contains("logo")){
                builder.addAction(com.mixpanel.android.R.drawable.notification_pic, " Update Now", intent);
            }
        }

        builder.setContentIntent(intent);

        Notification notification = builder.build();
        NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, notification);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        //nm.notify(0, n);
    }
}