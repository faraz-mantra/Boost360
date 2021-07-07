package com.nfx.leadmessages;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Telephony;

import androidx.core.app.ActivityCompat;

import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;

import static android.content.Context.POWER_SERVICE;
import static com.nfx.leadmessages.Constants.SMS_REGEX;

/**
 * Created by Admin on 01-02-2017.
 */

public class SmsReceiver extends BroadcastReceiver {
    String fpId, mobileId = null;

    PowerManager.WakeLock wakeLock;
    PowerManager powerManager;
    android.telephony.SmsMessage[] sms;


    @Override
    public void onReceive(final Context context, final Intent intent) {

        final SharedPreferences pref = context.getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE);

        if (pref == null) return;

        fpId = pref.getString(Constants.FP_ID, null);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            mobileId = tm.getDeviceId();
        }

        final String smsAddresses = pref.getString(SMS_REGEX, null);
        if (mobileId == null || fpId == null || smsAddresses == null) {
            return;
        }

        powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakelockTag");
                if (!wakeLock.isHeld())
                    wakeLock.acquire();

                FirebaseOptions options = new FirebaseOptions.Builder()
                        .setApiKey("AIzaSyBowYAh4TLbruwJoqL3QnQaKhBmB83EgEs")
                        .setApplicationId("1:506969475000:android:afe3b748e8b5c95f")
                        .setDatabaseUrl("https://nfxteam-153211.firebaseio.com")
                        .build();
                FirebaseApp secondApp = null;
                try {
                    secondApp = FirebaseApp.initializeApp(context, options, "second app");

                } catch (Exception e) {
                    try {
                        secondApp = FirebaseApp.getInstance("second app");
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
                if (secondApp == null) return;
                FirebaseDatabase secondDatabase = FirebaseDatabase.getInstance(secondApp);
                DatabaseReference mDatabase = secondDatabase.getReference()
                        .child(fpId + Constants.MESSAGES)
                        .child(mobileId);

                PhoneIds phoneIds = new PhoneIds();
                phoneIds.setDate(String.valueOf(System.currentTimeMillis()));
                phoneIds.setPhoneId(mobileId);
                DatabaseReference phoneIdRef = secondDatabase.getReference()
                        .child(fpId + Constants.DETAILS)
                        .child(Constants.PHONE_IDS);
                phoneIdRef.child(mobileId).setValue(phoneIds);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    sms = Telephony.Sms.Intents.getMessagesFromIntent(intent);
                } else {
                    Bundle bundle = intent.getExtras();
                    if (bundle != null) {
                        Object[] data = (Object[]) bundle.get("pdus");
                        if (data == null) {
                            if (wakeLock.isHeld())
                                wakeLock.release();
                            return;
                        }
                        sms = new android.telephony.SmsMessage[1];
                        sms[0] = android.telephony.SmsMessage.createFromPdu((byte[]) data[0]);
                    }
                }
                SmsMessage model;

                List<String> selectionList = Arrays.asList(TextUtils.split(smsAddresses.replaceAll("%", ""), ","));

                for (android.telephony.SmsMessage ms : sms) {
                    if (selectionList.contains(ms.getOriginatingAddress())) {
                        model = new SmsMessage()
                                .setBody(ms.getMessageBody())
                                .setSubject(ms.getOriginatingAddress())
                                .setDate(System.currentTimeMillis())
                                .setSeen("0");
                        //Log.v("ggg1",model.toString());
                        mDatabase.push().setValue(model);
                        break;
                    }

                }
                if (wakeLock.isHeld())
                    wakeLock.release();
            }
        }).start();

    }

}
