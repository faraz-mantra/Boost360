package com.nowfloats.riachatsdk.helpers;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nowfloats.riachatsdk.ChatManager;
import com.nowfloats.riachatsdk.models.ChatEventModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.TimeZone;

/**
 * Created by NowFloats on 30-03-2017.
 */

public class ChatLogger {
    private static final String DB_CHILD_NAME = "RiaChatSDK";
    //        private static final String DB_CHILD_NAME = "RiaChatTestSDK";
    private static final String DB_FEEDBACK_CHILD_NAME = "NpsSDK";
    private static final String EVENT_CATEGORY_ONBOARDING = "RIA_ONBOARDING_CHAT";
    private static final String EVENT_CATEGORY_NPS = "NF_FEEDBACK_CHAT";
    //    private static final String DB_CHILD_NAME = "RiaChatTestSDK";
    //    private static final String DB_CHILD_NAME = "ChatSDKTestAndroid";
    //private static Bus mBus;
    public static boolean lastEventStatus;
    public static ChatManager.ChatType chatType;
    private static ChatLogger sRiaEventLogger;
    private DatabaseReference mDatabase;

    private ChatLogger() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public static ChatLogger getInstance(ChatManager.ChatType chatOType) {
        if (sRiaEventLogger == null) {
            sRiaEventLogger = new ChatLogger();
        }
        chatType = chatOType;
        return sRiaEventLogger;
    }

    public void logViewEvent(String deviceId, String NodeId, String appVersion, String flowId, String sessionId, String fpTag) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        ChatEventModel Event = new ChatEventModel()
                .setEventChannel("APP_ANDR")
                .setEventName("VIEW")
                .setAppVersion(appVersion)
                .setEventDateTime(System.currentTimeMillis() / 1000)
                .setDeviceId(deviceId)
                .setNodeId(NodeId)
                .setFlowId(flowId)
                .setSessionId(sessionId);

        switch (chatType) {
            case CREATE_WEBSITE:
                Event.setEventCategory(EVENT_CATEGORY_ONBOARDING);
                mDatabase.child(DB_CHILD_NAME).push().setValue(Event);
                break;
            case FEEDBACK:
                Event.setFpTag(fpTag);
                Event.setEventCategory(EVENT_CATEGORY_NPS);
                mDatabase.child(DB_FEEDBACK_CHILD_NAME).push().setValue(Event);
                break;
        }

    }

    public void logClickEvent(String deviceId, String NodeId, String buttonId,
                              String buttonLabel, String varName, String varValue, String buttonType, String appVersion
            , String flowId, String sessionId, String fpTag) {

        DateFormat df = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        HashMap<String, String> EventData = new HashMap<>();
        EventData.put("ButtonId", buttonId);
        EventData.put("ButtonLabel", buttonLabel);
        EventData.put("ButtonType", buttonType);
        ChatEventModel Event = new ChatEventModel();
        if (null == varName || null == varValue) {
            Event
                    .setEventCategory("RIA_ONBOARDING_CHAT")
                    .setEventChannel("APP_ANDR")
                    .setEventName("CLICK")
                    .setEventDateTime(System.currentTimeMillis() / 1000)
                    .setDeviceId(deviceId)
                    .setNodeId(NodeId)
                    .setFlowId(flowId)
                    .setSessionId(sessionId)
                    .setAppVersion(appVersion)
                    .setEventData(EventData);
        } else {
            HashMap<String, String> UserData = new HashMap<>();
            UserData.put(varName, varValue);
            Event
                    .setEventCategory("RIA_ONBOARDING_CHAT")
                    .setEventChannel("APP_ANDR")
                    .setEventName("CLICK")
                    .setEventDateTime(System.currentTimeMillis() / 1000)
                    .setDeviceId(deviceId)
                    .setNodeId(NodeId)
                    .setFlowId(flowId)
                    .setSessionId(sessionId)
                    .setAppVersion(appVersion)
                    .setEventData(EventData)
                    .setUserData(UserData);

        }
        switch (chatType) {
            case CREATE_WEBSITE:
                Event.setEventCategory(EVENT_CATEGORY_ONBOARDING);
                mDatabase.child(DB_CHILD_NAME).push().setValue(Event);
                break;
            case FEEDBACK:
                Event.setFpTag(fpTag);
                Event.setEventCategory(EVENT_CATEGORY_NPS);
                mDatabase.child(DB_FEEDBACK_CHILD_NAME).push().setValue(Event);
                break;
        }

    }

    public void logPostEvent(String deviceId, String NodeId, String buttonId, String buttonLabel,
                             int status, String buttonType, HashMap<String, String> UserData, String appVersion,
                             String flowId, String sessionId, String fpTag) {
        if (status == EventStatus.COMPLETED.getValue()) {
            lastEventStatus = true;
        } else {
            lastEventStatus = false;
        }
        DateFormat df = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        HashMap<String, String> EventData = new HashMap<>();
        EventData.put("ButtonId", buttonId);
        EventData.put("ButtonLabel", buttonLabel);
        EventData.put("ButtonType", buttonType);
        EventData.put("EventStatus", EventStatus.values()[status].name());
//        EventData.put(varName, varValue);
        ChatEventModel Event = null;
        if (UserData != null) {

            Event = new ChatEventModel()
                    .setEventCategory("RIA_ONBOARDING_CHAT")
                    .setEventChannel("APP_ANDR")
                    .setEventName("POST_CLICK")
                    .setEventDateTime(System.currentTimeMillis() / 1000)
                    .setDeviceId(deviceId)
                    .setNodeId(NodeId)
                    .setFlowId(flowId)
                    .setSessionId(sessionId)
                    .setAppVersion(appVersion)
                    .setEventData(EventData)
                    .setUserData(UserData);
        } else {

            Event = new ChatEventModel()
                    .setEventCategory("RIA_ONBOARDING_CHAT")
                    .setEventChannel("APP_ANDR")
                    .setEventName("POST_CLICK")
                    .setEventDateTime(System.currentTimeMillis() / 1000)
                    .setDeviceId(deviceId)
                    .setNodeId(NodeId)
                    .setFlowId(flowId)
                    .setSessionId(sessionId)
                    .setAppVersion(appVersion)
                    .setEventData(EventData);
        }

        switch (chatType) {
            case CREATE_WEBSITE:
                Event.setEventCategory(EVENT_CATEGORY_ONBOARDING);
                mDatabase.child(DB_CHILD_NAME).push().setValue(Event);
                break;
            case FEEDBACK:
                Event.setFpTag(fpTag);
                Event.setEventCategory(EVENT_CATEGORY_NPS);
                mDatabase.child(DB_FEEDBACK_CHILD_NAME).push().setValue(Event);
                break;
        }

    }

    public enum EventStatus {
        COMPLETED(0), DROPPED(1);
        private final int value;

        private EventStatus(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
