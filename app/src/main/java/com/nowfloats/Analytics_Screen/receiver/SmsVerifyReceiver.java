package com.nowfloats.Analytics_Screen.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Telephony;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsMessage;

import com.nowfloats.util.Constants;

import static android.content.Context.POWER_SERVICE;

/**
 * Created by Admin on 17-05-2017.
 */

public class SmsVerifyReceiver extends BroadcastReceiver {
    SmsMessage[] sms;
    @Override
    public void onReceive(Context context, Intent intent) {
       PowerManager powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakelockTag");
        if(!wakeLock.isHeld())
            wakeLock.acquire();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            sms = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        }
        else
        {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] data = (Object[]) bundle.get("pdus");
                if(data == null){
                    return;
                }
                sms = new android.telephony.SmsMessage[1];
                sms[0] = android.telephony.SmsMessage.createFromPdu((byte[]) data[0]);
            }
        }
        if(sms[0].getOriginatingAddress().toLowerCase().contains("auth") && sms[0].getMessageBody().contains("Boost")) {
            String str = sms[0].getMessageBody();
            Intent smsIntent = new Intent(Constants.SMS_OTP_RECEIVER);
            smsIntent.putExtra("OTP_CODE", str);
            LocalBroadcastManager.getInstance(context).sendBroadcast(smsIntent);

        }
        if(wakeLock.isHeld())
            wakeLock.release();
    }

}
