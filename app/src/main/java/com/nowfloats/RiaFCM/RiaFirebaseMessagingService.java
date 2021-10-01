package com.nowfloats.RiaFCM;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;

import com.anachat.chatsdk.AnaCore;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.nowfloats.Analytics_Screen.model.VmnCallModel;
import com.nowfloats.Business_Enquiries.Model.Business_Enquiry_Model;
import com.nowfloats.Login.Login_Interface;
import com.nowfloats.bubble.CustomerAssistantService;
import com.nowfloats.managecustomers.FacebookChatDetailActivity;
import com.nowfloats.managecustomers.models.FacebookChatDataModel;
import com.nowfloats.managenotification.CallerInfoDialog;
import com.nowfloats.managenotification.OrderModel;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.thinksity.R;
import com.webengage.sdk.android.WebEngage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.nowfloats.util.Constants.PREF_NOTI_CALL_LOGS;
import static com.nowfloats.util.Constants.PREF_NOTI_ENQUIRIES;
import static com.nowfloats.util.Constants.PREF_NOTI_ORDERS;

/**
 * Created by NowFloats on 05-10-2016.
 */

public class RiaFirebaseMessagingService extends FirebaseMessagingService {
  private static final String TAG = RiaFirebaseMessagingService.class.getSimpleName();
  private static final String SAM_BUBBLE_MSG = "I have Got some data";
  private static final String SAM_BUBBLE_MSG_KEY = "100";
  public static String deepLinkUrl;
  private SharedPreferences pref;

  @Override
  public void onNewToken(String token) {
    super.onNewToken(token);
    Log.d(TAG, "Token: " + token);
    try {
      saveTokenToPreferenceAndUpload(token);
      WebEngage.get().setRegistrationID(token);
      //try {ZopimChat.setPushToken(token);} catch (Exception e) {}
      AnaCore.saveFcmToken(this, token);
    } catch (Exception e) {
      Crashlytics.log("Failed to process FCM Token by RiaFirebaseMessagingService" + e.getMessage());
    }
  }

  @Override
  public void onMessageReceived(RemoteMessage remoteMessage) {
    try {
      pref = getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
      Map<String, String> mapResult = remoteMessage.getData();
      BoostLog.d("onMessageReceived: ", "onMessageReceived");
      if (mapResult != null) {
        Boolean isWebEnagage = mapResult.containsKey("source") && "webengage".equals(mapResult.get("source"));
        if (isWebEnagage) {
          WebEngage.get().receive(mapResult);
        }
        if (mapResult.containsKey("payload")) {
          AnaCore.handlePush(this, mapResult.get("payload"));
        } else if (!isWebEnagage){
          String deepLinkUrl = mapResult.get("url");
          String title = mapResult.get("title");
          String message = mapResult.get("mp_message");
          sendNotification(this.getBaseContext(), title, message, deepLinkUrl);
          Constants.GCM_Msg = true;
        }
      }
    } catch (Exception e) {
      Crashlytics.log("Failed to process onMessageReceived in RiaFirebaseMessagingService" + e.getMessage());
    }
  }


  private void sendNotification(Context c, String messageTitle, String messageBody, String deepLinkUrl) {
    Intent intent = new Intent("com.dashboard.controller.DashboardActivity");
    TaskStackBuilder stackBuilder = TaskStackBuilder.create(c);
    intent.putExtra("from", "notification");
    intent.putExtra("url", deepLinkUrl);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    stackBuilder.addNextIntentWithParentStack(intent);

    PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(c, "111")
        .setSmallIcon(R.drawable.app_launcher2)
        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.app_launcher))
        .setContentText(messageBody)
        .setAutoCancel(true)
        .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
        .setColor(ContextCompat.getColor(c, R.color.primaryColor))
        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        .setContentIntent(pendingIntent)
        .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setLights(Color.GREEN, 3000, 3000);

    if (messageTitle != null && messageTitle.isEmpty()) notificationBuilder.setContentTitle(messageTitle);
    else notificationBuilder.setContentTitle(getResources().getString(R.string.app_name));
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
      NotificationManager notificationManager =
          (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
      int importance = NotificationManager.IMPORTANCE_HIGH;
      NotificationChannel notificationChannel = new NotificationChannel("111", "NOTIFICATION_CHANNEL_NAME", importance);
      notificationChannel.enableLights(true);
      notificationChannel.setLightColor(Color.YELLOW);
      notificationChannel.enableVibration(true);
      notificationChannel.setShowBadge(false);
      notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
      assert notificationManager != null;
      notificationBuilder.setChannelId("111");
      notificationManager.createNotificationChannel(notificationChannel);
      notificationManager.notify(0, notificationBuilder.build());
    } else {
      NotificationManagerCompat notificationManager = NotificationManagerCompat.from(c);
      notificationManager.notify(0, notificationBuilder.build());
    }
  }

  public int createID() {
    Date now = new Date();
    int id = Integer.parseInt(new SimpleDateFormat("ddHHmmss", Locale.US).format(now));
    return id;
  }


  private void saveTokenToPreferenceAndUpload(String refreshedToken) {
    SharedPreferences pref = getSharedPreferences("nowfloatsPrefs", Context.MODE_PRIVATE);
    if (pref.getString("fpid", null) != null) {
      registerChat(pref.getString("fpid", null),refreshedToken);
    }
  }

  private void registerChat(String userId, String refreshedToken) {
    try {
      final HashMap<String, String> params = new HashMap<>();
      params.put("Channel", refreshedToken);
      params.put("UserId", userId);
      params.put("DeviceType", "ANDROID");
      params.put("clientId", Constants.clientId);
      Log.i("Ria_Register GCM id--", "API call Started");
      Login_Interface emailValidation = Constants.restAdapter.create(Login_Interface.class);
      emailValidation.post_RegisterRia(params, new Callback<String>() {
        @Override
        public void success(String s, Response response) {
          Log.d("Response", "Response : " + s);
        }

        @Override
        public void failure(RetrofitError error) {
          Log.e("GCM local ", "reg FAILed" + params.toString());
        }
      });
    } catch (Exception e) {
      Log.e("Ria_Register ", "API Exception:" + e);
    }
  }
}
