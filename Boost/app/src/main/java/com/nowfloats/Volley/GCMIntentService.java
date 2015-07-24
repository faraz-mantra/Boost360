package com.nowfloats.Volley;

/**
 * Created by NowFloatsDev on 29/04/2015.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.demach.konotor.Konotor;
import com.demach.konotor.access.K;
import com.google.android.gcm.GCMBaseIntentService;
import com.mixpanel.android.mpmetrics.GCMReceiver;
import com.nowfloats.NavigationDrawer.Chat.ChatFragment;
import com.nowfloats.NavigationDrawer.Chat.ChatModel;
import com.nowfloats.NavigationDrawer.HomeActivity;
import com.nowfloats.util.Constants;
import com.thinksity.R;

public class GCMIntentService extends GCMBaseIntentService
{
    public static final String TAG = GCMIntentService.class.getName();
    Handler handler = new Handler();
    public GCMIntentService()
    {
        super(K.ANDROID_PROJECT_SENDER_ID);
    }

    @Override
    protected void onMessage(Context context, Intent intent)
    {
        Log.i("","Push NOtif came.....");
        if(intent!=null){
            if(intent.hasExtra("message")){
                String message = intent.getExtras().getString("message");
                ChatFragment.chatModels.add(new ChatModel(message,true));
                if(ChatFragment.ChatFragmentPage!=null){
                    if(ChatFragment.chatAdapter!=null){
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                ChatFragment.chatAdapter.notifyDataSetChanged();
                            }
                        };
                        handler.post(runnable);
                    }
                }else{
                    generateNotification(context,message);
                    GCMReceiver.deeplinkUrl = "chatWindow";
                }
            }else{
                Konotor.getInstance(context).handleGcmOnMessage(intent);
            }
        }
    }

    @Override
    protected void onError(Context context, String errorId)
    {
        Konotor.getInstance(context).handleGcmOnError(errorId);
    }

    @Override
    protected void onRegistered(Context context, String registrationId)
    {
        Log.d("GCMIntentService","Registration Id : "+registrationId);
        Constants.gcmRegistrationID = registrationId ;
//        Konotor.getInstance(getApplicationContext()).updateGcmRegistrationId(Constants.gcmRegistrationID);
        Konotor.getInstance(context).handleGcmOnRegistered(registrationId);
        HomeActivity.setGCMId(Constants.gcmRegistrationID);
    }

    @Override
    protected void onUnregistered(Context context, String registrationId)
    {
        Konotor.getInstance(context).handleGcmOnUnRegistered(registrationId);
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    private static void generateNotification(Context context, String message) {
        int icon = R.drawable.app_launcher;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(icon, message, when);

        String title = context.getString(R.string.app_name);

        Intent notificationIntent = new Intent(context, HomeActivity.class);
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        notification.setLatestEventInfo(context, title, message, intent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        // Play default notification sound
        notification.defaults |= Notification.DEFAULT_SOUND;

        // Vibrate if vibrate is enabled
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notificationManager.notify(0, notification);

    }
}
