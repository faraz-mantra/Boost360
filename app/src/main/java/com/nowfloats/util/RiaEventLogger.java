package com.nowfloats.util;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nowfloats.NavigationDrawer.model.RiaEventModel;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by NowFloats on 09-02-2017.
 */

public class RiaEventLogger {
    private static RiaEventLogger sRiaEventLogger;
    private DatabaseReference mDatabase;
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

    public void logViewEvent(String fpTag, String nodeId){
        RiaEventModel event = new RiaEventModel().setEventCategory("RIA_CARD")
                .setEventChannel("APP_ANDR")
                .setEventName("VIEW")
                .setEventDateTime(new Date())
                .setFpTag(fpTag)
                .setNodeId(nodeId);

        mDatabase.child("RIAUserActivityLog").push().setValue(event);
    }

    public void logClickEvent(String fpTag, String nodeId, String buttonId, String buttonLabel){
        HashMap<String, String> eventData = new HashMap<>();
        eventData.put("ButtonId", buttonId);
        eventData.put("ButtonLabel", buttonLabel);
        RiaEventModel event = new RiaEventModel().setEventCategory("RIA_CARD")
                .setEventChannel("APP_ANDR")
                .setEventName("CLICK")
                .setEventDateTime(new Date())
                .setFpTag(fpTag)
                .setNodeId(nodeId)
                .setEventData(eventData);
        mDatabase.child("RIAUserActivityLog").push().setValue(event);
    }

    public void logPostEvent(String fpTag, String nodeId, String buttonId, String buttonLabel, int status){
        HashMap<String, String> eventData = new HashMap<>();
        eventData.put("ButtonId", buttonId);
        eventData.put("ButtonLabel", buttonLabel);
        eventData.put("EventStatus", EventStatus.values()[status].name());
        RiaEventModel event = new RiaEventModel().setEventCategory("RIA_CARD")
                .setEventChannel("APP_ANDR")
                .setEventName("POST_CLICK")
                .setEventDateTime(new Date())
                .setFpTag(fpTag)
                .setNodeId(nodeId)
                .setEventData(eventData);
        mDatabase.child("RIAUserActivityLog").push().setValue(event);
    }
}
