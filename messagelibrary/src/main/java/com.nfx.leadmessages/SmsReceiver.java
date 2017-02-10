package com.nfx.leadmessages;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.content.Context.POWER_SERVICE;

/**
 * Created by Admin on 01-02-2017.
 */

public class SmsReceiver extends BroadcastReceiver {
    String fpId,mobileId;

    PowerManager.WakeLock wakeLock;
    PowerManager powerManager;
    SmsMessage[] sms ;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.v("ggg","on receive");
        SharedPreferences pref =context.getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE);
        fpId =pref.getString(Constants.FP_ID,null);

        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        mobileId = tm.getDeviceId();
        if(mobileId == null || fpId == null){
            return;
        }
        //MOBILE_ID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                        "MyWakelockTag");
                if(!wakeLock.isHeld())
                    wakeLock.acquire();

                FirebaseOptions options = new FirebaseOptions.Builder()
                        .setApiKey("AIzaSyBowYAh4TLbruwJoqL3QnQaKhBmB83EgEs")
                        .setApplicationId("1:506969475000:android:afe3b748e8b5c95f")
                        .setDatabaseUrl("https://nfxteam-153211.firebaseio.com")
                        .build();
                FirebaseApp secondApp = null;
                try {
                    secondApp = FirebaseApp.getInstance("second app");
                }catch(Exception e) {
                    secondApp = FirebaseApp.initializeApp(context, options, "second app");
                }
                FirebaseDatabase secondDatabase = FirebaseDatabase.getInstance(secondApp);
                DatabaseReference mDatabase = secondDatabase.getReference()
                        .child(fpId+Constants.MESSAGES)
                        .child(mobileId);

                MessageListModel.PhoneIds phoneIds=new MessageListModel.PhoneIds();
                phoneIds.setDate(String.valueOf(System.currentTimeMillis()));
                phoneIds.setPhoneId(mobileId);
                DatabaseReference phoneIdRef =  secondDatabase.getReference()
                        .child(fpId+Constants.DETAILS)
                        .child(Constants.PHONE_IDS);
                phoneIdRef.child(mobileId).setValue(phoneIds);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    sms = Telephony.Sms.Intents.getMessagesFromIntent(intent);
                }
                else
                {
                    Bundle bundle = intent.getExtras();
                    if (bundle != null) {
                        Object[] data = (Object[]) bundle.get("pdus");
                        if(data == null){
                            if(wakeLock.isHeld())
                                wakeLock.release();
                            return;
                        }
                        sms = new SmsMessage[1];
                        sms[0] = SmsMessage.createFromPdu((byte[]) data[0]);
                    }
                }
                MessageListModel.SmsMessage model;
                for (SmsMessage ms:sms) {
                    for (String s:Constants.selections) {
                        if (ms.getOriginatingAddress().contains(s)){
                            model =  MessageListModel.SmsMessage.getInstance()
                                    .setBody(ms.getMessageBody())
                                    .setSubject(ms.getOriginatingAddress())
                                    .setDate(System.currentTimeMillis());
                            Log.v("ggg","\n"+ms.getOriginatingAddress()+"\n"+ms.getProtocolIdentifier()
                                    +"\n"+ms.getTimestampMillis());

                            String key = mDatabase.push().getKey();
                            mDatabase.child(key).setValue(model);
                            break;
                        }
                    }
                }
                if(wakeLock.isHeld())
                    wakeLock.release();
            }
        }).start();

    }

    //class OneTimeService extends
}
