package com.nowfloats.NavigationDrawer.model;

import java.util.HashMap;

/**
 * Created by NowFloats on 09-02-2017.
 */

public class RiaEventModel {
    private String EventCategory;
    private long EventDateTime;
    private String FpTag;
    private String EventChannel = "APP_ANDR";
    private String EventName;
    private String NodeId;
    private HashMap<String, String> EventData;

    public RiaEventModel(String eventCategory, long eventDateTime, String fpTag, String eventChannel,
                         String eventName, String nodeId, HashMap<String, String> eventData) {
        this.EventCategory = eventCategory;
        this.EventDateTime = eventDateTime;
        this.FpTag = fpTag;
        this.EventChannel = eventChannel;
        this.EventName = eventName;
        this.NodeId = nodeId;
        this.EventData = eventData;
    }

    public RiaEventModel(){

    }

    public String getEventCategory() {
        return EventCategory;
    }

    public RiaEventModel setEventCategory(String eventCategory) {
        this.EventCategory = eventCategory;
        return this;
    }

    public long getEventDateTime() {
        return EventDateTime;
    }

    public RiaEventModel setEventDateTime(long eventDateTime) {
        this.EventDateTime = eventDateTime;
        return this;
    }

    public String getFpTag() {
        return FpTag;
    }

    public RiaEventModel setFpTag(String fpTag) {
        this.FpTag = fpTag;
        return this;
    }

    public String getEventChannel() {
        return EventChannel;
    }

    public RiaEventModel setEventChannel(String eventChannel) {
        this.EventChannel = eventChannel;
        return this;
    }

    public String getEventName() {
        return EventName;
    }

    public RiaEventModel setEventName(String eventName) {
        this.EventName = eventName;
        return this;
    }

    public String getNodeId() {
        return NodeId;
    }

    public RiaEventModel setNodeId(String nodeId) {
        this.NodeId = nodeId;
        return this;
    }

    public HashMap<String, String> getEventData() {
        return EventData;
    }

    public RiaEventModel setEventData(HashMap<String, String> eventData) {
        this.EventData = eventData;
        return this;
    }
}
