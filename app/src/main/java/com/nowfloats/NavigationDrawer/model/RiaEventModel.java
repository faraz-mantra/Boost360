package com.nowfloats.NavigationDrawer.model;

import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;

/**
 * Created by NowFloats on 09-02-2017.
 */

public class RiaEventModel {
    private String eventCategory;
    private Date eventDateTime;
    private String fpTag;
    private String eventChannel = "APP_ANDR";
    private String eventName;
    private String nodeId;
    private HashMap<String, String> eventData;

    public RiaEventModel(String eventCategory, Date eventDateTime, String fpTag, String eventChannel,
                         String eventName, String nodeId, HashMap<String, String> eventData) {
        this.eventCategory = eventCategory;
        this.eventDateTime = eventDateTime;
        this.fpTag = fpTag;
        this.eventChannel = eventChannel;
        this.eventName = eventName;
        this.nodeId = nodeId;
        this.eventData = eventData;
    }

    public RiaEventModel(){

    }

    public String getEventCategory() {
        return eventCategory;
    }

    public RiaEventModel setEventCategory(String eventCategory) {
        this.eventCategory = eventCategory;
        return this;
    }

    public Date getEventDateTime() {
        return eventDateTime;
    }

    public RiaEventModel setEventDateTime(Date eventDateTime) {
        this.eventDateTime = eventDateTime;
        return this;
    }

    public String getFpTag() {
        return fpTag;
    }

    public RiaEventModel setFpTag(String fpTag) {
        this.fpTag = fpTag;
        return this;
    }

    public String getEventChannel() {
        return eventChannel;
    }

    public RiaEventModel setEventChannel(String eventChannel) {
        this.eventChannel = eventChannel;
        return this;
    }

    public String getEventName() {
        return eventName;
    }

    public RiaEventModel setEventName(String eventName) {
        this.eventName = eventName;
        return this;
    }

    public String getNodeId() {
        return nodeId;
    }

    public RiaEventModel setNodeId(String nodeId) {
        this.nodeId = nodeId;
        return this;
    }

    public HashMap<String, String> getEventData() {
        return eventData;
    }

    public RiaEventModel setEventData(HashMap<String, String> eventData) {
        this.eventData = eventData;
        return this;
    }
}
