package com.nowfloats.riachatsdk.models;

import java.util.HashMap;

/**
 * Created by NowFloats on 30-03-2017.
 */

public class ChatEventModel {
    public String EventCategory;
    public String ExternalSourceId;
    public long EventDateTime;
    public String DeviceId;
    public String EventChannel = "APP_ANDR";
    public String EventName;
    public String AppVersion;
    public String NodeId;
    public String FlowId;
    public String SessionId;
    public HashMap<String, String> EventData;
    public HashMap<String, String> UserData;

    public ChatEventModel setExternalSourceId(String externalSourceId) {
        ExternalSourceId = externalSourceId;
        return this;
    }

    public ChatEventModel setEventCategory(String eventCategory) {
        EventCategory = eventCategory;
        return this;
    }

    public ChatEventModel setEventDateTime(long eventDateTime) {
        EventDateTime = eventDateTime;
        return this;
    }

    public ChatEventModel setDeviceId(String deviceId) {
        DeviceId = deviceId;
        return this;
    }

    public ChatEventModel setEventChannel(String eventChannel) {
        EventChannel = eventChannel;
        return this;
    }

    public ChatEventModel setEventName(String eventName) {
        EventName = eventName;
        return this;
    }

    public ChatEventModel setAppVersion(String appVersion) {
        AppVersion = appVersion;
        return this;
    }

    public ChatEventModel setNodeId(String nodeId) {
        NodeId = nodeId;
        return this;
    }

    public ChatEventModel setFlowId(String flowId) {
        FlowId = flowId;
        return this;
    }

    public ChatEventModel setSessionId(String sessionId) {
        SessionId = sessionId;
        return this;
    }

    public ChatEventModel setEventData(HashMap<String, String> eventData) {
        EventData = eventData;
        return this;
    }

    public ChatEventModel setUserData(HashMap<String, String> userData) {
        UserData = userData;
        return this;
    }
}
