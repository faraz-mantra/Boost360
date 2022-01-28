package com.nfx.leadmessages;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.provider.CallLog;

import androidx.annotation.Nullable;

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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
