package com.nowfloats.util;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nowfloats.NavigationDrawer.model.RiaEventModel;
import com.thinksity.BuildConfig;
import com.thinksity.Specific;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.TimeZone;

/**
 * Created by NowFloats on 09-02-2017.
 */

public class RiaEventLogger {
    private static RiaEventLogger sRiaEventLogger;
    private DatabaseReference mDatabase;
    //private static Bus mBus;
    public static boolean lastEventStatus;
    public static boolean isLastEventCompleted;
    public static RiaEventLogger getInstance(){
        if(sRiaEventLogger==null){
            sRiaEventLogger = new RiaEventLogger();
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

    private RiaEventLogger(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void logViewEvent(String FPTag, String NodeId){
        if(!BuildConfig.DEBUG) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            RiaEventModel Event = new RiaEventModel()
                    .setEventCategory("RIA_CARD")
                    .setEventChannel("APP_ANDR")
                    .setEventName("VIEW")
                    .setEventDateTime(System.currentTimeMillis()/1000)
                    .setFpTag(FPTag)
                    .setNodeId(NodeId);

            mDatabase.child(Specific.RIA_FIREBASE_COLLECTION_NAME).push().setValue(Event);
        }
    }

    public void logClickEvent(String FPTag, String NodeId, String buttonId, String buttonLabel){
        if(!BuildConfig.DEBUG) {
            DateFormat df = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            HashMap<String, String> EventData = new HashMap<>();
            EventData.put("ButtonId", buttonId);
            EventData.put("ButtonLabel", buttonLabel);
            RiaEventModel Event = new RiaEventModel()
                    .setEventCategory("RIA_CARD")
                    .setEventChannel("APP_ANDR")
                    .setEventName("CLICK")
                    .setEventDateTime(System.currentTimeMillis()/1000)
                    .setFpTag(FPTag)
                    .setNodeId(NodeId)
                    .setEventData(EventData);
            mDatabase.child(Specific.RIA_FIREBASE_COLLECTION_NAME).push().setValue(Event);
        }
    }

    public void logPostEvent(String FPTag, String NodeId, String buttonId, String buttonLabel, int status){
        if(status == EventStatus.COMPLETED.getValue()){
            isLastEventCompleted = true;
        }else {
            isLastEventCompleted = false;
        }
        if(!BuildConfig.DEBUG) {

            DateFormat df = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            HashMap<String, String> EventData = new HashMap<>();
            EventData.put("ButtonId", buttonId);
            EventData.put("ButtonLabel", buttonLabel);
            EventData.put("EventStatus", EventStatus.values()[status].name());
            RiaEventModel Event = new RiaEventModel()
                    .setEventCategory("RIA_CARD")
                    .setEventChannel("APP_ANDR")
                    .setEventName("POST_CLICK")
                    .setEventDateTime(System.currentTimeMillis()/1000)
                    .setFpTag(FPTag)
                    .setNodeId(NodeId)
                    .setEventData(EventData);
            mDatabase.child(Specific.RIA_FIREBASE_COLLECTION_NAME).push().setValue(Event);
        }
    }
}
