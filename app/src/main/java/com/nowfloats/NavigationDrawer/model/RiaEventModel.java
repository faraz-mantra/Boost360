package com.nowfloats.NavigationDrawer.model;

import java.util.HashMap;

/**
 * Created by NowFloats on 09-02-2017.
 */

public class RiaEventModel {
    public String EventCategory;
    public long EventDateTime;
    public String FPTag;
    public String EventChannel = "APP_ANDR";
    public String EventName;
    public String NodeId;
    public HashMap<String, String> EventData;

    public RiaEventModel(String EventCategory, long EventDateTime, String FPTag, String EventChannel,
                         String EventName, String NodeId, HashMap<String, String> EventData) {
        this.EventCategory = EventCategory;
        this.EventDateTime = EventDateTime;
        this.FPTag = FPTag;
        this.EventChannel = EventChannel;
        this.EventName = EventName;
        this.NodeId = NodeId;
        this.EventData = EventData;
    }

    public RiaEventModel(){

    }

    public RiaEventModel setEventCategory(String EventCategory) {
        this.EventCategory = EventCategory;
        return this;
    }


    public RiaEventModel setEventDateTime(long EventDateTime) {
        this.EventDateTime = EventDateTime;
        return this;
    }


    public RiaEventModel setFpTag(String FPTag) {
        this.FPTag = FPTag;
        return this;
    }

    public RiaEventModel setEventChannel(String EventChannel) {
        this.EventChannel = EventChannel;
        return this;
    }

    public RiaEventModel setEventName(String EventName) {
        this.EventName = EventName;
        return this;
    }

    public RiaEventModel setNodeId(String NodeId) {
        this.NodeId = NodeId;
        return this;
    }

    public RiaEventModel setEventData(HashMap<String, String> EventData) {
        this.EventData = EventData;
        return this;
    }
}
