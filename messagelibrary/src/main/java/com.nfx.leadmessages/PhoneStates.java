package com.nfx.leadmessages;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.PowerManager;

import androidx.core.app.ActivityCompat;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

import static android.content.Context.POWER_SERVICE;

/**
 * Created by Admin on 17-04-2017.
 */

public class PhoneStates extends BroadcastReceiver {

    //The receiver will be recreated whenever android feels like it.  We need a static variable to remember data between instantiations
    static PhonecallStartEndDetector listener;

    protected Context savedContext;
    String fpId, mobileId = null;

    PowerManager.WakeLock wakeLock;
    PowerManager powerManager;


    @Override
    public void onReceive(Context context, Intent intent) {
        savedContext = context;

        SharedPreferences pref = context.getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE);

        if (pref == null) return;

        fpId = pref.getString(Constants.FP_ID, null);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (tm == null) return;
            if (Build.VERSION.SDK_INT >= 26) {
                if (tm.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA) {
                    mobileId = tm.getMeid();
                } else if (tm.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM) {
                    mobileId = tm.getImei();
                } else mobileId = ""; // default
            } else mobileId = tm.getDeviceId();
        }

        if (mobileId == null || fpId == null) {
            return;
        }

        powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);

        if (listener == null) {
            listener = new PhonecallStartEndDetector(context);
        }

        //We listen to two intents.  The new outgoing call only tells us of an outgoing call.  We use it to get the number.
        if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            listener.setOutgoingNumber(intent.getExtras().getString("android.intent.extra.PHONE_NUMBER"));

            return;
        }

        //The other intent tells us the phone state changed.  Here we set a listener to deal with it
        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    //Deals with actual events
    public class PhonecallStartEndDetector extends PhoneStateListener {
        private final Context context;
        int lastState = TelephonyManager.CALL_STATE_IDLE;
        Date callStartTime;
        boolean isIncoming;
        String savedNumber;  //because the passed incoming is only valid in ringing

        public PhonecallStartEndDetector(Context context) {
            this.context = context;
        }

        //The outgoing number is only sent via a separate intent, so we need to store it out of band
        public void setOutgoingNumber(String number) {
            savedNumber = number;
        }

        //Incoming call-  goes from IDLE to RINGING when it rings, to OFFHOOK when it's answered, to IDLE when its hung up
        //Outgoing call-  goes from IDLE to OFFHOOK when it dials out, to IDLE when hung up
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            if (lastState == state) {
                //No change, debounce extras
                return;
            }
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    isIncoming = true;
                    callStartTime = new Date();
                    savedNumber = incomingNumber;
                    //onIncomingCallStarted(incomingNumber, callStartTime);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    //Transition of ringing->offhook are pickups of incoming calls.  Nothing donw on them
                    if (lastState != TelephonyManager.CALL_STATE_RINGING) {
                        isIncoming = false;
                        callStartTime = new Date();
                        //onOutgoingCallStarted(savedNumber, callStartTime);
                    }
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakelockTag");
                            if (!wakeLock.isHeld())
                                wakeLock.acquire();

                            //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                        /*    if (lastState == TelephonyManager.CALL_STATE_RINGING) {
                                //Ring but no pickup-  a miss
                                type = "missed";
                                //onMissedCall(savedNumber, callStartTime);
                            } else if (isIncoming) {
                                //onIncomingCallEnded(savedNumber, callStartTime, new Date());
                                type = "incoming";

                            } else {
                                //onOutgoingCallEnded(savedNumber, callStartTime, new Date());
                                type = "outgoing";
                            }*/
                            try {
                                Thread.sleep(4000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            try {
                                onCallEnd(context);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    break;
            }
            lastState = state;
        }

    }

    private void onCallEnd(final Context context) {

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApiKey("AIzaSyBowYAh4TLbruwJoqL3QnQaKhBmB83EgEs")
                .setApplicationId("1:506969475000:android:afe3b748e8b5c95f")
                .setDatabaseUrl("https://nfxteam-153211.firebaseio.com")
                .build();
        FirebaseApp secondApp = null;
        try {
            secondApp = FirebaseApp.getInstance("second app");
        } catch (Exception e) {
            secondApp = FirebaseApp.initializeApp(context, options, "second app");
        }
        FirebaseDatabase secondDatabase = FirebaseDatabase.getInstance(secondApp);
        final DatabaseReference mDatabase = secondDatabase.getReference();

        //readCallLog(mDatabase);
    }

    /*private void readCallLog(DatabaseReference mDatabase) {
        if (ActivityCompat.checkSelfPermission(savedContext, android.Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        Uri CALL_LOG_URI = CallLog.Calls.CONTENT_URI;
        String[] CALL_LOG_PROJECTIONS = new String[]{
                CallLog.Calls.CACHED_NAME,
                CallLog.Calls.NUMBER,
                CallLog.Calls.DATE,
                CallLog.Calls.DURATION,
                CallLog.Calls.TYPE
        };
        String CALL_order = CallLog.Calls.DATE + " DESC";
        ContentResolver resolver = savedContext.getContentResolver();

        Cursor cursor = resolver.query(CALL_LOG_URI, CALL_LOG_PROJECTIONS, null, null, CALL_order);
        if(cursor!=null && cursor.moveToFirst()){

            PhoneIds phoneIds=new PhoneIds();
            phoneIds.setDate(String.valueOf(Calendar.getInstance().getTimeInMillis()));
            phoneIds.setPhoneId(mobileId);
            DatabaseReference phoneIdRef = mDatabase.child(fpId+Constants.DETAILS).child(Constants.PHONE_IDS);
            phoneIdRef.child(mobileId).setValue(phoneIds);

            DatabaseReference MessageIdRef = mDatabase.child(fpId+Constants.CALL_LOG).child(mobileId);

            CallLogModel model = new CallLogModel();

            model.setName(cursor.getString(0));
            model.setNumber(cursor.getString(1));
            model.setDate(String.valueOf(cursor.getLong(2)));
            model.setDuration_seconds(String.valueOf(cursor.getLong(3)));
            int callType = cursor.getInt(4);

            String type = null;
            switch (callType) {
                case CallLog.Calls.OUTGOING_TYPE:
                    type = "Outgoing";
                    break;

                case CallLog.Calls.INCOMING_TYPE:
                    type = "Incoming";
                    break;

                case CallLog.Calls.MISSED_TYPE:
                    type = "Missed";
                    break;
                case CallLog.Calls.REJECTED_TYPE:
                    type = "Rejected";
                    break;
            }
            model.setCallType(type);
            MessageIdRef.push().setValue(model);
            cursor.close();
        }
        if (wakeLock.isHeld())
            wakeLock.release();
    }*/

}