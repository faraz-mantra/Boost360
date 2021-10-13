package com.nowfloats.NotificationCenter;

import android.util.Log;

import com.framework.analytics.SentryController;
import com.nowfloats.util.Constants;

import org.json.JSONObject;

import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by guru on 23-06-2015.
 */
public class AlertArchive {
    public AlertArchive(NotificationInterface anInterface, String type, String fpid) {
        try {
            HashMap<String, String> map = new HashMap<>();
            map.put("clientId", Constants.clientId);
            map.put("fpId", fpid);
            map.put("notificationId", "");
            map.put("isTargetAchieved", "TRUE");
            map.put("Type", type);
            anInterface.archiveAlert(new JSONObject(), map, new Callback<String>() {
                @Override
                public void success(String s, Response response) {
                    Log.i("Alert Archive Success -" + s, " status--" + response.getStatus());
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.i("Alert Archive fail", " --" + error);
                }
            });
        } catch (Exception e) {
            SentryController.INSTANCE.captureException(e);
            e.printStackTrace();
        }
    }
}