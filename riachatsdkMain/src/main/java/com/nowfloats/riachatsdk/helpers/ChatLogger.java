package com.nowfloats.riachatsdk.helpers;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nowfloats.riachatsdk.BuildConfig;
import com.nowfloats.riachatsdk.models.ChatEventModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.TimeZone;

/**
 * Created by NowFloats on 30-03-2017.
 */

public class ChatLogger {
    private static ChatLogger sRiaEventLogger;
    private DatabaseReference mDatabase;
    private static final String DB_CHILD_NAME = "ChatSDKTest";
    //private static Bus mBus;
    public static boolean lastEventStatus;
    public static ChatLogger getInstance(){
        if(sRiaEventLogger==null){
            sRiaEventLogger = new ChatLogger();
        }

        return sRiaEventLogger;
    }


    public enum EventStatus{
        COMPLETED(0), DROPPED(1);
        private final int value;

        private EventStatus(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    private ChatLogger(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void logViewEvent(String deviceId, String NodeId){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        ChatEventModel Event = new ChatEventModel()
                .setEventCategory("RIA_ONBOARDING_CHAT")
                .setEventChannel("APP_ANDR")
                .setEventName("VIEW")
                .setEventDateTime(System.currentTimeMillis()/1000)
                .setDeviceId(deviceId)
                .setNodeId(NodeId);

        mDatabase.child(DB_CHILD_NAME).push().setValue(Event);

    }

    public void logClickEvent(String deviceId, String NodeId, String buttonId, String buttonLabel, String varName, String varValue, String buttonType){
        DateFormat df = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        HashMap<String, String> EventData = new HashMap<>();
        EventData.put("ButtonId", buttonId);
        EventData.put("ButtonLabel", buttonLabel);
        EventData.put("ButtonType", buttonType);
        ChatEventModel Event = new ChatEventModel();
        if(null==varName && null==varValue) {
            Event
            .setEventCategory("RIA_ONBOARDING_CHAT")
            .setEventChannel("APP_ANDR")
            .setEventName("CLICK")
            .setEventDateTime(System.currentTimeMillis() / 1000)
            .setDeviceId(deviceId)
            .setNodeId(NodeId)
            .setEventData(EventData);
        }else {
            HashMap<String, String> UserData = new HashMap<>();
            UserData.put(varName, varValue);
             Event
              .setEventCategory("RIA_ONBOARDING_CHAT")
              .setEventChannel("APP_ANDR")
              .setEventName("CLICK")
            .setEventDateTime(System.currentTimeMillis() / 1000)
            .setDeviceId(deviceId)
            .setNodeId(NodeId)
            .setEventData(EventData)
             .setUserData(UserData);

        }
        mDatabase.child(DB_CHILD_NAME).push().setValue(Event);

    }

    public void logPostEvent(String deviceId, String NodeId, String buttonId, String buttonLabel, int status, String varName, String varValue, String buttonType){
        if(status == EventStatus.COMPLETED.getValue()){
            lastEventStatus = true;
        }else {
            lastEventStatus = false;
        }
        DateFormat df = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        HashMap<String, String> EventData = new HashMap<>();
        EventData.put("ButtonId", buttonId);
        EventData.put("ButtonLabel", buttonLabel);
        EventData.put("ButtonType", buttonType);
        EventData.put("EventStatus", EventStatus.values()[status].name());
        EventData.put(varName, varValue);
        ChatEventModel Event = new ChatEventModel()
                .setEventCategory("RIA_ONBOARDING_CHAT")
                .setEventChannel("APP_ANDR")
                .setEventName("POST_CLICK")
                .setEventDateTime(System.currentTimeMillis()/1000)
                .setDeviceId(deviceId)
                .setNodeId(NodeId)
                .setEventData(EventData);
        mDatabase.child(DB_CHILD_NAME).push().setValue(Event);

    }
}
