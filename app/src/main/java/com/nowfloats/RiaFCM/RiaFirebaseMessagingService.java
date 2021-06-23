package com.nowfloats.RiaFCM;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.anachat.chatsdk.AnaCore;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
  public static String deepLinkUrl;
  private SharedPreferences pref;

  @Override
  public void onNewToken(String token) {
    super.onNewToken(token);

    Log.d(TAG, "Token: " + token);

    try {
      saveTokenToPreferenceAndUpload(token);
      WebEngage.get().setRegistrationID(token);
//                try {
//                    ZopimChat.setPushToken(token);
//                } catch (Exception e) {
//                }
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
      BoostLog.d(TAG, "onMessageReceived");
      if (mapResult != null) {
        if (mapResult.containsKey("source") && "webengage".equals(mapResult.get("source"))) {
          WebEngage.get().receive(mapResult);
        }
        if (mapResult.containsKey("payload")) {
          AnaCore.handlePush(this, mapResult.get("payload"));
        } else {
          sendNotification(mapResult);
          Constants.GCM_Msg = true;
        }
      }
    } catch (Exception e) {
      Crashlytics.log("Failed to process onMessageReceived in RiaFirebaseMessagingService" + e.getMessage());
    }
  }

  private static final String SAM_BUBBLE_MSG = "I have Got some data";
  private static final String SAM_BUBBLE_MSG_KEY = "100";

  private void sendNotification(Map<String, String> message) {

    BoostLog.d("Message", message.toString());
    if (message == null || message.size() == 0) {

    } else {
      if ((message.containsKey("mp_message") && message.get("mp_message").equalsIgnoreCase(SAM_BUBBLE_MSG))
          || (message.containsKey("mp_message_key") && message.get("mp_message_key").equalsIgnoreCase(SAM_BUBBLE_MSG_KEY))) {
        message.put("url", "thirdPartyQueries");
        message.put("mp_message", getString(R.string.you_have_new_enquires_from_third_party_check_now));
      }

      deepLinkUrl = message.get("url");
      BoostLog.d("Message", deepLinkUrl);

      if (deepLinkUrl != null && !deepLinkUrl.contains(Constants.PACKAGE_NAME)) {
        if (!deepLinkUrl.contains(Constants.DEFAULT_PACKAGE_NAME_WEB_ERROR))
          return;
      }
      if (Methods.isUserLoggedIn(this) && Methods.isMyAppOpen(this)) {
        MixPanelController.track("$campaign_received", null);
      }


      final PackageManager manager = getPackageManager();
      Intent intent = manager.getLaunchIntentForPackage(getPackageName());
      if (intent == null) return;
      intent.putExtra("from", "notification");
      intent.putExtra("url", deepLinkUrl);

      //******************** to override showing notifications **************************
      if (!Util.isNullOrEmpty(deepLinkUrl)) {

        if (deepLinkUrl.contains(getString(R.string.facebook_chat))) {
          SharedPreferences pref = getSharedPreferences(Constants.PREF_NAME, Activity.MODE_PRIVATE);
          pref.edit().putBoolean("IsNewFacebookMessage", true).apply();
          intent.putExtra("user_data", message.get("user_data"));
          Intent messageIntent = new Intent(FacebookChatDetailActivity.INTENT_FILTER);
          messageIntent.putExtra("user_data", message.get("user_data"));
          messageIntent.putExtra("message", message.get("message"));
          if (LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent)) {
            if (!TextUtils.isEmpty(pref.getString("facebookChatUser", "")) &&
                message.get("user_data").contains(pref.getString("facebookChatUser", ""))) {
              return;
            }
          }

        }

      }
      PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);


      String title = message.get("title");
      String notiMessage = message.get("mp_message");
      String channelId = "0001";
      boolean isNotificationShow = !TextUtils.isEmpty(notiMessage);

      String jsonData = "";

      if (message.containsKey("json_data")) {
        jsonData = message.get("json_data");
      }

      BoostLog.d("Message-3", title + "----" + notiMessage);

      Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
      NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
          .setSmallIcon(R.drawable.app_launcher2)
          .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.app_launcher))
          .setContentText(notiMessage)
          .setAutoCancel(true)
          .setSound(defaultSoundUri)
          .setColor(ContextCompat.getColor(this, R.color.primaryColor))
          .setStyle(new NotificationCompat.BigTextStyle().bigText(notiMessage))
          .setPriority(NotificationCompat.PRIORITY_HIGH);

      if (!Util.isNullOrEmpty(title)) {
        notificationBuilder.setContentTitle(title);
      } else {
        notificationBuilder.setContentTitle(getResources().getString(R.string.app_name));
      }

      if (pendingIntent != null) {
        notificationBuilder.setContentIntent(pendingIntent);
      }

      NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
      NotificationChannel channel = null;
      if (notificationManager != null) {
        BoostLog.d("Message-4", "fsdf");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
          channel = new NotificationChannel(channelId, getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT);
          notificationManager.createNotificationChannel(channel);
        }

        if (!Util.isNullOrEmpty(deepLinkUrl) && !TextUtils.isEmpty(jsonData)) {

          final Gson gson = new Gson();
          if (deepLinkUrl.contains(getString(R.string.deep_link_call_tracker))) {


            ArrayList<VmnCallModel> vmnList = new ArrayList<>();

            VmnCallModel vmnCallModel = gson.fromJson(jsonData, new TypeToken<VmnCallModel>() {}.getType());

            String oldData = pref.getString(PREF_NOTI_CALL_LOGS, "");

            if (!TextUtils.isEmpty(oldData)) {
              Type type = new TypeToken<List<VmnCallModel>>() {}.getType();
              vmnList = gson.fromJson(oldData, type);
            }

            vmnList.add(vmnCallModel);

            if (vmnCallModel.getCallStatus().equalsIgnoreCase("MISSED")) {
              notificationBuilder.setContentTitle("Missed Call");
            } else {
              notificationBuilder.setContentTitle("Received Call");
            }

            isNotificationShow = !TextUtils.isEmpty(vmnCallModel.getCallerNumber());
            notificationBuilder.setContentText("" + vmnCallModel.getCallerNumber());
            notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText("" + vmnCallModel.getCallerNumber()));

            String json = gson.toJson(vmnList);
            pref.edit().putString(PREF_NOTI_CALL_LOGS, json).commit();
            BoostLog.e("deepLinkUrl", jsonData);


          } else if (deepLinkUrl.contains(getResources().getString(R.string.deeplink_bizenquiry)) || deepLinkUrl.contains("enquiries")) {

            ArrayList<Business_Enquiry_Model> eqList = new ArrayList<>();

            Business_Enquiry_Model entity_model = gson.fromJson(jsonData, new TypeToken<Business_Enquiry_Model>() {}.getType());

            String oldData = pref.getString(PREF_NOTI_ENQUIRIES, "");

            if (!TextUtils.isEmpty(oldData)) {
              Type type = new TypeToken<List<Business_Enquiry_Model>>() {}.getType();
              eqList = gson.fromJson(oldData, type);
            }

            eqList.add(entity_model);

            String json = gson.toJson(eqList);
            pref.edit().putString(PREF_NOTI_ENQUIRIES, json).commit();
            BoostLog.e("deepLinkUrl", jsonData);

            notificationBuilder.setContentTitle("Business Enquiry");
            isNotificationShow = !TextUtils.isEmpty(entity_model.getMessage());
            notificationBuilder.setContentText("" + entity_model.getMessage());
            notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(entity_model.getMessage()));
          } else if (deepLinkUrl.contains("myorders")) {

            ArrayList<OrderModel> orderList = new ArrayList<>();

            OrderModel entity_model = gson.fromJson(jsonData, new TypeToken<OrderModel>() {}.getType());

            String oldData = pref.getString(PREF_NOTI_ORDERS, "");

            if (!TextUtils.isEmpty(oldData)) {
              Type type = new TypeToken<List<OrderModel>>() {}.getType();
              orderList = gson.fromJson(oldData, type);
            }

            orderList.add(entity_model);

            String json = gson.toJson(orderList);
            pref.edit().putString(PREF_NOTI_ORDERS, json).commit();
            BoostLog.e("deepLinkUrl", jsonData);
          }

          pref.edit().putBoolean(Key_Preferences.HAS_SUGGESTIONS, true).commit();
          if (Methods.hasOverlayPerm(this)) {
            if (!Methods.isMyServiceRunning(this, CustomerAssistantService.class)) {

              Intent bubbleIntent = new Intent(this, CustomerAssistantService.class);
              if (deepLinkUrl.contains("myorders")) {
                bubbleIntent.putExtra("shouldOpen", true);
              } else {
                bubbleIntent.putExtra("shouldOpen", false);
              }

              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(bubbleIntent);
              } else {
                startService(bubbleIntent);
              }
            } else {
              if (deepLinkUrl.contains("myorders")) {
                Intent callIntent = new Intent(this, CallerInfoDialog.class);
                callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(callIntent);
              }
            }
            sendBroadcast(new Intent(CustomerAssistantService.ACTION_REFRESH_DIALOG));
            MixPanelController.track(MixPanelController.BUBBLE_ENABLED, null);
          }
          MixPanelController.track(MixPanelController.PRODUCTIVE_CALLS, null);
        }


        if (!Util.isNullOrEmpty(deepLinkUrl) && deepLinkUrl.contains(getString(R.string.facebook_chat))) {
          FacebookChatDataModel.UserData data = new Gson().fromJson(message.get("user_data"), FacebookChatDataModel.UserData.class);
          if (data.getId() != null) {
            if (isNotificationShow) notificationManager.notify(data.getId().hashCode(), notificationBuilder.build());
          }
        } else {
          if (isNotificationShow) notificationManager.notify(createID(), notificationBuilder.build());
          BoostLog.d("Message-", "fsdf");

        }
      }
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
      registerChat(pref.getString("fpid", null));
    }
  }

  private void registerChat(String userId) {
    try {
      final HashMap<String, String> params = new HashMap<>();
      params.put("Channel", FirebaseInstanceId.getInstance().getToken());
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
